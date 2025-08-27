package data.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import data.dto.AntwortDTO;
import data.dto.FrageDTO;
import data.dto.QuizErgebnisDTO;
import data.dto.StatistikDTO;
import data.dto.ThemaDTO;
import data.repository.QuizRepository;
import exception.PersistenceException;

/**
 * Repository-Implementierung für JDBC-Datenquellen (SQLite, MariaDB).
 */
public class JDBCRepository implements QuizRepository {

	private final Connection connection;

	public JDBCRepository(Connection connection) {
		this.connection = connection;
	}

	// --- Themen-Operationen ---
	@Override
	public List<ThemaDTO> findAllThemen() {
		List<ThemaDTO> themen = new ArrayList<>();
		String sql = "SELECT id, titel, information FROM Themen ORDER BY titel";

		try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				long id = rs.getLong("id");
				String titel = rs.getString("titel");
				String information = rs.getString("information");

				// Anzahl Fragen für dieses Thema ermitteln
				int anzahlFragen = countFragenForThema(id);

				ThemaDTO thema = new ThemaDTO(id, titel, information, anzahlFragen);
				themen.add(thema);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden aller Themen", e);
		}

		return themen;
	}

	@Override
	public Optional<ThemaDTO> findThemaById(long id) {
		String sql = "SELECT id, titel, information FROM Themen WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String titel = rs.getString("titel");
					String information = rs.getString("information");
					int anzahlFragen = countFragenForThema(id);

					return Optional.of(new ThemaDTO(id, titel, information, anzahlFragen));
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden des Themas mit ID " + id, e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<ThemaDTO> findThemaByTitel(String titel) {
		String sql = "SELECT id, titel, information FROM Themen WHERE titel = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, titel);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					long id = rs.getLong("id");
					String information = rs.getString("information");
					int anzahlFragen = countFragenForThema(id);

					return Optional.of(new ThemaDTO(id, titel, information, anzahlFragen));
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden des Themas mit Titel '" + titel + "'", e);
		}

		return Optional.empty();
	}

	@Override
	public ThemaDTO saveThema(ThemaDTO thema) {
		if (thema.getId() <= 0) {
			// Neues Thema einfügen
			String sql = "INSERT INTO Themen(titel, information) VALUES(?, ?)";

			try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, thema.getTitel());
				stmt.setString(2, thema.getInformation());
				stmt.executeUpdate();

				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						thema.setId(rs.getLong(1));
					}
				}
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Speichern des neuen Themas", e);
			}
		} else {
			// Bestehendes Thema aktualisieren
			String sql = "UPDATE Themen SET titel = ?, information = ? WHERE id = ?";

			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, thema.getTitel());
				stmt.setString(2, thema.getInformation());
				stmt.setLong(3, thema.getId());
				stmt.executeUpdate();
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Aktualisieren des Themas", e);
			}
		}

		// Anzahl Fragen aktualisieren
		thema.setAnzahlFragen(countFragenForThema(thema.getId()));

		return thema;
	}

	@Override
	public void deleteThema(long id) {
		String sql = "DELETE FROM Themen WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Löschen des Themas mit ID " + id, e);
		}
	}

	@Override
	public boolean existsThemaWithTitel(String titel) {
		String sql = "SELECT COUNT(*) FROM Themen WHERE titel = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, titel);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Prüfen der Existenz des Themas", e);
		}

		return false;
	}

	// --- Fragen-Operationen ---
	@Override
	public List<FrageDTO> findFragenByThemaId(long themaId) {
		List<FrageDTO> fragen = new ArrayList<>();
		String sql = "SELECT id, frage_titel, frage_text FROM Fragen WHERE thema_id = ? ORDER BY frage_titel";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, themaId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					long id = rs.getLong("id");
					String titel = rs.getString("frage_titel");
					String text = rs.getString("frage_text");

					// Antworten für diese Frage laden
					List<AntwortDTO> antworten = findAntwortenByFrageId(id);

					FrageDTO frage = new FrageDTO(id, titel, text, "");
					frage.setAntworten(antworten);
					fragen.add(frage);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Fragen für Thema " + themaId, e);
		}

		return fragen;
	}

	@Override
	public List<FrageDTO> findFragenByThemaName(String themaName) {
		Optional<ThemaDTO> thema = findThemaByTitel(themaName);
		if (thema.isPresent()) {
			return findFragenByThemaId(thema.get().getId());
		}
		return new ArrayList<>();
	}

	@Override
	public Optional<FrageDTO> findFrageById(long id) {
		String sql = "SELECT id, frage_titel, frage_text, thema_id FROM Fragen WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String titel = rs.getString("frage_titel");
					String text = rs.getString("frage_text");
					long themaId = rs.getLong("thema_id");

					// Antworten für diese Frage laden
					List<AntwortDTO> antworten = findAntwortenByFrageId(id);

					FrageDTO frage = new FrageDTO(id, titel, text, "");
					frage.setAntworten(antworten);

					return Optional.of(frage);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Frage mit ID " + id, e);
		}

		return Optional.empty();
	}

	@Override
	public Optional<FrageDTO> findFrageByTitel(String titel, long themaId) {
		String sql = "SELECT id, frage_titel, frage_text FROM Fragen WHERE frage_titel = ? AND thema_id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, titel);
			stmt.setLong(2, themaId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					long id = rs.getLong("id");
					String text = rs.getString("frage_text");

					// Antworten für diese Frage laden
					List<AntwortDTO> antworten = findAntwortenByFrageId(id);

					FrageDTO frage = new FrageDTO(id, titel, text, "");
					frage.setAntworten(antworten);

					return Optional.of(frage);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Frage mit Titel '" + titel + "'", e);
		}

		return Optional.empty();
	}

	@Override
	public FrageDTO saveFrage(FrageDTO frage, long themaId) {
		if (frage.getId() <= 0) {
			// Neue Frage einfügen
			String sql = "INSERT INTO Fragen(frage_titel, frage_text, thema_id) VALUES(?, ?, ?)";

			try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, frage.getTitel());
				stmt.setString(2, frage.getText());
				stmt.setLong(3, themaId);
				stmt.executeUpdate();

				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						frage.setId(rs.getLong(1));
					}
				}
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Speichern der neuen Frage", e);
			}
		} else {
			// Bestehende Frage aktualisieren
			String sql = "UPDATE Fragen SET frage_titel = ?, frage_text = ? WHERE id = ?";

			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, frage.getTitel());
				stmt.setString(2, frage.getText());
				stmt.setLong(3, frage.getId());
				stmt.executeUpdate();
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Aktualisieren der Frage", e);
			}
		}

		// Antworten speichern
		saveAntworten(frage);

		return frage;
	}

	@Override
	public void deleteFrage(long id) {
		// Zuerst alle Antworten löschen (CASCADE sollte das automatisch machen)
		String sql = "DELETE FROM Fragen WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Löschen der Frage mit ID " + id, e);
		}
	}

	@Override
	public boolean existsFrageWithTitel(String titel, long themaId) {
		String sql = "SELECT COUNT(*) FROM Fragen WHERE frage_titel = ? AND thema_id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, titel);
			stmt.setLong(2, themaId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Prüfen der Existenz der Frage", e);
		}

		return false;
	}

	// --- Antworten-Operationen ---
	@Override
	public List<AntwortDTO> findAntwortenByFrageId(long frageId) {
		List<AntwortDTO> antworten = new ArrayList<>();
		String sql = "SELECT id, antwort_text, ist_richtig FROM Antworten WHERE frage_id = ? ORDER BY id";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, frageId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					long id = rs.getLong("id");
					String text = rs.getString("antwort_text");
					boolean istRichtig = rs.getBoolean("ist_richtig");

					antworten.add(new AntwortDTO(id, text, istRichtig));
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Antworten für Frage " + frageId, e);
		}

		return antworten;
	}

	@Override
	public AntwortDTO saveAntwort(AntwortDTO antwort, long frageId) {
		if (antwort.getId() <= 0) {
			// Neue Antwort einfügen
			String sql = "INSERT INTO Antworten(antwort_text, ist_richtig, frage_id) VALUES(?, ?, ?)";

			try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, antwort.getText());
				stmt.setBoolean(2, antwort.istRichtig());
				stmt.setLong(3, frageId);
				stmt.executeUpdate();

				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						antwort.setId(rs.getLong(1));
					}
				}
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Speichern der neuen Antwort", e);
			}
		} else {
			// Bestehende Antwort aktualisieren
			String sql = "UPDATE Antworten SET antwort_text = ?, ist_richtig = ? WHERE id = ?";

			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, antwort.getText());
				stmt.setBoolean(2, antwort.istRichtig());
				stmt.setLong(3, antwort.getId());
				stmt.executeUpdate();
			} catch (SQLException e) {
				throw new PersistenceException("Fehler beim Aktualisieren der Antwort", e);
			}
		}

		return antwort;
	}

	@Override
	public void deleteAntwort(long id) {
		String sql = "DELETE FROM Antworten WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Löschen der Antwort mit ID " + id, e);
		}
	}

	// --- Private Hilfsmethoden ---
	private int countFragenForThema(long themaId) {
		String sql = "SELECT COUNT(*) FROM Fragen WHERE thema_id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, themaId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			// Bei Fehler 0 zurückgeben
			return 0;
		}

		return 0;
	}

	private void saveAntworten(FrageDTO frage) {
		// Zuerst alle bestehenden Antworten löschen
		String deleteSql = "DELETE FROM Antworten WHERE frage_id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
			stmt.setLong(1, frage.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Löschen der bestehenden Antworten", e);
		}

		// Dann alle neuen Antworten einfügen
		for (AntwortDTO antwort : frage.getAntworten()) {
			saveAntwort(antwort, frage.getId());
		}
	}

	// --- Quiz-Statistik-Operationen ---
	@Override
	public void saveQuizErgebnis(QuizErgebnisDTO ergebnis) {
		String sql = "INSERT INTO QuizErgebnisse(thema_id, frage_id, antwort_richtig, antwort_vorher_gezeigt, antwort_zeit_sekunden, zeitpunkt, punkte) VALUES(?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, ergebnis.getThemaId());
			stmt.setLong(2, ergebnis.getFrageId());
			stmt.setBoolean(3, ergebnis.isAntwortRichtig());
			stmt.setBoolean(4, ergebnis.isAntwortVorherGezeigt());
			stmt.setInt(5, ergebnis.getAntwortZeitSekunden());
			stmt.setString(6, ergebnis.getZeitpunkt().toString());
			stmt.setInt(7, ergebnis.getPunkte());
			stmt.executeUpdate();

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					ergebnis.setId(rs.getLong(1));
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Speichern des Quiz-Ergebnisses", e);
		}
	}

	@Override
	public List<QuizErgebnisDTO> findQuizErgebnisseByThemaId(long themaId) {
		List<QuizErgebnisDTO> ergebnisse = new ArrayList<>();
		String sql = "SELECT id, thema_id, frage_id, antwort_richtig, antwort_vorher_gezeigt, antwort_zeit_sekunden, zeitpunkt, punkte FROM QuizErgebnisse WHERE thema_id = ? ORDER BY zeitpunkt DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, themaId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					QuizErgebnisDTO ergebnis = new QuizErgebnisDTO();
					ergebnis.setId(rs.getLong("id"));
					ergebnis.setThemaId(rs.getLong("thema_id"));
					ergebnis.setFrageId(rs.getLong("frage_id"));
					ergebnis.setAntwortRichtig(rs.getBoolean("antwort_richtig"));
					ergebnis.setAntwortVorherGezeigt(rs.getBoolean("antwort_vorher_gezeigt"));
					ergebnis.setAntwortZeitSekunden(rs.getInt("antwort_zeit_sekunden"));
					ergebnis.setZeitpunkt(java.time.LocalDateTime.parse(rs.getString("zeitpunkt")));
					ergebnis.setPunkte(rs.getInt("punkte"));
					ergebnisse.add(ergebnis);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Quiz-Ergebnisse für Thema " + themaId, e);
		}

		return ergebnisse;
	}

	@Override
	public List<QuizErgebnisDTO> findQuizErgebnisseByFrageId(long frageId) {
		List<QuizErgebnisDTO> ergebnisse = new ArrayList<>();
		String sql = "SELECT id, thema_id, frage_id, antwort_richtig, antwort_vorher_gezeigt, antwort_zeit_sekunden, zeitpunkt, punkte FROM QuizErgebnisse WHERE frage_id = ? ORDER BY zeitpunkt DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, frageId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					QuizErgebnisDTO ergebnis = new QuizErgebnisDTO();
					ergebnis.setId(rs.getLong("id"));
					ergebnis.setThemaId(rs.getLong("thema_id"));
					ergebnis.setFrageId(rs.getLong("frage_id"));
					ergebnis.setAntwortRichtig(rs.getBoolean("antwort_richtig"));
					ergebnis.setAntwortVorherGezeigt(rs.getBoolean("antwort_vorher_gezeigt"));
					ergebnis.setAntwortZeitSekunden(rs.getInt("antwort_zeit_sekunden"));
					ergebnis.setZeitpunkt(java.time.LocalDateTime.parse(rs.getString("zeitpunkt")));
					ergebnis.setPunkte(rs.getInt("punkte"));
					ergebnisse.add(ergebnis);
				}
			}
		} catch (SQLException e) {
			throw new PersistenceException("Fehler beim Laden der Quiz-Ergebnisse für Frage " + frageId, e);
		}

		return ergebnisse;
	}

	@Override
	public List<StatistikDTO> findStatistikenByThemaId(long themaId) {
		List<StatistikDTO> statistiken = new ArrayList<>();

		// Alle Fragen des Themas laden
		List<FrageDTO> fragen = findFragenByThemaId(themaId);

		for (FrageDTO frage : fragen) {
			StatistikDTO statistik = new StatistikDTO(themaId, "", frage.getId(), frage.getTitel());

			// Quiz-Ergebnisse für diese Frage laden
			List<QuizErgebnisDTO> ergebnisse = findQuizErgebnisseByFrageId(frage.getId());

			for (QuizErgebnisDTO ergebnis : ergebnisse) {
				statistik.addErgebnis(ergebnis.isAntwortRichtig(), ergebnis.getAntwortZeitSekunden(),
						ergebnis.getPunkte());
			}

			statistiken.add(statistik);
		}

		return statistiken;
	}

	@Override
	public List<StatistikDTO> findAlleStatistiken() {
		List<StatistikDTO> alleStatistiken = new ArrayList<>();

		// Alle Themen laden
		List<ThemaDTO> themen = findAllThemen();

		for (ThemaDTO thema : themen) {
			List<StatistikDTO> themaStatistiken = findStatistikenByThemaId(thema.getId());
			alleStatistiken.addAll(themaStatistiken);
		}

		return alleStatistiken;
	}
}
