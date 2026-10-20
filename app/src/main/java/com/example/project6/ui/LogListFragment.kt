package com.example.project6.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project6.BitFitApplication
import com.example.project6.data.BitFitEntity
import com.example.project6.databinding.FragmentLogListBinding

class LogListFragment : Fragment() {

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as BitFitApplication).repository)
    }

    private lateinit var adapter: BitFitAdapter
    private var allList: List<BitFitEntity> = emptyList()
    private var selectedCategory: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipFilter()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = BitFitAdapter { item ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("EXTRA_BITFIT_ENTRY", item)
            }
            startActivity(intent)
        }
        binding.rvEntries.adapter = adapter
    }

    private fun setupChipFilter() {
        binding.cgFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedCategory = when {
                checkedIds.contains(binding.chipFood.id) -> "Food"
                checkedIds.contains(binding.chipExercise.id) -> "Exercise"
                checkedIds.contains(binding.chipWater.id) -> "Water"
                checkedIds.contains(binding.chipSleep.id) -> "Sleep"
                else -> "All"
            }
            filterList()
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddEntryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            allList = entries ?: emptyList()
            filterList()
        }
    }

    private fun filterList() {
        val filtered = if (selectedCategory == "All") {
            allList
        } else {
            allList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        adapter.submitList(filtered)

        if (filtered.isEmpty()) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvEntries.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvEntries.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
