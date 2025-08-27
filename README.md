# Quiz-Game - Java-Swing-Anwendung

Eine  Java-Swing-Anwendung zur Verwaltung von Quiz-Themen, -Fragen und -Statistiken mit moderner MVC-Architektur, umfassender Funktionalität und erweiterten Konfigurationsmöglichkeiten.

## 🎯 Übersicht

Eine Quiz-Anwendung die es Benutzern ermöglicht:
- **Quiz-Themen** zu erstellen und zu verwalten
- **Fragen mit Antworten** zu verschiedenen Themen zu erstellen
- **Quiz zu spielen** mit Punktezählung und Zeitmessung
- **Detaillierte Statistiken** über Lernfortschritt zu erhalten
- **Mehrere Datenbanktypen** zu unterstützen (SQLite, MariaDB, Lokal/Serialisierung)
- **Responsive UI** mit automatischen Anpassungen
- **Event-System** für lose Kopplung zwischen Komponenten

## 🏗️ Projektarchitektur

### Schichten-Struktur (MVC Pattern)
```
src/
├── gui/                    # Präsentationsschicht (View)
│   ├── common/            # Gemeinsame UI-Komponenten und Utilities
│   ├── controller/        # MVC-Controller für alle Views
│   ├── mvc/               # MVC-Framework und Factory
│   ├── statistik/         # Statistik-UI
│   ├── themen/            # Themen-Verwaltung
│   ├── fragen/            # Fragen-Verwaltung
│   ├── quiz/              # Quiz-UI
│   └── util/              # UI-Utilities
├── business/               # Geschäftslogik-Schicht (Controller)
│   ├── event/             # Event-System für Schichten-Kommunikation
│   └── service/            # Service-Layer für Geschäftsregeln
├── data/                   # Daten-Schicht (Model)
│   ├── dto/               # Data Transfer Objects
│   ├── repository/         # Repository Pattern Interface
│   └── persistence/        # Persistierung (JDBC, Lokal)
├── quiz/                   # Quiz-spezifische Logik
│   ├── data/              # Quiz-Datenmodelle und DAOs
│   └── logic/             # Quiz-Logik und Data Provider
├── config/                 # Erweiterte Konfiguration und Properties
├── exception/              # Benutzerdefinierte Exceptions
└── util/                   # Allgemeine Utilities
```

### Architektur-Prinzipien
- **Separation of Concerns**: Klare Trennung zwischen UI, Logik und Daten
- **Dependency Injection**: Services erhalten ihre Abhängigkeiten injiziert
- **Event-Driven Architecture**: Lose Kopplung durch Event-System
- **Repository Pattern**: Abstrakte Datenzugriffs-Schicht
- **DTO Pattern**: Trennung von UI- und Datenmodellen
- **MVC Pattern**: Modell-View-Controller für saubere Trennung
- **Factory Pattern**: Zentrale Erstellung von Komponenten

## 🚀 Hauptfunktionen

### 1. **Themen-Verwaltung** (`ThemenMainPanel`)
- Neue Quiz-Themen erstellen
- Bestehende Themen bearbeiten
- Themen löschen mit Bestätigung
- Validierung von Titel und Informationen
- **MVC-Controller**: `ThemenController`

### 2. **Fragen-Verwaltung** (`FragenMainPanel`)
- Fragen zu Themen erstellen
- Mehrere Antworten pro Frage (bis zu 4)
- Markierung richtiger Antworten
- Umfassende Validierung (Titel, Text, Antworten)
- **MVC-Controller**: `FragenController`

### 3. **Quiz-Spiel** (`QuizMainPanel`)
- Interaktives Quiz-Erlebnis
- Antworten auswählen mit Checkboxen
- Punktezählung (nur bei korrekten Antworten)
- Zeitmessung für Antworten
- Hilfe-Funktion ("Antwort zeigen")
- **MVC-Controller**: `QuizController`

### 4. **Statistiken** (`StatistikMainPanel`)
- Übersicht über alle Themen und Fragen
- Detaillierte Statistiken pro Thema
- Quiz-Ergebnisse und Erfolgsraten
- Empfehlungen basierend auf Statistiken
- **MVC-Controller**: `StatistikController`

## 🛠️ Technische Features

