package com.iraimjanov.alarm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.iraimjanov.alarm.databinding.ActivityAlarmBinding
import com.iraimjanov.alarm.db.AppDatabase
import com.iraimjanov.alarm.models.Alarm
import com.iraimjanov.alarm.utils.AlarmSetManager
import com.iraimjanov.alarm.utils.PlayRingtone
import java.text.SimpleDateFormat
import java.util.*


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var listAlarm: ArrayList<Alarm>
    private lateinit var timer: CountDownTimer
    private lateinit var vibrator: Vibrator
    private var currentAlarmIndex = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        read()
        searchAlarm()
        showActivity()
        settingWindow()

        timer = object : CountDownTimer(30000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                if (listAlarm[currentAlarmIndex].vibrateMode.toBoolean()) {
                    vibrator()
                }
            }

            override fun onFinish() {
                if (!isFinishing) {
                    goMainActivity()
                    finish()
                }
            }
        }.start()

        binding.tvCancel.setOnClickListener {
            goMainActivity()
            finish()
        }
    }

    private fun goMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun settingWindow() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    }

    private fun modesCheck() {
        if (listAlarm[currentAlarmIndex].everyDayMode.toBoolean()) {
            listAlarm[currentAlarmIndex].alarmTimeMillis += 86400000
            AlarmSetManager().setAlarm(this, listAlarm[currentAlarmIndex])
        } else {
            listAlarm[currentAlarmIndex].alarmActive = "false"
        }
        appDatabase.myDao().updateAlarm(listAlarm[currentAlarmIndex])
    }

    private fun read() {
        appDatabase = AppDatabase.getInstance(this)
        listAlarm = appDatabase.myDao().getOneAlarm() as ArrayList<Alarm>
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    private fun searchAlarm() {
        val dateAndTime = getCurrentDateStringFormat("dd.MM.yyyy HH:mm")

        for (i in listAlarm.indices) {
            if (dateAndTime == getMillisDateStringFormat(listAlarm[i].alarmTimeMillis)) {
                currentAlarmIndex = i
                break
            }
        }
    }

    private fun showActivity() {
        binding.tvTime.text = getCurrentDateStringFormat("HH:mm")
        binding.tvDate.text = getCurrentDateStringFormat("dd MMMM, EEEE")
        PlayRingtone().playRingtone(this, listAlarm[currentAlarmIndex])
    }

    private fun vibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //if API = 26(Oreo) or higher
            vibrator.vibrate(
                VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            //vibrate for 1 second
            vibrator.vibrate(1000)

            //Vibration Pattern - you can create yours
            val pattern = longArrayOf(0, 200, 10, 500)
            vibrator.vibrate(pattern, -1)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getMillisDateStringFormat(timeMillis: Long): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(timeMillis))
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateStringFormat(format: String): String {
        return SimpleDateFormat(format).format(Date())
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
        PlayRingtone().stopRingtone()
        modesCheck()
    }

}