@echo off
set MYSQL_HOME=D:\APP\dev\mysql-8.4
set MYSQL_DATA=D:\APP\dev\mysql-data
set MYSQL_CONFIG=%MYSQL_HOME%\my.ini

if not exist "%MYSQL_HOME%\bin\mysqld.exe" (
  echo MySQL not found: %MYSQL_HOME%
  exit /b 1
)

if not exist "%MYSQL_CONFIG%" (
  echo MySQL config not found: %MYSQL_CONFIG%
  exit /b 1
)

powershell -NoProfile -Command "if (-not (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue)) { Start-Process -FilePath '%MYSQL_HOME%\bin\mysqld.exe' -ArgumentList '--defaults-file=%MYSQL_CONFIG%' -WindowStyle Hidden }"
echo MySQL is starting or already running on port 3306.
