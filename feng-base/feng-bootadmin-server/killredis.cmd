@echo off
chcp 65001 >nul

:: 检查管理员权限
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo 请右键选择"以管理员身份运行"
    pause
    exit /b
)

:: 获取端口参数
set "PORT=%~1"
if "%PORT%"=="" (
    echo 用法: %~nx0 ^<端口号^>
    echo 示例: %~nx0 6370
    pause
    exit /b
)

:: 验证端口是否为数字
echo %PORT%| findstr /r "^[0-9][0-9]*$" >nul
if errorlevel 1 (
    echo 错误: 端口号必须为数字
    pause
    exit /b
)

echo 正在检查 %PORT% 端口占用...
set "FOUND_PID="
set "FOUND_NAME="

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT%"') do (
    set "FOUND_PID=%%a"
    for /f "tokens=1,*" %%b in ('tasklist /FI "PID eq %%a" /NH') do (
        set "FOUND_NAME=%%b"
    )
)

if not defined FOUND_PID (
    echo %PORT% 端口未被占用
    pause
    exit /b
)

:: 显示进程信息
echo 发现进程占用:
echo [PID] %FOUND_PID%  [名称] %FOUND_NAME%
echo.
choice /c yn /m "确认终止进程？(y/n)"
if %errorlevel% equ 2 exit /b

:: 终止进程
taskkill /F /PID %FOUND_PID%
if %errorlevel% equ 0 (
    echo 进程已终止
) else (
    echo 终止失败，错误代码: %errorlevel%
)
pause