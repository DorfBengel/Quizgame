package data.dto;

import java.io.Serializable;

/**
 * DTO für Antwort-Daten.
 * 
 * <p>Diese Klasse repräsentiert eine Quiz-Antwort als Data Transfer Object.
 * Sie implementiert Serializable für die Persistierung und dient der Übertragung
 * von Antwortdaten zwischen verschiedenen Schichten der Anwendung.</p>
 * 
 * <p>Eigenschaften:</p>
 * <ul>
 *   <li>Eindeutige ID für die Identifikation</li>
 *   <li>Antworttext für die Anzeige</li>
 *   <li>Richtig-Flag für die Bewertung</li>
 *   <li>Serialisierbar für Persistierung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * AntwortDTO antwort = new AntwortDTO(1L, "Java ist eine Programmiersprache", true);
 * antwort.setId(1L);
 * antwort.setText("Neuer Antworttext");
 * antwort.setIstRichtig(false);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see FrageDTO
 * @see Serializable
 */
public class AntwortDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String text;
	private boolean istRichtig;

	/**
	 * Standard-Konstruktor.
	 * 
	 * <p>Erstellt eine leere AntwortDTO-Instanz. Alle Felder werden auf
	 * Standardwerte gesetzt (id=0, text=null, istRichtig=false).</p>
	 */
	public AntwortDTO() {
	}

	/**
	 * Konstruktor mit allen Feldern.
	 * 
	 * @param id Die eindeutige ID der Antwort
	 * @param text Der Antworttext
	 * @param istRichtig Ob die Antwort richtig ist
	 */
	public AntwortDTO(long id, String text, boolean istRichtig) {
		this.id = id;
		this.text = text;
		this.istRichtig = istRichtig;
	}

	/**
	 * Gibt die ID der Antwort zurück.
	 * 
	 * @return Die eindeutige ID der Antwort
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setzt die ID der Antwort.
	 * 
	 * @param id Die neue ID der Antwort
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gibt den Antworttext zurück.
	 * 
	 * @return Der Text der Antwort
	 */
	public String getText() {
		return text;
	}

	/**
	 * Setzt den Antworttext.
	 * 
	 * @param text Der neue Text der Antwort
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Prüft, ob die Antwort richtig ist.
	 * 
	 * @return true wenn die Antwort richtig ist, false sonst
	 */
	public boolean istRichtig() {
		return istRichtig;
	}

	/**
	 * Setzt, ob die Antwort richtig ist.
	 * 
	 * @param istRichtig true wenn die Antwort richtig ist, false sonst
	 */
	public void setIstRichtig(boolean istRichtig) {
		this.istRichtig = istRichtig;
	}

	/**
	 * Gibt eine String-Repräsentation der Antwort zurück.
	 * 
	 * <p>Das Format ist: "AntwortDTO{id=X, text='Y', istRichtig=Z}"</p>
	 * 
	 * @return String-Repräsentation der Antwort
	 */
	@Override
	public String toString() {
		return "AntwortDTO{id=" + id + ", text='" + text + "', istRichtig=" + istRichtig + "}";
	}
}
