package business.event;

import java.time.LocalDateTime;

/**
 * Basis-Event-Klasse für alle Quiz-Events.
 * 
 * <p>Diese abstrakte Klasse definiert die Grundstruktur für alle Events in der
 * Quiz-Anwendung. Sie implementiert das Observer-Pattern und ermöglicht die
 * lose Kopplung zwischen verschiedenen Komponenten.</p>
 * 
 * <p>Event-Eigenschaften:</p>
 * <ul>
 *   <li>Automatische Zeitstempel-Generierung</li>
 *   <li>Quellen-Identifikation für Event-Handler</li>
 *   <li>Abstrakte Event-Typ-Implementierung</li>
 *   <li>Thread-sichere Event-Behandlung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * public class MeineEvent extends QuizEvent {
 *     public MeineEvent(String source) {
 *         super(source);
 *     }
 *     
 *     @Override
 *     public String getEventType() {
 *         return "MEINE_EVENT";
 *     }
 * }
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see EventManager
 * @see EventListener
 * @see DataChangedEvent
 */
public abstract class QuizEvent {

	private final LocalDateTime timestamp;
	private final String source;

	/**
	 * Erstellt ein neues QuizEvent.
	 * 
	 * <p>Der Zeitstempel wird automatisch auf die aktuelle Zeit gesetzt.
	 * Die Quelle sollte den Namen der Komponente enthalten, die das Event feuert.</p>
	 * 
	 * @param source Der Name der Komponente, die das Event feuert
	 */
	protected QuizEvent(String source) {
		this.timestamp = LocalDateTime.now();
		this.source = source;
	}

	/**
	 * Gibt den Zeitstempel des Events zurück.
	 * 
	 * @return Der Zeitstempel, wann das Event erstellt wurde
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * Gibt die Quelle des Events zurück.
	 * 
	 * @return Der Name der Komponente, die das Event gefeuert hat
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Gibt den Event-Typ zurück.
	 * 
	 * <p>Diese Methode muss von allen Unterklassen implementiert werden.
	 * Der Event-Typ wird zur Kategorisierung und Filterung von Events verwendet.</p>
	 * 
	 * @return Eine String-Repräsentation des Event-Typs
	 */
	public abstract String getEventType();
}
