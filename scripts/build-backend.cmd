@echo off
set JAVA_HOME=D:\APP\dev\jdk21\jdk-21.0.12.1+1
set PATH=%JAVA_HOME%\bin;D:\APP\dev\apache-maven-3.8.8\bin;%PATH%
call D:\APP\dev\apache-maven-3.8.8\bin\mvn.cmd -s .mvn\settings.xml -Dmaven.repo.local=D:\APP\dev\maven_repo clean package
