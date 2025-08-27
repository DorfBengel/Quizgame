package data.repository;

import java.util.List;
import java.util.Optional;

import data.dto.AntwortDTO;
import data.dto.FrageDTO;
import data.dto.QuizErgebnisDTO;
import data.dto.StatistikDTO;
import data.dto.ThemaDTO;

/**
 * Repository-Interface für den Datenzugriff auf Quiz-Daten.
 * 
 * <p>Dieses Interface definiert den Vertrag für alle Repository-Implementierungen
 * in der Quiz-Anwendung. Es implementiert das Repository-Pattern und stellt
 * eine einheitliche API für den Datenzugriff bereit.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Themen-Verwaltung (CRUD-Operationen)</li>
 *   <li>Fragen-Verwaltung (CRUD-Operationen)</li>
 *   <li>Antworten-Verwaltung (CRUD-Operationen)</li>
 *   <li>Quiz-Ergebnis-Verwaltung</li>
 *   <li>Statistik-Berechnung und -Abruf</li>
 * </ul>
 * 
 * <p>Implementierungen:</p>
 * <ul>
 *   <li>JDBCRepository - Für SQLite und MariaDB</li>
 *   <li>LokalRepository - Für lokale Dateispeicherung</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * QuizRepository repository = RepositoryFactory.createRepository();
 * 
 * // Themen verwalten
 * List<ThemaDTO> themen = repository.findAllThemen();
 * ThemaDTO thema = repository.saveThema(newThema);
 * 
 * // Fragen verwalten
 * List<FrageDTO> fragen = repository.findFragenByThemaId(themaId);
 * FrageDTO frage = repository.saveFrage(frageDTO, themaId);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see data.persistence.JDBCRepository
 * @see data.persistence.LokalRepository
 * @see data.persistence.RepositoryFactory
 */
public interface QuizRepository {

	/**
	 * Findet alle verfügbaren Themen.
	 * 
	 * @return Liste aller Themen, sortiert nach Titel
	 */
	List<ThemaDTO> findAllThemen();

	/**
	 * Findet ein Thema nach seiner ID.
	 * 
	 * @param id Die ID des gesuchten Themas
	 * @return Optional mit dem gefundenen Thema oder leer wenn nicht gefunden
	 */
	Optional<ThemaDTO> findThemaById(long id);

	/**
	 * Findet ein Thema nach seinem Titel.
	 * 
	 * @param titel Der Titel des gesuchten Themas
	 * @return Optional mit dem gefundenen Thema oder leer wenn nicht gefunden
	 */
	Optional<ThemaDTO> findThemaByTitel(String titel);

	/**
	 * Speichert ein Thema (erstellt neu oder aktualisiert bestehendes).
	 * 
	 * @param thema Das zu speichernde Thema
	 * @return Das gespeicherte Thema mit aktualisierter ID
	 */
	ThemaDTO saveThema(ThemaDTO thema);

	/**
	 * Löscht ein Thema nach seiner ID.
	 * 
	 * @param id Die ID des zu löschenden Themas
	 */
	void deleteThema(long id);

	/**
	 * Prüft, ob bereits ein Thema mit dem angegebenen Titel existiert.
	 * 
	 * @param titel Der zu prüfende Titel
	 * @return true wenn ein Thema mit diesem Titel existiert, false sonst
	 */
	boolean existsThemaWithTitel(String titel);

	/**
	 * Findet alle Fragen für ein bestimmtes Thema.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste aller Fragen des Themas, sortiert nach Titel
	 */
	List<FrageDTO> findFragenByThemaId(long themaId);

	/**
	 * Findet alle Fragen für ein Thema nach Namen.
	 * 
	 * @param themaName Der Name des Themas
	 * @return Liste aller Fragen des Themas
	 */
	List<FrageDTO> findFragenByThemaName(String themaName);

	/**
	 * Findet eine Frage nach ihrer ID.
	 * 
	 * @param id Die ID der gesuchten Frage
	 * @return Optional mit der gefundenen Frage oder leer wenn nicht gefunden
	 */
	Optional<FrageDTO> findFrageById(long id);

	/**
	 * Findet eine Frage nach Titel und Thema.
	 * 
	 * @param titel Der Titel der gesuchten Frage
	 * @param themaId Die ID des Themas
	 * @return Optional mit der gefundenen Frage oder leer wenn nicht gefunden
	 */
	Optional<FrageDTO> findFrageByTitel(String titel, long themaId);

	/**
	 * Speichert eine Frage (erstellt neu oder aktualisiert bestehende).
	 * 
	 * @param frage Die zu speichernde Frage
	 * @param themaId Die ID des Themas, zu dem die Frage gehört
	 * @return Die gespeicherte Frage mit aktualisierter ID
	 */
	FrageDTO saveFrage(FrageDTO frage, long themaId);

	/**
	 * Löscht eine Frage nach ihrer ID.
	 * 
	 * @param id Die ID der zu löschenden Frage
	 */
	void deleteFrage(long id);

	/**
	 * Prüft, ob bereits eine Frage mit dem angegebenen Titel im Thema existiert.
	 * 
	 * @param titel Der zu prüfende Titel
	 * @param themaId Die ID des Themas
	 * @return true wenn eine Frage mit diesem Titel im Thema existiert, false sonst
	 */
	boolean existsFrageWithTitel(String titel, long themaId);

	/**
	 * Findet alle Antworten für eine bestimmte Frage.
	 * 
	 * @param frageId Die ID der Frage
	 * @return Liste aller Antworten der Frage
	 */
	List<AntwortDTO> findAntwortenByFrageId(long frageId);

	/**
	 * Speichert eine Antwort (erstellt neu oder aktualisiert bestehende).
	 * 
	 * @param antwort Die zu speichernde Antwort
	 * @param frageId Die ID der Frage, zu der die Antwort gehört
	 * @return Die gespeicherte Antwort mit aktualisierter ID
	 */
	AntwortDTO saveAntwort(AntwortDTO antwort, long frageId);

	/**
	 * Löscht eine Antwort nach ihrer ID.
	 * 
	 * @param id Die ID der zu löschenden Antwort
	 */
	void deleteAntwort(long id);

	/**
	 * Speichert ein Quiz-Ergebnis.
	 * 
	 * @param ergebnis Das zu speichernde Quiz-Ergebnis
	 */
	void saveQuizErgebnis(QuizErgebnisDTO ergebnis);

	/**
	 * Findet alle Quiz-Ergebnisse für ein bestimmtes Thema.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste aller Quiz-Ergebnisse des Themas, sortiert nach Zeitstempel
	 */
	List<QuizErgebnisDTO> findQuizErgebnisseByThemaId(long themaId);

	/**
	 * Findet alle Quiz-Ergebnisse für eine bestimmte Frage.
	 * 
	 * @param frageId Die ID der Frage
	 * @return Liste aller Quiz-Ergebnisse der Frage, sortiert nach Zeitstempel
	 */
	List<QuizErgebnisDTO> findQuizErgebnisseByFrageId(long frageId);

	/**
	 * Findet Statistiken für alle Fragen eines Themas.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste der Statistiken für alle Fragen des Themas
	 */
	List<StatistikDTO> findStatistikenByThemaId(long themaId);

	/**
	 * Findet alle verfügbaren Statistiken.
	 * 
	 * @return Liste aller Statistiken für alle Themen und Fragen
	 */
	List<StatistikDTO> findAlleStatistiken();
}
