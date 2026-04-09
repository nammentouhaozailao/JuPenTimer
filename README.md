# 举盆计时器 (JuPenTimer)

一个专门为"举盆"运动设计的训练计时器安卓App。

## 功能特点

- **默认设置**: 训练40秒 + 休息20秒，总时长40分钟（约40轮）
- **自定义设置**: 可调整训练时间、休息时间、总时长
- **多种提醒**: 音效 + 语音播报 + 振动
- **后台运行**: 锁屏也能计时，通知栏显示进度
- **简洁界面**: 圆形进度条，颜色区分训练/休息状态

## 如何构建APK

### 方法1: 使用Android Studio (推荐)

1. 打开Android Studio
2. 选择 "Open" → 选择 `JuPenTimer` 文件夹
3. 等待Gradle同步完成
4. 点击菜单栏 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. APK将生成在: `app/build/outputs/apk/debug/app-debug.apk`

### 方法2: 使用命令行

需要安装:
- Java JDK 17
- Android SDK

```bash
# 进入项目目录
cd JuPenTimer

# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug

# APK路径: app/build/outputs/apk/debug/app-debug.apk
```

### 方法3: 生成签名发布版APK

```bash
# 创建签名密钥
keytool -genkey -v -keystore my-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias jupentimer

# 在 app/build.gradle.kts 中添加签名配置，然后执行
./gradlew assembleRelease
```

## 安装使用

1. 将APK文件传输到安卓手机
2. 在手机上安装APK（可能需要允许"未知来源"安装）
3. 打开应用，点击开始按钮即可使用

## 项目结构

```
app/src/main/java/com/example/jupentimer/
├── data/              # 数据层
├── service/           # 后台服务
├── ui/                # UI界面
├── util/              # 工具类
└── MainActivity.kt    # 主Activity
```

## 技术栈

- Kotlin
- Jetpack Compose (UI)
- DataStore (设置存储)
- ForegroundService (后台计时)
- TextToSpeech (语音播报)

## 注意事项

- Android 6.0+ (API 24+)
- 需要通知权限（Android 13+）
- 建议开启电池优化白名单以确保后台运行
