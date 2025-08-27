package quiz.data.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import quiz.data.dao.IThemaDAO;
import quiz.data.model.Thema;

public class ThemaDAO_JDBC implements IThemaDAO {
	private final Connection conn;

	public ThemaDAO_JDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void speichereThema(Thema thema) {
		String sql = "INSERT INTO Themen(titel, information) VALUES(?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, thema.getTitel());
			pstmt.setString(2, thema.getInformation());
			pstmt.executeUpdate();
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					thema.setId(generatedKeys.getLong(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loescheThema(Thema thema) {
		String sql = "DELETE FROM Themen WHERE id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, thema.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateThema(Thema thema) {
		String sql = "UPDATE Themen SET titel = ?, information = ? WHERE id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, thema.getTitel());
			pstmt.setString(2, thema.getInformation());
			pstmt.setLong(3, thema.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Thema> getAlleThemen() {
		List<Thema> themen = new ArrayList<>();
		String sql = "SELECT id, titel, information FROM Themen";
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Thema thema = new Thema(rs.getString("titel"), rs.getString("information"));
				thema.setId(rs.getLong("id"));
				themen.add(thema);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return themen;
	}

	@Override
	public Optional<Thema> findeThemaById(long id) {
		String sql = "SELECT id, titel, information FROM Themen WHERE id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Thema thema = new Thema(rs.getString("titel"), rs.getString("information"));
					thema.setId(rs.getLong("id"));
					return Optional.of(thema);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
}