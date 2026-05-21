package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survival_guides")
data class SurvivalGuide(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "FIRST_AID", "SHELTER", "WATER", "FIRE", "NAVIGATION"
    val content: String,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "mesh_messages")
data class MeshMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "Me", "Survivor-XR7", "Basecamp-Beta", "SYSTEM"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val signalStrength: Int = 100, // 0 - 100 percentage
    val isEmergency: Boolean = false,
    val hops: Int = 1 // Mesh network hops
)

@Entity(tableName = "checklist_items")
data class SurvivalChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "BUG_OUT_BAG", "FIRST_AID_KIT", "SHELTER_PREP"
    val isCompleted: Boolean = false,
    val quantity: String = "1 unit"
)
