@echo off
title Building Untitled Moorhuhn Game (Standalone EXE)
echo ===========================================
echo   Untitled Moorhuhn Game - Full EXE Builder
echo ===========================================
echo.

setlocal enabledelayedexpansion

REM --- CONFIG ---
set MAIN_CLASS=com.example.untitledmoorhuhngame.HelloApplication
set OUTPUT_NAME=UntitledMoorHuhnGame
set ICON_PATH=src\main\resources\assets\UntitledIcon.ico
set APP_VERSION=1.0
set JAVA_VERSION=21
set BUNDLED_JRE=jre
set JAVA_FX_DIR=%BUNDLED_JRE%\javafx
set EXTRACTED_JDK_FOLDER=jdk-21.0.2
set TARGET_DIR=target

REM Absolute path to JRE for Launch4j
set ABS_JRE_PATH=%CD%\%BUNDLED_JRE%\%EXTRACTED_JDK_FOLDER%
if "%ABS_JRE_PATH:~-1%"=="\" set ABS_JRE_PATH=%ABS_JRE_PATH:~0,-1%

REM --- CHECK CURL ---
where curl >nul 2>nul
if errorlevel 1 (
    echo curl not found! Please install curl or ensure it is in PATH.
    pause
    exit /b
)

REM --- MAVEN CHECK / AUTO-INSTALL ---
where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven not found!
    echo Downloading Apache Maven 3.9.11...
    
    set "MAVEN_VERSION=3.9.11"
    set "MAVEN_DIR=tools\maven"
    set "MAVEN_FILE=apache-maven-%MAVEN_VERSION%-bin.zip"
    set "MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/%MAVEN_FILE%"
    
    if not exist tools mkdir tools
    
    echo Running: curl -L -o "%MAVEN_FILE%" "%MAVEN_URL%"
    curl -L -o "%MAVEN_FILE%" "%MAVEN_URL%"
    if errorlevel 1 (
        echo Failed to download Maven from default mirror. Trying backup...
        set "MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/%MAVEN_FILE%"
        curl -L -o "%MAVEN_FILE%" "%MAVEN_URL%"
        if errorlevel 1 (
            echo Still failed to download Maven.
            pause
            exit /b
        )
    )
    
    echo Extracting Maven...
    powershell -Command "Expand-Archive -LiteralPath '%MAVEN_FILE%' -DestinationPath 'tools' -Force"
    
    del "%MAVEN_FILE%"
    rename "tools\apache-maven-%MAVEN_VERSION%" "maven"
    
    set "MAVEN_HOME=%CD%\tools\maven"
    set "PATH=%MAVEN_HOME%\bin;%PATH%"
    
    echo Maven installed locally in: %MAVEN_HOME%

) else (
    echo Maven found in PATH.
)

REM --- REMOVE OLD BUNDLED JRE ---
if exist "%BUNDLED_JRE%" (
    echo Removing previous bundled JRE folder...
    rmdir /s /q "%BUNDLED_JRE%"
)

REM --- DOWNLOAD JDK + JAVAFX ---
if not exist "%BUNDLED_JRE%" mkdir "%BUNDLED_JRE%"

echo Downloading JDK...
curl -L -o "temurin-jdk.zip" "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"

echo Downloading JavaFX SDK...
curl -L -o "javafx-sdk.zip" "https://download2.gluonhq.com/openjfx/21/openjfx-21_windows-x64_bin-sdk.zip"

echo Extracting JDK...
powershell -Command "Expand-Archive -LiteralPath 'temurin-jdk.zip' -DestinationPath '%BUNDLED_JRE%' -Force"

echo Extracting JavaFX SDK...
powershell -Command "Expand-Archive -LiteralPath 'javafx-sdk.zip' -DestinationPath '%JAVA_FX_DIR%' -Force"

del temurin-jdk.zip
del javafx-sdk.zip
echo JDK + JavaFX ready.

REM --- CLEAN & BUILD JAR ---
echo Cleaning previous build...
call mvn clean

echo Building JAR...
call mvn package -DskipTests

