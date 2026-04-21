@echo off
REM Double-click launcher for the fat JAR on Windows. Requires a JRE/JDK on PATH.
setlocal
set "ROOT=%~dp0"
if not exist "%ROOT%dist\FiveMBooster.jar" (
    echo [INFO] JAR not found. Building it now...
    call "%ROOT%build-exe.bat"
)
where javaw >nul 2>nul && (
    start "" javaw -jar "%ROOT%dist\FiveMBooster.jar"
) || (
    java -jar "%ROOT%dist\FiveMBooster.jar"
)
endlocal
