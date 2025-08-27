package business;

import java.util.List;

import business.event.DataChangedEvent;
import business.event.EventManager;
import data.dto.QuizErgebnisDTO;
import data.dto.StatistikDTO;
import data.repository.QuizRepository;

/**
 * Service für Quiz-Statistiken.
 * 
 * <p>Diese Klasse implementiert die Geschäftslogik für die Verwaltung und Berechnung
 * von Quiz-Statistiken. Sie sammelt Quiz-Ergebnisse und berechnet aggregierte
 * Statistiken für verschiedene Analysezwecke.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Speichern von Quiz-Ergebnissen</li>
 *   <li>Berechnung von Themen-Statistiken</li>
 *   <li>Berechnung von Fragen-Statistiken</li>
 *   <li>Berechnung von Gesamtstatistiken</li>
 *   <li>Event-Benachrichtigung bei neuen Ergebnissen</li>
 * </ul>
 * 
 * <p>Statistik-Metriken:</p>
 * <ul>
 *   <li>Anzahl Versuche und Erfolgsrate</li>
 *   <li>Durchschnittliche Antwortzeiten</li>
 *   <li>Durchschnittliche und beste Punktzahlen</li>
 *   <li>Vergleiche zwischen verschiedenen Zeiträumen</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see FrageService
 * @see ThemaService
 * @see DataChangedEvent
 */
public class QuizStatistikService {

	private final QuizRepository repository;

	/**
	 * Erstellt einen neuen QuizStatistikService mit dem angegebenen Repository.
	 * 
	 * @param repository Das Repository für den Datenzugriff
	 */
	public QuizStatistikService(QuizRepository repository) {
		this.repository = repository;
	}

	/**
	 * Speichert ein Quiz-Ergebnis.
	 * 
	 * <p>Die Methode speichert das Ergebnis in der Datenbank und feuert ein Event
	 * zur Benachrichtigung anderer Komponenten über die neue Statistik.</p>
	 * 
	 * @param themaId Die ID des Themas
	 * @param frageId Die ID der Frage
	 * @param antwortRichtig Ob die Antwort richtig war
	 * @param antwortVorherGezeigt Ob die Antwort vorher angezeigt wurde
	 * @param antwortZeitSekunden Die Antwortzeit in Sekunden
	 * @param punkte Die erreichten Punkte
	 */
	public void saveQuizErgebnis(long themaId, long frageId, boolean antwortRichtig, boolean antwortVorherGezeigt,
			int antwortZeitSekunden, int punkte) {
		QuizErgebnisDTO ergebnis = new QuizErgebnisDTO(themaId, frageId, antwortRichtig, antwortVorherGezeigt,
				antwortZeitSekunden, punkte);

		repository.saveQuizErgebnis(ergebnis);

		// Event feuern
		DataChangedEvent event = new DataChangedEvent("QuizStatistikService", DataChangedEvent.ChangeType.CREATED,
				DataChangedEvent.EntityType.QUIZ_ERGEBNIS, ergebnis);
		EventManager.getInstance().fireEvent(event);
	}

	/**
	 * Findet alle Quiz-Ergebnisse für ein Thema.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste aller Quiz-Ergebnisse für das Thema
	 */
	public List<QuizErgebnisDTO> findQuizErgebnisseByThemaId(long themaId) {
		return repository.findQuizErgebnisseByThemaId(themaId);
	}

	/**
	 * Findet alle Quiz-Ergebnisse für eine Frage.
	 * 
	 * @param frageId Die ID der Frage
	 * @return Liste aller Quiz-Ergebnisse für die Frage
	 */
	public List<QuizErgebnisDTO> findQuizErgebnisseByFrageId(long frageId) {
		return repository.findQuizErgebnisseByFrageId(frageId);
	}

	/**
	 * Findet Statistiken für ein Thema.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste der Statistiken für alle Fragen des Themas
	 */
	public List<StatistikDTO> findStatistikenByThemaId(long themaId) {
		return repository.findStatistikenByThemaId(themaId);
	}

	/**
	 * Findet alle Statistiken.
	 * 
	 * @return Liste aller verfügbaren Statistiken
	 */
	public List<StatistikDTO> findAlleStatistiken() {
		return repository.findAlleStatistiken();
	}

