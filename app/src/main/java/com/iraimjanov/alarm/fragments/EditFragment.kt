package com.iraimjanov.alarm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iraimjanov.alarm.R
import com.iraimjanov.alarm.databinding.FragmentEditBinding
import com.iraimjanov.alarm.db.AppDatabase
import com.iraimjanov.alarm.models.Alarm
import com.iraimjanov.alarm.models.Music
import com.iraimjanov.alarm.utils.AlarmSetManager
import com.iraimjanov.alarm.utils.GetTimeFormat
import java.util.*

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var appDatabase: AppDatabase
    private val alarm = HomeFragment.alarm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater)
        appDatabase = AppDatabase.getInstance(requireActivity())

        showFragment()

        binding.cardAlarmTime.setOnClickListener {
            buildTimePicker()
        }

        binding.cardAlarmSound.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_musicSelectionFragment)
        }

        binding.btnEdit.setOnClickListener {
            edit()
        }

        return binding.root
    }

    private fun edit() {
        val time = HomeFragment.alarm.alarmTimeMillis
        val everyDayMode = binding.switchButtonEveryDay.isChecked.toString()
        val vibrateMode = binding.switchVibrate.isChecked.toString()
        val newAlarm = if (music.title.isNotEmpty()) {
            Alarm(alarm.id,
                alarm.alarmTimeMillis,
                music.musicPath,
                music.title,
                alarm.alarmActive,
                everyDayMode,
                vibrateMode)
        } else {
            Alarm(alarm.id,
                alarm.alarmTimeMillis,
                alarm.alarmAudioPath,
                alarm.alarmAudioTittle,
                alarm.alarmActive,
                everyDayMode,
                vibrateMode)
        }

        if (time <= Calendar.getInstance().time.time) {
            newAlarm.alarmTimeMillis += 86400000
            appDatabase.myDao().updateAlarm(newAlarm)
            if (alarm.alarmActive.toBoolean()) {
                AlarmSetManager().cancelAlarm(requireActivity(), newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            }
        } else {
            appDatabase.myDao().updateAlarm(newAlarm)
            if (alarm.alarmActive.toBoolean()) {
                AlarmSetManager().cancelAlarm(requireActivity(), newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            }
        }
        Toast.makeText(requireActivity(), "Edited", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun buildTimePicker() {
        val timeArray = binding.tvAlarmTimeMillis.text.split(":")

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(timeArray[0].toInt())
            .setMinute(timeArray[1].toInt())
            .setTitleText(getString(R.string.choose_time))
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MINUTE, materialTimePicker.minute)
            calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)

            HomeFragment.alarm.alarmTimeMillis = calendar.time.time
            binding.tvAlarmTimeMillis.text =
                GetTimeFormat().getTimeFormat(calendar.time.time, "HH:mm")
        }
        materialTimePicker.show(requireActivity().supportFragmentManager, "tag_picker")
    }

    private fun showFragment() {
        if (alarm.alarmAudioPath.isNotEmpty()) {
            binding.tvAlarmSoundName.text = alarm.alarmAudioTittle
        }
        if (music.title.isNotEmpty()) {
            binding.tvAlarmSoundName.text = music.title
        }
        binding.tvAlarmTimeMillis.text =
            GetTimeFormat().getTimeFormat(alarm.alarmTimeMillis, "HH:mm")
        binding.switchVibrate.isChecked = alarm.vibrateMode.toBoolean()
        binding.switchButtonEveryDay.isChecked = alarm.everyDayMode.toBoolean()
    }

    companion object {
        var music = Music()
    }

}