package data.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import config.DatabaseFactory;
import config.DatabaseType;
import config.PropertiesLoader;
import data.repository.QuizRepository;
import exception.PersistenceException;

/**
 * Factory für die Erstellung von Repository-Implementierungen.
 * 
 * <p>Diese Klasse implementiert das Factory-Pattern und dient der zentralen
 * Erstellung von Repository-Implementierungen basierend auf der aktuellen
 * Konfiguration der Anwendung.</p>
 * 
 * <p>Unterstützte Repository-Typen:</p>
 * <ul>
 *   <li>LokalRepository - Für lokale Dateispeicherung</li>
 *   <li>JDBCRepository - Für SQLite und MariaDB-Datenbanken</li>
 * </ul>
 * 
 * <p>Konfiguration:</p>
 * <ul>
 *   <li>Automatische Erkennung des Datenbanktyps aus Properties</li>
 *   <li>Unterstützung verschiedener Umgebungen (Development, Production, Testing)</li>
 *   <li>Fallback auf Standard-Konfiguration bei Fehlern</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * // Standard-Repository basierend auf Konfiguration
 * QuizRepository repository = RepositoryFactory.createRepository();
 * 
 * // Repository für spezifische Umgebung
 * QuizRepository repository = RepositoryFactory.createRepositoryForEnvironment("production");
 * 
 * // Repository mit benutzerdefinierten Properties
 * Properties props = new Properties();
 * props.setProperty("quiz.database.type", "sqlite");
 * QuizRepository repository = RepositoryFactory.createRepositoryWithProperties(props);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see data.repository.QuizRepository
 * @see LokalRepository
 * @see JDBCRepository
 * @see config.DatabaseFactory
 * @see config.PropertiesLoader
 */
public class RepositoryFactory {

	/**
	 * Erstellt ein Repository basierend auf der aktuellen Konfiguration.
	 * 
	 * <p>Diese Methode liest die Konfiguration aus den Properties und erstellt
	 * das entsprechende Repository. Bei Fehlern wird eine PersistenceException geworfen.</p>
	 * 
	 * @return Eine Repository-Implementierung basierend auf der Konfiguration
	 * @throws PersistenceException wenn das Repository nicht erstellt werden kann
	 */
	public static QuizRepository createRepository() {
		try {
			DatabaseType dbType = PropertiesLoader.getDatabaseType();
			System.out.println("RepositoryFactory: Erstelle Repository für Datenbanktyp: " + dbType.getDisplayName());
			
			switch (dbType) {
			case LOKAL:
				System.out.println("RepositoryFactory: Verwende LokalRepository");
				return new LokalRepository();
				
			case SQLITE:
			case MARIADB:
				System.out.println("RepositoryFactory: Verwende JDBCRepository für " + dbType.getDisplayName());
				Connection connection = DatabaseFactory.getInstance().createConnection(dbType);
				// Schema initialisieren
				try {
					initialisiereSchema(connection, dbType);
					System.out.println("RepositoryFactory: Schema erfolgreich initialisiert");
				} catch (SQLException e) {
					System.err.println("Fehler beim Initialisieren des Schemas: " + e.getMessage());
					// Trotzdem Repository erstellen, da das Schema bereits existieren könnte
				}
				return new JDBCRepository(connection);
				
			default:
				throw new PersistenceException("Nicht unterstützter Datenbanktyp: " + dbType);
			}
		} catch (Exception e) {
			System.err.println("Fehler beim Erstellen des Repositories: " + e.getMessage());
			System.err.println("Verwende lokales Repository als Fallback");
			return new LokalRepository();
		}
	}

	/**
	 * Initialisiert das Datenbankschema für eine Verbindung.
	 * 
	 * <p>Diese private Methode erstellt alle notwendigen Tabellen in der Datenbank,
	 * falls sie noch nicht existieren. Sie wird für JDBC-basierte Repositories verwendet.</p>
	 * 
	 * @param conn Die Datenbankverbindung
	 * @throws SQLException bei Fehlern beim Erstellen des Schemas
	 */
	private static void initialisiereSchema(Connection conn) throws SQLException {
		DatabaseType dbType = PropertiesLoader.getDatabaseType();
		initialisiereSchema(conn, dbType);
	}
	
