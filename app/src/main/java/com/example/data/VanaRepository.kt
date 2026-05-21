package com.example.data

import kotlinx.coroutines.flow.Flow

class VanaRepository(private val survivalDao: SurvivalDao) {

    // Guides
    val allGuides: Flow<List<SurvivalGuide>> = survivalDao.getAllGuides()

    fun getGuidesByCategory(category: String): Flow<List<SurvivalGuide>> {
        return survivalDao.getGuidesByCategory(category)
    }

    suspend fun getGuideById(id: String): SurvivalGuide? {
        return survivalDao.getGuideById(id)
    }

    fun searchGuides(query: String): Flow<List<SurvivalGuide>> {
        return survivalDao.searchGuides(query)
    }

    suspend fun insertGuides(guides: List<SurvivalGuide>) {
        survivalDao.insertGuides(guides)
    }

    suspend fun updateGuide(guide: SurvivalGuide) {
        survivalDao.updateGuide(guide)
    }

    // Mesh Messaging
    val allMeshMessages: Flow<List<MeshMessage>> = survivalDao.getAllMeshMessages()

    suspend fun sendMessage(message: MeshMessage) {
        survivalDao.insertMessage(message)
    }

    suspend fun clearMessageHistory() {
        survivalDao.clearMessageHistory()
    }

    // Checklists
    fun getChecklistByCategory(category: String): Flow<List<SurvivalChecklistItem>> {
        return survivalDao.getChecklistByCategory(category)
    }

    suspend fun insertChecklistItem(item: SurvivalChecklistItem) {
        survivalDao.insertChecklistItem(item)
    }

    suspend fun updateChecklistItem(item: SurvivalChecklistItem) {
        survivalDao.updateChecklistItem(item)
    }

    suspend fun deleteChecklistItem(item: SurvivalChecklistItem) {
        survivalDao.deleteChecklistItem(item)
    }
}
