package com.iraimjanov.alarm.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class GetTimeFormat {
    @SuppressLint("SimpleDateFormat")
    fun getTimeFormat(time: Long, format: String): String {
        return SimpleDateFormat(format).format(Date(time))
    }
}