	/**
	 * Initialisiert das Datenbankschema basierend auf dem Datenbanktyp.
	 * 
	 * <p>Erstellt alle notwendigen Tabellen mit der entsprechenden SQL-Syntax
	 * für den angegebenen Datenbanktyp.</p>
	 * 
	 * @param conn Die Datenbankverbindung
	 * @param dbType Der Typ der Datenbank
	 * @throws SQLException bei Fehlern beim Erstellen des Schemas
	 */
	private static void initialisiereSchema(Connection conn, DatabaseType dbType) throws SQLException {
		// SQLite-spezifische Syntax
		String sqlThemen = "CREATE TABLE IF NOT EXISTS Themen (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "titel TEXT NOT NULL, " + "information TEXT" + ")";

		String sqlFragen = "CREATE TABLE IF NOT EXISTS Fragen (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "frage_titel TEXT NOT NULL, " + "frage_text TEXT NOT NULL, " + "thema_id INTEGER NOT NULL, "
				+ "FOREIGN KEY (thema_id) REFERENCES Themen (id) ON DELETE CASCADE" + ")";

		String sqlAntworten = "CREATE TABLE IF NOT EXISTS Antworten (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "antwort_text TEXT NOT NULL, " + "ist_richtig INTEGER NOT NULL, " + "frage_id INTEGER NOT NULL, "
				+ "FOREIGN KEY (frage_id) REFERENCES Fragen (id) ON DELETE CASCADE" + ")";

		String sqlQuizErgebnisse = "CREATE TABLE IF NOT EXISTS QuizErgebnisse ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "thema_id INTEGER NOT NULL, "
				+ "frage_id INTEGER NOT NULL, " + "antwort_richtig INTEGER NOT NULL, "
				+ "antwort_vorher_gezeigt INTEGER NOT NULL, " + "antwort_zeit_sekunden INTEGER NOT NULL, "
				+ "zeitpunkt TEXT NOT NULL, " + "punkte INTEGER NOT NULL, "
				+ "FOREIGN KEY (thema_id) REFERENCES Themen (id) ON DELETE CASCADE, "
				+ "FOREIGN KEY (frage_id) REFERENCES Fragen (id) ON DELETE CASCADE" + ")";

		// MariaDB-spezifische Syntax
		if (dbType == DatabaseType.MARIADB) {
			sqlThemen = "CREATE TABLE IF NOT EXISTS Themen (" + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
					+ "titel VARCHAR(255) NOT NULL, " + "information TEXT" + ")";

			sqlFragen = "CREATE TABLE IF NOT EXISTS Fragen (" + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
					+ "frage_titel VARCHAR(255) NOT NULL, " + "frage_text TEXT NOT NULL, "
					+ "thema_id BIGINT NOT NULL, " + "FOREIGN KEY (thema_id) REFERENCES Themen (id) ON DELETE CASCADE"
					+ ")";

			sqlAntworten = "CREATE TABLE IF NOT EXISTS Antworten (" + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
					+ "antwort_text VARCHAR(255) NOT NULL, " + "ist_richtig BOOLEAN NOT NULL, "
					+ "frage_id BIGINT NOT NULL, " + "FOREIGN KEY (frage_id) REFERENCES Fragen (id) ON DELETE CASCADE"
					+ ")";

			sqlQuizErgebnisse = "CREATE TABLE IF NOT EXISTS QuizErgebnisse (" + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
					+ "thema_id BIGINT NOT NULL, " + "frage_id BIGINT NOT NULL, " + "antwort_richtig BOOLEAN NOT NULL, "
					+ "antwort_vorher_gezeigt BOOLEAN NOT NULL, " + "antwort_zeit_sekunden INT NOT NULL, "
					+ "zeitpunkt DATETIME NOT NULL, " + "punkte INT NOT NULL, "
					+ "FOREIGN KEY (thema_id) REFERENCES Themen (id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (frage_id) REFERENCES Fragen (id) ON DELETE CASCADE" + ")";
		}

		try (java.sql.Statement stmt = conn.createStatement()) {
			stmt.execute(sqlThemen);
			stmt.execute(sqlFragen);
			stmt.execute(sqlAntworten);
			stmt.execute(sqlQuizErgebnisse);
		}
	}
}
