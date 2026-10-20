package com.example.project6.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.project6.databinding.FragmentReminderBinding
import com.example.project6.notifications.NotificationHelper
import java.util.Calendar
import java.util.Locale

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private var reminderHour = 20
    private var reminderMinute = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableReminder()
        } else {
            binding.switchReminder.isChecked = false
            Toast.makeText(requireContext(), "Notification permission required for daily reminders", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPreferences()
        setupUI()
    }

    private fun loadPreferences() {
        val prefs = requireContext().getSharedPreferences("bitfit_prefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("reminder_enabled", false)
        reminderHour = prefs.getInt("reminder_hour", 20)
        reminderMinute = prefs.getInt("reminder_minute", 0)

        binding.switchReminder.isChecked = isEnabled
        updateTimeText()
    }

    private fun setupUI() {
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            val prefs = requireContext().getSharedPreferences("bitfit_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("reminder_enabled", isChecked).apply()

            if (isChecked) {
                checkPermissionAndEnable()
            } else {
                NotificationHelper.cancelDailyReminder(requireContext())
                Toast.makeText(requireContext(), "Daily reminders disabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSetTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    reminderHour = hourOfDay
                    reminderMinute = minute

                    val prefs = requireContext().getSharedPreferences("bitfit_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putInt("reminder_hour", hourOfDay)
                        .putInt("reminder_minute", minute)
                        .apply()

                    updateTimeText()

                    if (binding.switchReminder.isChecked) {
                        NotificationHelper.scheduleDailyReminder(requireContext(), reminderHour, reminderMinute)
                        Toast.makeText(requireContext(), "Reminder updated to ${formatTime(hourOfDay, minute)}", Toast.LENGTH_SHORT).show()
                    }
                },
                reminderHour,
                reminderMinute,
                false
            )
            timePicker.show()
        }

        binding.btnTestNotification.setOnClickListener {
            NotificationHelper.showNotification(
                requireContext(),
                "BitFit Test Notification 🔔",
                "Your daily reminder notification is working perfectly!"
            )
            Toast.makeText(requireContext(), "Test notification triggered!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                enableReminder()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            enableReminder()
        }
    }

    private fun enableReminder() {
        NotificationHelper.scheduleDailyReminder(requireContext(), reminderHour, reminderMinute)
        Toast.makeText(
            requireContext(),
            "Daily reminder scheduled for ${formatTime(reminderHour, reminderMinute)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateTimeText() {
        val formattedTime = formatTime(reminderHour, reminderMinute)
        binding.btnSetTime.text = formattedTime
        binding.tvReminderStatus.text = "Prompt set for $formattedTime daily"
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val displayHour = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
        return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
