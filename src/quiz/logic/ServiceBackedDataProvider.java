package quiz.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import business.FrageService;
import business.QuizApplication;
import business.ThemaService;
import business.event.EventManager;
import data.dto.AntwortDTO;
import data.dto.FrageDTO;
import data.dto.ThemaDTO;
import quiz.data.model.Antwort;
import quiz.data.model.Frage;
import quiz.data.model.Thema;
import quiz.logic.interfaces.QuizDataProvider;

/**
 * Adapter, der die neue Business-Schicht (Services) mit der bestehenden GUI-API
 * (QuizDataProvider) verbindet.
 */
public class ServiceBackedDataProvider implements QuizDataProvider {

	private final ThemaService themaService;
	private final FrageService frageService;
	private final EventManager eventManager;
	private final PropertyChangeSupport propertyChangeSupport;

	// Debouncing für GUI-Updates
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> themenUpdateTask;
	private ScheduledFuture<?> fragenUpdateTask;

	// Kein Caching - direkter Zugriff auf Services für bessere Konsistenz

	// Event-Properties für MVC-Entkopplung (kompatibel mit QuizController)
	public static final String THEMEN_CHANGED = "themenChanged";
	public static final String FRAGEN_CHANGED = "fragenChanged";

	public ServiceBackedDataProvider() {
		QuizApplication app = QuizApplication.getInstance();
		this.themaService = app.getThemaService();
		this.frageService = app.getFrageService();
		this.eventManager = app.getEventManager();
		this.propertyChangeSupport = new PropertyChangeSupport(this);

		// Event-Listener registrieren, um GUI-Updates zu triggern
		registerEventListeners();
	}

	// PropertyChangeSupport-Methoden für MVC-Entkopplung (kompatibel mit
	// QuizController)
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Registriert Event-Listener für automatische GUI-Updates.
	 */
	private void registerEventListeners() {
		// Generische Event-Listener für alle Event-Typen
		eventManager.addEventListener("THEMA_CREATED", event -> {
			System.out.println("ServiceBackedDataProvider: THEMA_CREATED Event empfangen");
			scheduleThemenUpdate();
		});

		eventManager.addEventListener("THEMA_UPDATED", event -> {
			System.out.println("ServiceBackedDataProvider: THEMA_UPDATED Event empfangen");
			scheduleThemenUpdate();
		});

		eventManager.addEventListener("THEMA_DELETED", event -> {
			System.out.println("ServiceBackedDataProvider: THEMA_DELETED Event empfangen");
			scheduleThemenUpdate();
		});

		eventManager.addEventListener("FRAGE_CREATED", event -> {
			System.out.println("ServiceBackedDataProvider: FRAGE_CREATED Event empfangen");
			scheduleFragenUpdate();
		});

		eventManager.addEventListener("FRAGE_UPDATED", event -> {
			System.out.println("ServiceBackedDataProvider: FRAGE_UPDATED Event empfangen");
			// Bei Fragen-Updates auch Themen neu laden, da Fragen in Themen eingebettet sind
			scheduleThemenUpdate();
			scheduleFragenUpdate();
		});

		eventManager.addEventListener("FRAGE_DELETED", event -> {
			System.out.println("ServiceBackedDataProvider: FRAGE_DELETED Event empfangen");
			scheduleFragenUpdate();
		});
	}

	/**
	 * Plant ein Themen-Update mit Debouncing (vermeidet redundante Updates).
	 */
	private void scheduleThemenUpdate() {
		if (themenUpdateTask != null && !themenUpdateTask.isDone()) {
			themenUpdateTask.cancel(false);
		}

		themenUpdateTask = scheduler.schedule(() -> {
			System.out.println("ServiceBackedDataProvider: Führe verzögertes Themen-Update aus");
			SwingUtilities
					.invokeLater(() -> propertyChangeSupport.firePropertyChange(THEMEN_CHANGED, null, getAlleThemen()));
		}, 150, TimeUnit.MILLISECONDS);
	}

