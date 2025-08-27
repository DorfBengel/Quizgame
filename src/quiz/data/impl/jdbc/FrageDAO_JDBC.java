
// src/quiz/data/impl/jdbc/FrageDAO_JDBC.java
package quiz.data.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import quiz.data.dao.IFrageDAO;
import quiz.data.model.Antwort;
import quiz.data.model.Frage;

public class FrageDAO_JDBC implements IFrageDAO {
	private final Connection conn;

	public FrageDAO_JDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void speichereFrage(Frage frage, long themaId) {
		String sqlFrage = "INSERT INTO Fragen(frage_text, frage_titel, thema_id) VALUES(?, ?, ?)";
		String sqlAntwort = "INSERT INTO Antworten(antwort_text, ist_richtig, frage_id) VALUES(?, ?, ?)";
		try (PreparedStatement pstmtFrage = conn.prepareStatement(sqlFrage, Statement.RETURN_GENERATED_KEYS)) {
			pstmtFrage.setString(1, frage.getFrageText());
			pstmtFrage.setString(2, frage.getFrageTitel());
			pstmtFrage.setLong(3, themaId);
			pstmtFrage.executeUpdate();
			try (ResultSet rs = pstmtFrage.getGeneratedKeys()) {
				if (rs.next()) {
					long frageId = rs.getLong(1);
					frage.setId(frageId);
					try (PreparedStatement pstmtAntwort = conn.prepareStatement(sqlAntwort)) {
						for (Antwort antwort : frage.getAntworten()) {
							pstmtAntwort.setString(1, antwort.getText());
							pstmtAntwort.setBoolean(2, antwort.istRichtig());
							pstmtAntwort.setLong(3, frageId);
							pstmtAntwort.addBatch();
						}
						pstmtAntwort.executeBatch();
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Speichern der Frage: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateFrage(Frage frage) {
		String sqlUpdateFrage = "UPDATE Fragen SET frage_text = ?, frage_titel = ? WHERE id = ?";
		String sqlDeleteAntworten = "DELETE FROM Antworten WHERE frage_id = ?";
		String sqlInsertAntwort = "INSERT INTO Antworten(antwort_text, ist_richtig, frage_id) VALUES(?, ?, ?)";
		try (PreparedStatement pstmtFrage = conn.prepareStatement(sqlUpdateFrage)) {
			pstmtFrage.setString(1, frage.getFrageText());
			pstmtFrage.setString(2, frage.getFrageTitel());
			pstmtFrage.setLong(3, frage.getId());
			pstmtFrage.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Aktualisieren der Frage: " + e.getMessage(), e);
		}

		try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDeleteAntworten)) {
			pstmtDelete.setLong(1, frage.getId());
			pstmtDelete.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Löschen der Antworten: " + e.getMessage(), e);
		}

		try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertAntwort)) {
			for (Antwort antwort : frage.getAntworten()) {
				pstmtInsert.setString(1, antwort.getText());
				pstmtInsert.setBoolean(2, antwort.istRichtig());
				pstmtInsert.setLong(3, frage.getId());
				pstmtInsert.addBatch();
			}
			pstmtInsert.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Einfügen der Antworten: " + e.getMessage(), e);
		}
	}

	@Override
	public void loescheFrage(Frage frage) {
		String sql = "DELETE FROM Fragen WHERE id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, frage.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Löschen der Frage: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Frage> findeFragenFuerThema(long themaId) {
		List<Frage> fragen = new ArrayList<>();
		String sqlFragen = "SELECT id, frage_text, frage_titel FROM Fragen WHERE thema_id = ?";
		String sqlAntworten = "SELECT id, antwort_text, ist_richtig FROM Antworten WHERE frage_id = ?";
		try (PreparedStatement pstmtFragen = conn.prepareStatement(sqlFragen)) {
			pstmtFragen.setLong(1, themaId);
			try (ResultSet rsFragen = pstmtFragen.executeQuery()) {
				while (rsFragen.next()) {
					Frage frage = new Frage(rsFragen.getString("frage_text"));
					frage.setId(rsFragen.getLong("id"));
					frage.setFrageTitel(rsFragen.getString("frage_titel"));
					try (PreparedStatement pstmtAntworten = conn.prepareStatement(sqlAntworten)) {
						pstmtAntworten.setLong(1, frage.getId());
						try (ResultSet rsAntworten = pstmtAntworten.executeQuery()) {
							List<Antwort> antworten = new ArrayList<>();
							while (rsAntworten.next()) {
								Antwort antwort = new Antwort(rsAntworten.getString("antwort_text"),
										rsAntworten.getBoolean("ist_richtig"));
								antwort.setId(rsAntworten.getLong("id"));
								antworten.add(antwort);
							}
							frage.setAntworten(antworten);
						}
					}
					fragen.add(frage);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Fehler beim Laden der Fragen: " + e.getMessage(), e);
		}
		return fragen;
	}
}
