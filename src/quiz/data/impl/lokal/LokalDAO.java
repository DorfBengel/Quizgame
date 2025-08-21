/*
 *
 * LokalDAO.java
 *
 * Implementierung der Datenzugriffsschicht für lokale Speicherung von Quizdaten.
 *
 */
package quiz.data.impl.lokal;

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

import quiz.data.dao.IFrageDAO;
import quiz.data.dao.IThemaDAO;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

public class LokalDAO implements IThemaDAO, IFrageDAO {

	private List<Thema> themenListe;
	private final String DATEINAME = "quizdaten.ser";
	private final AtomicLong themaIdCounter;
	private final AtomicLong frageIdCounter;

	public LokalDAO() {
		this.themenListe = ladeDatenAusDatei();

		long maxThemaId = themenListe.stream().mapToLong(Thema::getId).max().orElse(0);
		this.themaIdCounter = new AtomicLong(maxThemaId);

		long maxFrageId = themenListe.stream().flatMap(t -> t.getFragen().stream()).mapToLong(Frage::getId).max()
				.orElse(0);
		this.frageIdCounter = new AtomicLong(maxFrageId);
	}

	// --- IThemaDAO Implementierung ---
	@Override
	public void speichereThema(Thema thema) {
		thema.setId(themaIdCounter.incrementAndGet());
		themenListe.add(thema);
		speichereDatenInDatei();
	}

	@Override
	public void loescheThema(Thema thema) {
		themenListe.remove(thema);
		speichereDatenInDatei();
	}

	@Override
	public void updateThema(Thema thema) {
		// Thema in der Liste ersetzen
		for (int i = 0; i < themenListe.size(); i++) {
			if (themenListe.get(i).getId() == thema.getId()) {
				themenListe.set(i, thema);
				break;
			}
		}
		speichereDatenInDatei();
	}

	@Override
	public List<Thema> getAlleThemen() {
		return new ArrayList<>(themenListe);
	}

	@Override
	public Optional<Thema> findeThemaById(long id) {
		return themenListe.stream().filter(t -> t.getId() == id).findFirst();
	}

	// --- IFrageDAO Implementierung ---
	@Override
	public void speichereFrage(Frage frage, long themaId) {
		// ID setzen, falls noch nicht vorhanden
		if (frage.getId() <= 0) {
			frage.setId(frageIdCounter.incrementAndGet());
		}
		List<Frage> fragen = findeFragenFuerThema(themaId);
		for (int i = 0; i < fragen.size(); i++) {
			Frage f = fragen.get(i);
			if (f.getId() == frage.getId() || f.getFrageTitel().equalsIgnoreCase(frage.getFrageTitel())) {
				fragen.set(i, frage);
				speichereDatenInDatei();
				return;
			}
		}
		fragen.add(frage);
		speichereDatenInDatei();
	}

	@Override
	public void updateFrage(Frage frage) {
		if (frage == null) {
			return;
		}
		for (Thema thema : themenListe) {
			for (int i = 0; i < thema.getFragen().size(); i++) {
				if (thema.getFragen().get(i).getId() == frage.getId()) {
					thema.getFragen().set(i, frage);
					speichereDatenInDatei();
					return;
				}
			}
		}
	}

	@Override
	public void loescheFrage(Frage frage) {
		if (frage == null) {
			return;
		}
		for (Thema thema : themenListe) {
			if (thema.getFragen().removeIf(f -> f.getId() == frage.getId())) {
				speichereDatenInDatei();
				return;
			}
		}
	}

	@Override
	public List<Frage> findeFragenFuerThema(long themaId) {
		return findeThemaById(themaId).map(Thema::getFragen).orElse(new ArrayList<>());
	}

	// --- Private Hilfsmethoden ---
	private void speichereDatenInDatei() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATEINAME))) {
			oos.writeObject(themenListe);
		} catch (IOException e) {
			System.err.println("Fehler beim Speichern der lokalen Daten in " + DATEINAME);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Thema> ladeDatenAusDatei() {
		File datei = new File(DATEINAME);
		if (datei.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datei))) {
				return (List<Thema>) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Fehler beim Laden der lokalen Daten aus " + DATEINAME
						+ ". Eine neue Datei wird beim Speichern erstellt.");
			}
		}
		return new ArrayList<>();
	}
}
