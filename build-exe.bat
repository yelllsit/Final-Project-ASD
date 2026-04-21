@echo off
REM ============================================================================
REM  FiveM Booster - Windows .exe builder
REM ----------------------------------------------------------------------------
REM  Produces a native Windows executable using the JDK's jpackage tool.
REM  Requirements:
REM    * JDK 17 or newer (JDK 21 recommended) - must be on PATH
REM    * WiX Toolset 3.x (optional, only if you want --type msi/exe installer)
REM  Usage:
REM    build-exe.bat            -> produces dist\FiveMBooster\ (app-image)
REM    build-exe.bat installer  -> produces dist\FiveMBooster-1.0.0.exe (EXE installer)
REM ============================================================================

setlocal enabledelayedexpansion
set "ROOT=%~dp0"
cd /d "%ROOT%"

where javac >nul 2>nul || (echo [ERROR] JDK not found on PATH. Install JDK 17+ and re-run. & exit /b 1)
where jar   >nul 2>nul || (echo [ERROR] JDK missing 'jar'. & exit /b 1)
where jpackage >nul 2>nul || (echo [ERROR] 'jpackage' not found. Use JDK 17 or newer. & exit /b 1)

if exist out  rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist
mkdir build 2>nul

echo Manifest-Version: 1.0 > build\manifest.txt
echo Main-Class: exitlag.Main >> build\manifest.txt
echo Implementation-Title: FiveM Booster >> build\manifest.txt
echo Implementation-Version: 1.0.0 >> build\manifest.txt

echo [1/3] Compiling Java sources...
dir /s /b src\*.java > build\sources.txt
javac -d out @build\sources.txt
if errorlevel 1 exit /b 1

echo [2/3] Packaging fat JAR...
jar --create --file dist\FiveMBooster.jar --manifest build\manifest.txt -C out .
if errorlevel 1 exit /b 1

set "MODE=%~1"
if /i "%MODE%"=="installer" (
    echo [3/3] Building Windows EXE installer via jpackage...
    jpackage ^
      --type exe ^
      --name "FiveM Booster" ^
      --app-version 1.0.0 ^
      --vendor "Final Project ASD" ^
      --input dist ^
      --main-jar FiveMBooster.jar ^
      --main-class exitlag.Main ^
      --dest dist ^
      --win-console ^
      --win-shortcut ^
      --win-menu ^
      --win-dir-chooser
) else (
    echo [3/3] Building Windows app-image via jpackage...
    jpackage ^
      --type app-image ^
      --name "FiveM Booster" ^
      --app-version 1.0.0 ^
      --vendor "Final Project ASD" ^
      --input dist ^
      --main-jar FiveMBooster.jar ^
      --main-class exitlag.Main ^
      --dest dist
)
if errorlevel 1 exit /b 1

echo.
echo ============================================================================
echo  BUILD OK
echo ============================================================================
if /i "%MODE%"=="installer" (
    echo  Installer:   dist\FiveM Booster-1.0.0.exe
) else (
    echo  App folder:  dist\FiveM Booster\
    echo  Executable:  dist\FiveM Booster\FiveM Booster.exe
)
echo  Runnable JAR: dist\FiveMBooster.jar
endlocal