### Datenbank-Unterstützung
- **SQLite**: Lokale Datei-basierte Datenbank
- **MariaDB/MySQL**: Vollständige Client-Server-Datenbank
- **Lokal**: Serialisierung in Dateien
- **Automatische Schema-Initialisierung**
- **Enum-basierte Konfiguration** für verschiedene Umgebungen

### Event-System
```java
// Beispiel: Event für Datenänderungen
DataChangedEvent event = new DataChangedEvent(
    "FrageService", 
    DataChangedEvent.ChangeType.CREATED, 
    DataChangedEvent.EntityType.FRAGE, 
    frage
);
EventManager.getInstance().fireEvent(event);
```

### MVC-Controller-System
```java
// Controller-Factory für zentrale Verwaltung
ControllerFactory controllerFactory = ControllerFactory.getInstance();
controllerFactory.connectControllersWithViews(
    themenMainPanel, 
    fragenMainPanel, 
    quizMainPanel, 
    statistikMainPanel
);
controllerFactory.startAllControllers();
```

### Validierung
- **Titel**: Maximal 100 Zeichen
- **Frage-Text**: Maximal 500 Zeichen  
- **Antwort-Text**: Maximal 200 Zeichen
- **Mindestens eine richtige Antwort** pro Frage
- **Echtzeit-Validierung** in der UI
- **Zentrale Validierungslogik** in `ValidationHelper`

### Responsive UI
- **Panel-Gewichtungen** passen sich automatisch an
- **Minimale Größen** für alle Komponenten
- **Resize-Optimierung** mit Debouncing
- **Skalierbare Schriftarten** je nach Fenstergröße
- **Automatische Anpassungen** für verschiedene Bildschirmgrößen

### Erweiterte Konfiguration
- **Enum-basierte Datenbankkonfiguration** (Development, Production, Testing, Local)
- **Dynamische Properties-Ladung** mit Fallback-Mechanismen
- **Umgebungsspezifische Einstellungen**
- **Validierung der Konfiguration** vor Anwendungsstart

## 📦 Installation und Ausführung

### Voraussetzungen
- **Java 8 oder höher**
- **JDBC-Treiber**
  - `mariadb-java-client-3.5.5.jar`
  - `sqlite-jdbc-3.36.0.3.jar`

### Kompilierung
```bash
# Alle Java-Dateien kompilieren
find src -name "*.java" -print0 | xargs -0 javac -cp "lib/*:src" -d bin

# .class-Dateien aus src-Verzeichnis entfernen (falls vorhanden)
find src -name "*.class" -delete
```

### Ausführung
```bash
# Anwendung starten
java -cp "bin:lib/*" gui.MainFrame
```

### Konfiguration
Die Anwendung wird über `src/config/application.properties` konfiguriert:

```properties
# Datenbank-Konfiguration: development, production, testing, local
quiz.database.config=development

# Datenbank-Typ: wird automatisch aus der Konfiguration abgeleitet
# quiz.database.type=sqlite

# SQLite Konfiguration
quiz.database.sqlite.file=quiz_datenbank.db

# MariaDB Konfiguration
quiz.database.mariadb.host=localhost
quiz.database.mariadb.port=3306
quiz.database.mariadb.name=quiz_db
quiz.database.mariadb.user=root
quiz.database.mariadb.password=your_password

# UI-Konfiguration
quiz.ui.scale=2.0
quiz.ui.window.width=900
quiz.ui.window.height=600

# Validierung
quiz.validation.thema.titel.maxlength=100
quiz.validation.frage.titel.maxlength=100
quiz.validation.frage.text.maxlength=500
quiz.validation.antwort.text.maxlength=200

# Logging
quiz.logging.level=INFO
quiz.logging.file=quiz.log
```

### Umgebungskonfiguration
```properties
# Development (SQLite)
quiz.database.config=development

# Production (MariaDB)
quiz.database.config=production

# Testing (SQLite)
quiz.database.config=testing

# Local (Standard,Serialisierung)
quiz.database.config=local
```

## 🔧 Datenbank-Setup

### MariaDB/MySQL
```sql
-- Datenbank erstellen
CREATE DATABASE quiz_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Benutzer erstellen (optional)
CREATE USER 'quiz_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON quiz_db.* TO 'quiz_user'@'localhost';
FLUSH PRIVILEGES;
```

### SQLite
- Wird automatisch erstellt: `quiz_datenbank.db`
- Keine zusätzliche Konfiguration erforderlich

