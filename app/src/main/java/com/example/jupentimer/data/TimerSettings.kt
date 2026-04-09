package com.example.jupentimer.data

/**
 * 计时器设置数据类
 */
data class TimerSettings(
    val workSeconds: Int = 40,           // 训练时间（秒）
    val restSeconds: Int = 20,           // 休息时间（秒）
    val totalMinutes: Int = 40,          // 总时长（分钟）
    val soundEnabled: Boolean = true,    // 音效开关
    val ttsEnabled: Boolean = true,      // 语音播报开关
    val vibrateEnabled: Boolean = true,  // 振动开关
    val countdownSeconds: Int = 5        // 开始倒计时秒数
) {
    /**
     * 计算总轮数
     */
    fun getTotalRounds(): Int {
        val cycleSeconds = workSeconds + restSeconds
        val totalSeconds = totalMinutes * 60
        return totalSeconds / cycleSeconds
    }

    companion object {
        val DEFAULT = TimerSettings()
    }
}
