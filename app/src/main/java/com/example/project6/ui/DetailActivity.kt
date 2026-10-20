package com.example.project6.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.project6.BitFitApplication
import com.example.project6.R
import com.example.project6.data.BitFitEntity
import com.example.project6.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as BitFitApplication).repository)
    }

    private var currentItem: BitFitEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarDetail.setNavigationOnClickListener {
            finish()
        }

        currentItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_BITFIT_ENTRY", BitFitEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_BITFIT_ENTRY") as? BitFitEntity
        }

        currentItem?.let { item ->
            populateDetails(item)
        } ?: run {
            Toast.makeText(this, "Error loading item details", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateDetails(item: BitFitEntity) {
        binding.tvDetailName.text = item.name
        binding.tvDetailCategory.text = "Category: ${item.category}"
        binding.tvDetailCalories.text = "${item.calories} Cal"
        binding.tvDetailDate.text = item.date.ifBlank { "Recorded Log" }
        binding.tvDetailNotes.text = item.notes.ifBlank { "No additional notes recorded for this item." }

        when (item.category.lowercase()) {
            "exercise" -> binding.ivDetailIcon.setImageResource(R.drawable.ic_exercise)
            "water" -> binding.ivDetailIcon.setImageResource(R.drawable.ic_water)
            "sleep" -> binding.ivDetailIcon.setImageResource(R.drawable.ic_sleep)
            else -> binding.ivDetailIcon.setImageResource(R.drawable.ic_food)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.delete(item)
            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
