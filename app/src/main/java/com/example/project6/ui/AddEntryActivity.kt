package com.example.project6.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.project6.BitFitApplication
import com.example.project6.data.BitFitEntity
import com.example.project6.databinding.ActivityAddEntryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as BitFitApplication).repository)
    }

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAdd.setNavigationOnClickListener {
            finish()
        }

        updateDateButtonText()
        setupListeners()
    }

    private fun updateDateButtonText() {
        binding.btnSelectDate.text = "Date: ${dateFormat.format(selectedCalendar.time)}"
    }

    private fun setupListeners() {
        binding.btnSelectDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedCalendar.set(Calendar.YEAR, year)
                    selectedCalendar.set(Calendar.MONTH, month)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateButtonText()
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val caloriesStr = binding.etCalories.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Name is required"
                return@setOnClickListener
            }

            if (caloriesStr.isEmpty()) {
                binding.etCalories.error = "Value is required"
                return@setOnClickListener
            }

            val calories = caloriesStr.toIntOrNull() ?: 0

            val selectedCategory = when {
                binding.chipCategoryExercise.isChecked -> "Exercise"
                binding.chipCategoryWater.isChecked -> "Water"
                binding.chipCategorySleep.isChecked -> "Sleep"
                else -> "Food"
            }

            val newEntry = BitFitEntity(
                name = name,
                calories = calories,
                category = selectedCategory,
                notes = notes,
                date = dateFormat.format(selectedCalendar.time),
                timestamp = selectedCalendar.timeInMillis
            )

            viewModel.insert(newEntry)
            Toast.makeText(this, "Log saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
