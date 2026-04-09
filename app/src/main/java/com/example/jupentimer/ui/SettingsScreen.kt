package com.example.jupentimer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jupentimer.data.TimerSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TimerViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val totalRounds = settings.getTotalRounds()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 训练设置
            SettingsSection(title = "训练设置") {
                // 训练时间
                SliderSettingItem(
                    title = "训练时间",
                    value = settings.workSeconds,
                    range = 10..120,
                    unit = "秒",
                    onValueChange = { viewModel.updateWorkSeconds(it) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 休息时间
                SliderSettingItem(
                    title = "休息时间",
                    value = settings.restSeconds,
                    range = 10..60,
                    unit = "秒",
                    onValueChange = { viewModel.updateRestSeconds(it) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 总时长
                SliderSettingItem(
                    title = "总时长",
                    value = settings.totalMinutes,
                    range = 5..60,
                    unit = "分钟",
                    onValueChange = { viewModel.updateTotalMinutes(it) }
                )

                // 显示总轮数
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "预计总轮数",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "$totalRounds 轮",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 提醒设置
            SettingsSection(title = "提醒设置") {
                SwitchSettingItem(
                    title = "音效",
                    subtitle = "播放提示音",
                    checked = settings.soundEnabled,
                    onCheckedChange = { viewModel.updateSoundEnabled(it) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingItem(
                    title = "语音播报",
                    subtitle = "播报状态和时间",
                    checked = settings.ttsEnabled,
                    onCheckedChange = { viewModel.updateTtsEnabled(it) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingItem(
                    title = "振动",
                    subtitle = "状态切换时振动",
                    checked = settings.vibrateEnabled,
                    onCheckedChange = { viewModel.updateVibrateEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 说明
            Text(
                text = "说明",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "• 训练40秒，休息20秒，为一轮\n" +
                        "• 默认40分钟约40轮\n" +
                        "• 支持后台运行，有通知显示进度\n" +
                        "• 训练开始和结束时会有语音提醒",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

@Composable
fun SliderSettingItem(
    title: String,
    value: Int,
    range: IntRange,
    unit: String,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$value $unit",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first) / 5 - 1,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
