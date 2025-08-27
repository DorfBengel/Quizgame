# Quiz-Anwendung - Architektur-Dokumentation

## 📋 Inhaltsverzeichnis

1. [Übersicht](#übersicht)
2. [Architektur-Prinzipien](#architektur-prinzipien)
3. [Schichten-Architektur](#schichten-architektur)
4. [MVC-Controller-System](#mvc-controller-system)
5. [Klassen-Diagramme](#klassen-diagramme)
6. [Datenfluss](#datenfluss)
7. [Event-System](#event-system)
8. [Repository-Pattern](#repository-pattern)
9. [Service-Layer](#service-layer)
10. [UI-Architektur](#ui-architektur)
11. [Konfiguration](#konfiguration)
12. [Exception-Handling](#exception-handling)
13. [Performance-Optimierungen](#performance-optimierungen)

## 🎯 Übersicht

Die Quiz-Anwendung folgt dem **MVC (Model-View-Controller) Pattern** mit einer **3-Schichten-Architektur** und erweiterten Controller-Funktionalitäten:

```
┌─────────────────────────────────────────────────────────────┐
│                    Präsentationsschicht (GUI)               │
│                     ───────────────                         │
│  MainFrame → QuizTabPane → AbstractMainPanel → Panels       │
│                    ↓                                        │
│              MVC-Controller                                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Geschäftslogik-Schicht                    │
│                     ───────────────                         │
│  QuizApplication → Services → EventManager → EventSystem    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Daten-Schicht                          │
│                     ───────────────                         │
│  RepositoryFactory → QuizRepository → Persistence Layer     │
└─────────────────────────────────────────────────────────────┘
```

## 🏗️ Architektur-Prinzipien

### 1. **Separation of Concerns**
- **GUI**: Nur Präsentation und Benutzerinteraktion
- **Controller**: MVC-Controller für View-Logic
- **Business Logic**: Geschäftsregeln und Validierung
- **Data Access**: Datenzugriff und Persistierung

### 2. **Dependency Injection**
- Services erhalten ihre Abhängigkeiten injiziert
- Controller erhalten Services injiziert
- Lose Kopplung zwischen Komponenten
- Einfache Testbarkeit

### 3. **Event-Driven Architecture**
- Lose Kopplung durch Events
- Asynchrone Kommunikation
- Erweiterbare Architektur
- PropertyChange-System für UI-Updates

### 4. **Repository Pattern**
- Abstrakte Datenzugriffs-Schicht
- Einfacher Wechsel zwischen Datenquellen
- Einheitliche API für alle Datenoperationen
- Thread-sichere Implementierungen

### 5. **MVC Pattern**
- **Model**: Daten und Geschäftslogik
- **View**: UI-Komponenten
- **Controller**: Verbindung zwischen Model und View

## 🏛️ Schichten-Architektur

### **Präsentationsschicht (GUI)**

#### Hauptkomponenten:
- **`MainFrame`**: Hauptfenster der Anwendung
- **`QuizTabPane`**: Tab-Container für verschiedene Ansichten
- **`AbstractMainPanel`**: Basis für alle Hauptpanels
- **Spezifische Panels**: Für Themen, Fragen, Quiz, Statistiken
- **MVC-Controller**: Verbindung zwischen UI und Business-Logik

#### Funktionsweise:
```java
// Beispiel: Erstellung eines Quiz-Panels mit Controller
public class QuizMainPanel extends AbstractMainPanel {
    private final QuizController controller;
    
    public QuizMainPanel(QuizDataProvider dataProvider) {
        super(
            new QuizspielPanelLinks(),      // Linkes Panel
            new QuizfragenRechtesPanel(),   // Rechtes Panel
            new QuizActionPanel()           // Action-Panel
        );
        
        // Controller erstellen und verbinden
        this.controller = new QuizController(quizStatistikService);
        this.controller.setViews(this, actionPanel, linkesPanel, rechtesPanel);
    }
}
```

### **MVC-Controller-Schicht**

#### Hauptkomponenten:
- **`ControllerFactory`**: Zentrale Factory für alle Controller
- **`AbstractController`**: Basis-Klasse für alle Controller
- **`ThemenController`**: Controller für Themen-Verwaltung
- **`FragenController`**: Controller für Fragen-Verwaltung
- **`QuizController`**: Controller für Quiz-Funktionalität
- **`StatistikController`**: Controller für Statistiken

#### Funktionsweise:
```java
// Controller-Factory für zentrale Verwaltung
public class ControllerFactory {
    private static ControllerFactory instance;
    private final ThemenController themenController;
    private final FragenController fragenController;
    private final QuizController quizController;
    private final StatistikController statistikController;
    
    public void connectControllersWithViews(
            ThemenMainPanel themenMainPanel,
            FragenMainPanel fragenMainPanel,
            QuizMainPanel quizMainPanel,
            StatistikMainPanel statistikMainPanel) {
        
        // Controller mit Views verbinden
        themenController.setViews(themenMainPanel, ...);
        fragenController.setViews(fragenMainPanel, ...);
        quizController.setViews(quizMainPanel, ...);
        statistikController.setViews(statistikMainPanel, ...);
    }
}
```

### **Geschäftslogik-Schicht**

#### Hauptkomponenten:
- **`QuizApplication`**: Zentrale Anwendungsklasse (Singleton)
- **`ThemaService`**: Geschäftslogik für Themen
- **`FrageService`**: Geschäftslogik für Fragen
- **`QuizStatistikService`**: Geschäftslogik für Statistiken
- **`EventManager`**: Zentrale Event-Verwaltung

#### Funktionsweise:
```java
// Beispiel: Service mit Event-Feuerung
public class FrageService {
    public FrageDTO createFrage(String titel, String text, List<AntwortDTO> antworten, long themaId) {
        // Validierung
        validateFrageData(titel, text, antworten);
        
        // Speichern
        FrageDTO frage = repository.saveFrage(frage, themaId);
        
        // Event feuern
        DataChangedEvent event = new DataChangedEvent("FrageService", 
            DataChangedEvent.ChangeType.CREATED, 
            DataChangedEvent.EntityType.FRAGE, 
            frage);
        EventManager.getInstance().fireEvent(event);
        
        return frage;
    }
}
```

### **Daten-Schicht**

#### Hauptkomponenten:
- **`RepositoryFactory`**: Factory für Repository-Erstellung
- **`QuizRepository`**: Interface für Datenzugriff
- **`JDBCRepository`**: JDBC-Implementierung
- **`LokalRepository`**: Lokale Datei-Implementierung

#### Funktionsweise:
```java
// Beispiel: Repository-Factory mit erweiterter Konfiguration
public class RepositoryFactory {
    public static QuizRepository createRepository() {
        try {
            DatabaseType dbType = PropertiesLoader.getDatabaseType();
            
            switch (dbType) {
                case LOKAL:
                    return new LokalRepository();
                case SQLITE:
                case MARIADB:
                    Connection connection = DatabaseFactory.getInstance().createConnection(dbType);
                    return new JDBCRepository(connection);
                default:
                    throw new PersistenceException("Nicht unterstützter Datenbanktyp: " + dbType);
            }
        } catch (Exception e) {
            throw new PersistenceException("Fehler beim Erstellen des Repositories", e);
        }
    }
}
```

## 🎮 MVC-Controller-System

### **Controller-Hierarchie**

```
AbstractController (Basis)
    │
    ├── ThemenController
    │   ├── ThemaService (injiziert)
    │   ├── EventManager (injiziert)
    │   └── View-Referenzen
    │
    ├── FragenController
    │   ├── FrageService (injiziert)
    │   ├── EventManager (injiziert)
    │   └── View-Referenzen
    │
    ├── QuizController
    │   ├── QuizStatistikService (injiziert)
    │   ├── EventManager (injiziert)
    │   └── View-Referenzen
    │
    └── StatistikController
        ├── QuizStatistikService (injiziert)
        ├── EventManager (injiziert)
        └── View-Referenzen
```

### **Controller-Factory**

```java
public class ControllerFactory {
    private static ControllerFactory instance;
    private final QuizApplication quizApplication;
    
    private ControllerFactory() {
        this.quizApplication = QuizApplication.getInstance();
        initializeControllers();
    }
    
    private void initializeControllers() {
        this.themenController = new ThemenController(quizApplication.getThemaService());
        this.fragenController = new FragenController(quizApplication.getFrageService());
        this.quizController = new QuizController(quizApplication.getQuizStatistikService());
        this.statistikController = new StatistikController(quizApplication.getQuizStatistikService());
        
        // Inter-Controller-Kommunikation einrichten
        setupInterControllerCommunication();
    }
    
    public void startAllControllers() {
        // Alle Controller starten
        themenController.registerForEvents("THEMA_*");
        fragenController.registerForEvents("FRAGE_*");
        quizController.registerForEvents("QUIZ_*");
        statistikController.registerForEvents("STATISTIK_*");
    }
}
```

### **Controller-View-Verbindung**

```java
// Beispiel: ThemenController mit Views verbinden
public class ThemenController extends AbstractController {
    private ThemenMainPanel mainPanel;
    private QuizthemenActionPanel actionPanel;
    private QuizthemenLinkesPanel linkesPanel;
    private QuizthemenRechtesPanel rechtesPanel;
    
    public void setViews(ThemenMainPanel mainPanel, QuizthemenActionPanel actionPanel,
                        QuizthemenLinkesPanel linkesPanel, QuizthemenRechtesPanel rechtesPanel) {
        this.mainPanel = mainPanel;
        this.actionPanel = actionPanel;
        this.linkesPanel = linkesPanel;
        this.rechtesPanel = rechtesPanel;
        
        // Action-Listeners einrichten
        setupActionListeners();
        
        // Parent-Component für Dialoge setzen
        setParentComponent(mainPanel);
    }
    
    private void setupActionListeners() {
        actionPanel.getButton1().addActionListener(e -> neuesThema());
        actionPanel.getButton2().addActionListener(e -> speichereThema());
        actionPanel.getButton3().addActionListener(e -> loescheThema());
    }
}
```

## 🗂️ Klassen-Diagramme

### **Hauptarchitektur mit MVC**

```
MainFrame
    │
    ├── QuizTabPane
    │   ├── ThemenMainPanel
    │   │   ├── QuizthemenLinkesPanel
    │   │   ├── QuizthemenRechtesPanel
    │   │   └── QuizthemenActionPanel
    │   │
    │   ├── FragenMainPanel
    │   │   ├── QuizfragenLinkesPanel
    │   │   ├── QuizfragenRechtesPanel
    │   │   └── QuizfragenActionPanel
    │   │
    │   ├── QuizMainPanel
    │   │   ├── QuizspielPanelLinks
    │   │   ├── QuizfragenRechtesPanel
    │   │   └── QuizActionPanel
    │   │
    │   └── StatistikMainPanel
    │       ├── StatistikLinkesPanel
    │       ├── StatistikRechtesPanel
    │       └── StatistikActionPanel
    │
    └── QuizApplication (Singleton)
        ├── ControllerFactory
        │   ├── ThemenController
        │   ├── FragenController
        │   ├── QuizController
        │   └── StatistikController
        │
        ├── ThemaService
        ├── FrageService
        ├── QuizStatistikService
        └── EventManager
```

### **Controller-System**

```
ControllerFactory (Singleton)
    │
    ├── ThemenController
    │   ├── ThemaService
    │   ├── EventManager
    │   └── Views (MainPanel, ActionPanel, LinkesPanel, RechtesPanel)
    │
    ├── FragenController
    │   ├── FrageService
    │   ├── EventManager
    │   └── Views (MainPanel, ActionPanel, LinkesPanel, RechtesPanel)
    │
    ├── QuizController
    │   ├── QuizStatistikService
    │   ├── EventManager
    │   └── Views (MainPanel, ActionPanel, LinkesPanel, RechtesPanel)
    │
    └── StatistikController
        ├── QuizStatistikService
        ├── EventManager
        └── Views (MainPanel, ActionPanel, LinkesPanel, RechtesPanel)
```

### **Service-Layer**

```
QuizApplication
    │
    ├── ThemaService
    │   ├── QuizRepository (injiziert)
    │   └── EventManager (injiziert)
    │
    ├── FrageService
    │   ├── QuizRepository (injiziert)
    │   └── EventManager (injiziert)
    │
    └── QuizStatistikService
        ├── QuizRepository (injiziert)
        └── EventManager (injiziert)
```

### **Repository-Pattern**

```
QuizRepository (Interface)
    │
    ├── JDBCRepository
    │   ├── Connection (injiziert)
    │   ├── SQL-Implementierungen
    │   └── Schema-Initialisierung
    │
    └── LokalRepository
        ├── Datei-Operationen
        ├── Serialisierung
        └── Thread-Sicherheit
```

### **Event-System**

```
QuizEvent (abstract)
    │
    └── DataChangedEvent
        ├── ChangeType (CREATED, UPDATED, DELETED)
        ├── EntityType (THEMA, FRAGE, QUIZ_ERGEBNIS)
        └── entity (Object)

EventManager (Singleton)
    ├── listeners (Map<String, List<EventListener>>)
    ├── fireEvent(QuizEvent)
    └── fireEventAsync(QuizEvent)
```

## 🔄 Datenfluss

### **1. Themen erstellen (mit MVC)**

```
GUI (QuizthemenActionPanel)
    │
    ├── Button-Click → ThemenController.neuesThema()
    │
    ├── ThemenController
    │   ├── Validierung
    │   ├── ThemaService.createThema()
    │   │   ├── Validierung
    │   │   ├── Repository.saveThema()
    │   │   └── Event feuern
    │   └── UI aktualisieren
    │
    └── Event-Listener reagiert
        ├── ControllerFactory benachrichtigt
        └── Alle verbundenen Views aktualisieren
```

### **2. Fragen erstellen (mit MVC)**

```
GUI (QuizfragenActionPanel)
    │
    ├── Button-Click → FragenController.speichereFrage()
    │
    ├── FragenController
    │   ├── Validierung
    │   ├── FrageService.createFrage()
    │   │   ├── Validierung
    │   │   ├── Repository.saveFrage()
    │   │   ├── Antworten speichern
    │   │   └── Event feuern
    │   └── UI aktualisieren
    │
    └── Event-Listener reagiert
        ├── ControllerFactory benachrichtigt
        └── Fragen-Liste aktualisieren
```

### **3. Quiz spielen (mit MVC)**

```
GUI (QuizActionPanel)
    │
    ├── Button-Click → QuizController.gebeAntwortAb()
    │
    ├── QuizController
    │   ├── Antwort validieren
    │   ├── QuizStatistikService.saveQuizErgebnis()
    │   │   ├── Antwort validieren
    │   │   ├── Punkte berechnen
    │   │   ├── Zeit messen
    │   │   ├── Repository.saveQuizErgebnis()
    │   │   └── Event feuern
    │   └── Ergebnis anzeigen
    │
    └── Event-Listener reagiert
        ├── ControllerFactory benachrichtigt
        └── Punkte und Fortschritt aktualisieren
```

## 📡 Event-System

### **Event-Typen**

```java
public enum ChangeType {
    CREATED,    // Neue Entität erstellt
    UPDATED,    // Bestehende Entität aktualisiert
    DELETED     // Entität gelöscht
}

public enum EntityType {
    THEMA,          // Thema geändert
    FRAGE,          // Frage geändert
    QUIZ_ERGEBNIS   // Quiz-Ergebnis gespeichert
}
```

### **Event-Listener-Registrierung**

```java
// Globaler Listener für alle Events
eventManager.addEventListener("*", new EventListener() {
    @Override
    public void onEvent(QuizEvent event) {
        System.out.println("Event gefeuert: " + event.getEventType() + 
                         " von " + event.getSource() + 
                         " um " + event.getTimestamp());
    }
});

// Spezifischer Listener für Themen-Events
eventManager.addEventListener("THEMA_*", new EventListener() {
    @Override
    public void onEvent(QuizEvent event) {
        System.out.println("Thema-Event empfangen: " + event.getEventType());
    }
});

// Controller-spezifische Event-Registrierung
themenController.registerForEvents("THEMA_*");
fragenController.registerForEvents("FRAGE_*");
quizController.registerForEvents("QUIZ_*");
statistikController.registerForEvents("STATISTIK_*");
```

### **Event-Feuerung**

```java
// Event in Service feuern
DataChangedEvent event = new DataChangedEvent(
    "ThemaService", 
    DataChangedEvent.ChangeType.CREATED, 
    DataChangedEvent.EntityType.THEMA, 
    thema
);
EventManager.getInstance().fireEvent(event);

// Event asynchron feuern
EventManager.getInstance().fireEventAsync(event);
```

## 🗄️ Repository-Pattern

### **Interface-Design**

```java
public interface QuizRepository {
    // Themen-Operationen
    List<ThemaDTO> findAllThemen();
    Optional<ThemaDTO> findThemaById(long id);
    Optional<ThemaDTO> findThemaByTitel(String titel);
    ThemaDTO saveThema(ThemaDTO thema);
    void deleteThema(long id);
    boolean existsThemaWithTitel(String titel);
    
    // Fragen-Operationen
    List<FrageDTO> findFragenByThemaId(long themaId);
    List<FrageDTO> findFragenByThemaName(String themaName);
    Optional<FrageDTO> findFrageById(long id);
    Optional<FrageDTO> findFrageByTitel(String titel, long themaId);
    FrageDTO saveFrage(FrageDTO frage, long themaId);
    void deleteFrage(long id);
    boolean existsFrageWithTitel(String titel, long themaId);
    
    // Antworten-Operationen
    List<AntwortDTO> findAntwortenByFrageId(long frageId);
    AntwortDTO saveAntwort(AntwortDTO antwort, long frageId);
    void deleteAntwort(long id);
    
    // Quiz-Statistik-Operationen
    void saveQuizErgebnis(QuizErgebnisDTO ergebnis);
    List<QuizErgebnisDTO> findQuizErgebnisseByThemaId(long themaId);
    List<QuizErgebnisDTO> findQuizErgebnisseByFrageId(long frageId);
    List<StatistikDTO> findStatistikenByThemaId(long themaId);
    List<StatistikDTO> findAlleStatistiken();
}
```

### **Implementierungen**

#### **JDBCRepository**
- Verwendet SQL-Datenbanken (SQLite, MariaDB)
- Prepared Statements für Sicherheit
- Transaktionale Operationen
- Automatische Schema-Initialisierung
- Datenbanktyp-spezifische SQL-Syntax

#### **LokalRepository**
- Serialisierung in Dateien
- In-Memory-Operationen
- Einfache Persistierung
- Thread-sichere Implementierung mit ReadWriteLock
- Für Entwicklung und Tests

## 🔧 Service-Layer

### **Service-Charakteristika**

1. **Stateless**: Keine internen Zustände
2. **Transaction-Boundary**: Jede Methode ist eine Transaktion
3. **Validation**: Zentrale Validierungslogik
4. **Event-Feuerung**: Automatische Event-Benachrichtigung
5. **Dependency Injection**: Repository wird injiziert

### **Service-Implementierung**

```java
public class ThemaService {
    private final QuizRepository repository;
    
    public ThemaService(QuizRepository repository) {
        this.repository = repository; // Dependency Injection
    }
    
    public ThemaDTO createThema(String titel, String information) {
        // 1. Validierung
        validateThemaData(titel, information);
        
        // 2. Duplikat-Prüfung
        if (repository.existsThemaWithTitel(titel)) {
            throw new ValidationException("Ein Thema mit dem Titel '" + titel + "' existiert bereits.");
        }
        
        // 3. Erstellen und speichern
        ThemaDTO thema = new ThemaDTO(0, titel, information, 0);
        thema = repository.saveThema(thema);
        
        // 4. Event feuern
        DataChangedEvent event = new DataChangedEvent("ThemaService", 
            DataChangedEvent.ChangeType.CREATED, 
            DataChangedEvent.EntityType.THEMA, 
            thema);
        EventManager.getInstance().fireEvent(event);
        
        return thema;
    }
}
```

## 🖥️ UI-Architektur

### **Panel-Hierarchie**

```
AbstractMainPanel (Basis)
    ├── panelLinks (Component)
    ├── panelRechts (Component)
    └── panelUnten (Component)

Spezifische Implementierungen:
├── ThemenMainPanel
├── FragenMainPanel
├── QuizMainPanel
└── StatistikMainPanel
```

### **Action-Panel-Pattern**

```java
public abstract class AbstractActionPanel extends JPanel {
    protected JLabel messageLabel;    // Status-Meldungen
    protected JButton button1;        // Primäre Aktion
    protected JButton button2;        // Sekundäre Aktion
    protected JButton button3;        // Tertiäre Aktion
    
    protected abstract void configureButtons(); // Template Method
}
```

### **Responsive Design**

```java
// Panel-Gewichtungen automatisch anpassen
public void adjustPanelWeightsForWindowSize(int windowWidth) {
    if (windowWidth < 800) {
        setPanelWeights(0.5, 0.5);        // Kleine Fenster: 50/50
    } else if (windowWidth < 1200) {
        setPanelWeights(0.7, 0.3);        // Mittlere Fenster: 70/30
    } else {
        setPanelWeights(0.8, 0.2);        // Große Fenster: 80/20
    }
}

// Resize-Optimierung mit Debouncing
private void setupResizeListener() {
    addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int currentWidth = getWidth();
            
            if (currentWidth == lastProcessedWidth) {
                return;
            }
            
            if (resizeTask != null && !resizeTask.isDone()) {
                resizeTask.cancel(false);
            }
            
            resizeTask = resizeScheduler.schedule(() -> {
                if (getWidth() == currentWidth) {
                    lastProcessedWidth = currentWidth;
                    SwingUtilities.invokeLater(() -> {
                        adjustForWindowSize(currentWidth);
                    });
                }
            }, RESIZE_DEBOUNCE_DELAY, TimeUnit.MILLISECONDS);
        }
    });
}
```

### **Gemeinsame UI-Komponenten**

```java
// AbstractMainPanelWithWorker für asynchrone Operationen
public abstract class AbstractMainPanelWithWorker {
    protected final AtomicBoolean updateInProgress = new AtomicBoolean(false);
    
    public <T> void loadDataAsync(
            DataLoader<T> dataLoader,
            DataSuccessCallback<T> onSuccess,
            DataErrorCallback onError,
            String operationName) {
        
        if (updateInProgress.get()) {
            return;
        }
        
        updateInProgress.set(true);
        
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return dataLoader.loadData();
            }
            
            @Override
            protected void done() {
                try {
                    T data = get();
                    onSuccess.onSuccess(data);
                } catch (Exception e) {
                    onError.onError(e);
                } finally {
                    updateInProgress.set(false);
                }
            }
        }.execute();
    }
}
```

## ⚙️ Konfiguration

### **Enum-basierte Konfiguration**

```java
public enum DatabaseConfig {
    DEVELOPMENT("development", DatabaseType.SQLITE, "Entwicklungsumgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.sqlite.file": return "quiz_dev.db";
                case "quiz.database.type": return "sqlite";
                case "quiz.ui.scale": return "1.0";
                case "quiz.logging.level": return "DEBUG";
                default: return null;
            }
        }
    },
    
    PRODUCTION("production", DatabaseType.MARIADB, "Produktionsumgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.type": return "mariadb";
                case "quiz.database.mariadb.host": return "prod-db.example.com";
                case "quiz.database.mariadb.port": return "3306";
                case "quiz.database.mariadb.name": return "quiz_prod";
                case "quiz.database.mariadb.user": return "quiz_user";
                case "quiz.database.mariadb.password": return "secure_password";
                case "quiz.ui.scale": return "1.0";
                case "quiz.logging.level": return "WARN";
                default: return null;
            }
        }
    };
    
    // ... weitere Konfigurationen
}
```

### **DatabaseFactory**

```java
public class DatabaseFactory {
    private static DatabaseFactory instance;
    private final PropertiesLoader propertiesLoader;
    
    public Connection createConnection() throws SQLException {
        try {
            DatabaseType dbType = getCurrentDatabaseType();
            return createConnection(dbType);
        } catch (Exception e) {
            throw new SQLException("Fehler beim Erstellen der Datenbankverbindung", e);
        }
    }
    
    public Connection createConnectionForEnvironment(String environment) throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromConfigKey(environment);
        DatabaseType dbType = config.getDefaultDatabaseType();
        
        // Temporäre Properties für diese Umgebung setzen
        Properties tempProps = new Properties();
        for (String key : dbType.getRequiredProperties()) {
            String value = config.getProperty(key);
            if (value != null) {
                tempProps.setProperty(key, value);
            }
        }
        
        return createConnectionWithProperties(dbType, tempProps);
    }
}
```

### **Properties-Struktur**

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
quiz.database.mariadb.password=password

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

### **PropertiesLoader-Klasse**

```java
public class PropertiesLoader {
    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties;
    
    static {
        loadProperties(); // Automatisches Laden beim Klassenstart
    }
    
    private static void loadProperties() {
        properties = new Properties();
        
        // Versuche zuerst, die Datei aus dem Projektstammverzeichnis zu laden
        File projectRoot = new File(".").getAbsoluteFile().getParentFile();
        File configFile = new File(projectRoot, PROPERTIES_FILE);
        
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                System.out.println("Properties geladen aus: " + configFile.getAbsolutePath());
                return;
            } catch (IOException e) {
                System.err.println("Fehler beim Laden der Properties aus " + configFile.getAbsolutePath() + ": " + e.getMessage());
            }
        }
        
        // Fallback: Versuche über ClassLoader
        try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
                System.out.println("Properties geladen über ClassLoader");
                return;
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Properties über ClassLoader: " + e.getMessage());
        }
        
        // Wenn keine Datei gefunden wurde, verwende Standardwerte
        System.err.println("Konnte " + PROPERTIES_FILE + " nicht finden. Verwende Standardwerte.");
        setDefaultProperties();
    }
    
    public static DatabaseType getDatabaseType() {
        String dbType = getProperty("quiz.database.type");
        if (dbType != null) {
            return DatabaseType.fromConfigKey(dbType);
        }
        
        DatabaseConfig config = getDatabaseConfig();
        return config.getDefaultDatabaseType();
    }
    
    public static DatabaseConfig getDatabaseConfig() {
        String configKey = getProperty("quiz.database.config", "development");
        return DatabaseConfig.fromConfigKey(configKey);
    }
}
```

## 🚨 Exception-Handling

### **Exception-Hierarchie**

```
QuizException (RuntimeException)
    ├── ValidationException
    │   ├── Eingabevalidierung
    │   └── Geschäftsregel-Verletzungen
    │
    └── PersistenceException
        ├── Datenbankfehler
        └── Persistierungsprobleme
```

### **Exception-Behandlung in Controllern**

```java
// In Controllern
public class ThemenController extends AbstractController {
    public void erstelleThema(String titel, String information) {
        try {
            validateThemaData(titel, information);
            ThemaDTO thema = themaService.createThema(titel, information);
            showInfo("Thema erfolgreich erstellt: " + thema.getTitel());
            refreshViews();
        } catch (ValidationException e) {
            showError("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Erstellen des Themas", e);
        }
    }
    
    @Override
    protected void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        showError(message + ": " + e.getMessage());
    }
}
```

### **Exception-Behandlung in Services**

```java
// In Services
public ThemaDTO createThema(String titel, String information) {
    try {
        validateThemaData(titel, information);
        
        if (repository.existsThemaWithTitel(titel)) {
            throw new ValidationException("Ein Thema mit dem Titel '" + titel + "' existiert bereits.");
        }
        
        ThemaDTO thema = new ThemaDTO(0, titel, information, 0);
        thema = repository.saveThema(thema);
        
        // Event feuern
        DataChangedEvent event = new DataChangedEvent("ThemaService", 
            DataChangedEvent.ChangeType.CREATED, 
            DataChangedEvent.EntityType.THEMA, 
            thema);
        EventManager.getInstance().fireEvent(event);
        
        return thema;
    } catch (ValidationException e) {
        throw e; // Re-throw für Controller
    } catch (Exception e) {
        throw new QuizException("Unerwarteter Fehler beim Erstellen des Themas", e);
    }
}
```

## 🔄 Datenmodell-Beziehungen

### **DTO-Struktur**

```java
// ThemaDTO
public class ThemaDTO {
    private long id;
    private String titel;
    private String information;
    private int anzahlFragen;  // Abgeleitet, nicht persistiert
}

// FrageDTO
public class FrageDTO {
    private long id;
    private String titel;
    private String text;
    private String themaName;
    private List<AntwortDTO> antworten;  // Komposition
}

// AntwortDTO
public class AntwortDTO {
    private long id;
    private String text;
    private boolean istRichtig;
}

// QuizErgebnisDTO
public class QuizErgebnisDTO {
    private long id;
    private long themaId;
    private long frageId;
    private boolean antwortRichtig;
    private boolean antwortVorherGezeigt;
    private int antwortZeitSekunden;
    private LocalDateTime zeitpunkt;
    private int punkte;
}

// StatistikDTO
public class StatistikDTO {
    private long themaId;
    private String themaTitel;
    private long frageId;
    private String frageTitel;
    private int anzahlVersuche;
    private int anzahlRichtig;
    private int anzahlFalsch;
    private double durchschnittlicheAntwortZeit;
    private double erfolgsRate;
    private int durchschnittlichePunkte;
    private int bestePunkte;
}
```

### **Mapping zwischen Schichten**

```java
// DTO zu Domain-Objekt
private Thema mapToThema(ThemaDTO dto) {
    Thema thema = new Thema(dto.getTitel(), dto.getInformation());
    thema.setId(dto.getId());
    return thema;
}

// Domain-Objekt zu DTO
private ThemaDTO mapToThemaDTO(Thema thema) {
    return new ThemaDTO(
        thema.getId(), 
        thema.getTitel(), 
        thema.getInformation(), 
        thema.getFragen().size()
    );
}
```

## 📊 Performance-Optimierungen

### **Direkter Service-Zugriff (kein Caching)**

```java
// ServiceBackedDataProvider ohne Caching für bessere Konsistenz
public List<Thema> getAlleThemen() {
    System.out.println("ServiceBackedDataProvider: Lade alle Themen von Service...");
    List<ThemaDTO> themenDTO = themaService.findAllThemen();
    
    List<Thema> themen = new ArrayList<>();
    for (ThemaDTO t : themenDTO) {
        Thema thema = mapToThema(t);
        // Fragen zu diesem Thema nachladen
        List<FrageDTO> fragenDTO = frageService.findFragenByThemaId(t.getId());
        thema.setFragen(fragenDTO.stream().map(this::mapToFrage).collect(Collectors.toList()));
        themen.add(thema);
    }
    
    return themen;
}

// Kein Caching - direkter Zugriff auf Services für bessere Konsistenz
```

### **Asynchrone Operationen**

```java
// SwingWorker für UI-Updates
new SwingWorker<List<Thema>, Void>() {
    @Override
    protected List<Thema> doInBackground() throws Exception {
        return dataProvider.getAlleThemen(); // Im Hintergrund
    }
    
    @Override
    protected void done() {
        try {
            List<Thema> themen = get();
            updateUI(themen); // Im EDT
        } catch (Exception e) {
            handleError(e);
        }
    }
}.execute();
```

### **Thread-Sicherheit**

```java
// LokalRepository mit ReadWriteLock
public class LokalRepository implements QuizRepository {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Override
    public List<ThemaDTO> findAllThemen() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(themenListe);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public ThemaDTO saveThema(ThemaDTO thema) {
        lock.writeLock().lock();
        try {
            // Speicherlogik implementieren
            return thema;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### **PropertyChange-System**

```java
// ServiceBackedDataProvider mit PropertyChange-Support
public class ServiceBackedDataProvider implements QuizDataProvider {
    private final PropertyChangeSupport propertyChangeSupport;
    
    public ServiceBackedDataProvider() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        registerEventListeners();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    private void notifyThemenChanged() {
        propertyChangeSupport.firePropertyChange(THEMEN_CHANGED, null, null);
    }
    
    private void notifyFragenChanged() {
        propertyChangeSupport.firePropertyChange(FRAGEN_CHANGED, null, null);
    }
}
```

## 🔮 Erweiterbarkeit

### **Neue Service hinzufügen**

```java
public class NeuerService {
    private final QuizRepository repository;
    private final EventManager eventManager;
    
    public NeuerService(QuizRepository repository) {
        this.repository = repository;
        this.eventManager = EventManager.getInstance();
    }
    
    public void neueFunktion() {
        // Geschäftslogik implementieren
        // Event feuern
        // Repository verwenden
    }
}
```

### **Neue Events hinzufügen**

```java
public class NeuerEvent extends QuizEvent {
    private final String spezifischeEigenschaft;
    
    public NeuerEvent(String source, String eigenschaft) {
        super(source);
        this.spezifischeEigenschaft = eigenschaft;
    }
    
    @Override
    public String getEventType() {
        return "NEUER_EVENT";
    }
}
```

### **Neue Repository-Implementierung**

```java
public class NeuerRepository implements QuizRepository {
    // Alle Interface-Methoden implementieren
    
    @Override
    public List<ThemaDTO> findAllThemen() {
        // Neue Persistierungslogik
        return new ArrayList<>();
    }
    
    // ... weitere Methoden
}
```

### **Neue MVC-Controller hinzufügen**

```java
public class NeuerController extends AbstractController {
    private final NeuerService neuerService;
    
    public NeuerController(NeuerService neuerService) {
        super();
        this.neuerService = neuerService;
    }
    
    public void setViews(NeuerMainPanel mainPanel, NeuerActionPanel actionPanel,
                        NeuerLinkesPanel linkesPanel, NeuerRechtesPanel rechtesPanel) {
        // Views verbinden
        setupActionListeners();
        setParentComponent(mainPanel);
    }
    
    private void setupActionListeners() {
        // Action-Listeners einrichten
    }
    
    @Override
    public void onEvent(QuizEvent event) {
        // Event-Behandlung
    }
}
```

### **Controller in Factory registrieren**

```java
public class ControllerFactory {
    private NeuerController neuerController;
    
    private void initializeControllers() {
        // ... bestehende Controller
        this.neuerController = new NeuerController(quizApplication.getNeuerService());
    }
    
    public void connectControllersWithViews(/* ... */) {
        // ... bestehende Verbindungen
        neuerController.setViews(neuerMainPanel, neuerActionPanel, neuerLinkesPanel, neuerRechtesPanel);
    }
    
    public void startAllControllers() {
        // ... bestehende Controller
        neuerController.registerForEvents("NEUER_*");
    }
}
```

## 📊 Performance-Optimierungen

### **Direkter Service-Zugriff (kein Caching)**

```java
// ServiceBackedDataProvider ohne Caching für bessere Konsistenz
public List<Thema> getAlleThemen() {
    System.out.println("ServiceBackedDataProvider: Lade alle Themen von Service...");
    List<ThemaDTO> themenDTO = themaService.findAllThemen();
    
    List<Thema> themen = new ArrayList<>();
    for (ThemaDTO t : themenDTO) {
        Thema thema = mapToThema(t);
        // Fragen zu diesem Thema nachladen
        List<FrageDTO> fragenDTO = frageService.findFragenByThemaId(t.getId());
        thema.setFragen(fragenDTO.stream().map(this::mapToFrage).collect(Collectors.toList()));
        themen.add(thema);
    }
    
    return themen;
}

// Kein Caching - direkter Zugriff auf Services für bessere Konsistenz
```

### **Asynchrone Operationen**

```java
// SwingWorker für UI-Updates
new SwingWorker<List<Thema>, Void>() {
    @Override
    protected List<Thema> doInBackground() throws Exception {
        return dataProvider.getAlleThemen(); // Im Hintergrund
    }
    
    @Override
    protected void done() {
        try {
            List<Thema> themen = get();
            updateUI(themen); // Im EDT
        } catch (Exception e) {
            handleError(e);
        }
    }
}.execute();
```

### **Thread-Sicherheit**

```java
// LokalRepository mit ReadWriteLock
public class LokalRepository implements QuizRepository {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Override
    public List<ThemaDTO> findAllThemen() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(themenListe);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public ThemaDTO saveThema(ThemaDTO thema) {
        lock.writeLock().lock();
        try {
            // Speicherlogik implementieren
            return thema;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### **PropertyChange-System**

```java
// ServiceBackedDataProvider mit PropertyChange-Support
public class ServiceBackedDataProvider implements QuizDataProvider {
    private final PropertyChangeSupport propertyChangeSupport;
    
    public ServiceBackedDataProvider() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        registerEventListeners();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    private void notifyThemenChanged() {
        propertyChangeSupport.firePropertyChange(THEMEN_CHANGED, null, null);
    }
    
    private void notifyFragenChanged() {
        propertyChangeSupport.firePropertyChange(FRAGEN_CHANGED, null, null);
    }
}
```

---

**Diese Architektur-Dokumentation bietet einen umfassenden Überblick über die Quiz-Anwendung mit MVC-Controller-System und dient als Referenz für Entwickler und Architekten.**

**Neue Features**: MVC-Controller, erweiterte Konfiguration, Thread-Sicherheit, PropertyChange-System
**Architektur**: Clean Architecture mit Separation of Concerns und Dependency Injection
**Qualität**: Responsive UI, Performance-Optimierungen, erweiterte Fehlerbehandlung
