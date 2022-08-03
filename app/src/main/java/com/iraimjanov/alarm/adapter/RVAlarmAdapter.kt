package com.iraimjanov.alarm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.alarm.databinding.ItemAlarmBinding
import com.iraimjanov.alarm.models.Alarm
import java.text.SimpleDateFormat
import java.util.*

class RVAlarmAdapter(private val listAlarm: List<Alarm>, private val rvClickAlarm: RVClickAlarm) :
    RecyclerView.Adapter<RVAlarmAdapter.VH>() {

    inner class VH(var itemRV: ItemAlarmBinding) : RecyclerView.ViewHolder(itemRV.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(alarm: Alarm, position: Int) {
            itemRV.switchButton.setEnableEffect(false)
            itemRV.switchButton.setShadowEffect(false)
            itemRV.switchButton.isChecked = alarm.alarmActive.toBoolean()
            itemRV.switchButton.setEnableEffect(true)
            itemRV.switchButton.setShadowEffect(true)
            itemRV.tvTime.text = getTimeFormat(alarm.alarmTimeMillis)
            if (alarm.everyDayMode.toBoolean()) {
                itemRV.tvAlarmMode.text = "Every day"
            } else {
                itemRV.tvAlarmMode.text = "One day"
            }
            itemRV.switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rvClickAlarm.setAlarm(position)
                } else {
                    rvClickAlarm.cancelAlarm(position)
                }
            }
            itemRV.imageMenu.setOnClickListener {
                rvClickAlarm.popup(it, alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listAlarm[position], position)
    }

    override fun getItemCount(): Int = listAlarm.size

    @SuppressLint("SimpleDateFormat")
    fun getTimeFormat(time: Long): String {
        return SimpleDateFormat("HH:mm").format(Date(time))
    }

    interface RVClickAlarm {
        fun popup(view: View, alarm: Alarm)
        fun setAlarm(position: Int)
        fun cancelAlarm(position: Int)
    }

}