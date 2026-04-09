package com.example.jupentimer.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jupentimer.data.SettingsDataStore
import com.example.jupentimer.data.TimerSettings
import com.example.jupentimer.data.TimerState
import com.example.jupentimer.service.TimerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)
    private var timerService: TimerService? = null
    private var isBound = false

    private val _settings = MutableStateFlow(TimerSettings.DEFAULT)
    val settings: StateFlow<TimerSettings> = _settings.asStateFlow()

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Ready())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _totalRounds = MutableStateFlow(40)
    val totalRounds: StateFlow<Int> = _totalRounds.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true

            // 绑定后开始监听服务的状态
            viewModelScope.launch {
                timerService?.timerState?.collectLatest { state ->
                    _timerState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
        }
    }

    init {
        // 加载设置
        viewModelScope.launch {
            settingsDataStore.settings.collect { newSettings ->
                _settings.value = newSettings
                _totalRounds.value = newSettings.getTotalRounds()
            }
        }

        // 绑定服务
        bindTimerService()
    }

    private fun bindTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        getApplication<Application>().bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun startTimer() {
        timerService?.startTimer() ?: run {
            val intent = Intent(getApplication(), TimerService::class.java).apply {
                action = TimerService.ACTION_START
            }
            getApplication<Application>().startService(intent)
        }
    }

    fun pauseTimer() {
        timerService?.pauseTimer()
    }

    fun resumeTimer() {
        timerService?.resumeTimer()
    }

    fun stopTimer() {
        timerService?.stopTimer()
    }

    fun resetTimer() {
        timerService?.resetTimer()
    }

    fun updateWorkSeconds(seconds: Int) {
        viewModelScope.launch {
            settingsDataStore.updateWorkSeconds(seconds)
        }
    }

    fun updateRestSeconds(seconds: Int) {
        viewModelScope.launch {
            settingsDataStore.updateRestSeconds(seconds)
        }
    }

    fun updateTotalMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.updateTotalMinutes(minutes)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSoundEnabled(enabled)
        }
    }

    fun updateTtsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateTtsEnabled(enabled)
        }
    }

    fun updateVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateVibrateEnabled(enabled)
        }
    }

    fun saveSettings(settings: TimerSettings) {
        viewModelScope.launch {
            settingsDataStore.updateSettings(settings)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
