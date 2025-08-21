package business.event;

/**
 * Interface für Event-Listener.
 * 
 * <p>Dieses funktionale Interface definiert den Vertrag für alle Event-Listener
 * in der Quiz-Anwendung. Es implementiert das Observer-Pattern und ermöglicht
 * die lose Kopplung zwischen Event-Producern und Event-Consumern.</p>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * // Lambda-Implementierung
 * EventListener listener = event -> {
 *     System.out.println("Event empfangen: " + event.getEventType());
 * };
 * 
 * // Anonyme Klasse
 * EventListener listener = new EventListener() {
 *     @Override
 *     public void onEvent(QuizEvent event) {
 *         // Event-Behandlung
 *     }
 * };
 * </pre>
 * 
 * <p>Event-Behandlung:</p>
 * <ul>
 *   <li>Events werden asynchron verarbeitet</li>
 *   <li>Fehler in einem Listener beeinträchtigen andere nicht</li>
 *   <li>Listener können Events filtern und verarbeiten</li>
 *   <li>Thread-sichere Event-Behandlung</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizEvent
 * @see EventManager
 * @see DataChangedEvent
 */
@FunctionalInterface
public interface EventListener {

	/**
	 * Wird aufgerufen, wenn ein Event gefeuert wird.
	 * 
	 * <p>Diese Methode wird vom EventManager aufgerufen, wenn ein Event
	 * des registrierten Typs gefeuert wird. Die Implementierung sollte
	 * thread-safe sein und keine langwierigen Operationen durchführen.</p>
	 * 
	 * <p>Wichtige Hinweise:</p>
	 * <ul>
	 *   <li>Methode sollte schnell zurückkehren</li>
	 *   <li>Lange Operationen in separaten Threads ausführen</li>
	 *   <li>Fehlerbehandlung implementieren</li>
	 *   <li>Keine EventManager-Aufrufe von hier aus</li>
	 * </ul>
	 *
	 * @param event Das gefeuerte Event
	 */
	void onEvent(QuizEvent event);
}
