#!/bin/bash
# Build-Script für die Quiz-Anwendung
# Autor: TvT
# Version: 1.0

# Farben für bessere Lesbarkeit
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Konfiguration
PROJECT_NAME="Quizgame"
SOURCE_DIR="src"
BUILD_DIR="build"
CLASSES_DIR="$BUILD_DIR/classes"
JAR_DIR="$BUILD_DIR/jar"
LIB_DIR="lib"
MAIN_CLASS="gui.MainFrame"
JAR_NAME="quizgame.jar"

# Java-Version prüfen
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "11" ]; then
    echo -e "${RED}Fehler: Java 11 oder höher wird benötigt. Aktuelle Version: $JAVA_VERSION${NC}"
    exit 1
fi

echo -e "${BLUE}=== $PROJECT_NAME Build-Script ===${NC}"
echo -e "${BLUE}Java-Version: $(java -version 2>&1 | head -n 1)${NC}"
echo ""

# Verzeichnisse erstellen
echo -e "${YELLOW}Erstelle Build-Verzeichnisse...${NC}"
mkdir -p "$CLASSES_DIR"
mkdir -p "$JAR_DIR"
mkdir -p "$LIB_DIR"

# Clean-Build (optional)
if [ "$1" = "--clean" ]; then
    echo -e "${YELLOW}Clean-Build wird durchgeführt...${NC}"
    rm -rf "$BUILD_DIR"
    mkdir -p "$CLASSES_DIR"
    mkdir -p "$JAR_DIR"
fi

