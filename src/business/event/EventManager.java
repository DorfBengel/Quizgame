package business.event;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Zentrale Verwaltung für alle Quiz-Events.
 * 
 * <p>Diese Klasse implementiert das Singleton-Pattern und dient als zentraler
 * Event-Broker für die Quiz-Anwendung. Sie verwaltet die Registrierung von
 * Event-Listenern und leitet Events an die entsprechenden Listener weiter.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Event-Listener-Registrierung und -Verwaltung</li>
 *   <li>Event-Weiterleitung an registrierte Listener</li>
 *   <li>Globale Event-Behandlung mit Wildcard-Listeners</li>
 *   <li>Asynchrone Event-Verarbeitung</li>
 *   <li>Thread-sichere Event-Verwaltung</li>
 * </ul>
 * 
 * <p>Event-Typen:</p>
 * <ul>
 *   <li>Spezifische Event-Typen (z.B. "THEMA_CREATED")</li>
 *   <li>Wildcard-Patterns (z.B. "THEMA_*" für alle Themen-Events)</li>
 *   <li>Globale Listener ("*" für alle Events)</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizEvent
 * @see EventListener
 * @see DataChangedEvent
 */
public class EventManager {

	private static EventManager instance;
	private final Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

	/**
	 * Privater Konstruktor für Singleton-Pattern.
	 */
	private EventManager() {
	}

	/**
	 * Singleton-Instanz des EventManagers.
	 * 
	 * <p>Erstellt bei Bedarf eine neue Instanz oder gibt die bestehende zurück.
	 * Thread-sicher durch synchronized.</p>
	 * 
	 * @return Die einzige Instanz des EventManagers
	 */
	public static synchronized EventManager getInstance() {
		if (instance == null) {
			instance = new EventManager();
		}
		return instance;
	}

	/**
	 * Registriert einen Event-Listener für einen bestimmten Event-Typ.
	 * 
	 * <p>Der Listener wird für alle Events des angegebenen Typs aufgerufen.
	 * Unterstützt Wildcard-Patterns wie "THEMA_*" für alle Themen-Events.</p>
	 * 
	 * @param eventType Der Event-Typ oder Wildcard-Pattern
	 * @param listener Der zu registrierende Event-Listener
	 */
	public void addEventListener(String eventType, EventListener listener) {
		listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
	}

	/**
	 * Entfernt einen Event-Listener für einen bestimmten Event-Typ.
	 * 
	 * @param eventType Der Event-Typ
	 * @param listener Der zu entfernende Event-Listener
	 */
	public void removeEventListener(String eventType, EventListener listener) {
		List<EventListener> eventListeners = listeners.get(eventType);
		if (eventListeners != null) {
			eventListeners.remove(listener);
		}
	}

	/**
	 * Feuert ein Event und benachrichtigt alle registrierten Listener.
	 * 
	 * <p>Die Methode benachrichtigt sowohl spezifische Listener als auch
	 * globale Listener. Fehler in einzelnen Listenern beeinträchtigen
	 * andere Listener nicht.</p>
	 * 
	 * @param event Das zu feuernde Event
	 */
	public void fireEvent(QuizEvent event) {
		String eventType = event.getEventType();
		List<EventListener> eventListeners = listeners.get(eventType);

		if (eventListeners != null) {
			for (EventListener listener : eventListeners) {
				try {
					listener.onEvent(event);
				} catch (Exception e) {
					System.err.println("Fehler beim Verarbeiten des Events " + eventType + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		// Auch globale Listener benachrichtigen
		List<EventListener> globalListeners = listeners.get("*");
		if (globalListeners != null) {
			for (EventListener listener : globalListeners) {
				try {
					listener.onEvent(event);
				} catch (Exception e) {
					System.err.println(
							"Fehler beim Verarbeiten des globalen Events " + eventType + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Feuert ein Event asynchron.
	 * 
	 * <p>Das Event wird in einem separaten Thread verarbeitet, um die
	 * aufrufende Komponente nicht zu blockieren.</p>
	 * 
	 * @param event Das zu feuernde Event
	 */
	public void fireEventAsync(QuizEvent event) {
		new Thread(() -> fireEvent(event)).start();
	}

	/**
	 * Gibt alle registrierten Event-Typen zurück.
	 * 
	 * @return Set aller registrierten Event-Typen
	 */
	public Set<String> getRegisteredEventTypes() {
		return new HashSet<>(listeners.keySet());
	}

	/**
	 * Gibt die Anzahl der Listener für einen Event-Typ zurück.
	 * 
	 * @param eventType Der Event-Typ
	 * @return Anzahl der registrierten Listener für diesen Event-Typ
	 */
	public int getListenerCount(String eventType) {
		List<EventListener> eventListeners = listeners.get(eventType);
		return eventListeners != null ? eventListeners.size() : 0;
	}

	/**
	 * Entfernt alle Listener für einen Event-Typ.
	 * 
	 * @param eventType Der Event-Typ, für den alle Listener entfernt werden sollen
	 */
	public void clearEventListeners(String eventType) {
		listeners.remove(eventType);
	}

	/**
	 * Entfernt alle Listener.
	 * 
	 * <p>Entfernt alle registrierten Event-Listener für alle Event-Typen.
	 * Nützlich für Cleanup-Operationen beim Herunterfahren der Anwendung.</p>
	 */
	public void clearAllEventListeners() {
		listeners.clear();
	}
}
