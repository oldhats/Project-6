package com.example.project6.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project6.R
import com.example.project6.data.BitFitEntity
import com.example.project6.databinding.ItemBitfitLogBinding

class BitFitAdapter(
    private val onItemClick: (BitFitEntity) -> Unit
) : ListAdapter<BitFitEntity, BitFitAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemBitfitLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BitFitEntity, onItemClick: (BitFitEntity) -> Unit) {
            binding.tvEntryName.text = item.name
            binding.tvCategoryDate.text = "${item.category} • ${item.date}"
            binding.tvCaloriesBadge.text = "${item.calories} Cal"

            if (item.notes.isNotBlank()) {
                binding.tvNotes.visibility = View.VISIBLE
                binding.tvNotes.text = "Note: ${item.notes}"
            } else {
                binding.tvNotes.visibility = View.GONE
            }

            // Category Icon
            when (item.category.lowercase()) {
                "exercise" -> binding.ivCategoryIcon.setImageResource(R.drawable.ic_exercise)
                "water" -> binding.ivCategoryIcon.setImageResource(R.drawable.ic_water)
                "sleep" -> binding.ivCategoryIcon.setImageResource(R.drawable.ic_sleep)
                else -> binding.ivCategoryIcon.setImageResource(R.drawable.ic_food)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBitfitLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<BitFitEntity>() {
        override fun areItemsTheSame(oldItem: BitFitEntity, newItem: BitFitEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BitFitEntity, newItem: BitFitEntity): Boolean {
            return oldItem == newItem
        }
    }
}
