package data.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import data.dto.AntwortDTO;
import data.dto.FrageDTO;
import data.dto.QuizErgebnisDTO;
import data.dto.StatistikDTO;
import data.dto.ThemaDTO;
import data.repository.QuizRepository;
import exception.PersistenceException;

/**
 * Repository-Implementierung für lokale Speicherung von Quiz-Daten.
 * Vollständig implementiert mit Thread-Sicherheit und optimierter Datenstruktur.
 */
public class LokalRepository implements QuizRepository {

	// Separate Listen für bessere Performance und einfachere Verwaltung
	private List<ThemaDTO> themenListe;
	private List<FrageDTO> fragenListe;
	private List<AntwortDTO> antwortenListe;
	private List<QuizErgebnisDTO> ergebnisseListe;
	
	// Thread-sichere Implementierung
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	// Dateinamen für verschiedene Datentypen
	private final String THEMEN_DATEINAME = "quiz_themen.ser";
	private final String FRAGEN_DATEINAME = "quiz_fragen.ser";
	private final String ANTWORTEN_DATEINAME = "quiz_antworten.ser";
	private final String ERGEBNISSE_DATEINAME = "quiz_ergebnisse.ser";
	
	// ID-Counter für eindeutige Identifikation
	private final AtomicLong themaIdCounter;
	private final AtomicLong frageIdCounter;
	private final AtomicLong antwortIdCounter;
	private final AtomicLong ergebnisIdCounter;

	public LokalRepository() {
		// Daten aus Dateien laden
		this.themenListe = ladeDatenAusDatei(THEMEN_DATEINAME, new ArrayList<>());
		this.fragenListe = ladeDatenAusDatei(FRAGEN_DATEINAME, new ArrayList<>());
		this.antwortenListe = ladeDatenAusDatei(ANTWORTEN_DATEINAME, new ArrayList<>());
		this.ergebnisseListe = ladeDatenAusDatei(ERGEBNISSE_DATEINAME, new ArrayList<>());

		// ID-Counter initialisieren
		this.themaIdCounter = new AtomicLong(
			themenListe.stream().mapToLong(ThemaDTO::getId).max().orElse(0));
		this.frageIdCounter = new AtomicLong(
			fragenListe.stream().mapToLong(FrageDTO::getId).max().orElse(0));
		this.antwortIdCounter = new AtomicLong(
			antwortenListe.stream().mapToLong(AntwortDTO::getId).max().orElse(0));
		this.ergebnisIdCounter = new AtomicLong(
			ergebnisseListe.stream().mapToLong(QuizErgebnisDTO::getId).max().orElse(0));
	}

