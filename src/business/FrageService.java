package business;

import java.util.List;
import java.util.Optional;

import business.event.DataChangedEvent;
import business.event.EventManager;
import data.dto.AntwortDTO;
import data.dto.FrageDTO;
import data.repository.QuizRepository;
import exception.ValidationException;

/**
 * Service für die Fragen-Geschäftslogik.
 * 
 * <p>Diese Klasse implementiert die Geschäftsregeln für die Verwaltung von Quiz-Fragen.
 * Sie stellt eine Abstraktionsschicht zwischen der GUI und der Datenpersistierung dar
 * und validiert alle Eingabedaten vor dem Speichern.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Erstellen, Aktualisieren und Löschen von Fragen</li>
 *   <li>Validierung von Fragen- und Antwortdaten</li>
 *   <li>Eindeutigkeitsprüfung von Fragetiteln innerhalb eines Themas</li>
 *   <li>Event-Benachrichtigung bei Datenänderungen</li>
 * </ul>
 * 
 * <p>Validierungsregeln:</p>
 * <ul>
 *   <li>Titel: 1-100 Zeichen, nicht leer</li>
 *   <li>Text: 1-500 Zeichen, nicht leer</li>
 *   <li>Antworten: Mindestens eine Antwort erforderlich</li>
 *   <li>Richtige Antworten: Mindestens eine muss als richtig markiert sein</li>
 *   <li>Antworttext: 1-200 Zeichen, nicht leer wenn als richtig markiert</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see ThemaService
 * @see QuizStatistikService
 * @see DataChangedEvent
 */
public class FrageService {

	private final QuizRepository repository;

	/**
	 * Erstellt einen neuen FrageService mit dem angegebenen Repository.
	 * 
	 * @param repository Das Repository für den Datenzugriff
	 */
	public FrageService(QuizRepository repository) {
		this.repository = repository;
	}

	/**
	 * Erstellt eine neue Frage.
	 * 
	 * <p>Die Methode validiert alle Eingabedaten, prüft auf Eindeutigkeit des Titels
	 * und speichert die Frage in der Datenbank. Bei erfolgreicher Erstellung wird
	 * ein Event gefeuert.</p>
	 * 
	 * @param titel Der Titel der Frage (1-100 Zeichen)
	 * @param text Der Fragetext (1-500 Zeichen)
	 * @param antworten Liste der Antworten (mindestens eine erforderlich)
	 * @param themaId Die ID des Themas, zu dem die Frage gehört
	 * @return Die erstellte Frage mit generierter ID
	 * @throws ValidationException wenn die Validierung fehlschlägt
	 * @throws ValidationException wenn bereits eine Frage mit diesem Titel im Thema existiert
	 */
	public FrageDTO createFrage(String titel, String text, List<AntwortDTO> antworten, long themaId) {
		validateFrageData(titel, text, antworten);

		if (repository.existsFrageWithTitel(titel, themaId)) {
			throw new ValidationException(
					"Eine Frage mit dem Titel '" + titel + "' existiert bereits in diesem Thema.");
		}

		FrageDTO frage = new FrageDTO(0, titel, text, "");
		frage.setAntworten(antworten);

		frage = repository.saveFrage(frage, themaId);

		// Generisches Event feuern
		DataChangedEvent event = new DataChangedEvent("FrageService", DataChangedEvent.ChangeType.CREATED,
				DataChangedEvent.EntityType.FRAGE, frage);
		EventManager.getInstance().fireEvent(event);

		return frage;
	}

	/**
	 * Aktualisiert eine bestehende Frage.
	 * 
	 * <p>Die Methode validiert alle Eingabedaten, prüft auf Eindeutigkeit des Titels
	 * und aktualisiert die Frage in der Datenbank. Bei erfolgreicher Aktualisierung
	 * wird ein Event gefeuert.</p>
	 * 
	 * @param id Die ID der zu aktualisierenden Frage
	 * @param titel Der neue Titel der Frage
	 * @param text Der neue Fragetext
	 * @param antworten Die neuen Antworten
	 * @param themaId Die ID des Themas
	 * @return Die aktualisierte Frage
	 * @throws ValidationException wenn die Frage nicht gefunden wird
	 * @throws ValidationException wenn der neue Titel bereits von einer anderen Frage verwendet wird
	 */
	public FrageDTO updateFrage(long id, String titel, String text, List<AntwortDTO> antworten, long themaId) {
		validateFrageData(titel, text, antworten);

		Optional<FrageDTO> existing = repository.findFrageById(id);
		if (existing.isEmpty()) {
			throw new ValidationException("Frage mit ID " + id + " nicht gefunden.");
		}

		// Prüfen, ob der neue Titel bereits von einer anderen Frage in diesem Thema
		// verwendet wird
		Optional<FrageDTO> titelConflict = repository.findFrageByTitel(titel, themaId);
		if (titelConflict.isPresent() && titelConflict.get().getId() != id) {
			throw new ValidationException(
					"Eine andere Frage in diesem Thema verwendet bereits den Titel '" + titel + "'.");
		}

		FrageDTO frage = existing.get();
		frage.setTitel(titel);
		frage.setText(text);
		frage.setAntworten(antworten);

		frage = repository.saveFrage(frage, themaId);

		// Generisches Event feuern
		DataChangedEvent event = new DataChangedEvent("FrageService", DataChangedEvent.ChangeType.UPDATED,
				DataChangedEvent.EntityType.FRAGE, frage);
		EventManager.getInstance().fireEvent(event);

		return frage;
	}

