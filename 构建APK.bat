@echo off
chcp 65001
cls

echo ======================================
echo      举盆计时器 - APK构建脚本
echo ======================================
echo.

:: 检查Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到Java，请先安装JDK 17
    pause
    exit /b 1
)

echo [1/3] 检查环境... OK
echo.

:: 检查gradlew
if not exist "gradlew.bat" (
    echo [错误] 未找到gradlew.bat
    pause
    exit /b 1
)

echo [2/3] 开始构建APK...
echo.

:: 执行构建
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo [错误] 构建失败
    pause
    exit /b 1
)

echo.
echo [3/3] 构建完成！
echo.
echo APK文件位置:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.

:: 检查文件是否存在
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo [成功] APK已生成！
    echo.
    echo 按任意键打开文件夹...
    pause >nul
    explorer "app\build\outputs\apk\debug"
) else (
    echo [警告] 未找到APK文件，请检查构建日志
    pause
)
