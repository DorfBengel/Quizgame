@echo off
REM Build-Script für die Quiz-Anwendung (Windows)
REM Autor: TvT
REM Version: 1.0

REM Farben für bessere Lesbarkeit (Windows 10+)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Konfiguration
set "PROJECT_NAME=Quizgame"
set "SOURCE_DIR=src"
set "BUILD_DIR=build"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "JAR_DIR=%BUILD_DIR%\jar"
set "LIB_DIR=lib"
set "MAIN_CLASS=gui.MainFrame"
set "JAR_NAME=quizgame.jar"

echo %BLUE%=== %PROJECT_NAME% Build-Script (Windows) ===%NC%
echo.

REM Java-Version prüfen
echo %YELLOW%Prüfe Java-Version...%NC%
java -version >nul 2>&1
if errorlevel 1 (
    echo %RED%Fehler: Java ist nicht installiert oder nicht im PATH!%NC%
    echo Bitte installieren Sie Java 11 oder höher.
    pause
    exit /b 1
)

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JAVA_VERSION=%%g"
    set "JAVA_VERSION=!JAVA_VERSION:~1,1!"
    goto :check_version
)
:check_version

if %JAVA_VERSION% LSS 11 (
    echo %RED%Fehler: Java 11 oder höher wird benötigt. Aktuelle Version: %JAVA_VERSION%%NC%
    pause
    exit /b 1
)

echo %GREEN%Java-Version: %JAVA_VERSION% ✓%NC%
echo.

REM Verzeichnisse erstellen
echo %YELLOW%Erstelle Build-Verzeichnisse...%NC%
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if not exist "%JAR_DIR%" mkdir "%JAR_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

REM Clean-Build (optional)
if "%1"=="--clean" (
    echo %YELLOW%Clean-Build wird durchgeführt...%NC%
    if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
    mkdir "%CLASSES_DIR%"
    mkdir "%JAR_DIR%"
)

REM Prüfe ob lib-Ordner existiert und Libraries enthält
set "CLASSPATH=%SOURCE_DIR%"
set "LIB_COUNT=0"

if exist "%LIB_DIR%\*.jar" (
    echo %YELLOW%Externe Libraries gefunden in %LIB_DIR%:%NC%
    for %%f in ("%LIB_DIR%\*.jar") do (
        echo %BLUE%  %%~nxf%NC%
        set "CLASSPATH=!CLASSPATH!;%LIB_DIR%\%%~nxf"
        set /a "LIB_COUNT+=1"
    )
    echo %BLUE%Classpath: %CLASSPATH%%NC%
) else (
    echo %YELLOW%Keine externen Libraries im %LIB_DIR%-Ordner gefunden.%NC%
)

echo.

REM Java-Dateien kompilieren
echo %YELLOW%Kompiliere Java-Dateien...%NC%
javac -d "%CLASSES_DIR%" -cp "%CLASSPATH%" -sourcepath "%SOURCE_DIR%" -encoding UTF-8 -Xlint:unchecked -Xlint:deprecation "%SOURCE_DIR%\**\*.java"

if errorlevel 1 (
    echo %RED%✗ Kompilierung fehlgeschlagen%NC%
    pause
    exit /b 1
)

echo %GREEN%✓ Kompilierung erfolgreich%NC%

REM Prüfe ob Main-Class kompiliert wurde
set "MAIN_CLASS_FILE=%CLASSES_DIR%\%MAIN_CLASS:.=\%.class"
if not exist "%MAIN_CLASS_FILE%" (
    echo %RED%✗ Fehler: Main-Class %MAIN_CLASS% wurde nicht kompiliert!%NC%
    echo %YELLOW%Verfügbare Klassen:%NC%
    dir /s /b "%CLASSES_DIR%\*.class" | findstr /i "mainframe" | head -10
    pause
    exit /b 1
)

echo %GREEN%✓ Main-Class gefunden: %MAIN_CLASS_FILE%%NC%

REM Manifest-Datei mit Main-Class und Class-Path erstellen
echo %YELLOW%Erstelle Manifest mit Library-Referenzen...%NC%
set "MANIFEST_FILE=%BUILD_DIR%\MANIFEST.MF"
echo Manifest-Version: 1.0 > "%MANIFEST_FILE%"
echo Main-Class: %MAIN_CLASS% >> "%MANIFEST_FILE%"

REM Füge alle Libraries zum Class-Path hinzu
if %LIB_COUNT% GTR 0 (
    echo Class-Path: . >> "%MANIFEST_FILE%"
    for %%f in ("%LIB_DIR%\*.jar") do (
        echo  %%~nxf >> "%MANIFEST_FILE%"
    )
) else (
    echo Class-Path: . >> "%MANIFEST_FILE%"
)

