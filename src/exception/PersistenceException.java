package exception;

/**
 * Exception für Persistierungsfehler bei Quiz-Daten.
 * 
 * <p>Diese Exception wird geworfen, wenn Fehler beim Speichern, Laden oder Löschen
 * von Daten auftreten. Sie kann sowohl Datenbankfehler als auch Probleme mit der
 * lokalen Dateispeicherung abdecken.</p>
 * 
 * <p>Häufige Ursachen:</p>
 * <ul>
 *   <li>Datenbankverbindungsfehler</li>
 *   <li>SQL-Syntaxfehler</li>
 *   <li>Dateizugriffsfehler bei lokaler Speicherung</li>
 *   <li>Serialisierungsfehler</li>
 *   <li>Berechtigungsprobleme</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * try {
 *     repository.saveFrage(frage);
 * } catch (SQLException e) {
 *     throw new PersistenceException("Fehler beim Speichern der Frage", e);
 * }
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizException
 */
public class PersistenceException extends QuizException {
	private static final long serialVersionUID = 1L;

	/**
	 * Erstellt eine neue PersistenceException mit der angegebenen Fehlermeldung.
	 * 
	 * @param message Die Fehlermeldung, die den Persistierungsfehler beschreibt
	 */
	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * Erstellt eine neue PersistenceException mit der angegebenen Fehlermeldung und Ursache.
	 * 
	 * @param message Die Fehlermeldung, die den Persistierungsfehler beschreibt
	 * @param cause Die ursprüngliche Exception, die diesen Persistierungsfehler verursacht hat
	 */
	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
