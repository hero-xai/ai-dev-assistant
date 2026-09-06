@echo off
set MYSQL_HOME=D:\APP\dev\mysql-8.4
set MYSQL_BIN=%MYSQL_HOME%\bin\mysql.exe

if exist "%~dp0local-env.cmd" call "%~dp0local-env.cmd"
if "%MYSQL_USERNAME%"=="" set MYSQL_USERNAME=ai_dev_user
if "%MYSQL_PASSWORD%"=="" (
  echo MYSQL_PASSWORD is required. Please configure scripts\local-env.cmd first.
  exit /b 1
)

if not exist "%MYSQL_BIN%" (
  echo MySQL client not found: %MYSQL_BIN%
  exit /b 1
)

"%MYSQL_BIN%" --user=%MYSQL_USERNAME% --password=%MYSQL_PASSWORD% --protocol=tcp --host=127.0.0.1 --port=3306 --default-character-set=utf8mb4 ai_dev_assistant
