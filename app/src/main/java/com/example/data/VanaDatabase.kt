package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SurvivalGuide::class, MeshMessage::class, SurvivalChecklistItem::class],
    version = 1,
    exportSchema = false
)
abstract class VanaDatabase : RoomDatabase() {
    abstract fun survivalDao(): SurvivalDao

    companion object {
        @Volatile
        private var INSTANCE: VanaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VanaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VanaDatabase::class.java,
                    "vana_survival_db"
                )
                .addCallback(VanaDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class VanaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.survivalDao()

                    // Pre-populate Survival Guides
                    val standardGuides = listOf(
                        SurvivalGuide(
                            id = "first_aid_bleed",
                            title = "Severe Bleeding Control",
                            category = "FIRST_AID",
                            content = """
                                # EMERGENCY PROTOCOL: SEVERE BLEEDING Control

                                1. **Apply Direct Pressure**: Place a clean cloth over the wound and press firmly with both hands. Maintain pressure until bleeding stops.
                                2. **Elevate**: Raise the injured limb above heart level (if no fractural damage is suspected) to decrease blood pressure at the wound.
                                3. **Pressure Bandage**: Wrap the wound firmly with a sterile bandage to maintain continuous pressure. Do not restrict general arterial blood flow.
                                4. **Tourniquet Application (Critical Alert)**: If bleeding is life-threatening and pressure fails, apply a tourniquet 2-3 inches above the wound (never on joint). Record the exact time (T-hour) of application explicitly on forehead as 'T: HH-MM'.
                                5. **Shock Preventive Position**: Lay victim flat, elevate legs slightly, and keep warm with space blankets to prevent core temp drop.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "first_aid_cpr",
                            title = "High-Quality CPR (Adult/Youth)",
                            category = "FIRST_AID",
                            content = """
                                # FIELD REPRIMAND: CARDIO-PULMONARY RESUSCITATION

                                1. **Confirm Victim Unresponsive & Non-breathing**: Shake shoulders in military grid-style alignment and ask loudly "Are you responsive?". Check chest rise for <10 seconds.
                                2. **Place Hands**: Place heel of one hand in center of the chest (lower half of sternum), lock other hand on top. Keep shoulders direct-aligned above compression vector.
                                3. **Deliver Rapid Compressions**: Push hard (at least 2-2.4 inches deep) and fast (100-120 beats per minute, synced to 'Stayin\' Alive'). Let chest fully recoil after each push.
                                4. **Maintain Cycle**: Deliver 30 compressions followed by 2 quick survival breaths. Repeat. Continue uninterrupted until breathing returns or physical collapse limit is reached.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "water_distill",
                            title = "Solar Still Condensation DIY",
                            category = "WATER",
                            content = """
                                # WATER DEFENSE: THE SOLAR FIELD STILL

                                1. **Excavate Ground Hole**: Dig a circular hole roughly 3 feet wide and 2 feet deep in soil with active sunlight exposure.
                                2. **Mount Collector Cup**: Sit standard collection container perfectly upright in bottom center of hole.
                                3. **Add Green Vegetation**: Surround cup with fresh, non-toxic green leaves / moist soil layers. Soil works as hydration base, foliage increases vapor yield.
                                4. **Seal Over Membrane**: Stretch transparent plastic sheeting over hole opening. Anchor outer seal completely using rocks/heavy sand layers to block escape vectors.
                                5. **Angulate Vapor Drift**: Place single small pebble directly on center of plastic top sheet, directly above center collector cup. Make plastic slide slope down exactly a 45-degree angle pointing to container.
                                6. **Allow Condensation**: Sunlight vaporizes moisture from mud / vegetation, forming droplets under plastic slope which fall directly inside container.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "water_purify",
                            title = "Emergency Water Filtration Methods",
                            category = "WATER",
                            content = """
                                # HYDRATION DISCIPLINE: COMBAT WATER PURIFICATION

                                1. **Pre-Sediment Stage (Coarse Filtration)**: Pass raw wilderness water through layered fabric, clean sand, and local charcoal chunks to filter physical sediments.
                                2. **Thermal Extraction (Boiling - Gold Standard)**: Bring water to a roaring bubble boil. Keep at rolling boil for at least 60 seconds (increase to 3 minutes at extreme high altitude >2000m) to destroy all biological pathogens.
                                3. **Chemical Neutralization**: Use Sodium Dichloroisocyanurate (Aquatabs) or Iodine drops. Wait exactly 30 minutes inside light-shielded flask before consuming.
                                4. **Emergency Charcoal Interaction**: Crushed campfire charcoal absorbs trace toxins, biological vectors, and metal residues. Allow charcoal powder to sit inside sediment water, then pass through sterile fabric.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "shelter_debris",
                            title = "Thermal A-Frame Debris Shelter",
                            category = "SHELTER",
                            content = """
                                # FIELD SHELTER: A-FRAME DEBRIS ASSEMBLY

                                1. **Ridgepole Foundation**: Find a solid, straight branch roughly 10 feet long. Secure one end on a stump/forked branches at waist-height (3 feet), other ground-anchored.
                                2. **Rib Branch Grid**: Lean strong branch ribs along both sides of ridgepole at roughly 45-degree diagonal lines. This forms an enclosed triangular prism cockpit body.
                                3. **Debris Thermal Armor**: Pile dried leaves, pine-needles, and forest floor moss at least 2-3 feet thick over framework. Thickness is direct insulation from blizzard / freeze.
                                4. **Sub-Cockpit Insulation Bed**: Pack floor inside structure with dry foliage at least 1 foot thick. Ground soil absorbs biological core-warmth immediately if un-insulated.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "fire_friction",
                            title = "Bow Drill Friction Fire Mechanics",
                            category = "FIRE",
                            content = """
                                # PYRO-PRODUCTION: THE DIALECTICAL FRICTION BOW DRILL

                                1. **The Bow**: Cut sturdy green branch with curve diameter. String tightly with tactical paracord / bootlaces.
                                2. **Spackle Hearth-Board**: Choose a dry, flat soft-wood block (e.g. Cedar, Willow). Carve standard shallow divot 1 inch from board edge with V-shaped slide-cut for ember deposition.
                                3. **Spindle Prep**: Carve straight 10-inch stick of same soft-wood species. Sharpen top tip and blunt bottom spindle contact circle.
                                4. **Loop Rotation**: Twist bow string once around spindle centre, locking spindle under pressure inside bow leash.
                                5. **Eminent Ignite Stroke**: Brace top spindle tip with a rock hand-hold. Plant left foot on hearth-board, then execute sweeping strokes back-and-forth using flat bow parallel to ground. Look for brown carbon powder followed by amber-gold smoke indicators. Tap powder into dry leaf-tinder bundle, blow gently.
                            """.trimIndent()
                        ),
                        SurvivalGuide(
                            id = "nav_star",
                            title = "Night Navigation: Polaris Celestial Vector",
                            category = "NAVIGATION",
                            content = """
                                # VECTOR TACTIC: NORTH HEMISPHERE CELESTIAL ALIGNMENT

                                1. **Locate Big Dipper (Ursa Major)**: Identify the signature seven-star celestial spoon structure in night sky coordinates.
                                2. **Identify Pointer Stars (Merak & Dubhe)**: Locate the two stars forming outer edge of Big Dipper spoon bowl.
                                3. **Project Vector Pointer**: Draw an imaginary structural line extending outwards from Merak through Dubhe.
                                4. **Trace Polaris (North Star)**: Traverse vector distance roughly 5x the Merak-Dubhe interval. Your line will intercept Polaris, the bright star at end of Ursa Minor (Little Dipper) tail handle.
                                5. **Determine Absolute Heading**: Facing Polaris is directly Facing True Geological North (000° azimuth vector). Map coordinates accordingly.
                            """.trimIndent()
                        )
                    )

                    dao.insertGuides(standardGuides)

                    // Pre-populate Survival Checklist Items
                    val standardItems = listOf(
                        // Bug Out Bag Checklist
                        SurvivalChecklistItem(category = "BUG_OUT_BAG", title = "Stainless Steel Water Filtration Canteen (Pre-Filter)", isCompleted = false, quantity = "1 L"),
                        SurvivalChecklistItem(category = "BUG_OUT_BAG", title = "Military Paracord (550 lb tensile)", isCompleted = false, quantity = "50 feet"),
                        SurvivalChecklistItem(category = "BUG_OUT_BAG", title = "Magnesium Fire-Steel Flint Striker", isCompleted = false, quantity = "2 units"),
                        SurvivalChecklistItem(category = "BUG_OUT_BAG", title = "Compact space blankets & thermal bivy", isCompleted = false, quantity = "3 packs"),
                        SurvivalChecklistItem(category = "BUG_OUT_BAG", title = "Dried Field MRE Rations (High kcal)", isCompleted = false, quantity = "6 pouches"),

                        // First Aid Kit Checklist
                        SurvivalChecklistItem(category = "FIRST_AID_KIT", title = "CAT Gen-7 Combat Arterial Tourniquet", isCompleted = false, quantity = "1 unit"),
                        SurvivalChecklistItem(category = "FIRST_AID_KIT", title = "Sterile Gauze Roll & Blood clotting pads", isCompleted = false, quantity = "4 packs"),
                        SurvivalChecklistItem(category = "FIRST_AID_KIT", title = "Splinting Wire / Sam Splint", isCompleted = false, quantity = "1 set"),
                        SurvivalChecklistItem(category = "FIRST_AID_KIT", title = "Disinfectant Alcohol swabs & Betadine", isCompleted = false, quantity = "20 pieces"),
                        SurvivalChecklistItem(category = "FIRST_AID_KIT", title = "Burn defense gel & hydrocortisone", isCompleted = false, quantity = "3 tubes"),

                        // Shelter Plan Checklist
                        SurvivalChecklistItem(category = "SHELTER_PREP", title = "Waterproof silicon ripstop tarp (10x10ft)", isCompleted = false, quantity = "1 sheet"),
                        SurvivalChecklistItem(category = "SHELTER_PREP", title = "Foldable ground thermal mat", isCompleted = false, quantity = "1 pad"),
                        SurvivalChecklistItem(category = "SHELTER_PREP", title = "Aluminum ultra-light tent stakes", isCompleted = false, quantity = "8 units")
                    )

                    dao.insertChecklistItems(standardItems)

                    // Insert System Notification
                    dao.insertMessage(
                        MeshMessage(
                            sender = "SYSTEM",
                            content = "Vana OS initialized in Offline Mode. Core emergency protocols and topographical maps fully compiled inside local storage.",
                            signalStrength = 100,
                            isEmergency = false,
                            hops = 0
                        )
                    )
                }
            }
        }
    }
}
