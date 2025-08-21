package exception;

/**
 * Exception für Validierungsfehler bei Quiz-Daten.
 * 
 * <p>Diese Exception wird geworfen, wenn Eingabedaten die Validierungsregeln nicht erfüllen.
 * Sie wird hauptsächlich in den Service-Klassen verwendet, um Benutzereingaben zu validieren
 * bevor sie in der Datenbank gespeichert werden.</p>
 * 
 * <p>Typische Validierungsfehler:</p>
 * <ul>
 *   <li>Leere oder zu lange Texte</li>
 *   <li>Fehlende Pflichtfelder</li>
 *   <li>Ungültige Datenformate</li>
 *   <li>Geschäftsregel-Verletzungen</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * if (titel == null || titel.trim().isEmpty()) {
 *     throw new ValidationException("Der Titel darf nicht leer sein.");
 * }
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizException
 */
public class ValidationException extends QuizException {
	private static final long serialVersionUID = 1L;

	/**
	 * Erstellt eine neue ValidationException mit der angegebenen Fehlermeldung.
	 * 
	 * @param message Die Fehlermeldung, die den Validierungsfehler beschreibt
	 */
	public ValidationException(String message) {
		super(message);
	}

	/**
	 * Erstellt eine neue ValidationException mit der angegebenen Fehlermeldung und Ursache.
	 * 
	 * @param message Die Fehlermeldung, die den Validierungsfehler beschreibt
	 * @param cause Die ursprüngliche Exception, die diesen Validierungsfehler verursacht hat
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
