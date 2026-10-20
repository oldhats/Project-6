package com.example.project6.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bitfit_table")
data class BitFitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val category: String = "Food",
    val notes: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
