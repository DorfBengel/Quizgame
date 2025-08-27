package data.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO für Quiz-Ergebnisse.
 * 
 * <p>Diese Klasse repräsentiert ein Quiz-Ergebnis als Data Transfer Object.
 * Sie implementiert Serializable für die Persistierung und dient der Übertragung
 * von Ergebnisdaten zwischen verschiedenen Schichten der Anwendung.</p>
 * 
 * <p>Eigenschaften:</p>
 * <ul>
 *   <li>Eindeutige ID für die Identifikation</li>
 *   <li>Thema- und Fragen-Referenzen</li>
 *   <li>Antwort-Ergebnis (richtig/falsch)</li>
 *   <li>Antwort-Metadaten (Zeit, Punkte)</li>
 *   <li>Zeitstempel der Antwort</li>
 *   <li>Serialisierbar für Persistierung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * QuizErgebnisDTO ergebnis = new QuizErgebnisDTO(1L, 2L, true, false, 30, 10);
 * ergebnis.setId(1L);
 * ergebnis.setPunkte(15);
 * ergebnis.setZeitpunkt(LocalDateTime.now());
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see ThemaDTO
 * @see FrageDTO
 * @see Serializable
 */
public class QuizErgebnisDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long themaId;
	private long frageId;
	private boolean antwortRichtig;
	private boolean antwortVorherGezeigt;
	private int antwortZeitSekunden;
	private LocalDateTime zeitpunkt;
	private int punkte;

	/**
	 * Standard-Konstruktor.
	 * 
	 * <p>Erstellt eine leere QuizErgebnisDTO-Instanz. Alle Felder werden auf
	 * Standardwerte gesetzt und der Zeitstempel wird auf die aktuelle Zeit gesetzt.</p>
	 */
	public QuizErgebnisDTO() {
	}

	/**
	 * Konstruktor mit allen Feldern außer ID und Zeitstempel.
	 * 
	 * <p>Der Zeitstempel wird automatisch auf die aktuelle Zeit gesetzt.</p>
	 * 
	 * @param themaId Die ID des Themas
	 * @param frageId Die ID der Frage
	 * @param antwortRichtig Ob die Antwort richtig war
	 * @param antwortVorherGezeigt Ob die Antwort vorher angezeigt wurde
	 * @param antwortZeitSekunden Die Antwortzeit in Sekunden
	 * @param punkte Die erreichten Punkte
	 */
	public QuizErgebnisDTO(long themaId, long frageId, boolean antwortRichtig, boolean antwortVorherGezeigt,
			int antwortZeitSekunden, int punkte) {
		this.themaId = themaId;
		this.frageId = frageId;
		this.antwortRichtig = antwortRichtig;
		this.antwortVorherGezeigt = antwortVorherGezeigt;
		this.antwortZeitSekunden = antwortZeitSekunden;
		this.punkte = punkte;
		this.zeitpunkt = LocalDateTime.now();
	}

	/**
	 * Gibt die ID des Ergebnisses zurück.
	 * 
	 * @return Die eindeutige ID des Ergebnisses
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setzt die ID des Ergebnisses.
	 * 
	 * @param id Die neue ID des Ergebnisses
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gibt die ID des Themas zurück.
	 * 
	 * @return Die ID des Themas, zu dem das Ergebnis gehört
	 */
	public long getThemaId() {
		return themaId;
	}

	/**
	 * Setzt die ID des Themas.
	 * 
	 * @param themaId Die neue ID des Themas
	 */
	public void setThemaId(long themaId) {
		this.themaId = themaId;
	}

	/**
	 * Gibt die ID der Frage zurück.
	 * 
	 * @return Die ID der Frage, zu der das Ergebnis gehört
	 */
	public long getFrageId() {
		return frageId;
	}

	/**
	 * Setzt die ID der Frage.
	 * 
	 * @param frageId Die neue ID der Frage
	 */
	public void setFrageId(long frageId) {
		this.frageId = frageId;
	}

	/**
	 * Prüft, ob die Antwort richtig war.
	 * 
	 * @return true wenn die Antwort richtig war, false sonst
	 */
	public boolean isAntwortRichtig() {
		return antwortRichtig;
	}

	/**
	 * Setzt, ob die Antwort richtig war.
	 * 
	 * @param antwortRichtig true wenn die Antwort richtig war, false sonst
	 */
	public void setAntwortRichtig(boolean antwortRichtig) {
		this.antwortRichtig = antwortRichtig;
	}

	/**
	 * Prüft, ob die Antwort vorher angezeigt wurde.
	 * 
	 * @return true wenn die Antwort vorher angezeigt wurde, false sonst
	 */
	public boolean isAntwortVorherGezeigt() {
		return antwortVorherGezeigt;
	}

	/**
	 * Setzt, ob die Antwort vorher angezeigt wurde.
	 * 
	 * @param antwortVorherGezeigt true wenn die Antwort vorher angezeigt wurde, false sonst
	 */
	public void setAntwortVorherGezeigt(boolean antwortVorherGezeigt) {
		this.antwortVorherGezeigt = antwortVorherGezeigt;
	}

	/**
	 * Gibt die Antwortzeit in Sekunden zurück.
	 * 
	 * @return Die Zeit, die für die Antwort benötigt wurde (in Sekunden)
	 */
	public int getAntwortZeitSekunden() {
		return antwortZeitSekunden;
	}

	/**
	 * Setzt die Antwortzeit in Sekunden.
	 * 
	 * @param antwortZeitSekunden Die neue Antwortzeit in Sekunden
	 */
	public void setAntwortZeitSekunden(int antwortZeitSekunden) {
		this.antwortZeitSekunden = antwortZeitSekunden;
	}

	/**
	 * Gibt den Zeitstempel der Antwort zurück.
	 * 
	 * @return Der Zeitstempel, wann die Antwort gegeben wurde
	 */
	public LocalDateTime getZeitpunkt() {
		return zeitpunkt;
	}

	/**
	 * Setzt den Zeitstempel der Antwort.
	 * 
	 * @param zeitpunkt Der neue Zeitstempel der Antwort
	 */
	public void setZeitpunkt(LocalDateTime zeitpunkt) {
		this.zeitpunkt = zeitpunkt;
	}

	/**
	 * Gibt die erreichten Punkte zurück.
	 * 
	 * @return Die Anzahl der erreichten Punkte
	 */
	public int getPunkte() {
		return punkte;
	}

	/**
	 * Setzt die erreichten Punkte.
	 * 
	 * @param punkte Die neue Anzahl der erreichten Punkte
	 */
	public void setPunkte(int punkte) {
		this.punkte = punkte;
	}

	/**
	 * Gibt eine String-Repräsentation des Ergebnisses zurück.
	 * 
	 * <p>Das Format ist: "QuizErgebnisDTO{id=X, themaId=Y, frageId=Z, antwortRichtig=W, punkte=V}"</p>
	 * 
	 * @return String-Repräsentation des Ergebnisses
	 */
	@Override
	public String toString() {
		return "QuizErgebnisDTO{id=" + id + ", themaId=" + themaId + ", frageId=" + frageId + ", antwortRichtig="
				+ antwortRichtig + ", punkte=" + punkte + "}";
	}
}