	/**
	 * Löscht eine Frage.
	 * 
	 * <p>Die Methode prüft zuerst, ob die Frage existiert, feuert dann ein Event
	 * und löscht sie schließlich aus der Datenbank.</p>
	 * 
	 * @param id Die ID der zu löschenden Frage
	 * @throws ValidationException wenn die Frage nicht gefunden wird
	 */
	public void deleteFrage(long id) {
		Optional<FrageDTO> frage = repository.findFrageById(id);
		if (frage.isEmpty()) {
			throw new ValidationException("Frage mit ID " + id + " nicht gefunden.");
		}

		// Event vor dem Löschen feuern
		DataChangedEvent event = new DataChangedEvent("FrageService", DataChangedEvent.ChangeType.DELETED,
				DataChangedEvent.EntityType.FRAGE, frage.get());
		EventManager.getInstance().fireEvent(event);

		repository.deleteFrage(id);
	}

	/**
	 * Findet alle Fragen eines Themas.
	 * 
	 * @param themaId Die ID des Themas
	 * @return Liste aller Fragen des Themas
	 */
	public List<FrageDTO> findFragenByThemaId(long themaId) {
		return repository.findFragenByThemaId(themaId);
	}

	/**
	 * Findet alle Fragen eines Themas nach Namen.
	 * 
	 * @param themaName Der Name des Themas
	 * @return Liste aller Fragen des Themas
	 */
	public List<FrageDTO> findFragenByThemaName(String themaName) {
		return repository.findFragenByThemaName(themaName);
	}

	/**
	 * Findet eine Frage nach ID.
	 * 
	 * @param id Die ID der Frage
	 * @return Optional mit der gefundenen Frage oder leer wenn nicht gefunden
	 */
	public Optional<FrageDTO> findFrageById(long id) {
		return repository.findFrageById(id);
	}

	/**
	 * Validiert Fragen-Daten.
	 * 
	 * <p>Diese private Methode implementiert alle Validierungsregeln für Fragen.
	 * Sie wird vor dem Speichern aufgerufen, um die Datenintegrität zu gewährleisten.</p>
	 * 
	 * @param titel Der zu validierende Titel
	 * @param text Der zu validierende Fragetext
	 * @param antworten Die zu validierenden Antworten
	 * @throws ValidationException wenn die Validierung fehlschlägt
	 */
	private void validateFrageData(String titel, String text, List<AntwortDTO> antworten) {
		if (titel == null || titel.trim().isEmpty()) {
			throw new ValidationException("Der Fragetitel darf nicht leer sein.");
		}

		if (titel.trim().length() > 100) {
			throw new ValidationException("Der Fragetitel darf maximal 100 Zeichen lang sein.");
		}

		if (text == null || text.trim().isEmpty()) {
			throw new ValidationException("Der Fragetext darf nicht leer sein.");
		}

		if (text.trim().length() > 500) {
			throw new ValidationException("Der Fragetext darf maximal 500 Zeichen lang sein.");
		}

		if (antworten == null || antworten.isEmpty()) {
			throw new ValidationException("Bitte geben Sie mindestens eine Antwort ein.");
		}

		boolean hatMindestensEineRichtige = false;
		for (AntwortDTO antwort : antworten) {
			if (antwort.getText() == null || antwort.getText().trim().isEmpty()) {
				if (antwort.istRichtig()) {
					throw new ValidationException(
							"Eine als richtig markierte Antwort ist leer. Bitte Text eingeben oder Markierung entfernen.");
				}
			} else {
				if (antwort.getText().trim().length() > 200) {
					throw new ValidationException("Antworttext darf maximal 200 Zeichen lang sein.");
				}
				if (antwort.istRichtig()) {
					hatMindestensEineRichtige = true;
				}
			}
		}

		if (!hatMindestensEineRichtige) {
			throw new ValidationException("Bitte markieren Sie mindestens eine Antwort als richtig.");
		}
	}
}
