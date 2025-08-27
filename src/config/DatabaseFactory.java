package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory für die Erstellung von Datenbankverbindungen.
 * Verwendet die enum-basierte Konfiguration für typsichere und erweiterbare
 * Datenbankverbindungen.
 */
public class DatabaseFactory {
    
    private static DatabaseFactory instance;
    private final PropertiesLoader propertiesLoader;
    
    private DatabaseFactory() {
        this.propertiesLoader = new PropertiesLoader();
    }
    
    /**
     * Singleton-Instanz der DatabaseFactory.
     */
    public static synchronized DatabaseFactory getInstance() {
        if (instance == null) {
            instance = new DatabaseFactory();
        }
        return instance;
    }
    
    /**
     * Erstellt eine Datenbankverbindung basierend auf der aktuellen Konfiguration.
     */
    public Connection createConnection() throws SQLException {
        try {
            DatabaseType dbType = getCurrentDatabaseType();
            return createConnection(dbType);
        } catch (Exception e) {
            throw new SQLException("Fehler beim Erstellen der Datenbankverbindung", e);
        }
    }
    
    /**
     * Erstellt eine Datenbankverbindung für einen spezifischen Datenbanktyp.
     */
    public Connection createConnection(DatabaseType dbType) throws SQLException {
        try {
            // JDBC-Treiber laden
            dbType.loadDriver();
            
            // Verbindung erstellen
            switch (dbType) {
                case SQLITE:
                    return createSQLiteConnection();
                    
                case MARIADB:
                    return createMariaDBConnection();
                    
                case LOKAL:
                    throw new SQLException("Lokale Speicherung benötigt keine Datenbankverbindung");
                    
                default:
                    throw new SQLException("Nicht unterstützter Datenbanktyp: " + dbType);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC-Treiber nicht gefunden: " + e.getMessage(), e);
        }
    }
    
    /**
     * Erstellt eine SQLite-Verbindung.
     */
    private Connection createSQLiteConnection() throws SQLException {
        String dbFile = propertiesLoader.getProperty("quiz.database.sqlite.file", "quiz_datenbank.db");
        return DatabaseType.SQLITE.createConnection(dbFile);
    }
    
    /**
     * Erstellt eine MariaDB-Verbindung.
     */
    private Connection createMariaDBConnection() throws SQLException {
        String host = propertiesLoader.getProperty("quiz.database.mariadb.host", "localhost");
        String port = propertiesLoader.getProperty("quiz.database.mariadb.port", "3306");
        String database = propertiesLoader.getProperty("quiz.database.mariadb.name", "quiz_db");
        String user = propertiesLoader.getProperty("quiz.database.mariadb.user", "root");
        String password = propertiesLoader.getProperty("quiz.database.mariadb.password", "");
        
        return DatabaseType.MARIADB.createConnection(host, port, database, user, password);
    }
    
    /**
     * Erstellt eine Datenbankverbindung für eine spezifische Umgebung.
     */
    public Connection createConnectionForEnvironment(String environment) throws SQLException {
        DatabaseConfig config = DatabaseConfig.fromConfigKey(environment);
        DatabaseType dbType = config.getDefaultDatabaseType();
        
        // Temporäre Properties für diese Umgebung setzen
        Properties tempProps = new Properties();
        for (String key : dbType.getRequiredProperties()) {
            String value = config.getProperty(key);
            if (value != null) {
                tempProps.setProperty(key, value);
            }
        }
        
        // Verbindung mit temporären Properties erstellen
        return createConnectionWithProperties(dbType, tempProps);
    }
    
    /**
     * Erstellt eine Datenbankverbindung mit benutzerdefinierten Properties.
     */
    public Connection createConnectionWithProperties(DatabaseType dbType, Properties properties) throws SQLException {
        try {
            dbType.loadDriver();
            
            switch (dbType) {
                case SQLITE:
                    String dbFile = properties.getProperty("quiz.database.sqlite.file", "quiz_datenbank.db");
                    return dbType.createConnection(dbFile);
                    
                case MARIADB:
                    String host = properties.getProperty("quiz.database.mariadb.host", "localhost");
                    String port = properties.getProperty("quiz.database.mariadb.port", "3306");
                    String database = properties.getProperty("quiz.database.mariadb.name", "quiz_db");
                    String user = properties.getProperty("quiz.database.mariadb.user", "root");
                    String password = properties.getProperty("quiz.database.mariadb.password", "");
                    
                    return dbType.createConnection(host, port, database, user, password);
                    
                case LOKAL:
                    throw new SQLException("Lokale Speicherung benötigt keine Datenbankverbindung");
                    
                default:
                    throw new SQLException("Nicht unterstützter Datenbanktyp: " + dbType);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC-Treiber nicht gefunden: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gibt den aktuell konfigurierten Datenbanktyp zurück.
     */
    public DatabaseType getCurrentDatabaseType() {
        // Versuche zuerst den expliziten Datenbanktyp zu lesen
        String dbTypeStr = propertiesLoader.getProperty("quiz.database.type");
        if (dbTypeStr != null) {
            return DatabaseType.fromConfigKey(dbTypeStr);
        }
        
        // Fallback: Leite den Typ aus der Konfiguration ab
        DatabaseConfig config = getCurrentDatabaseConfig();
        return config.getDefaultDatabaseType();
    }
    
    /**
     * Gibt die aktuelle Datenbankkonfiguration zurück.
     */
    public DatabaseConfig getCurrentDatabaseConfig() {
        String configKey = propertiesLoader.getProperty("quiz.database.config", "development");
        return DatabaseConfig.fromConfigKey(configKey);
    }
    
    /**
     * Prüft, ob alle erforderlichen Properties für den aktuellen Datenbanktyp verfügbar sind.
     */
    public boolean validateConfiguration() {
        try {
            DatabaseType dbType = getCurrentDatabaseType();
            return validateConfiguration(dbType);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Prüft, ob alle erforderlichen Properties für einen spezifischen Datenbanktyp verfügbar sind.
     */
    public boolean validateConfiguration(DatabaseType dbType) {
        for (String requiredProperty : dbType.getRequiredProperties()) {
            String value = propertiesLoader.getProperty(requiredProperty);
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gibt eine Zusammenfassung der aktuellen Datenbankkonfiguration zurück.
     */
    public String getConfigurationSummary() {
        try {
            DatabaseType dbType = getCurrentDatabaseType();
            DatabaseConfig config = getCurrentDatabaseConfig();
            
            StringBuilder summary = new StringBuilder();
            summary.append("Datenbank: ").append(dbType.getDisplayName()).append("\n");
            summary.append("Konfiguration: ").append(config.getDisplayName()).append("\n");
            summary.append("Status: ").append(validateConfiguration() ? "OK" : "Fehlerhaft");
            
            return summary.toString();
        } catch (Exception e) {
            return "Konfiguration konnte nicht geladen werden: " + e.getMessage();
        }
    }
    
    /**
     * Testet die Datenbankverbindung.
     */
    public boolean testConnection() {
        try (Connection conn = createConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Verbindungstest fehlgeschlagen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gibt alle verfügbaren Datenbanktypen zurück.
     */
    public DatabaseType[] getAvailableDatabaseTypes() {
        return DatabaseType.getAvailableTypes();
    }
    
    /**
     * Gibt alle verfügbaren Datenbankkonfigurationen zurück.
     */
    public DatabaseConfig[] getAvailableDatabaseConfigs() {
        return DatabaseConfig.getAvailableConfigs();
    }
}
