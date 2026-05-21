package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurvivalDao {
    // Survival Guides
    @Query("SELECT * FROM survival_guides")
    fun getAllGuides(): Flow<List<SurvivalGuide>>

    @Query("SELECT * FROM survival_guides WHERE category = :category")
    fun getGuidesByCategory(category: String): Flow<List<SurvivalGuide>>

    @Query("SELECT * FROM survival_guides WHERE id = :id LIMIT 1")
    suspend fun getGuideById(id: String): SurvivalGuide?

    @Query("SELECT * FROM survival_guides WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchGuides(query: String): Flow<List<SurvivalGuide>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuides(guides: List<SurvivalGuide>)

    @Update
    suspend fun updateGuide(guide: SurvivalGuide)

    // Mesh Messages
    @Query("SELECT * FROM mesh_messages ORDER BY timestamp ASC")
    fun getAllMeshMessages(): Flow<List<MeshMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MeshMessage)

    @Query("DELETE FROM mesh_messages")
    suspend fun clearMessageHistory()

    // Checklist Items
    @Query("SELECT * FROM checklist_items WHERE category = :category")
    fun getChecklistByCategory(category: String): Flow<List<SurvivalChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: SurvivalChecklistItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<SurvivalChecklistItem>)

    @Update
    suspend fun updateChecklistItem(item: SurvivalChecklistItem)

    @Delete
    suspend fun deleteChecklistItem(item: SurvivalChecklistItem)
}
