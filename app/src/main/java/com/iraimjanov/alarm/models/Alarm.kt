package com.iraimjanov.alarm.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Alarm {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var alarmTimeMillis: Long = 0L
    var alarmAudioPath: String = ""
    var alarmAudioTittle: String = ""
    var alarmActive: String = "false"
    var everyDayMode: String = "false"
    var vibrateMode: String = "false"

    constructor(
        id: Int,
        alarmTimeMillis: Long,
        alarmAudioPath: String,
        alarmAudioTittle: String,
        alarmActive: String,
        everyDayMode: String,
        vibrateMode: String,
    ) {
        this.id = id
        this.alarmTimeMillis = alarmTimeMillis
        this.alarmAudioPath = alarmAudioPath
        this.alarmAudioTittle = alarmAudioTittle
        this.alarmActive = alarmActive
        this.everyDayMode = everyDayMode
        this.vibrateMode = vibrateMode
    }

    constructor()

}