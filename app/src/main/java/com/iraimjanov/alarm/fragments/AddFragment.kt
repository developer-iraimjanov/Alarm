package com.iraimjanov.alarm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iraimjanov.alarm.R
import com.iraimjanov.alarm.databinding.FragmentAddBinding
import com.iraimjanov.alarm.db.AppDatabase
import com.iraimjanov.alarm.models.Alarm
import com.iraimjanov.alarm.models.Music
import com.iraimjanov.alarm.utils.AlarmSetManager
import com.iraimjanov.alarm.utils.GetTimeFormat
import java.util.*

class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater)
        appDatabase = AppDatabase.getInstance(requireActivity())

        showFragment()

        binding.cardAlarmTime.setOnClickListener {
            buildTimePicker()
        }

        binding.cardAlarmSound.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_musicSelectionFragment)
        }

        binding.btnSave.setOnClickListener {
            save()
        }

        return binding.root
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

            time = calendar.time.time
            binding.tvAlarmTimeMillis.text =
                GetTimeFormat().getTimeFormat(calendar.time.time, "HH:mm")
        }
        materialTimePicker.show(requireActivity().supportFragmentManager, "tag_picker")
    }

    private fun showFragment() {
        if (music.title.isNotEmpty()) {
            binding.tvAlarmSoundName.text = music.title
        }
        binding.tvAlarmTimeMillis.text = GetTimeFormat().getTimeFormat(time, "HH:mm")
    }

    private fun save() {
        val everyDayMode = binding.switchButtonEveryDay.isChecked.toString()
        val vibrateMode = binding.switchVibrate.isChecked.toString()
        val lastAlarm = appDatabase.myDao().getOneAlarm()
        if (lastAlarm.isNotEmpty()) {
            val id = lastAlarm[0].id + 1
            val newAlarm =
                Alarm(id, time, music.musicPath, music.title, "true", everyDayMode, vibrateMode)
            if (time <= Calendar.getInstance().time.time) {
                newAlarm.alarmTimeMillis += 86400000
                appDatabase.myDao().addAlarm(newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            } else {
                appDatabase.myDao().addAlarm(newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            }
        } else {
            val newAlarm =
                Alarm(1, time, music.musicPath, music.title, "true", everyDayMode, vibrateMode)
            if (time <= Calendar.getInstance().time.time) {
                newAlarm.alarmTimeMillis += 86400000
                appDatabase.myDao().addAlarm(newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            } else {
                appDatabase.myDao().addAlarm(newAlarm)
                AlarmSetManager().setAlarm(requireActivity(), newAlarm)
            }
        }
        findNavController().popBackStack()
    }

    companion object {
        var time = Calendar.getInstance().time.time
        var music = Music()
    }
}