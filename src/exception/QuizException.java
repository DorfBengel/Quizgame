package exception;

/**
 * Basis-Exception für alle Quiz-bezogenen Fehler.
 * 
 * <p>Diese Klasse dient als Grundlage für alle spezifischen Exceptions in der Quiz-Anwendung.
 * Sie erweitert RuntimeException, da es sich um nicht-behandelbare Fehler handelt, die
 * normalerweise auf Programmierfehler oder schwerwiegende Systemprobleme hinweisen.</p>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * throw new QuizException("Ungültige Quiz-Konfiguration");
 * throw new QuizException("Datenbankfehler", cause);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 */
public class QuizException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Erstellt eine neue QuizException mit der angegebenen Fehlermeldung.
	 * 
	 * @param message Die Fehlermeldung, die den Fehler beschreibt
	 */
	public QuizException(String message) {
		super(message);
	}

	/**
	 * Erstellt eine neue QuizException mit der angegebenen Fehlermeldung und Ursache.
	 * 
	 * @param message Die Fehlermeldung, die den Fehler beschreibt
	 * @param cause Die ursprüngliche Exception, die diesen Fehler verursacht hat
	 */
	public QuizException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Erstellt eine neue QuizException mit der angegebenen Ursache.
	 * 
	 * @param cause Die ursprüngliche Exception, die diesen Fehler verursacht hat
	 */
	public QuizException(Throwable cause) {
		super(cause);
	}
}
