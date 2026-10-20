package com.example.project6.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BitFitDao {
    @Query("SELECT * FROM bitfit_table ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BitFitEntity>>

    @Query("SELECT * FROM bitfit_table WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): BitFitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BitFitEntity)

    @Delete
    suspend fun delete(entry: BitFitEntity)

    @Query("DELETE FROM bitfit_table")
    suspend fun deleteAll()
}
