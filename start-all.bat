@echo off
chcp 65001
title 音乐网站启动脚本

echo ======================================
echo 正在启动所有服务，请稍候...
echo ======================================

:: 设置MySQL路径（请根据实际安装路径修改）
cd /d %~dp0
set MYSQL_HOME=%~dp0mysql-9.2.0-winx64
set PATH=%MYSQL_HOME%\bin;%PATH%

echo [1/5] 启动 MySQL 服务...
echo 检查MySQL服务是否已启动...
sc query MySQL | findstr RUNNING
if %ERRORLEVEL% == 0 (
    echo MySQL服务已经在运行中...
) else (
    echo 启动MySQL服务...
    net start MySQL
    if %ERRORLEVEL% == 0 (
        echo MySQL服务启动成功！
    ) else (
        echo 尝试使用mysqld命令启动...
        start "MySQL服务" /min "%MYSQL_HOME%\bin\mysqld.exe" --defaults-file="%MYSQL_HOME%\my.ini" --console
        echo MySQL服务启动中，请稍候...
    )
)
echo MySQL数据库服务已启动（端口:3308 用户名:root 密码:123456）
timeout /t 5 /nobreak > nul

echo [2/5] 启动 MinIO 服务...
cd /d C:\Users\1\Desktop\music-website-master
start "MinIO服务" cmd /c ".\minio.exe server C:\minio --console-address ":9001""
timeout /t 5 /nobreak > nul

echo [3/5] 启动后台服务...
cd /d C:\Users\1\Desktop\music-website-master\music-server
start "后台服务" cmd /c "mvn spring-boot:run"
timeout /t 20 /nobreak > nul

echo [4/5] 启动前端页面...
cd /d C:\Users\1\Desktop\music-website-master\music-client
start "前端页面" cmd /c "npm run serve"
timeout /t 5 /nobreak > nul

echo [5/5] 启动管理后台...
cd /d C:\Users\1\Desktop\music-website-master\music-manage
start "管理后台" cmd /c "npm run serve"

echo ======================================
echo 所有服务启动完成！
echo 前端页面: http://localhost:8080
echo 管理后台: http://localhost:8081
echo MinIO控制台: http://localhost:9001
echo MySQL数据库: 端口3308
echo ======================================

echo 按任意键退出...
pause > nul 