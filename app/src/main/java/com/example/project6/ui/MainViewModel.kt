package com.example.project6.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.project6.data.BitFitEntity
import com.example.project6.data.BitFitRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: BitFitRepository) : ViewModel() {

    val allEntries = repository.allEntries.asLiveData()

    fun insert(entry: BitFitEntity) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun delete(entry: BitFitEntity) = viewModelScope.launch {
        repository.delete(entry)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}

class MainViewModelFactory(private val repository: BitFitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
