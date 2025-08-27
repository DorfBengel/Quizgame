package business;

import java.util.List;
import java.util.Optional;

import business.event.DataChangedEvent;
import business.event.EventManager;
import data.dto.ThemaDTO;
import data.repository.QuizRepository;
import exception.ValidationException;

/**
 * Service für die Themen-Geschäftslogik.
 * 
 * <p>Diese Klasse implementiert die Geschäftsregeln für die Verwaltung von Quiz-Themen.
 * Sie stellt eine Abstraktionsschicht zwischen der GUI und der Datenpersistierung dar
 * und validiert alle Eingabedaten vor dem Speichern.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Erstellen, Aktualisieren und Löschen von Themen</li>
 *   <li>Validierung von Themen-Daten</li>
 *   <li>Eindeutigkeitsprüfung von Them Titeln</li>
 *   <li>Event-Benachrichtigung bei Datenänderungen</li>
 * </ul>
 * 
 * <p>Validierungsregeln:</p>
 * <ul>
 *   <li>Titel: 1-100 Zeichen, nicht leer, eindeutig</li>
 *   <li>Information: Nicht leer</li>
 * </ul>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see FrageService
 * @see QuizStatistikService
 * @see DataChangedEvent
 */
public class ThemaService {

	private final QuizRepository repository;

	/**
	 * Erstellt einen neuen ThemaService mit dem angegebenen Repository.
	 * 
	 * @param repository Das Repository für den Datenzugriff
	 */
	public ThemaService(QuizRepository repository) {
		this.repository = repository;
	}

	/**
	 * Erstellt ein neues Thema.
	 * 
	 * <p>Die Methode validiert alle Eingabedaten, prüft auf Eindeutigkeit des Titels
	 * und speichert das Thema in der Datenbank. Bei erfolgreicher Erstellung wird
	 * ein Event gefeuert.</p>
	 * 
	 * @param titel Der Titel des Themas (1-100 Zeichen)
	 * @param information Die Beschreibung des Themas
	 * @return Das erstellte Thema mit generierter ID
	 * @throws ValidationException wenn die Validierung fehlschlägt
	 * @throws ValidationException wenn bereits ein Thema mit diesem Titel existiert
	 */
	public ThemaDTO createThema(String titel, String information) {
		validateThemaData(titel, information);

		if (repository.existsThemaWithTitel(titel)) {
			throw new ValidationException("Ein Thema mit dem Titel '" + titel + "' existiert bereits.");
		}

		ThemaDTO thema = new ThemaDTO(0, titel, information, 0);
		thema = repository.saveThema(thema);

		// Generisches Event feuern
		DataChangedEvent event = new DataChangedEvent("ThemaService", DataChangedEvent.ChangeType.CREATED,
				DataChangedEvent.EntityType.THEMA, thema);
		EventManager.getInstance().fireEvent(event);

		return thema;
	}

	/**
	 * Aktualisiert ein bestehendes Thema.
	 * 
	 * <p>Die Methode validiert alle Eingabedaten, prüft auf Eindeutigkeit des Titels
	 * und aktualisiert das Thema in der Datenbank. Bei erfolgreicher Aktualisierung
	 * wird ein Event gefeuert.</p>
	 * 
	 * @param id Die ID des zu aktualisierenden Themas
	 * @param titel Der neue Titel des Themas
	 * @param information Die neue Beschreibung des Themas
	 * @return Das aktualisierte Thema
	 * @throws ValidationException wenn das Thema nicht gefunden wird
	 * @throws ValidationException wenn der neue Titel bereits von einem anderen Thema verwendet wird
	 */
	public ThemaDTO updateThema(long id, String titel, String information) {
		validateThemaData(titel, information);

		Optional<ThemaDTO> existing = repository.findThemaById(id);
		if (existing.isEmpty()) {
			throw new ValidationException("Thema mit ID " + id + " nicht gefunden.");
		}

		// Prüfen, ob der neue Titel bereits von einem anderen Thema verwendet wird
		Optional<ThemaDTO> titelConflict = repository.findThemaByTitel(titel);
		if (titelConflict.isPresent() && titelConflict.get().getId() != id) {
			throw new ValidationException("Ein anderes Thema verwendet bereits den Titel '" + titel + "'.");
		}

		ThemaDTO thema = existing.get();
		thema.setTitel(titel);
		thema.setInformation(information);

		thema = repository.saveThema(thema);

		// Generisches Event feuern
		DataChangedEvent event = new DataChangedEvent("ThemaService", DataChangedEvent.ChangeType.UPDATED,
				DataChangedEvent.EntityType.THEMA, thema);
		EventManager.getInstance().fireEvent(event);

		return thema;
	}

	/**
	 * Löscht ein Thema.
	 * 
	 * <p>Die Methode prüft zuerst, ob das Thema existiert, feuert dann ein Event
	 * und löscht es schließlich aus der Datenbank. Alle zugehörigen Fragen werden
	 * ebenfalls gelöscht (CASCADE).</p>
	 * 
	 * @param id Die ID des zu löschenden Themas
	 * @throws ValidationException wenn das Thema nicht gefunden wird
	 */
	public void deleteThema(long id) {
		Optional<ThemaDTO> thema = repository.findThemaById(id);
		if (thema.isEmpty()) {
			throw new ValidationException("Thema mit ID " + id + " nicht gefunden.");
		}

		// Event vor dem Löschen feuern
		DataChangedEvent event = new DataChangedEvent("ThemaService", DataChangedEvent.ChangeType.DELETED,
				DataChangedEvent.EntityType.THEMA, thema.get());
		EventManager.getInstance().fireEvent(event);

		repository.deleteThema(id);
	}

	/**
	 * Findet alle Themen.
	 * 
	 * @return Liste aller verfügbaren Themen
	 */
	public List<ThemaDTO> findAllThemen() {
		return repository.findAllThemen();
	}

	/**
	 * Findet ein Thema nach ID.
	 * 
	 * @param id Die ID des Themas
	 * @return Optional mit dem gefundenen Thema oder leer wenn nicht gefunden
	 */
	public Optional<ThemaDTO> findThemaById(long id) {
		return repository.findThemaById(id);
	}

	/**
	 * Findet ein Thema nach Titel.
	 * 
	 * @param titel Der Titel des Themas
	 * @return Optional mit dem gefundenen Thema oder leer wenn nicht gefunden
	 */
	public Optional<ThemaDTO> findThemaByTitel(String titel) {
		return repository.findThemaByTitel(titel);
	}

	/**
	 * Validiert Themen-Daten.
	 * 
	 * <p>Diese private Methode implementiert alle Validierungsregeln für Themen.
	 * Sie wird vor dem Speichern aufgerufen, um die Datenintegrität zu gewährleisten.</p>
	 * 
	 * @param titel Der zu validierende Titel
	 * @param information Die zu validierende Information
	 * @throws ValidationException wenn die Validierung fehlschlägt
	 */
	private void validateThemaData(String titel, String information) {
		if (titel == null || titel.trim().isEmpty()) {
			throw new ValidationException("Der Titel darf nicht leer sein.");
		}

		if (titel.trim().length() > 100) {
			throw new ValidationException("Der Titel darf maximal 100 Zeichen lang sein.");
		}

		if (information == null || information.trim().isEmpty()) {
			throw new ValidationException("Die Information darf nicht leer sein.");
		}
	}
}
