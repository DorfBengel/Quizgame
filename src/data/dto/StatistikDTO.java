package data.dto;

import java.io.Serializable;

/**
 * DTO für aggregierte Quiz-Statistiken.
 * 
 * <p>Diese Klasse repräsentiert aggregierte Quiz-Statistiken als Data Transfer Object.
 * Sie implementiert Serializable für die Persistierung und dient der Übertragung
 * von Statistikdaten zwischen verschiedenen Schichten der Anwendung.</p>
 * 
 * <p>Statistik-Metriken:</p>
 * <ul>
 *   <li>Anzahl Versuche und Erfolgsrate</li>
 *   <li>Richtige und falsche Antworten</li>
 *   <li>Durchschnittliche Antwortzeiten</li>
 *   <li>Durchschnittliche und beste Punktzahlen</li>
 *   <li>Thema- und Fragen-Referenzen</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * StatistikDTO statistik = new StatistikDTO(1L, "Java", 2L, "Was ist Java?");
 * statistik.addErgebnis(true, 30, 10);
 * statistik.addErgebnis(false, 45, 0);
 * statistik.berechneErfolgsRate();
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizErgebnisDTO
 * @see ThemaDTO
 * @see FrageDTO
 * @see Serializable
 */
public class StatistikDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long themaId;
	private String themaTitel;
	private long frageId;
	private String frageTitel;
	private int anzahlVersuche;
	private int anzahlRichtig;
	private int anzahlFalsch;
	private double durchschnittlicheAntwortZeit;
	private double erfolgsRate;
	private int durchschnittlichePunkte;
	private int bestePunkte;

	/**
	 * Standard-Konstruktor.
	 * 
	 * <p>Erstellt eine leere StatistikDTO-Instanz. Alle numerischen Felder werden auf 0
	 * gesetzt und alle String-Felder auf null.</p>
	 */
	public StatistikDTO() {
	}

	/**
	 * Konstruktor mit Thema- und Fragen-Referenzen.
	 * 
	 * @param themaId Die ID des Themas
	 * @param themaTitel Der Titel des Themas
	 * @param frageId Die ID der Frage (0 für Themen-Statistiken)
	 * @param frageTitel Der Titel der Frage (leer für Themen-Statistiken)
	 */
	public StatistikDTO(long themaId, String themaTitel, long frageId, String frageTitel) {
		this.themaId = themaId;
		this.themaTitel = themaTitel;
		this.frageId = frageId;
		this.frageTitel = frageTitel;
	}

	/**
	 * Gibt die ID des Themas zurück.
	 * 
	 * @return Die ID des Themas
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
	 * Gibt den Titel des Themas zurück.
	 * 
	 * @return Der Titel des Themas
	 */
	public String getThemaTitel() {
		return themaTitel;
	}

	/**
	 * Setzt den Titel des Themas.
	 * 
	 * @param themaTitel Der neue Titel des Themas
	 */
	public void setThemaTitel(String themaTitel) {
		this.themaTitel = themaTitel;
	}

	/**
	 * Gibt die ID der Frage zurück.
	 * 
	 * @return Die ID der Frage (0 für Themen-Statistiken)
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
	 * Gibt den Titel der Frage zurück.
	 * 
	 * @return Der Titel der Frage (leer für Themen-Statistiken)
	 */
	public String getFrageTitel() {
		return frageTitel;
	}

	/**
	 * Setzt den Titel der Frage.
	 * 
	 * @param frageTitel Der neue Titel der Frage
	 */
	public void setFrageTitel(String frageTitel) {
		this.frageTitel = frageTitel;
	}

	/**
	 * Gibt die Anzahl der Versuche zurück.
	 * 
	 * @return Die Gesamtanzahl der Versuche
	 */
	public int getAnzahlVersuche() {
		return anzahlVersuche;
	}

	/**
	 * Setzt die Anzahl der Versuche.
	 * 
	 * @param anzahlVersuche Die neue Anzahl der Versuche
	 */
	public void setAnzahlVersuche(int anzahlVersuche) {
		this.anzahlVersuche = anzahlVersuche;
	}

	/**
	 * Gibt die Anzahl der richtigen Antworten zurück.
	 * 
	 * @return Die Anzahl der richtigen Antworten
	 */
	public int getAnzahlRichtig() {
		return anzahlRichtig;
	}

	/**
	 * Setzt die Anzahl der richtigen Antworten.
	 * 
	 * @param anzahlRichtig Die neue Anzahl der richtigen Antworten
	 */
	public void setAnzahlRichtig(int anzahlRichtig) {
		this.anzahlRichtig = anzahlRichtig;
	}

	/**
	 * Gibt die Anzahl der falschen Antworten zurück.
	 * 
	 * @return Die Anzahl der falschen Antworten
	 */
	public int getAnzahlFalsch() {
		return anzahlFalsch;
	}

	/**
	 * Setzt die Anzahl der falschen Antworten.
	 * 
	 * @param anzahlFalsch Die neue Anzahl der falschen Antworten
	 */
	public void setAnzahlFalsch(int anzahlFalsch) {
		this.anzahlFalsch = anzahlFalsch;
	}

	/**
	 * Gibt die durchschnittliche Antwortzeit zurück.
	 * 
	 * @return Die durchschnittliche Antwortzeit in Sekunden
	 */
	public double getDurchschnittlicheAntwortZeit() {
		return durchschnittlicheAntwortZeit;
	}

	/**
	 * Setzt die durchschnittliche Antwortzeit.
	 * 
	 * @param durchschnittlicheAntwortZeit Die neue durchschnittliche Antwortzeit in Sekunden
	 */
	public void setDurchschnittlicheAntwortZeit(double durchschnittlicheAntwortZeit) {
		this.durchschnittlicheAntwortZeit = durchschnittlicheAntwortZeit;
	}

	/**
	 * Gibt die Erfolgsrate zurück.
	 * 
	 * @return Die Erfolgsrate in Prozent (0.0 bis 100.0)
	 */
	public double getErfolgsRate() {
		return erfolgsRate;
	}

	/**
	 * Setzt die Erfolgsrate.
	 * 
	 * @param erfolgsRate Die neue Erfolgsrate in Prozent
	 */
	public void setErfolgsRate(double erfolgsRate) {
		this.erfolgsRate = erfolgsRate;
	}

	/**
	 * Gibt die durchschnittlichen Punkte zurück.
	 * 
	 * @return Die durchschnittlichen Punkte pro Versuch
	 */
	public int getDurchschnittlichePunkte() {
		return durchschnittlichePunkte;
	}

	/**
	 * Setzt die durchschnittlichen Punkte.
	 * 
	 * @param durchschnittlichePunkte Die neuen durchschnittlichen Punkte
	 */
	public void setDurchschnittlichePunkte(int durchschnittlichePunkte) {
		this.durchschnittlichePunkte = durchschnittlichePunkte;
	}

	/**
	 * Gibt die besten Punkte zurück.
	 * 
	 * @return Die höchsten erreichten Punkte
	 */
	public int getBestePunkte() {
		return bestePunkte;
	}

	/**
	 * Setzt die besten Punkte.
	 * 
	 * @param bestePunkte Die neuen besten Punkte
	 */
	public void setBestePunkte(int bestePunkte) {
		this.bestePunkte = bestePunkte;
	}

	/**
	 * Berechnet die Erfolgsrate basierend auf den aktuellen Daten.
	 * 
	 * <p>Die Erfolgsrate wird als Prozentsatz der richtigen Antworten
	 * im Verhältnis zur Gesamtanzahl der Versuche berechnet.</p>
	 */
	public void berechneErfolgsRate() {
		if (anzahlVersuche > 0) {
			this.erfolgsRate = (double) anzahlRichtig / anzahlVersuche * 100.0;
		} else {
			this.erfolgsRate = 0.0;
		}
	}

	/**
	 * Fügt ein neues Ergebnis hinzu und aktualisiert die Statistiken.
	 * 
	 * <p>Diese Methode aktualisiert alle aggregierten Werte basierend auf
	 * dem neuen Ergebnis. Sie wird für die inkrementelle Statistik-Berechnung verwendet.</p>
	 * 
	 * @param richtig Ob die Antwort richtig war
	 * @param antwortZeit Die Antwortzeit in Sekunden
	 * @param punkte Die erreichten Punkte
	 */
	public void addErgebnis(boolean richtig, int antwortZeit, int punkte) {
		anzahlVersuche++;
		if (richtig) {
			anzahlRichtig++;
		} else {
			anzahlFalsch++;
		}

		// Durchschnittliche Antwortzeit aktualisieren
		double gesamtZeit = durchschnittlicheAntwortZeit * (anzahlVersuche - 1) + antwortZeit;
		durchschnittlicheAntwortZeit = gesamtZeit / anzahlVersuche;

		// Durchschnittliche Punkte aktualisieren
		double gesamtPunkte = durchschnittlichePunkte * (anzahlVersuche - 1) + punkte;
		durchschnittlichePunkte = (int) (gesamtPunkte / anzahlVersuche);

		// Beste Punkte aktualisieren
		if (punkte > bestePunkte) {
			bestePunkte = punkte;
		}

		// Erfolgsrate neu berechnen
		berechneErfolgsRate();
	}

	/**
	 * Gibt eine String-Repräsentation der Statistik zurück.
	 * 
	 * <p>Das Format ist: "StatistikDTO{thema='X', frage='Y', erfolgsRate=Z%, versuche=N}"</p>
	 * 
	 * @return String-Repräsentation der Statistik
	 */
	@Override
	public String toString() {
		return "StatistikDTO{thema='" + themaTitel + "', frage='" + frageTitel + "', erfolgsRate="
				+ String.format("%.1f", erfolgsRate) + "%, versuche=" + anzahlVersuche + "}";
	}
}
