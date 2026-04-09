package com.example.jupentimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.jupentimer.MainActivity
import com.example.jupentimer.R
import com.example.jupentimer.data.SettingsDataStore
import com.example.jupentimer.data.TimerSettings
import com.example.jupentimer.data.TimerState
import com.example.jupentimer.util.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerService : Service() {

    private val binder = TimerBinder()
    private lateinit var soundManager: SoundManager
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var wakeLock: PowerManager.WakeLock

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private var settings: TimerSettings = TimerSettings.DEFAULT
    private var currentRound = 0
    private var remainingSeconds = 0
    private var isPaused = false
    private var pausedState: TimerState? = null

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Ready())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        soundManager = SoundManager(this)
        settingsDataStore = SettingsDataStore(this)
        initWakeLock()
        createNotificationChannel()

        // 加载设置
        serviceScope.launch {
            settingsDataStore.settings.collect { newSettings ->
                settings = newSettings
                soundManager.updateSettings(newSettings)
            }
        }
    }

    private fun initWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "JuPenTimer::TimerWakeLock"
        )
        wakeLock.setReferenceCounted(false)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
            ACTION_RESET -> resetTimer()
        }
        return START_STICKY
    }

    /**
     * 开始计时器
     */
    fun startTimer() {
        if (timerJob?.isActive == true) return

        if (!wakeLock.isHeld) {
            wakeLock.acquire(settings.totalMinutes * 60 * 1000L)
        }

        startForeground(NOTIFICATION_ID, buildNotification())

        currentRound = 0
        val totalRounds = settings.getTotalRounds()

        timerJob = serviceScope.launch {
            // 倒计时阶段
            for (i in settings.countdownSeconds downTo 1) {
                _timerState.value = TimerState.Countdown(remainingSeconds = i)
                soundManager.playStateChange(_timerState.value)
                delay(1000)
            }

            // 开始正式训练循环
            while (currentRound < totalRounds) {
                currentRound++

                // 训练阶段
                remainingSeconds = settings.workSeconds
                while (remainingSeconds > 0) {
                    if (isPaused) {
                        delay(100)
                        continue
                    }
                    _timerState.value = TimerState.Working(
                        round = currentRound,
                        remainingSeconds = remainingSeconds
                    )
                    if (remainingSeconds == settings.workSeconds) {
                        soundManager.playStateChange(_timerState.value)
                    }
                    soundManager.announceRemainingTime(_timerState.value, remainingSeconds)
                    updateNotification()
                    delay(1000)
                    remainingSeconds--
                }

                // 检查是否完成所有轮次
                if (currentRound >= totalRounds) break

                // 休息阶段
                remainingSeconds = settings.restSeconds
                while (remainingSeconds > 0) {
                    if (isPaused) {
                        delay(100)
                        continue
                    }
                    _timerState.value = TimerState.Resting(
                        round = currentRound,
                        remainingSeconds = remainingSeconds
                    )
                    if (remainingSeconds == settings.restSeconds) {
                        soundManager.playStateChange(_timerState.value)
                    }
                    soundManager.announceRemainingTime(_timerState.value, remainingSeconds)
                    updateNotification()
                    delay(1000)
                    remainingSeconds--
                }
            }

            // 训练完成
            _timerState.value = TimerState.Finished()
            soundManager.playStateChange(_timerState.value)
            updateNotification()
            stopForeground(STOP_FOREGROUND_REMOVE)
            releaseWakeLock()
        }
    }

    /**
     * 暂停计时器
     */
    fun pauseTimer() {
        isPaused = true
        pausedState = _timerState.value
        _timerState.value = TimerState.Paused(previousState = pausedState!!)
        updateNotification()
    }

    /**
     * 恢复计时器
     */
    fun resumeTimer() {
        isPaused = false
        // 状态会在循环中自动恢复
    }

    /**
     * 停止计时器
     */
    fun stopTimer() {
        timerJob?.cancel()
        resetTimer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        releaseWakeLock()
    }

    /**
     * 重置计时器
     */
    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        currentRound = 0
        remainingSeconds = 0
        isPaused = false
        pausedState = null
        _timerState.value = TimerState.Ready()
        updateNotification()
        stopForeground(STOP_FOREGROUND_REMOVE)
        releaseWakeLock()
    }

    private fun releaseWakeLock() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.channel_description)
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val state = _timerState.value
        val title = when (state) {
            is TimerState.Working -> "训练中 - 第 ${state.round}/${settings.getTotalRounds()} 轮"
            is TimerState.Resting -> "休息中 - 第 ${state.round}/${settings.getTotalRounds()} 轮"
            is TimerState.Paused -> "已暂停"
            is TimerState.Finished -> "训练完成！"
            else -> "举盆计时器"
        }

        val content = when (state) {
            is TimerState.Working -> "剩余 ${state.remainingSeconds} 秒"
            is TimerState.Resting -> "休息 ${state.remainingSeconds} 秒"
            is TimerState.Paused -> {
                val prev = state.previousState
                when (prev) {
                    is TimerState.Working -> "训练暂停 - 剩余 ${prev.remainingSeconds} 秒"
                    is TimerState.Resting -> "休息暂停 - 剩余 ${prev.remainingSeconds} 秒"
                    else -> "已暂停"
                }
            }
            else -> "准备开始"
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        soundManager.release()
        releaseWakeLock()
    }

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START = "com.example.jupentimer.START"
        const val ACTION_PAUSE = "com.example.jupentimer.PAUSE"
        const val ACTION_RESUME = "com.example.jupentimer.RESUME"
        const val ACTION_STOP = "com.example.jupentimer.STOP"
        const val ACTION_RESET = "com.example.jupentimer.RESET"
    }
}
