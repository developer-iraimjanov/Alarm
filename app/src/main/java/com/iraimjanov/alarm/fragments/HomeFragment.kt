package com.iraimjanov.alarm.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iraimjanov.alarm.R
import com.iraimjanov.alarm.adapter.RVAlarmAdapter
import com.iraimjanov.alarm.databinding.FragmentHomeBinding
import com.iraimjanov.alarm.db.AppDatabase
import com.iraimjanov.alarm.models.Alarm
import com.iraimjanov.alarm.models.Music
import com.iraimjanov.alarm.utils.AlarmSetManager
import com.iraimjanov.alarm.utils.GetTimeFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var appDatabase: AppDatabase
    private var booleanAntiBagDialog = true
    private var booleanAntiBagPopupMenu = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        appDatabase = AppDatabase.getInstance(requireActivity())

        buildRV()

        binding.cardAdd.setOnClickListener {
            AddFragment.music = Music()
            AddFragment.time = Calendar.getInstance().time.time
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }

        return binding.root
    }

    @SuppressLint("CheckResult")
    private fun buildRV() {
        appDatabase.myDao().getAllAlarm()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.rvAlarm.adapter = RVAlarmAdapter(it, object : RVAlarmAdapter.RVClickAlarm {
                    override fun popup(view: View, alarm: Alarm) {
                        if (booleanAntiBagPopupMenu) {
                            buildPopupMenu(view, alarm)
                            booleanAntiBagPopupMenu = false
                        }
                    }

                    override fun setAlarm(position: Int) {
                        if (Date().after(Date(it[position].alarmTimeMillis))) {
                            it[position].alarmTimeMillis =
                                getNewTimeMillis(it[position].alarmTimeMillis)
                            AlarmSetManager().setAlarm(requireActivity(),
                                it[position])
                            it[position].alarmActive = "true"
                            appDatabase.myDao().updateAlarm(it[position])
                        } else {
                            AlarmSetManager().setAlarm(requireActivity(), it[position])
                            it[position].alarmActive = "true"
                            appDatabase.myDao().updateAlarm(it[position])
                        }
                    }

                    override fun cancelAlarm(position: Int) {
                        AlarmSetManager().cancelAlarm(requireActivity(),
                            it[position])
                        it[position].alarmActive = "false"
                        appDatabase.myDao().updateAlarm(it[position])
                    }
                })
            }
    }

    private fun getNewTimeMillis(time: Long): Long {
        val format = GetTimeFormat()
        val today = Calendar.getInstance().time.time
        val tomorrow = (format.getTimeFormat(today, "dd").toInt() + 1).toString()
        val myDate = "${format.getTimeFormat(today, "yyyy/MM/")}$tomorrow ${
            format.getTimeFormat(time,
                "HH:mm")
        }"
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val date = sdf.parse(myDate)
        return date!!.time
    }

    @SuppressLint("RestrictedApi")
    private fun buildPopupMenu(
        view: View,
        alarm: Alarm,
    ) {
        val menuBuilder = MenuBuilder(requireActivity())
        val menuInflater = MenuInflater(requireActivity())
        menuInflater.inflate(R.menu.popup_menu, menuBuilder)
        val menuPopupHelper = MenuPopupHelper(requireActivity(), menuBuilder, view)
        menuPopupHelper.setForceShowIcon(true)
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.menu_edit -> {
                        EditFragment.music = Music()
                        Companion.alarm = alarm
                        findNavController().navigate(R.id.action_homeFragment_to_editFragment)
                    }

                    R.id.menu_delete -> {
                        if (booleanAntiBagDialog) {
                            buildDeleteDialog(alarm)
                            booleanAntiBagDialog = false
                        }
                    }
                }
                return true
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        menuPopupHelper.setOnDismissListener {
            booleanAntiBagPopupMenu = true
        }

        menuPopupHelper.show()
    }

    private fun buildDeleteDialog(alarm: Alarm) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete alarm").setMessage("Are you sure you want to turn off the alarm?")
            .setPositiveButton("Delete") { _, _ ->
                appDatabase.myDao().deleteAlarm(alarm)
                if (alarm.alarmActive.toBoolean()) {
                    AlarmSetManager().cancelAlarm(requireActivity(), alarm)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        builder.setOnDismissListener {
            booleanAntiBagDialog = true
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    companion object {
        var alarm = Alarm()
    }

}