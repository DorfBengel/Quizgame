#!/bin/bash
# Run-Script für die Quiz-Anwendung
# Autor: TvT
# Version: 1.0

# Farben für bessere Lesbarkeit
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Konfiguration
JAR_FILE="build/jar/quizgame.jar"
LIB_DIR="lib"
MAIN_CLASS="gui.MainFrame"
BACKUP_JAR="quizgame.jar"

echo -e "${BLUE}=== Quiz-Anwendung wird gestartet ===${NC}"

# Prüfe ob JAR-Datei existiert
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${YELLOW}JAR-Datei nicht in $JAR_FILE gefunden.${NC}"
    echo -e "${YELLOW}Suche nach alternativen JAR-Dateien...${NC}"
    
    # Suche nach JAR-Dateien im aktuellen Verzeichnis
    if [ -f "$BACKUP_JAR" ]; then
        JAR_FILE="$BACKUP_JAR"
        echo -e "${GREEN}JAR-Datei gefunden: $JAR_FILE${NC}"
    else
        echo -e "${RED}Keine JAR-Datei gefunden!${NC}"
        echo -e "${YELLOW}Bitte führen Sie zuerst das Build-Script aus:${NC}"
        echo -e "${GREEN}./linux-build.sh${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}Verwende JAR-Datei: $JAR_FILE${NC}"

# Prüfe ob lib-Ordner existiert
if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    echo -e "${BLUE}Externe Libraries gefunden in $LIB_DIR${NC}"
    echo -e "${BLUE}Verfügbare Libraries:${NC}"
    ls -la "$LIB_DIR"/*.jar 2>/dev/null | while read -r line; do
        echo -e "${BLUE}  $line${NC}"
    done
    
    # Starte mit Libraries über Classpath
    echo -e "${YELLOW}Starte Anwendung mit externen Libraries...${NC}"
    echo -e "${BLUE}Befehl: java -cp \"$JAR_FILE:$LIB_DIR/*\" $MAIN_CLASS${NC}"
    
    if java -cp "$JAR_FILE:$LIB_DIR/*" "$MAIN_CLASS"; then
        echo -e "${GREEN}✓ Anwendung erfolgreich beendet${NC}"
    else
        echo -e "${RED}✗ Fehler beim Starten der Anwendung${NC}"
        echo -e "${YELLOW}Versuche alternative Start-Methode...${NC}"
        
        # Alternative: Starte JAR direkt
        echo -e "${BLUE}Versuche JAR direkt zu starten...${NC}"
        if java -jar "$JAR_FILE"; then
            echo -e "${GREEN}✓ Anwendung erfolgreich beendet${NC}"
        else
            echo -e "${RED}✗ Alle Start-Methoden fehlgeschlagen${NC}"
            exit 1
        fi
    fi
else
    echo -e "${YELLOW}Keine externen Libraries gefunden.${NC}"
    
    # Starte JAR ohne externe Libraries
    echo -e "${YELLOW}Starte Anwendung ohne externe Libraries...${NC}"
    echo -e "${BLUE}Befehl: java -jar $JAR_FILE${NC}"
    
    if java -jar "$JAR_FILE"; then
        echo -e "${GREEN}✓ Anwendung erfolgreich beendet${NC}"
    else
        echo -e "${RED}✗ Fehler beim Starten der Anwendung${NC}"
        echo -e "${YELLOW}Versuche alternative Start-Methode...${NC}"
        
        # Alternative: Starte mit Classpath
        echo -e "${BLUE}Versuche mit Classpath zu starten...${NC}"
        if java -cp "$JAR_FILE" "$MAIN_CLASS"; then
            echo -e "${GREEN}✓ Anwendung erfolgreich beendet${NC}"
        else
            echo -e "${RED}✗ Alle Start-Methoden fehlgeschlagen${NC}"
            exit 1
        fi
    fi
fi

echo -e "${GREEN}Run-Script beendet.${NC}"