### Lokale Speicherung
- Serialisierung in `.ser` Dateien
- Für Entwicklung und Tests geeignet

## 📊 Verwendung der Anwendung

### 1. **Themen erstellen**
- Tab "Quizthemen" öffnen
- Titel und Informationen eingeben
- "Speichern" klicken

### 2. **Fragen hinzufügen**
- Tab "Quizfragen" öffnen
- Thema aus der ComboBox wählen
- Fragetitel und -text eingeben
- Antworten mit Checkboxen markieren
- "Speichern" klicken

### 3. **Quiz spielen**
- Tab "Quiz" öffnen
- Thema auswählen
- Frage aus der Liste wählen
- Antworten auswählen
- "Antwort abgeben" für Punkte oder "Antwort zeigen" für Hilfe

### 4. **Statistiken einsehen**
- Tab "Statistiken" öffnen
- Thema auswählen für spezifische Statistiken
- "Alle Themen" für Gesamtübersicht
- Fragen in der rechten Liste für Details

## 🧪 Entwicklung und Erweiterung

### Neue Service hinzufügen
```java
public class NeuerService {
    private final QuizRepository repository;
    
    public NeuerService(QuizRepository repository) {
        this.repository = repository;
    }
    
    // Service-Methoden implementieren
}
```

### Neue Events hinzufügen
```java
public class NeuerEvent extends QuizEvent {
    // Event-spezifische Eigenschaften
}
```

### Neue UI-Panels hinzufügen
```java
public class NeuesPanel extends AbstractMainPanel {
    public NeuesPanel(QuizDataProvider dataProvider) {
        super(
            new LinkesPanel(),
            new RechtesPanel(),
            new ActionPanel()
        );
    }
}
```

### Neue MVC-Controller hinzufügen
```java
public class NeuerController extends AbstractController {
    public NeuerController(NeuerService service) {
        super();
        // Controller-Logik implementieren
    }
    
    @Override
    public void onEvent(QuizEvent event) {
        // Event-Behandlung
    }
}
```

## 🔍 Debugging und Logging

### Event-Logging
Alle Events werden automatisch geloggt:
```
Event gefeuert: FRAGE_CREATED von FrageService um $TIMESTAMP
```

### Controller-Status
```java
// Controller-Status abfragen
String status = controllerFactory.getControllerStatus();
System.out.println(status);
```

### Fehlerbehandlung
- **ValidationException**: Eingabevalidierung
- **PersistenceException**: Datenbankfehler
- **QuizException**: Allgemeine Quiz-Fehler

## 📈 Performance-Optimierungen

- **Lazy Loading** von Fragen
- **Direkter Service-Zugriff** für bessere Datenkonsistenz (kein Caching)
- **Asynchrone Updates** mit SwingWorker
- **Debounced Resize-Events**
- **Optimierte Datenbankabfragen**
- **Thread-sichere Repository-Implementierungen**
- **PropertyChange-System** für UI-Updates

## 🚧 Bekannte Einschränkungen

- **Keine Maven/Gradle-Integration** (manueller Build)
- **Keine Unit-Tests** 
- **Keine Logging-Framework-Integration**
- **Begrenzte Fehlerbehandlung** in der UI

## 🔮 Zukünftige Verbesserungen

1. **Export-Funktionalität** für Statistiken
2. **Benutzer-Management** und Authentifizierung
3. **Quiz-Templates** für verschiedene Fragetypen
4. **Offline-Modus** mit lokaler Synchronisation
5. **API-Integration** für externe Quiz-Datenbanken
6. **Plugin-System** für erweiterte Funktionalität
7. **Multi-Language-Support**

## 📝 Lizenz

Dieses Projekt ist für Bildungszwecke erstellt und dient als Beispiel für moderne Java-Entwicklung mit Swing und MVC-Architektur.

## 🤝 Beitragen

Bei Fragen oder Verbesserungsvorschlägen können Sie:
- Issues im Repository erstellen
- Code-Reviews durchführen
- Dokumentation verbessern

---

**Entwickelt mit ❤️ in Java und Swing**

**Architektur**: MVC-Pattern mit Event-System und Repository-Pattern
**Features**: Responsive UI, Multi-Database-Support, Statistik-System
**Qualität**: Clean Code, Separation of Concerns, Dependency Injection
