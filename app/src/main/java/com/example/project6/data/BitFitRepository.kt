package com.example.project6.data

import kotlinx.coroutines.flow.Flow

class BitFitRepository(private val bitFitDao: BitFitDao) {
    val allEntries: Flow<List<BitFitEntity>> = bitFitDao.getAllEntries()

    suspend fun getEntryById(id: Long): BitFitEntity? {
        return bitFitDao.getEntryById(id)
    }

    suspend fun insert(entry: BitFitEntity) {
        bitFitDao.insert(entry)
    }

    suspend fun delete(entry: BitFitEntity) {
        bitFitDao.delete(entry)
    }

    suspend fun deleteAll() {
        bitFitDao.deleteAll()
    }
}
