package com.example.jupentimer.util

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.jupentimer.data.TimerSettings
import com.example.jupentimer.data.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var settings: TimerSettings = TimerSettings.DEFAULT
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context, this)
        initVibrator()
    }

    private fun initVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.CHINESE)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!isTtsReady) {
                tts?.setLanguage(Locale.CHINESE)
            }
        }
    }

    fun updateSettings(newSettings: TimerSettings) {
        settings = newSettings
    }

    /**
     * 播放状态切换音效和语音
     */
    fun playStateChange(state: TimerState) {
        when (state) {
            is TimerState.Working -> {
                playBeepSound()
                vibrate(VIBRATE_PATTERN_WORK)
                speak("开始训练")
            }
            is TimerState.Resting -> {
                // 休息开始时不播放声音，只在最后5秒提醒
                vibrate(VIBRATE_PATTERN_REST)
            }
            is TimerState.Finished -> {
                playFinishSound()
                vibrate(VIBRATE_PATTERN_FINISH)
                speak("训练完成，辛苦了")
            }
            is TimerState.Countdown -> {
                playTickSound()
            }
            else -> {}
        }
    }

    /**
     * 播报剩余时间
     */
    fun announceRemainingTime(state: TimerState, seconds: Int) {
        if (!settings.ttsEnabled) return

        when {
            // 最后10秒倒计时
            seconds in 1..10 && state is TimerState.Working -> {
                speak("$seconds")
            }
            // 休息最后5秒倒计时
            seconds in 1..5 && state is TimerState.Resting -> {
                speak("$seconds")
            }
            // 每10秒报一次（训练时）
            seconds % 10 == 0 && seconds > 10 && state is TimerState.Working -> {
                speak("还剩 $seconds 秒")
            }
        }
    }

    private fun speak(text: String) {
        if (!settings.ttsEnabled || !isTtsReady) return

        CoroutineScope(Dispatchers.Main).launch {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
        }
    }

    private fun playBeepSound() {
        if (!settings.soundEnabled) return
        try {
            mediaPlayer?.release()
            val uri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.setOnCompletionListener { it.release() }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing beep sound", e)
        }
    }

    private fun playTickSound() {
        if (!settings.soundEnabled) return
        try {
            mediaPlayer?.release()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.setOnCompletionListener { it.release() }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing tick sound", e)
        }
    }

    private fun playRestSound() {
        if (!settings.soundEnabled) return
        try {
            mediaPlayer?.release()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.setVolume(0.5f, 0.5f)
            mediaPlayer?.setOnCompletionListener { it.release() }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing rest sound", e)
        }
    }

    private fun playFinishSound() {
        if (!settings.soundEnabled) return
        try {
            mediaPlayer?.release()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.setOnCompletionListener { it.release() }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing finish sound", e)
        }
    }

    private fun vibrate(pattern: LongArray) {
        if (!settings.vibrateEnabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error vibrating", e)
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        // 短震（开始训练）
        private val VIBRATE_PATTERN_WORK = longArrayOf(0, 300, 100, 300)
        // 双震（休息）
        private val VIBRATE_PATTERN_REST = longArrayOf(0, 500, 200, 500)
        // 长震（完成）
        private val VIBRATE_PATTERN_FINISH = longArrayOf(0, 1000, 300, 1000, 300, 1000)
    }
}
