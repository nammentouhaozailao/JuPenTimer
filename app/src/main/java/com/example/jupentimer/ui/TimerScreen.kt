package com.example.jupentimer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jupentimer.data.TimerState
import com.example.jupentimer.data.getColorType
import com.example.jupentimer.data.getStateName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onNavigateToSettings: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

            Spacer(modifier = Modifier.height(32.dp))

            // 倒计时圆圈
            val displayTime = when (timerState) {
                is TimerState.Working -> timerState.remainingSeconds
                is TimerState.Resting -> timerState.remainingSeconds
                is TimerState.Countdown -> timerState.remainingSeconds
                is TimerState.Paused -> timerState.remainingSeconds
                else -> 0
            }

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )

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

            Spacer(modifier = Modifier.height(32.dp))

            // 进度信息
            val currentRound = when (timerState) {
                is TimerState.Working -> timerState.round
                is TimerState.Resting -> timerState.round
                is TimerState.Paused -> timerState.round
                else -> 0
            }

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
            }

            Spacer(modifier = Modifier.weight(1f))

            // 控制按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (timerState) {
                    is TimerState.Ready, is TimerState.Finished -> {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("开始")
                        }
                    }

                    is TimerState.Paused -> {
                        Button(
                            onClick = { viewModel.resumeTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("继续")
                        }

                        Button(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("重置")
                        }

                        Button(
                            onClick = { viewModel.stopTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("停止")
                        }
                    }

                    is TimerState.Working, is TimerState.Resting, is TimerState.Countdown -> {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("暂停")
                        }

                        Button(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("重置")
                        }

                        Button(
                            onClick = { viewModel.stopTimer() },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("停止")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