REM Manifest anzeigen
echo %BLUE%Manifest-Inhalt:%NC%
type "%MANIFEST_FILE%"
echo.

REM JAR mit Manifest erstellen
echo %YELLOW%Erstelle JAR-Datei mit Manifest...%NC%
cd /d "%CLASSES_DIR%"
jar cfm "..\jar\%JAR_NAME%" "..\MANIFEST.MF" .

if errorlevel 1 (
    echo %RED%✗ JAR-Erstellung fehlgeschlagen%NC%
    pause
    exit /b 1
)

cd /d "%~dp0"
echo %GREEN%✓ JAR-Datei erfolgreich erstellt: %JAR_NAME%%NC%

REM JAR-Inhalt überprüfen
echo %YELLOW%Überprüfe JAR-Inhalt...%NC%
jar tf "%JAR_DIR%\%JAR_NAME%" | findstr /i "%MAIN_CLASS:.=\%.class" >nul

if errorlevel 1 (
    echo %RED%✗ Main-Class nicht in JAR gefunden!%NC%
    echo %YELLOW%JAR-Inhalt:%NC%
    jar tf "%JAR_DIR%\%JAR_NAME%" | findstr /v "META-INF" | head -20
    pause
    exit /b 1
)

echo %GREEN%✓ Main-Class in JAR gefunden%NC%

REM Libraries in JAR-Ordner kopieren (für einfache Distribution)
echo %YELLOW%Kopiere externe Libraries...%NC%
if %LIB_COUNT% GTR 0 (
    copy "%LIB_DIR%\*.jar" "%JAR_DIR%\" >nul 2>&1
    echo %GREEN%✓ Libraries in %JAR_DIR% kopiert%NC%
)

REM Build-Status anzeigen
echo.
echo %GREEN%=== Build erfolgreich abgeschlossen ===%NC%
echo %BLUE%Build-Verzeichnis: %BUILD_DIR%%NC%
echo %BLUE%JAR-Datei: %JAR_DIR%\%JAR_NAME%%NC%

REM Größe der JAR-Datei anzeigen
for %%A in ("%JAR_DIR%\%JAR_NAME%") do echo %BLUE%Größe: %%~zA Bytes%NC%

if %LIB_COUNT% GTR 0 (
    echo %BLUE%Libraries: %LIB_COUNT% gefunden%NC%
)

echo.

REM Ausführbarkeit der JAR-Datei testen
echo %YELLOW%Teste JAR-Datei...%NC%
echo %BLUE%Versuche JAR direkt zu starten...%NC%

REM Teste JAR mit verschiedenen Methoden
timeout /t 10 >nul
java -jar "%JAR_DIR%\%JAR_NAME%" 2>nul

if errorlevel 1 (
    echo %YELLOW%⚠ JAR-Test konnte nicht abgeschlossen werden (normal bei GUI-Anwendungen)%NC%
) else (
    echo %GREEN%✓ JAR-Datei ist ausführbar%NC%
)

REM Ausführungsoption anbieten
echo.
set /p "antwort=Möchten Sie die Anwendung jetzt ausführen? (J/N): "

if /i "%antwort%"=="J" (
    echo %GREEN%Starte die Anwendung...%NC%
    if exist "windows-run.bat" (
        echo %BLUE%Verwende windows-run.bat...%NC%
        call "windows-run.bat"
    ) else (
        echo %YELLOW%windows-run.bat nicht gefunden, starte direkt...%NC%
        if %LIB_COUNT% GTR 0 (
            echo %BLUE%Starte mit Libraries über Classpath...%NC%
            java -cp "%JAR_DIR%\%JAR_NAME%;%LIB_DIR%\*" %MAIN_CLASS%
        ) else (
            echo %BLUE%Starte JAR direkt...%NC%
            java -jar "%JAR_DIR%\%JAR_NAME%"
        )
    )
) else if /i "%antwort%"=="N" (
    echo %BLUE%Build abgeschlossen. Sie können die Anwendung später starten mit:%NC%
    if %LIB_COUNT% GTR 0 (
        echo %GREEN%java -cp "%JAR_DIR%\%JAR_NAME%;%LIB_DIR%\*" %MAIN_CLASS%%NC%
    ) else (
        echo %GREEN%java -jar %JAR_DIR%\%JAR_NAME%%NC%
    )
    echo.
    echo %BLUE%Oder verwenden Sie das Run-Script:%NC%
    echo %GREEN%windows-run.bat%NC%
) else (
    echo %RED%Ungültige Eingabe. Bitte nur 'J' oder 'N' eingeben.%NC%
)

echo.
echo %GREEN%Build-Script beendet.%NC%
pause
