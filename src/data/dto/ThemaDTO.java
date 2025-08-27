package data.dto;

import java.io.Serializable;

/**
 * DTO für Themen-Daten ohne direkte Referenzen zu Fragen.
 * 
 * <p>Diese Klasse repräsentiert ein Quiz-Thema als Data Transfer Object.
 * Sie implementiert Serializable für die Persistierung und dient der Übertragung
 * von Themendaten zwischen verschiedenen Schichten der Anwendung.</p>
 * 
 * <p>Eigenschaften:</p>
 * <ul>
 *   <li>Eindeutige ID für die Identifikation</li>
 *   <li>Titel des Themas für die Übersicht</li>
 *   <li>Informationen zum Thema für die Beschreibung</li>
 *   <li>Anzahl der zugehörigen Fragen</li>
 *   <li>Serialisierbar für Persistierung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * ThemaDTO thema = new ThemaDTO(1L, "Java Grundlagen", "Einführung in Java", 5);
 * thema.setTitel("Neuer Titel");
 * thema.setInformation("Neue Beschreibung");
 * thema.setAnzahlFragen(10);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see FrageDTO
 * @see Serializable
 */
public class ThemaDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String titel;
	private String information;
	private int anzahlFragen;

	/**
	 * Standard-Konstruktor.
	 * 
	 * <p>Erstellt eine leere ThemaDTO-Instanz. Alle Felder werden auf
	 * Standardwerte gesetzt (id=0, titel=null, information=null, anzahlFragen=0).</p>
	 */
	public ThemaDTO() {
	}

	/**
	 * Konstruktor mit allen Feldern.
	 * 
	 * @param id Die eindeutige ID des Themas
	 * @param titel Der Titel des Themas
	 * @param information Die Beschreibung des Themas
	 * @param anzahlFragen Die Anzahl der zugehörigen Fragen
	 */
	public ThemaDTO(long id, String titel, String information, int anzahlFragen) {
		this.id = id;
		this.titel = titel;
		this.information = information;
		this.anzahlFragen = anzahlFragen;
	}

	/**
	 * Gibt die ID des Themas zurück.
	 * 
	 * @return Die eindeutige ID des Themas
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setzt die ID des Themas.
	 * 
	 * @param id Die neue ID des Themas
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gibt den Titel des Themas zurück.
	 * 
	 * @return Der Titel des Themas
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * Setzt den Titel des Themas.
	 * 
	 * @param titel Der neue Titel des Themas
	 */
	public void setTitel(String titel) {
		this.titel = titel;
	}

	/**
	 * Gibt die Informationen zum Thema zurück.
	 * 
	 * @return Die Beschreibung des Themas
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * Setzt die Informationen zum Thema.
	 * 
	 * @param information Die neue Beschreibung des Themas
	 */
	public void setInformation(String information) {
		this.information = information;
	}

	/**
	 * Gibt die Anzahl der zugehörigen Fragen zurück.
	 * 
	 * @return Die Anzahl der Fragen in diesem Thema
	 */
	public int getAnzahlFragen() {
		return anzahlFragen;
	}

	/**
	 * Setzt die Anzahl der zugehörigen Fragen.
	 * 
	 * @param anzahlFragen Die neue Anzahl der Fragen
	 */
	public void setAnzahlFragen(int anzahlFragen) {
		this.anzahlFragen = anzahlFragen;
	}

	/**
	 * Gibt eine String-Repräsentation des Themas zurück.
	 * 
	 * <p>Das Format ist: "ThemaDTO{id=X, titel='Y', anzahlFragen=Z}"</p>
	 * 
	 * @return String-Repräsentation des Themas
	 */
	@Override
	public String toString() {
		return "ThemaDTO{id=" + id + ", titel='" + titel + "', anzahlFragen=" + anzahlFragen + "}";
	}
}