# Prüfe ob lib-Ordner existiert und Libraries enthält
if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    echo -e "${YELLOW}Externe Libraries gefunden in $LIB_DIR:${NC}"
    ls -la "$LIB_DIR" | grep -E '\.(jar|zip)$' | while read -r line; do
        echo -e "${BLUE}  $line${NC}"
    done
    
    # Erstelle Classpath mit allen Libraries
    CLASSPATH="$SOURCE_DIR"
    for lib in "$LIB_DIR"/*.jar; do
        if [ -f "$lib" ]; then
            CLASSPATH="$CLASSPATH:$lib"
        fi
    done
    
    echo -e "${BLUE}Classpath: $CLASSPATH${NC}"
else
    echo -e "${YELLOW}Keine externen Libraries im $LIB_DIR-Ordner gefunden.${NC}"
    CLASSPATH="$SOURCE_DIR"
fi

# Java-Dateien kompilieren
echo -e "${YELLOW}Kompiliere Java-Dateien...${NC}"
if javac -d "$CLASSES_DIR" \
    -cp "$CLASSPATH" \
    -sourcepath "$SOURCE_DIR" \
    -encoding UTF-8 \
    -Xlint:unchecked \
    -Xlint:deprecation \
    $(find "$SOURCE_DIR" -name "*.java" -type f); then
    
    echo -e "${GREEN}✓ Kompilierung erfolgreich${NC}"
else
    echo -e "${RED}✗ Kompilierung fehlgeschlagen${NC}"
    exit 1
fi

# Prüfe ob Main-Class kompiliert wurde
MAIN_CLASS_FILE="$CLASSES_DIR/$(echo $MAIN_CLASS | sed 's/\./\//g').class"
if [ ! -f "$MAIN_CLASS_FILE" ]; then
    echo -e "${RED}✗ Fehler: Main-Class $MAIN_CLASS wurde nicht kompiliert!${NC}"
    echo -e "${YELLOW}Verfügbare Klassen:${NC}"
    find "$CLASSES_DIR" -name "*.class" | head -10
    exit 1
fi

echo -e "${GREEN}✓ Main-Class gefunden: $MAIN_CLASS_FILE${NC}"

# Manifest-Datei mit Main-Class und Class-Path erstellen
echo -e "${YELLOW}Erstelle Manifest mit Library-Referenzen...${NC}"
MANIFEST_FILE="$BUILD_DIR/MANIFEST.MF"
echo "Manifest-Version: 1.0" > "$MANIFEST_FILE"
echo "Main-Class: $MAIN_CLASS" >> "$MANIFEST_FILE"

# Füge alle Libraries zum Class-Path hinzu
if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    echo "Class-Path: ." >> "$MANIFEST_FILE"
    for lib in "$LIB_DIR"/*.jar; do
        if [ -f "$lib" ]; then
            libname=$(basename "$lib")
            echo " $libname" >> "$MANIFEST_FILE"
        fi
    done
else
    echo "Class-Path: ." >> "$MANIFEST_FILE"
fi

# Manifest anzeigen
echo -e "${BLUE}Manifest-Inhalt:${NC}"
cat "$MANIFEST_FILE"

# JAR mit Manifest erstellen
echo -e "${YELLOW}Erstelle JAR-Datei mit Manifest...${NC}"
cd "$CLASSES_DIR"
if jar cfm "../jar/$JAR_NAME" "../MANIFEST.MF" .; then
    echo -e "${GREEN}✓ JAR-Datei erfolgreich erstellt: $JAR_NAME${NC}"
else
    echo -e "${RED}✗ JAR-Erstellung fehlgeschlagen${NC}"
    exit 1
fi
cd - > /dev/null

# JAR-Inhalt überprüfen
echo -e "${YELLOW}Überprüfe JAR-Inhalt...${NC}"
if jar tf "$JAR_DIR/$JAR_NAME" | grep -q "$(echo $MAIN_CLASS | sed 's/\./\//g').class"; then
    echo -e "${GREEN}✓ Main-Class in JAR gefunden${NC}"
else
    echo -e "${RED}✗ Main-Class nicht in JAR gefunden!${NC}"
    echo -e "${YELLOW}JAR-Inhalt:${NC}"
    jar tf "$JAR_DIR/$JAR_NAME" | head -20
    exit 1
fi

# Libraries in JAR-Ordner kopieren (für einfache Distribution)
echo -e "${YELLOW}Kopiere externe Libraries...${NC}"
if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    cp "$LIB_DIR"/*.jar "$JAR_DIR/" 2>/dev/null || true
    echo -e "${GREEN}✓ Libraries in $JAR_DIR kopiert${NC}"
fi

# Build-Status anzeigen
echo ""
echo -e "${GREEN}=== Build erfolgreich abgeschlossen ===${NC}"
echo -e "${BLUE}Build-Verzeichnis: $BUILD_DIR${NC}"
echo -e "${BLUE}JAR-Datei: $JAR_DIR/$JAR_NAME${NC}"
echo -e "${BLUE}Größe: $(du -h "$JAR_DIR/$JAR_NAME" | cut -f1)${NC}"

if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    echo -e "${BLUE}Libraries: $(ls "$LIB_DIR"/*.jar 2>/dev/null | wc -l) gefunden${NC}"
fi

echo ""

# Ausführbarkeit der JAR-Datei testen
echo -e "${YELLOW}Teste JAR-Datei...${NC}"
echo -e "${BLUE}Versuche JAR direkt zu starten...${NC}"

# Teste JAR mit verschiedenen Methoden
if timeout 10s java -jar "$JAR_DIR/$JAR_NAME" 2>/dev/null; then
    echo -e "${GREEN}✓ JAR-Datei ist ausführbar${NC}"
elif timeout 10s java -cp "$JAR_DIR/$JAR_NAME" "$MAIN_CLASS" 2>/dev/null; then
    echo -e "${GREEN}✓ JAR-Datei ist mit Classpath ausführbar${NC}"
else
    echo -e "${YELLOW}⚠ JAR-Test konnte nicht abgeschlossen werden (normal bei GUI-Anwendungen)${NC}"
fi

# Ausführungsoption anbieten
echo ""
while true; do
    read -p "Möchten Sie die Anwendung jetzt ausführen? (J/N): " antwort
    case $antwort in
        [Jj]|[Jj][Aa])
            echo -e "${GREEN}Starte die Anwendung...${NC}"
            if [ -f "./linux-run.sh" ]; then
                echo -e "${BLUE}Verwende linux-run.sh...${NC}"
                ./linux-run.sh
            else
                echo -e "${YELLOW}linux-run.sh nicht gefunden, starte direkt...${NC}"
                # Starte mit verschiedenen Methoden
                if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
                    echo -e "${BLUE}Starte mit Libraries über Classpath...${NC}"
                    java -cp "$JAR_DIR/$JAR_NAME:$LIB_DIR/*" "$MAIN_CLASS"
                else
                    echo -e "${BLUE}Starte JAR direkt...${NC}"
                    java -jar "$JAR_DIR/$JAR_NAME"
                fi
            fi
            break
            ;;
        [Nn])
            echo -e "${BLUE}Build abgeschlossen. Sie können die Anwendung später starten mit:${NC}"
            if [ -d "$LIB_DIR" ] && [ "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
                echo -e "${GREEN}java -cp \"$JAR_DIR/$JAR_NAME:$LIB_DIR/*\" $MAIN_CLASS${NC}"
            else
                echo -e "${GREEN}java -jar $JAR_DIR/$JAR_NAME${NC}"
            fi
            echo ""
            echo -e "${BLUE}Oder verwenden Sie das Run-Script:${NC}"
            echo -e "${GREEN}./linux-run.sh${NC}"
            break
            ;;
        *)
            echo -e "${RED}Ungültige Eingabe. Bitte nur 'J' oder 'N' eingeben.${NC}"
            ;;
    esac
done

echo -e "${GREEN}Build-Script beendet.${NC}"
