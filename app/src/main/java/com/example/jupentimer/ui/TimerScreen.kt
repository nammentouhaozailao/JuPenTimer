package com.example.jupentimer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jupentimer.data.TimerState
import com.example.jupentimer.data.getColorType
import com.example.jupentimer.data.getStateName

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onNavigateToSettings: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val totalRounds by viewModel.totalRounds.collectAsState()

    val backgroundColor = when (timerState.getColorType()) {
        "work" -> Color(0xFF4CAF50)
        "rest" -> Color(0xFFFF9800)
        else -> Color(0xFF2196F3)
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500),
        label = "background"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("举盆计时器", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedBackgroundColor
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundColor)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 状态显示
            StatusCard(
                timerState = timerState,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 倒计时圆圈
            TimerCircle(
                timerState = timerState,
                modifier = Modifier.size(280.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 进度信息
            ProgressInfo(
                timerState = timerState,
                totalRounds = totalRounds
            )

            Spacer(modifier = Modifier.weight(1f))

            // 控制按钮
            ControlButtons(
                timerState = timerState,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onResume = { viewModel.resumeTimer() },
                onReset = { viewModel.resetTimer() },
                onStop = { viewModel.stopTimer() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatusCard(
    timerState: TimerState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timerState.getStateName(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (timerState is TimerState.Working || timerState is TimerState.Resting) {
                Text(
                    text = "第 ${timerState.round} 轮",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TimerCircle(
    timerState: TimerState,
    modifier: Modifier = Modifier
) {
    val progress = when (timerState) {
        is TimerState.Working -> timerState.remainingSeconds.toFloat() / 40f
        is TimerState.Resting -> timerState.remainingSeconds.toFloat() / 20f
        is TimerState.Countdown -> timerState.remainingSeconds.toFloat() / 5f
        else -> 1f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    val displayTime = when (timerState) {
        is TimerState.Working -> timerState.remainingSeconds
        is TimerState.Resting -> timerState.remainingSeconds
        is TimerState.Countdown -> timerState.remainingSeconds
        is TimerState.Paused -> timerState.remainingSeconds
        else -> 0
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 背景圆
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        )

        // 进度圆
        CircularProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            strokeWidth = 12.dp
        )

        // 时间文字
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = displayTime.toString().padStart(2, '0'),
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "秒",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ProgressInfo(
    timerState: TimerState,
    totalRounds: Int
) {
    val currentRound = when (timerState) {
        is TimerState.Working -> timerState.round
        is TimerState.Resting -> timerState.round
        is TimerState.Paused -> timerState.round
        else -> 0
    }

    val progress = if (totalRounds > 0) currentRound.toFloat() / totalRounds else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "总进度: $currentRound / $totalRounds 轮",
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 进度条
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun ControlButtons(
    timerState: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (timerState) {
            is TimerState.Ready, is TimerState.Finished -> {
                // 开始按钮
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "开始",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            is TimerState.Paused -> {
                // 继续、重置、停止
                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2196F3)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "继续",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = onReset,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "停止",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            is TimerState.Working, is TimerState.Resting, is TimerState.Countdown -> {
                // 暂停、重置、停止
                Button(
                    onClick = onPause,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFF9800)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "暂停",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = onReset,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF757575)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "停止",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

// 简单的卡片组件
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// 简单的圆形进度条
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeWidth: androidx.compose.ui.unit.Dp = 4.dp
) {
    val sweepAngle = progress * 360f

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val stroke = androidx.compose.ui.graphics.StrokeCap.Round
        val size = size.minDimension
        val radius = (size - strokeWidth.toPx()) / 2
        val center = androidx.compose.ui.geometry.Offset(size / 2, size / 2)

        // 背景圆弧
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = androidx.compose.ui.graphics.draw.Stroke(strokeWidth.toPx(), cap = stroke),
            size = androidx.compose.ui.geometry.Size(size, size),
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f)
        )

        // 进度圆弧
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = -sweepAngle,
            useCenter = false,
            style = androidx.compose.ui.graphics.draw.Stroke(strokeWidth.toPx(), cap = stroke),
            size = androidx.compose.ui.geometry.Size(size, size),
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f)
        )
    }
}
