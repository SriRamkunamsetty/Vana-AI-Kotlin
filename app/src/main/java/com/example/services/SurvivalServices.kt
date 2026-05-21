package com.example.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SurvivalSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _azimuthFlow = MutableStateFlow(0f)
    val bearing: StateFlow<Float> = _azimuthFlow

    private val _stepsFlow = MutableStateFlow(0)
    val steps: StateFlow<Int> = _stepsFlow

    private var gravityValues = FloatArray(3)
    private var geostaticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeostatic = false

    // Callbacks for real physical event propagation
    var onFallDetected: (() -> Unit)? = null
    var onStepRegistered: ((Int) -> Unit)? = null

    // Fall Detection mathematical state
    private var fallCandidateDetected = false
    private var fallCandidateTime = 0L
    private var isImpactRegistered = false
    private var impactTime = 0L

    // Accelerometer rhythmic peak estimation for backup step calculations
    private var lastAccelerationMagnitude = 9.8f
    private var lastStepTime = 0L

    fun startListening() {
        try {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            stepDetector?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun stopListening() {
        try {
            sensorManager.unregisterListener(this)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        try {
            if (event == null || event.values == null) return

            val copyLength = minOf(3, event.values.size)
            if (copyLength == 0) return

            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                _stepsFlow.value += 1
                onStepRegistered?.invoke(_stepsFlow.value)
                return
            }

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                // 1. Low-pass filter smoothing (alpha = 0.15) to damp motor/hand jitter
                for (i in 0 until copyLength) {
                    gravityValues[i] = gravityValues[i] + 0.15f * (event.values[i] - gravityValues[i])
                }
                hasGravity = true

                // 2. Fall detection math based on physical 3D acceleration vectors
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val accMagnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                // Step detection backup through wave peak analysis
                val deltaAcc = accMagnitude - lastAccelerationMagnitude
                lastAccelerationMagnitude = accMagnitude

                if (accMagnitude > 11.5f && deltaAcc > 1.2f && (System.currentTimeMillis() - lastStepTime) > 350) {
                    _stepsFlow.value += 1
                    onStepRegistered?.invoke(_stepsFlow.value)
                    lastStepTime = System.currentTimeMillis()
                }

                // Fall Phase A: Free-fall/Weightless state (acc < 2 m/s^2)
                if (accMagnitude < 2.0f) {
                    fallCandidateDetected = true
                    fallCandidateTime = System.currentTimeMillis()
                } 
                // Fall Phase B: Impact high deceleration crash spike (acc > 26 m/s^2)
                else if (fallCandidateDetected && accMagnitude > 26.0f && (System.currentTimeMillis() - fallCandidateTime) < 1200) {
                    isImpactRegistered = true
                    impactTime = System.currentTimeMillis()
                    fallCandidateDetected = false
                }

                // Fall Phase C: Complete stillness / posture stagnation (acc stable close to gravity g for 1 sec)
                if (isImpactRegistered && (System.currentTimeMillis() - impactTime) > 1000) {
                    val isStill = Math.abs(accMagnitude - 9.8f) < 1.0f
                    if (isStill) {
                        onFallDetected?.invoke()
                    }
                    isImpactRegistered = false
                }

            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                // Low-pass filter magnet sensor
                for (i in 0 until copyLength) {
                    geostaticValues[i] = geostaticValues[i] + 0.15f * (event.values[i] - geostaticValues[i])
                }
                hasGeostatic = true
            }

            if (hasGravity && hasGeostatic) {
                val rRotationMatrix = FloatArray(9)
                val iInclinationMatrix = FloatArray(9)
                val success = SensorManager.getRotationMatrix(rRotationMatrix, iInclinationMatrix, gravityValues, geostaticValues)
                if (success) {
                    val orientationValues = FloatArray(3)
                    SensorManager.getOrientation(rRotationMatrix, orientationValues)
                    val azimuthInRadians = orientationValues[0]
                    var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                    if (azimuthInDegrees.isNaN() || azimuthInDegrees.isInfinite()) {
                        azimuthInDegrees = 0f
                    }
                    if (azimuthInDegrees < 0) {
                        azimuthInDegrees += 360f
                    }
                    _azimuthFlow.value = azimuthInDegrees
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Unused for standard compass tracking
    }
}
