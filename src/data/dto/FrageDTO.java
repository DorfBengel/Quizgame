package data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO für Fragen-Daten.
 * 
 * <p>Diese Klasse repräsentiert eine Quiz-Frage als Data Transfer Object.
 * Sie implementiert Serializable für die Persistierung und dient der Übertragung
 * von Fragendaten zwischen verschiedenen Schichten der Anwendung.</p>
 * 
 * <p>Eigenschaften:</p>
 * <ul>
 *   <li>Eindeutige ID für die Identifikation</li>
 *   <li>Titel der Frage für die Übersicht</li>
 *   <li>Fragetext für die Anzeige</li>
 *   <li>Thema-Name für die Kategorisierung</li>
 *   <li>Liste von Antworten</li>
 *   <li>Serialisierbar für Persistierung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * FrageDTO frage = new FrageDTO(1L, "Java Grundlagen", "Was ist Java?", "Programmierung");
 * frage.addAntwort(new AntwortDTO(1L, "Eine Programmiersprache", true));
 * frage.addAntwort(new AntwortDTO(2L, "Ein Betriebssystem", false));
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see AntwortDTO
 * @see ThemaDTO
 * @see Serializable
 */
public class FrageDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String titel;
	private String text;
	private String themaName;
	private List<AntwortDTO> antworten = new ArrayList<>();

	/**
	 * Standard-Konstruktor.
	 * 
	 * <p>Erstellt eine leere FrageDTO-Instanz. Alle Felder werden auf
	 * Standardwerte gesetzt und eine leere Antworten-Liste wird initialisiert.</p>
	 */
	public FrageDTO() {
	}

	/**
	 * Konstruktor mit allen Feldern außer Antworten.
	 * 
	 * @param id Die eindeutige ID der Frage
	 * @param titel Der Titel der Frage
	 * @param text Der Fragetext
	 * @param themaName Der Name des Themas
	 */
	public FrageDTO(long id, String titel, String text, String themaName) {
		this.id = id;
		this.titel = titel;
		this.text = text;
		this.themaName = themaName;
	}

	/**
	 * Gibt die ID der Frage zurück.
	 * 
	 * @return Die eindeutige ID der Frage
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setzt die ID der Frage.
	 * 
	 * @param id Die neue ID der Frage
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gibt den Titel der Frage zurück.
	 * 
	 * @return Der Titel der Frage
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * Setzt den Titel der Frage.
	 * 
	 * @param titel Der neue Titel der Frage
	 */
	public void setTitel(String titel) {
		this.titel = titel;
	}

	/**
	 * Gibt den Fragetext zurück.
	 * 
	 * @return Der Text der Frage
	 */
	public String getText() {
		return text;
	}

	/**
	 * Setzt den Fragetext.
	 * 
	 * @param text Der neue Text der Frage
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gibt den Namen des Themas zurück.
	 * 
	 * @return Der Name des Themas, zu dem die Frage gehört
	 */
	public String getThemaName() {
		return themaName;
	}

	/**
	 * Setzt den Namen des Themas.
	 * 
	 * @param themaName Der neue Name des Themas
	 */
	public void setThemaName(String themaName) {
		this.themaName = themaName;
	}

	/**
	 * Gibt die Liste der Antworten zurück.
	 * 
	 * @return Die Liste aller Antworten zu dieser Frage
	 */
	public List<AntwortDTO> getAntworten() {
		return antworten;
	}

	/**
	 * Setzt die Liste der Antworten.
	 * 
	 * @param antworten Die neue Liste der Antworten
	 */
	public void setAntworten(List<AntwortDTO> antworten) {
		this.antworten = antworten;
	}

	/**
	 * Fügt eine neue Antwort zur Frage hinzu.
	 * 
	 * @param antwort Die hinzuzufügende Antwort
	 */
	public void addAntwort(AntwortDTO antwort) {
		this.antworten.add(antwort);
	}

	/**
	 * Gibt eine String-Repräsentation der Frage zurück.
	 * 
	 * <p>Das Format ist: "FrageDTO{id=X, titel='Y', themaName='Z', anzahlAntworten=N}"</p>
	 * 
	 * @return String-Repräsentation der Frage
	 */
	@Override
	public String toString() {
		return "FrageDTO{id=" + id + ", titel='" + titel + "', themaName='" + themaName + "', anzahlAntworten="
				+ antworten.size() + "}";
	}
}