	/**
	 * Plant ein Fragen-Update mit Debouncing (vermeidet redundante Updates).
	 */
	private void scheduleFragenUpdate() {
		if (fragenUpdateTask != null && !fragenUpdateTask.isDone()) {
			fragenUpdateTask.cancel(false);
		}

		fragenUpdateTask = scheduler.schedule(() -> {
			System.out.println("ServiceBackedDataProvider: Führe verzögertes Fragen-Update aus");
			SwingUtilities.invokeLater(() -> propertyChangeSupport.firePropertyChange(FRAGEN_CHANGED, null, null));
		}, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * Beendet den Service und gibt Ressourcen frei.
	 */
	public void shutdown() {
		if (themenUpdateTask != null && !themenUpdateTask.isDone()) {
			themenUpdateTask.cancel(false);
		}
		if (fragenUpdateTask != null && !fragenUpdateTask.isDone()) {
			fragenUpdateTask.cancel(false);
		}
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public List<Thema> getAlleThemen() {
		System.out.println("ServiceBackedDataProvider: Lade alle Themen von Service...");
		List<ThemaDTO> themenDTO = themaService.findAllThemen();
		System.out.println("ServiceBackedDataProvider: " + themenDTO.size() + " Themen aus Service geladen");

		List<Thema> themen = new ArrayList<>();
		for (ThemaDTO t : themenDTO) {
			Thema thema = mapToThema(t);
			// Fragen zu diesem Thema nachladen
			List<FrageDTO> fragenDTO = frageService.findFragenByThemaId(t.getId());
			thema.setFragen(fragenDTO.stream().map(this::mapToFrage).collect(Collectors.toList()));
			themen.add(thema);
		}

		System.out.println("ServiceBackedDataProvider: " + themen.size() + " Themen für GUI aufbereitet");
		return themen;
	}

	@Override
	public List<Frage> findeFragenFuerThema(long themaId) {
		// Fragen direkt aus Service laden
		List<FrageDTO> fragenDTO = frageService.findFragenByThemaId(themaId);
		List<Frage> fragen = fragenDTO.stream().map(this::mapToFrage).collect(Collectors.toList());
		
		System.out.println("ServiceBackedDataProvider: " + fragen.size() + " Fragen für Thema " + themaId + " geladen");
		return fragen;
	}

	@Override
	public void speichereThema(String titel, String information, boolean forceOverwrite) {
		// Prüfen, ob Titel existiert
		var vorhanden = themaService.findThemaByTitel(titel);
		if (vorhanden.isPresent()) {
			if (forceOverwrite) {
				ThemaDTO dto = vorhanden.get();
				themaService.updateThema(dto.getId(), titel, information);
			} else {
				// Verhalten wie bisher: Exception, wird in GUI abgefangen
				throw new IllegalArgumentException("Ein Thema mit diesem Titel existiert bereits.");
			}
		} else {
			themaService.createThema(titel, information);
		}
	}

	@Override
	public void updateThema(Thema thema) {
		if (thema == null) {
			return;
		}
		themaService.updateThema(thema.getId(), thema.getTitel(), thema.getInformation());
	}

	@Override
	public void loescheThema(Thema thema) {
		if (thema == null) {
			return;
		}
		themaService.deleteThema(thema.getId());
	}

	@Override
	public void loescheFrage(Frage frage) {
		if (frage == null) {
			return;
		}
		frageService.deleteFrage(frage.getId());
	}

	@Override
	public void speichereFrage(Frage frage, long themaId) {
		if (frage == null) {
			return;
		}
		List<AntwortDTO> antworten = new ArrayList<>();
		for (Antwort a : frage.getAntworten()) {
			AntwortDTO dto = new AntwortDTO(a.getId(), a.getText(), a.istRichtig());
			antworten.add(dto);
		}
		if (frage.getId() > 0) {
			frageService.updateFrage(frage.getId(), safe(frage.getFrageTitel()), safe(frage.getFrageText()), antworten,
					themaId);
		} else {
			frageService.createFrage(safe(frage.getFrageTitel()), safe(frage.getFrageText()), antworten, themaId);
		}
		
		// Kein Caching - Daten werden direkt aus dem Service geladen
	}

	// --- Mapping-Hilfen ---
	private Thema mapToThema(ThemaDTO dto) {
		Thema t = new Thema(dto.getTitel(), dto.getInformation());
		t.setId(dto.getId());
		return t;
	}

	private Frage mapToFrage(FrageDTO dto) {
		Frage f = new Frage(dto.getText());
		f.setId(dto.getId());
		f.setFrageTitel(dto.getTitel());
		List<Antwort> antworten = new ArrayList<>();
		for (AntwortDTO a : dto.getAntworten()) {
			Antwort model = new Antwort(a.getText(), a.istRichtig());
			model.setId(a.getId());
			antworten.add(model);
		}
		f.setAntworten(antworten);
		return f;
	}

	private String safe(String s) {
		return s == null ? "" : s;
	}
}
