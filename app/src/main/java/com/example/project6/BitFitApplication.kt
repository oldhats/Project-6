package com.example.project6

import android.app.Application
import com.example.project6.data.BitFitDatabase
import com.example.project6.data.BitFitRepository

class BitFitApplication : Application() {
    val database by lazy { BitFitDatabase.getDatabase(this) }
    val repository by lazy { BitFitRepository(database.bitFitDao()) }
}
