package com.iraimjanov.alarm.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import com.iraimjanov.alarm.models.Alarm

class PlayRingtone {

    fun playRingtone(context: Context, alarm: Alarm) {
        var prepare = false
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(alarm.alarmAudioPath)
            prepare = true
        } catch (e: Exception) {
            if (mediaPlayer != MediaPlayer()) {
                val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                mediaPlayer = MediaPlayer.create(context, ringtoneUri)
            }
        }
        if (prepare) {
            mediaPlayer.prepare()
        }
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun stopRingtone() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    companion object {
        lateinit var mediaPlayer: MediaPlayer
    }
}