	/**
	 * Berechnet detaillierte Statistiken für ein Thema.
	 * 
	 * <p>Diese Methode aggregiert alle Quiz-Ergebnisse eines Themas und berechnet
	 * umfassende Statistiken wie Erfolgsrate, durchschnittliche Antwortzeiten
	 * und Punktzahlen.</p>
	 * 
	 * @param themaId Die ID des Themas
	 * @param themaTitel Der Titel des Themas für die Anzeige
	 * @return Eine StatistikDTO mit allen aggregierten Daten des Themas
	 */
	public StatistikDTO berechneThemaStatistik(long themaId, String themaTitel) {
		List<QuizErgebnisDTO> ergebnisse = findQuizErgebnisseByThemaId(themaId);

		if (ergebnisse.isEmpty()) {
			return new StatistikDTO(themaId, themaTitel, 0, "");
		}

		// Aggregierte Statistiken berechnen
		int gesamtVersuche = ergebnisse.size();
		int gesamtRichtig = (int) ergebnisse.stream().filter(QuizErgebnisDTO::isAntwortRichtig).count();
		int gesamtFalsch = gesamtVersuche - gesamtRichtig;

		double durchschnittZeit = ergebnisse.stream().mapToInt(QuizErgebnisDTO::getAntwortZeitSekunden).average()
				.orElse(0.0);

		double durchschnittPunkte = ergebnisse.stream().mapToInt(QuizErgebnisDTO::getPunkte).average().orElse(0.0);

		int bestePunkte = ergebnisse.stream().mapToInt(QuizErgebnisDTO::getPunkte).max().orElse(0);

		// Statistik erstellen
		StatistikDTO statistik = new StatistikDTO(themaId, themaTitel, 0, "");
		statistik.setAnzahlVersuche(gesamtVersuche);
		statistik.setAnzahlRichtig(gesamtRichtig);
		statistik.setAnzahlFalsch(gesamtFalsch);
		statistik.setDurchschnittlicheAntwortZeit(durchschnittZeit);
		statistik.setDurchschnittlichePunkte((int) durchschnittPunkte);
		statistik.setBestePunkte(bestePunkte);
		statistik.berechneErfolgsRate();

		return statistik;
	}

	/**
	 * Berechnet detaillierte Statistiken für eine Frage.
	 * 
	 * <p>Diese Methode aggregiert alle Quiz-Ergebnisse einer spezifischen Frage
	 * und berechnet detaillierte Statistiken für die Analyse der Fragequalität.</p>
	 * 
	 * @param themaId Die ID des Themas
	 * @param themaTitel Der Titel des Themas
	 * @param frageId Die ID der Frage
	 * @param frageTitel Der Titel der Frage
	 * @return Eine StatistikDTO mit allen aggregierten Daten der Frage
	 */
	public StatistikDTO berechneFrageStatistik(long themaId, String themaTitel, long frageId, String frageTitel) {
		List<QuizErgebnisDTO> ergebnisse = findQuizErgebnisseByFrageId(frageId);

		if (ergebnisse.isEmpty()) {
			return new StatistikDTO(themaId, themaTitel, frageId, frageTitel);
		}

		// Statistik mit allen Ergebnissen erstellen
		StatistikDTO statistik = new StatistikDTO(themaId, themaTitel, frageId, frageTitel);

		for (QuizErgebnisDTO ergebnis : ergebnisse) {
			statistik.addErgebnis(ergebnis.isAntwortRichtig(), ergebnis.getAntwortZeitSekunden(), ergebnis.getPunkte());
		}

		return statistik;
	}

	/**
	 * Berechnet die Gesamtstatistik für alle Themen.
	 * 
	 * <p>Diese Methode aggregiert alle verfügbaren Quiz-Ergebnisse über alle Themen
	 * hinweg und berechnet eine umfassende Gesamtstatistik der Quiz-Anwendung.</p>
	 * 
	 * @return Eine StatistikDTO mit allen aggregierten Daten aller Themen
	 */
	public StatistikDTO berechneGesamtStatistik() {
		List<QuizErgebnisDTO> alleErgebnisse = repository.findAlleStatistiken().stream()
				.flatMap(stat -> findQuizErgebnisseByFrageId(stat.getFrageId()).stream()).toList();

		if (alleErgebnisse.isEmpty()) {
			return new StatistikDTO(0, "Alle Themen", 0, "");
		}

		// Aggregierte Statistiken berechnen
		int gesamtVersuche = alleErgebnisse.size();
		int gesamtRichtig = (int) alleErgebnisse.stream().filter(QuizErgebnisDTO::isAntwortRichtig).count();
		int gesamtFalsch = gesamtVersuche - gesamtRichtig;

		double durchschnittZeit = alleErgebnisse.stream().mapToInt(QuizErgebnisDTO::getAntwortZeitSekunden).average()
				.orElse(0.0);

		double durchschnittPunkte = alleErgebnisse.stream().mapToInt(QuizErgebnisDTO::getPunkte).average().orElse(0.0);

		int bestePunkte = alleErgebnisse.stream().mapToInt(QuizErgebnisDTO::getPunkte).max().orElse(0);

		// Statistik erstellen
		StatistikDTO statistik = new StatistikDTO(0, "Alle Themen", 0, "");
		statistik.setAnzahlVersuche(gesamtVersuche);
		statistik.setAnzahlRichtig(gesamtRichtig);
		statistik.setAnzahlFalsch(gesamtFalsch);
		statistik.setDurchschnittlicheAntwortZeit(durchschnittZeit);
		statistik.setDurchschnittlichePunkte((int) durchschnittPunkte);
		statistik.setBestePunkte(bestePunkte);
		statistik.berechneErfolgsRate();

		return statistik;
	}
}
