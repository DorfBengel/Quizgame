package business;

import java.util.List;

import business.event.EventListener;
import business.event.EventManager;
import business.event.QuizEvent;
import config.PropertiesLoader;
import data.dto.ThemaDTO;
import data.persistence.RepositoryFactory;
import data.repository.QuizRepository;

/**
 * Zentrale Anwendungsklasse für die Quiz-Anwendung.
 * 
 * <p>Diese Klasse implementiert das Singleton-Pattern und dient als zentraler
 * Einstiegspunkt für die Quiz-Anwendung. Sie initialisiert alle Services,
 * das Repository und den Event-Manager.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Zentrale Verwaltung aller Services (Thema, Frage, Statistik)</li>
 *   <li>Repository-Initialisierung und -Verwaltung</li>
 *   <li>Event-Manager-Initialisierung</li>
 *   <li>Standard-Event-Listener-Registrierung</li>
 *   <li>Anwendungs-Lifecycle-Management (Start/Stop)</li>
 * </ul>
 * 
 * <p>Architektur:</p>
 * <ul>
 *   <li>Singleton-Pattern für globale Verfügbarkeit</li>
 *   <li>Dependency Injection für Services</li>
 *   <li>Event-basierte Kommunikation zwischen Komponenten</li>
 *   <li>Konfigurationsbasierte Repository-Erstellung</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see ThemaService
 * @see FrageService
 * @see QuizStatistikService
 * @see EventManager
 */
public class QuizApplication {

	private static QuizApplication instance;
	private final QuizRepository repository;
	private final ThemaService themaService;
	private final FrageService frageService;
	private final QuizStatistikService quizStatistikService;
	private final EventManager eventManager;

	/**
	 * Privater Konstruktor für Singleton-Pattern.
	 * 
	 * <p>Initialisiert alle Services, das Repository und den Event-Manager.
	 * Registriert Standard-Event-Listener für Logging und Debugging.</p>
	 */
	private QuizApplication() {
		// Repository erstellen
		this.repository = RepositoryFactory.createRepository();

		// Services erstellen
		this.themaService = new ThemaService(repository);
		this.frageService = new FrageService(repository);
		this.quizStatistikService = new QuizStatistikService(repository);

		// Event-Manager initialisieren
		this.eventManager = EventManager.getInstance();

		// Standard-Event-Listener registrieren
		registerDefaultEventListeners();
	}

	/**
	 * Singleton-Instanz der Quiz-Anwendung.
	 * 
	 * <p>Erstellt bei Bedarf eine neue Instanz oder gibt die bestehende zurück.
	 * Thread-sicher durch synchronized.</p>
	 * 
	 * @return Die einzige Instanz der Quiz-Anwendung
	 */
	public static synchronized QuizApplication getInstance() {
		if (instance == null) {
			instance = new QuizApplication();
		}
		return instance;
	}

	/**
	 * Registriert Standard-Event-Listener.
	 * 
	 * <p>Registriert Listener für alle Events, Themen-Events und Fragen-Events
	 * zur Unterstützung von Logging und Debugging.</p>
	 */
	private void registerDefaultEventListeners() {
		// Logging-Listener für alle Events
		eventManager.addEventListener("*", new EventListener() {
			@Override
			public void onEvent(QuizEvent event) {
				System.out.println("Event gefeuert: " + event.getEventType() + " von " + event.getSource() + " um "
						+ event.getTimestamp());
			}
		});

		// Generische Listener für alle Event-Typen
		eventManager.addEventListener("THEMA_*", new EventListener() {
			@Override
			public void onEvent(QuizEvent event) {
				System.out.println("Thema-Event empfangen: " + event.getEventType());
			}
		});

		eventManager.addEventListener("FRAGE_*", new EventListener() {
			@Override
			public void onEvent(QuizEvent event) {
				System.out.println("Frage-Event empfangen: " + event.getEventType());
			}
		});
	}

	/**
	 * Startet die Anwendung.
	 * 
	 * <p>Gibt Konfigurationsinformationen aus und markiert die Anwendung als bereit.
	 * Diese Methode wird beim Anwendungsstart aufgerufen.</p>
	 */
	public void start() {
		System.out.println("Quiz-Anwendung wird gestartet...");
		
		// Zeige Informationen über das bereits erstellte Repository
		if (repository instanceof data.persistence.LokalRepository) {
			System.out.println("Datenbank-Typ: Lokale Speicherung");
			System.out.println("Datenbank-Konfiguration: Lokale Umgebung");
		} else if (repository instanceof data.persistence.JDBCRepository) {
			System.out.println("Datenbank-Typ: JDBC-Verbindung");
			System.out.println("Datenbank-Konfiguration: Datenbank-basierte Umgebung");
		} else {
			System.out.println("Datenbank-Typ: " + repository.getClass().getSimpleName());
			System.out.println("Datenbank-Konfiguration: Unbekannt");
		}
		
		System.out.println("UI-Skalierung: " + PropertiesLoader.getProperty("quiz.ui.scale", "1.0"));
		System.out.println("Anwendung bereit!");
	}

	/**
	 * Stoppt die Anwendung.
	 * 
	 * <p>Führt Cleanup-Operationen durch und markiert die Anwendung als gestoppt.
	 * Diese Methode wird beim Anwendungsende aufgerufen.</p>
	 */
	public void stop() {
		System.out.println("Quiz-Anwendung wird gestoppt...");
		// Hier könnten Cleanup-Operationen durchgeführt werden
		System.out.println("Anwendung gestoppt!");
	}

	/**
	 * Gibt den ThemaService zurück.
	 * 
	 * @return Der ThemaService für Themen-Operationen
	 */
	public ThemaService getThemaService() {
		return themaService;
	}

	/**
	 * Gibt den FrageService zurück.
	 * 
	 * @return Der FrageService für Fragen-Operationen
	 */
	public FrageService getFrageService() {
		return frageService;
	}

	/**
	 * Gibt den EventManager zurück.
	 * 
	 * @return Der EventManager für Event-Operationen
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Gibt das Repository zurück.
	 * 
	 * @return Das Repository für Datenzugriffs-Operationen
	 */
	public QuizRepository getRepository() {
		return repository;
	}

	/**
	 * Gibt den QuizStatistikService zurück.
	 * 
	 * @return Der QuizStatistikService für Statistik-Operationen
	 */
	public QuizStatistikService getQuizStatistikService() {
		return quizStatistikService;
	}

	/**
	 * Hauptmethode für Tests.
	 * 
	 * <p>Diese Methode dient zum Testen der Anwendungsfunktionalität.
	 * Sie erstellt ein Thema und lädt alle verfügbaren Themen.</p>
	 * 
	 * @param args Kommandozeilenargumente (werden nicht verwendet)
	 */
	public static void main(String[] args) {
		QuizApplication app = QuizApplication.getInstance();
		app.start();

		// Test: Ein Thema erstellen
		try {
			ThemaDTO thema = app.getThemaService().createThema("Test Thema", "Test Information");
			System.out.println("Thema erstellt: " + thema.getTitel());

			// Alle Themen laden
			List<ThemaDTO> themen = app.getThemaService().findAllThemen();
			System.out.println("Anzahl Themen: " + themen.size());

		} catch (Exception e) {
			System.err.println("Fehler beim Testen: " + e.getMessage());
			e.printStackTrace();
		}

		app.stop();
	}
}
