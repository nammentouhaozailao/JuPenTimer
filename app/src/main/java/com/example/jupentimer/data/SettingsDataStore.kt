package com.example.jupentimer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val WORK_SECONDS = intPreferencesKey("work_seconds")
        val REST_SECONDS = intPreferencesKey("rest_seconds")
        val TOTAL_MINUTES = intPreferencesKey("total_minutes")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
        val VIBRATE_ENABLED = booleanPreferencesKey("vibrate_enabled")
        val COUNTDOWN_SECONDS = intPreferencesKey("countdown_seconds")
    }

    val settings: Flow<TimerSettings> = context.dataStore.data.map { preferences ->
        TimerSettings(
            workSeconds = preferences[WORK_SECONDS] ?: 40,
            restSeconds = preferences[REST_SECONDS] ?: 20,
            totalMinutes = preferences[TOTAL_MINUTES] ?: 40,
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            ttsEnabled = preferences[TTS_ENABLED] ?: true,
            vibrateEnabled = preferences[VIBRATE_ENABLED] ?: true,
            countdownSeconds = preferences[COUNTDOWN_SECONDS] ?: 5
        )
    }

    suspend fun updateSettings(settings: TimerSettings) {
        context.dataStore.edit { preferences ->
            preferences[WORK_SECONDS] = settings.workSeconds
            preferences[REST_SECONDS] = settings.restSeconds
            preferences[TOTAL_MINUTES] = settings.totalMinutes
            preferences[SOUND_ENABLED] = settings.soundEnabled
            preferences[TTS_ENABLED] = settings.ttsEnabled
            preferences[VIBRATE_ENABLED] = settings.vibrateEnabled
            preferences[COUNTDOWN_SECONDS] = settings.countdownSeconds
        }
    }

    suspend fun updateWorkSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[WORK_SECONDS] = seconds
        }
    }

    suspend fun updateRestSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[REST_SECONDS] = seconds
        }
    }

    suspend fun updateTotalMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_MINUTES] = minutes
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateTtsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TTS_ENABLED] = enabled
        }
    }

    suspend fun updateVibrateEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATE_ENABLED] = enabled
        }
    }
}
