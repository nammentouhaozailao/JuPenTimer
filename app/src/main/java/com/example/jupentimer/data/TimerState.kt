package com.example.jupentimer.data

/**
 * 计时器状态
 */
sealed class TimerState {
    abstract val round: Int
    abstract val remainingSeconds: Int

    /**
     * 准备状态
     */
    data class Ready(
        override val round: Int = 0,
        override val remainingSeconds: Int = 0
    ) : TimerState()

    /**
     * 倒计时状态
     */
    data class Countdown(
        override val round: Int = 0,
        override val remainingSeconds: Int
    ) : TimerState()

    /**
     * 训练状态
     */
    data class Working(
        override val round: Int,
        override val remainingSeconds: Int
    ) : TimerState()

    /**
     * 休息状态
     */
    data class Resting(
        override val round: Int,
        override val remainingSeconds: Int
    ) : TimerState()

    /**
     * 暂停状态
     */
    data class Paused(
        val previousState: TimerState,
        override val round: Int = previousState.round,
        override val remainingSeconds: Int = previousState.remainingSeconds
    ) : TimerState()

    /**
     * 完成状态
     */
    data class Finished(
        override val round: Int = 0,
        override val remainingSeconds: Int = 0
    ) : TimerState()
}

/**
 * 获取状态的中文名称
 */
fun TimerState.getStateName(): String = when (this) {
    is TimerState.Ready -> "准备"
    is TimerState.Countdown -> "倒计时"
    is TimerState.Working -> "训练中"
    is TimerState.Resting -> "休息中"
    is TimerState.Paused -> "已暂停"
    is TimerState.Finished -> "已完成"
}

/**
 * 获取状态对应的颜色类型
 */
fun TimerState.getColorType(): String = when (this) {
    is TimerState.Ready -> "ready"
    is TimerState.Countdown -> "ready"
    is TimerState.Working -> "work"
    is TimerState.Resting -> "rest"
    is TimerState.Paused -> "ready"
    is TimerState.Finished -> "ready"
}
