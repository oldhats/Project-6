package com.example.project6.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project6.BitFitApplication
import com.example.project6.data.BitFitEntity
import com.example.project6.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as BitFitApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnQuickAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEntryActivity::class.java))
        }

        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            updateDashboardMetrics(entries ?: emptyList())
            updateLineChart(entries ?: emptyList())
        }
    }

    private fun updateDashboardMetrics(entries: List<BitFitEntity>) {
        if (entries.isEmpty()) {
            binding.tvAvgCalories.text = "0"
            binding.tvTotalCalories.text = "0"
            binding.tvMinCalories.text = "0"
            binding.tvMaxCalories.text = "0"
            binding.tvTotalLogs.text = "0 entries"
            return
        }

        val totalCalories = entries.sumOf { it.calories }
        val avgCalories = totalCalories / entries.size
        val minCalories = entries.minOf { it.calories }
        val maxCalories = entries.maxOf { it.calories }

        binding.tvAvgCalories.text = String.format(Locale.getDefault(), "%d Cal", avgCalories)
        binding.tvTotalCalories.text = String.format(Locale.getDefault(), "%,d Cal", totalCalories)
        binding.tvMinCalories.text = String.format(Locale.getDefault(), "%d Cal", minCalories)
        binding.tvMaxCalories.text = String.format(Locale.getDefault(), "%d Cal", maxCalories)
        binding.tvTotalLogs.text = "${entries.size} entries"
    }

    private fun updateLineChart(entries: List<BitFitEntity>) {
        if (entries.isEmpty()) {
            binding.lineChart.visibility = View.GONE
            binding.tvNoChartData.visibility = View.VISIBLE
            return
        }

        binding.lineChart.visibility = View.VISIBLE
        binding.tvNoChartData.visibility = View.GONE

        // Chronological order for trend chart
        val sortedEntries = entries.sortedBy { it.timestamp }
        val chartEntries = ArrayList<Entry>()

        sortedEntries.forEachIndexed { index, fitItem ->
            chartEntries.add(Entry((index + 1).toFloat(), fitItem.calories.toFloat()))
        }

        val dataSet = LineDataSet(chartEntries, "Calories per Entry").apply {
            color = Color.parseColor("#6200EE")
            setCircleColor(Color.parseColor("#3700B3"))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(true)
            circleHoleRadius = 2.5f
            valueTextSize = 10f
            valueTextColor = Color.parseColor("#1E1B18")
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.parseColor("#146200EE")
        }

        val lineData = LineData(dataSet)
        binding.lineChart.apply {
            data = lineData
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.GRAY
            }

            axisLeft.apply {
                textColor = Color.GRAY
                setDrawGridLines(true)
            }

            axisRight.isEnabled = false
            animateX(600)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
