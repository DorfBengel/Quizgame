@echo off
REM Run-Script für die Quiz-Anwendung (Windows)
REM Autor: TvT
REM Version: 1.0

REM Farben für bessere Lesbarkeit (Windows 10+)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Konfiguration
set "JAR_FILE=build\jar\quizgame.jar"
set "LIB_DIR=lib"
set "MAIN_CLASS=gui.MainFrame"
set "BACKUP_JAR=quizgame.jar"

echo %BLUE%=== Quiz-Anwendung wird gestartet ===%NC%

REM Prüfe ob JAR-Datei existiert
if not exist "%JAR_FILE%" (
    echo %YELLOW%JAR-Datei nicht in %JAR_FILE% gefunden.%NC%
    echo %YELLOW%Suche nach alternativen JAR-Dateien...%NC%
    
    REM Suche nach JAR-Dateien im aktuellen Verzeichnis
    if exist "%BACKUP_JAR%" (
        set "JAR_FILE=%BACKUP_JAR%"
        echo %GREEN%JAR-Datei gefunden: %JAR_FILE%%NC%
    ) else (
        echo %RED%Keine JAR-Datei gefunden!%NC%
        echo %YELLOW%Bitte führen Sie zuerst das Build-Script aus:%NC%
        echo %GREEN%windows-build.bat%NC%
        pause
        exit /b 1
    )
)

echo %GREEN%Verwende JAR-Datei: %JAR_FILE%%NC%

REM Prüfe ob FlatLaf-Bibliothek vorhanden ist
set "FLATLAF_MISSING=false"
if not exist "%LIB_DIR%\flatlaf-*.jar" if not exist "%LIB_DIR%\flatlaf.jar" (
    echo %YELLOW%⚠ FlatLaf-Bibliothek nicht gefunden!%NC%
    echo %YELLOW%Das moderne Look ^& Feel wird nicht verfügbar sein.%NC%
    set "FLATLAF_MISSING=true"
)

REM Prüfe ob lib-Ordner existiert
set "LIB_COUNT=0"
if exist "%LIB_DIR%\*.jar" (
    echo %BLUE%Externe Libraries gefunden in %LIB_DIR%%NC%
    echo %BLUE%Verfügbare Libraries:%NC%
    for %%f in ("%LIB_DIR%\*.jar") do (
        echo %BLUE%  %%~nxf%NC%
        set /a "LIB_COUNT+=1"
    )
    
    REM Starte mit Libraries über Classpath
    echo %YELLOW%Starte Anwendung mit externen Libraries...%NC%
    echo %BLUE%Befehl: java -cp "%JAR_FILE%;%LIB_DIR%\*" %MAIN_CLASS%%NC%
    
    java -cp "%JAR_FILE%;%LIB_DIR%\*" %MAIN_CLASS%
    
    if errorlevel 1 (
        echo %RED%✗ Fehler beim Starten der Anwendung%NC%
        echo %YELLOW%Versuche alternative Start-Methode...%NC%
        
        REM Alternative: Starte JAR direkt
        echo %BLUE%Versuche JAR direkt zu starten...%NC%
        java -jar "%JAR_FILE%"
        
        if errorlevel 1 (
            echo %RED%✗ Alle Start-Methoden fehlgeschlagen%NC%
            pause
            exit /b 1
        ) else (
            echo %GREEN%✓ Anwendung erfolgreich beendet%NC%
        )
    ) else (
        echo %GREEN%✓ Anwendung erfolgreich beendet%NC%
    )
) else (
    echo %YELLOW%Keine externen Libraries gefunden.%NC%
    
    REM Starte JAR ohne externe Libraries
    echo %YELLOW%Starte Anwendung ohne externe Libraries...%NC%
    echo %BLUE%Befehl: java -jar %JAR_FILE%%NC%
    
    java -jar "%JAR_FILE%"
    
    if errorlevel 1 (
        echo %RED%✗ Fehler beim Starten der Anwendung%NC%
        echo %YELLOW%Versuche alternative Start-Methode...%NC%
        
        REM Alternative: Starte mit Classpath
        echo %BLUE%Versuche mit Classpath zu starten...%NC%
        java -cp "%JAR_FILE%" %MAIN_CLASS%
        
        if errorlevel 1 (
            echo %RED%✗ Alle Start-Methoden fehlgeschlagen%NC%
            pause
            exit /b 1
        ) else (
            echo %GREEN%✓ Anwendung erfolgreich beendet%NC%
        )
    ) else (
        echo %GREEN%✓ Anwendung erfolgreich beendet%NC%
    )
)

echo %GREEN%Run-Script beendet.%NC%
pause
