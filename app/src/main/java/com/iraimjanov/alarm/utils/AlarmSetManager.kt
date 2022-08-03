package com.iraimjanov.alarm.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iraimjanov.alarm.activities.AlarmActivity
import com.iraimjanov.alarm.activities.MainActivity
import com.iraimjanov.alarm.models.Alarm

class AlarmSetManager {

    fun setAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.alarmTimeMillis,
            getAlarmInfoPendingIntent(context, alarm.id))

        alarmManager.setAlarmClock(alarmClockInfo,
            getAlarmActionPendingIntent(context, alarm.id))
    }

    fun cancelAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getAlarmActionPendingIntent(context, alarm.id))
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getAlarmActionPendingIntent(context: Context, id: Int): PendingIntent? {
        val intent = Intent(context, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getAlarmInfoPendingIntent(context: Context, id: Int): PendingIntent? {
        val alarmInfoIntent = Intent(context, MainActivity::class.java)
        alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context,
            id,
            alarmInfoIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

}