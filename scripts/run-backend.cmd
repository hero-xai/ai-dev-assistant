@echo off
set JAVA_HOME=D:\APP\dev\jdk21\jdk-21.0.12.1+1
set PATH=%JAVA_HOME%\bin;%PATH%
if exist "%~dp0local-env.cmd" call "%~dp0local-env.cmd"
if "%SPRING_PROFILES_ACTIVE%"=="" set SPRING_PROFILES_ACTIVE=local
java -jar target\ai-dev-assistant-0.0.1-SNAPSHOT.jar
