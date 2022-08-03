package com.iraimjanov.alarm.db

import androidx.room.*
import com.iraimjanov.alarm.models.Alarm
import io.reactivex.Flowable

@Dao
interface MyDao {
    @Insert
    fun addAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Query("select * from Alarm")
    fun getAllAlarm(): Flowable<List<Alarm>>

    @Query("select * from Alarm")
    fun getAllAlarmOneCall(): List<Alarm>

    @Query("SELECT * FROM Alarm ORDER BY id DESC LIMIT 1;")
    fun getOneAlarm(): List<Alarm>

}