set JAR_FILE=%TARGET_DIR%\UntitledMoorHuhnGame-1.0-SNAPSHOT-jar-with-dependencies.jar
if not exist "%JAR_FILE%" (
    echo JAR build failed!
    pause
    exit /b
)

REM --- Download Launch4j if not exists ---
set LAUNCH4J_DIR=launch4j
set LAUNCH4J_EXE=%LAUNCH4J_DIR%\launch4j\launch4j.exe
set LAUNCH4J_CLI=%LAUNCH4J_DIR%\launch4j\launch4jc.exe

if not exist "%LAUNCH4J_EXE%" (
    echo Launch4j not found. Downloading ZIP...
    if not exist "%LAUNCH4J_DIR%" mkdir "%LAUNCH4J_DIR%"

    curl -L -o "launch4j.zip" "https://sourceforge.net/projects/launch4j/files/launch4j-3/3.50/launch4j-3.50-win32.zip/download"
    powershell -Command "Expand-Archive -LiteralPath 'launch4j.zip' -DestinationPath '%LAUNCH4J_DIR%' -Force"

    if exist "%LAUNCH4J_DIR%\launch4j-3.50" (
        if not exist "%LAUNCH4J_DIR%\launch4j" mkdir "%LAUNCH4J_DIR%\launch4j"
        move "%LAUNCH4J_DIR%\launch4j-3.50\*" "%LAUNCH4J_DIR%\launch4j\" >nul
        rmdir "%LAUNCH4J_DIR%\launch4j-3.50"
    )

    del launch4j.zip
    echo Launch4j ready!
)

REM --- Create Launch4j config ---
set L4J_CONFIG=launch4j_config.xml
(
echo ^<?xml version="1.0" encoding="UTF-8"?^>
echo ^<launch4jConfig^>
echo     ^<dontWrapJar^>false^</dontWrapJar^>
echo     ^<headerType^>gui^</headerType^>
echo     ^<jar^>%JAR_FILE%^</jar^>
echo     ^<outfile^>%TARGET_DIR%\%OUTPUT_NAME%.exe^</outfile^>
echo     ^<errTitle^>Untitled Moorhuhn Game Error^</errTitle^>
echo     ^<icon^>%ICON_PATH%^</icon^>
echo     ^<classPath^>
echo         ^<mainClass^>%MAIN_CLASS%^</mainClass^>
echo     ^</classPath^>
echo     ^<jre^>
echo         ^<path^>%ABS_JRE_PATH%^</path^>
echo         ^<minVersion^>%JAVA_VERSION%^</minVersion^>
echo         ^<opt^>-Xmx512m^</opt^>
echo         ^<opt^>--enable-preview^</opt^>
echo         ^<opt^>--module-path "%CD%\jre\javafx\javafx-sdk-21\lib"^</opt^>
echo         ^<opt^>--add-modules javafx.controls,javafx.fxml,javafx.media^</opt^>
echo     ^</jre^>
echo     ^<stayAlive^>true^</stayAlive^>
echo ^</launch4jConfig^>
) > "%L4J_CONFIG%"

REM --- Build EXE using CLI ---
"%LAUNCH4J_CLI%" "%L4J_CONFIG%"
if errorlevel 1 (
    echo Launch4j failed to create EXE.
    pause
    exit /b
)

REM --- CLEAN UP TEMP BUILD FILES ---
echo Cleaning up temporary files...

if exist "%LAUNCH4J_DIR%" rmdir /s /q "%LAUNCH4J_DIR%"
if exist "%L4J_CONFIG%" del /f "%L4J_CONFIG%"

for /f "delims=" %%F in ('dir "%TARGET_DIR%" /b /a') do (
    if /i not "%%F"=="%OUTPUT_NAME%.exe" (
        rmdir /s /q "%TARGET_DIR%\%%F" 2>nul
        del /f "%TARGET_DIR%\%%F" 2>nul
    )
)

echo.
echo ===========================================
echo Standalone EXE Build Complete!
echo Only EXE and JRE remain, target cleaned!
echo File created in: %TARGET_DIR%\%OUTPUT_NAME%.exe
echo ===========================================
pause