	// --- Themen-Operationen ---
	@Override
	public List<ThemaDTO> findAllThemen() {
		lock.readLock().lock();
		try {
			return new ArrayList<>(themenListe);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Optional<ThemaDTO> findThemaById(long id) {
		lock.readLock().lock();
		try {
			return themenListe.stream().filter(t -> t.getId() == id).findFirst();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Optional<ThemaDTO> findThemaByTitel(String titel) {
		lock.readLock().lock();
		try {
			return themenListe.stream().filter(t -> t.getTitel().equalsIgnoreCase(titel)).findFirst();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ThemaDTO saveThema(ThemaDTO thema) {
		lock.writeLock().lock();
		try {
			if (thema.getId() <= 0) {
				// Neues Thema
				thema.setId(themaIdCounter.incrementAndGet());
				themenListe.add(thema);
			} else {
				// Bestehendes Thema aktualisieren
				for (int i = 0; i < themenListe.size(); i++) {
					if (themenListe.get(i).getId() == thema.getId()) {
						themenListe.set(i, thema);
						break;
					}
				}
			}

			// Anzahl Fragen aktualisieren
			long anzahlFragen = fragenListe.stream()
				.filter(f -> f.getThemaName() != null && 
					f.getThemaName().equals(thema.getTitel())).count();
			thema.setAnzahlFragen((int) anzahlFragen);

			speichereDatenInDatei(THEMEN_DATEINAME, themenListe);
			return thema;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void deleteThema(long id) {
		lock.writeLock().lock();
		try {
			// Alle Fragen und Antworten des Themas löschen
			Optional<ThemaDTO> thema = findThemaById(id);
			if (thema.isPresent()) {
				String themaName = thema.get().getTitel();
				
				// Fragen des Themas finden und löschen
				List<FrageDTO> fragenZuLoeschen = fragenListe.stream()
					.filter(f -> themaName.equals(f.getThemaName()))
					.collect(Collectors.toList());
				
				for (FrageDTO frage : fragenZuLoeschen) {
					// Antworten der Frage löschen
					antwortenListe.removeIf(a -> frage.getAntworten().stream()
						.anyMatch(fa -> fa.getId() == a.getId()));
				}
				
				// Fragen löschen
				fragenListe.removeAll(fragenZuLoeschen);
				
				// Thema löschen
				themenListe.removeIf(t -> t.getId() == id);
				
				// Alle Dateien aktualisieren
				speichereDatenInDatei(THEMEN_DATEINAME, themenListe);
				speichereDatenInDatei(FRAGEN_DATEINAME, fragenListe);
				speichereDatenInDatei(ANTWORTEN_DATEINAME, antwortenListe);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean existsThemaWithTitel(String titel) {
		lock.readLock().lock();
		try {
			return themenListe.stream().anyMatch(t -> t.getTitel().equalsIgnoreCase(titel));
		} finally {
			lock.readLock().unlock();
		}
	}

	// --- Fragen-Operationen ---
	@Override
	public List<FrageDTO> findFragenByThemaId(long themaId) {
		lock.readLock().lock();
		try {
			Optional<ThemaDTO> thema = findThemaById(themaId);
			if (thema.isPresent()) {
				return fragenListe.stream()
					.filter(f -> thema.get().getTitel().equals(f.getThemaName()))
					.collect(Collectors.toList());
			}
			return new ArrayList<>();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<FrageDTO> findFragenByThemaName(String themaName) {
		lock.readLock().lock();
		try {
			return fragenListe.stream()
				.filter(f -> themaName.equals(f.getThemaName()))
				.collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Optional<FrageDTO> findFrageById(long id) {
		lock.readLock().lock();
		try {
			return fragenListe.stream().filter(f -> f.getId() == id).findFirst();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Optional<FrageDTO> findFrageByTitel(String titel, long themaId) {
		lock.readLock().lock();
		try {
			Optional<ThemaDTO> thema = findThemaById(themaId);
			if (thema.isPresent()) {
				return fragenListe.stream()
					.filter(f -> thema.get().getTitel().equals(f.getThemaName()) && 
						titel.equalsIgnoreCase(f.getTitel()))
					.findFirst();
			}
			return Optional.empty();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public FrageDTO saveFrage(FrageDTO frage, long themaId) {
		lock.writeLock().lock();
		try {
			Optional<ThemaDTO> thema = findThemaById(themaId);
			if (thema.isEmpty()) {
				throw new PersistenceException("Thema mit ID " + themaId + " nicht gefunden");
			}
			
			// Thema-Name setzen
			frage.setThemaName(thema.get().getTitel());
			
			if (frage.getId() <= 0) {
				// Neue Frage
				frage.setId(frageIdCounter.incrementAndGet());
				fragenListe.add(frage);
			} else {
				// Bestehende Frage aktualisieren
				for (int i = 0; i < fragenListe.size(); i++) {
					if (fragenListe.get(i).getId() == frage.getId()) {
						fragenListe.set(i, frage);
						break;
					}
				}
			}

			// Antworten-IDs setzen und speichern
			for (AntwortDTO antwort : frage.getAntworten()) {
				if (antwort.getId() <= 0) {
					antwort.setId(antwortIdCounter.incrementAndGet());
				}
				saveAntwort(antwort, frage.getId());
			}

			// Themen-Anzahl aktualisieren
			thema.get().setAnzahlFragen(findFragenByThemaId(themaId).size());

			speichereDatenInDatei(FRAGEN_DATEINAME, fragenListe);
			speichereDatenInDatei(THEMEN_DATEINAME, themenListe);
			
			return frage;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void deleteFrage(long id) {
		lock.writeLock().lock();
		try {
			Optional<FrageDTO> frage = findFrageById(id);
			if (frage.isPresent()) {
				// Antworten der Frage löschen
				antwortenListe.removeIf(a -> frage.get().getAntworten().stream()
					.anyMatch(fa -> fa.getId() == a.getId()));
				
				// Frage löschen
				fragenListe.removeIf(f -> f.getId() == id);
				
				// Themen-Anzahl aktualisieren
				String themaName = frage.get().getThemaName();
				themenListe.stream()
					.filter(t -> themaName.equals(t.getTitel()))
					.findFirst()
					.ifPresent(t -> t.setAnzahlFragen(findFragenByThemaName(themaName).size()));
				
				// Alle Dateien aktualisieren
				speichereDatenInDatei(FRAGEN_DATEINAME, fragenListe);
				speichereDatenInDatei(ANTWORTEN_DATEINAME, antwortenListe);
				speichereDatenInDatei(THEMEN_DATEINAME, themenListe);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean existsFrageWithTitel(String titel, long themaId) {
		lock.readLock().lock();
		try {
			Optional<ThemaDTO> thema = findThemaById(themaId);
			if (thema.isPresent()) {
				return fragenListe.stream()
					.anyMatch(f -> thema.get().getTitel().equals(f.getThemaName()) && 
						titel.equalsIgnoreCase(f.getTitel()));
			}
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

	// --- Antworten-Operationen ---
	@Override
	public List<AntwortDTO> findAntwortenByFrageId(long frageId) {
		lock.readLock().lock();
		try {
			Optional<FrageDTO> frage = findFrageById(frageId);
			if (frage.isPresent()) {
				return frage.get().getAntworten();
			}
			return new ArrayList<>();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public AntwortDTO saveAntwort(AntwortDTO antwort, long frageId) {
		lock.writeLock().lock();
		try {
			if (antwort.getId() <= 0) {
				// Neue Antwort
				antwort.setId(antwortIdCounter.incrementAndGet());
				antwortenListe.add(antwort);
			} else {
				// Bestehende Antwort aktualisieren
				for (int i = 0; i < antwortenListe.size(); i++) {
					if (antwortenListe.get(i).getId() == antwort.getId()) {
						antwortenListe.set(i, antwort);
						break;
					}
				}
			}

			speichereDatenInDatei(ANTWORTEN_DATEINAME, antwortenListe);
			return antwort;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void deleteAntwort(long id) {
		lock.writeLock().lock();
		try {
			antwortenListe.removeIf(a -> a.getId() == id);
			speichereDatenInDatei(ANTWORTEN_DATEINAME, antwortenListe);
		} finally {
			lock.writeLock().unlock();
		}
	}

	// --- Quiz-Statistik-Operationen ---
	@Override
	public void saveQuizErgebnis(QuizErgebnisDTO ergebnis) {
		lock.writeLock().lock();
		try {
			if (ergebnis.getId() <= 0) {
				// Neues Ergebnis
				ergebnis.setId(ergebnisIdCounter.incrementAndGet());
				ergebnisseListe.add(ergebnis);
			} else {
				// Bestehendes Ergebnis aktualisieren
				for (int i = 0; i < ergebnisseListe.size(); i++) {
					if (ergebnisseListe.get(i).getId() == ergebnis.getId()) {
						ergebnisseListe.set(i, ergebnis);
						break;
					}
				}
			}
			
			speichereDatenInDatei(ERGEBNISSE_DATEINAME, ergebnisseListe);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public List<QuizErgebnisDTO> findQuizErgebnisseByThemaId(long themaId) {
		lock.readLock().lock();
		try {
			return ergebnisseListe.stream()
				.filter(e -> e.getThemaId() == themaId)
				.collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<QuizErgebnisDTO> findQuizErgebnisseByFrageId(long frageId) {
		lock.readLock().lock();
		try {
			return ergebnisseListe.stream()
				.filter(e -> e.getFrageId() == frageId)
				.collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<StatistikDTO> findStatistikenByThemaId(long themaId) {
		lock.readLock().lock();
		try {
			List<StatistikDTO> statistiken = new ArrayList<>();
			
			// Alle Fragen des Themas laden
			List<FrageDTO> fragen = findFragenByThemaId(themaId);
			
			for (FrageDTO frage : fragen) {
				StatistikDTO statistik = new StatistikDTO(themaId, "", frage.getId(), frage.getTitel());
				
				// Quiz-Ergebnisse für diese Frage laden
				List<QuizErgebnisDTO> ergebnisse = findQuizErgebnisseByFrageId(frage.getId());
				
				for (QuizErgebnisDTO ergebnis : ergebnisse) {
					statistik.addErgebnis(ergebnis.isAntwortRichtig(), 
						ergebnis.getAntwortZeitSekunden(), ergebnis.getPunkte());
				}
				
				statistiken.add(statistik);
			}
			
			return statistiken;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<StatistikDTO> findAlleStatistiken() {
		lock.readLock().lock();
		try {
			List<StatistikDTO> alleStatistiken = new ArrayList<>();
			
			// Alle Themen laden
			List<ThemaDTO> themen = findAllThemen();
			
			for (ThemaDTO thema : themen) {
				List<StatistikDTO> themaStatistiken = findStatistikenByThemaId(thema.getId());
				alleStatistiken.addAll(themaStatistiken);
			}
			
			return alleStatistiken;
		} finally {
			lock.readLock().unlock();
		}
	}

	// --- Private Hilfsmethoden ---
	private <T> void speichereDatenInDatei(String dateiname, List<T> daten) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateiname))) {
			oos.writeObject(daten);
		} catch (IOException e) {
			throw new PersistenceException("Fehler beim Speichern der lokalen Daten in " + dateiname, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> ladeDatenAusDatei(String dateiname, List<T> defaultValue) {
		File datei = new File(dateiname);
		if (datei.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datei))) {
				return (List<T>) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Fehler beim Laden der lokalen Daten aus " + dateiname
						+ ". Eine neue Datei wird beim Speichern erstellt.");
			}
		}
		return defaultValue;
	}
}
