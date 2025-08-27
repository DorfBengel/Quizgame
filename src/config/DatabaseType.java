package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Enum für alle unterstützten Datenbanktypen.
 * Bietet eine typsichere und erweiterbare Lösung für verschiedene Datenbanken.
 */
public enum DatabaseType {
    
    SQLITE("sqlite", "org.sqlite.JDBC", "jdbc:sqlite:%s", "SQLite") {
        @Override
        public Connection createConnection(String... params) throws SQLException {
            if (params.length < 1) {
                throw new SQLException("SQLite benötigt einen Dateipfad");
            }
            String dbFile = params[0];
            return DriverManager.getConnection(String.format(getJdbcUrl(), dbFile));
        }
        
        @Override
        public String[] getRequiredProperties() {
            return new String[]{"quiz.database.sqlite.file"};
        }
        
        @Override
        public String getDefaultProperty(String propertyName) {
            if ("quiz.database.sqlite.file".equals(propertyName)) {
                return "quiz_datenbank.db";
            }
            return null;
        }
    },
    
    MARIADB("mariadb", "org.mariadb.jdbc.Driver", "jdbc:mariadb://%s:%s/%s", "MariaDB") {
        @Override
        public Connection createConnection(String... params) throws SQLException {
            if (params.length < 4) {
                throw new SQLException("MariaDB benötigt Host, Port, Datenbankname, Benutzer und Passwort");
            }
            String host = params[0];
            String port = params[1];
            String database = params[2];
            String user = params[3];
            String password = params.length > 4 ? params[4] : "";
            
            return DriverManager.getConnection(
                String.format(getJdbcUrl(), host, port, database), 
                user, 
                password
            );
        }
        
        @Override
        public String[] getRequiredProperties() {
            return new String[]{
                "quiz.database.mariadb.host",
                "quiz.database.mariadb.port", 
                "quiz.database.mariadb.name",
                "quiz.database.mariadb.user",
                "quiz.database.mariadb.password"
            };
        }
        
        @Override
        public String getDefaultProperty(String propertyName) {
            switch (propertyName) {
                case "quiz.database.mariadb.host": return "localhost";
                case "quiz.database.mariadb.port": return "3306";
                case "quiz.database.mariadb.name": return "quiz_db";
                case "quiz.database.mariadb.user": return "root";
                case "quiz.database.mariadb.password": return "";
                default: return null;
            }
        }
    },
    
    LOKAL("lokal", null, null, "Lokale Speicherung") {
        @Override
        public Connection createConnection(String... params) throws SQLException {
            throw new SQLException("Lokale Speicherung benötigt keine Datenbankverbindung");
        }
        
        @Override
        public String[] getRequiredProperties() {
            return new String[0]; // Keine Properties erforderlich
        }
        
        @Override
        public String getDefaultProperty(String propertyName) {
            return null;
        }
    };
    
    private final String configKey;
    private final String driverClass;
    private final String jdbcUrl;
    private final String displayName;
    
    DatabaseType(String configKey, String driverClass, String jdbcUrl, String displayName) {
        this.configKey = configKey;
        this.driverClass = driverClass;
        this.jdbcUrl = jdbcUrl;
        this.displayName = displayName;
    }
    
    /**
     * Erstellt eine Datenbankverbindung basierend auf den übergebenen Parametern.
     */
    public abstract Connection createConnection(String... params) throws SQLException;
    
    /**
     * Gibt die erforderlichen Properties für diesen Datenbanktyp zurück.
     */
    public abstract String[] getRequiredProperties();
    
    /**
     * Gibt den Standardwert für eine Property zurück.
     */
    public abstract String getDefaultProperty(String propertyName);
    
    /**
     * Lädt den JDBC-Treiber für diesen Datenbanktyp.
     */
    public void loadDriver() throws ClassNotFoundException {
        if (driverClass != null) {
            Class.forName(driverClass);
        }
    }
    
    /**
     * Prüft, ob alle erforderlichen Properties verfügbar sind.
     */
    public boolean hasRequiredProperties() {
        return getRequiredProperties().length == 0;
    }
    
    /**
     * Gibt den Konfigurationsschlüssel zurück.
     */
    public String getConfigKey() {
        return configKey;
    }
    
    /**
     * Gibt den JDBC-URL-Template zurück.
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    /**
     * Gibt den Anzeigenamen zurück.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gibt den Datenbanktyp basierend auf dem Konfigurationsschlüssel zurück.
     */
    public static DatabaseType fromConfigKey(String configKey) {
        if (configKey == null) {
            return LOKAL; // Standard;
        }
        
        for (DatabaseType type : values()) {
            if (type.configKey.equalsIgnoreCase(configKey)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unbekannter Datenbanktyp: " + configKey);
    }
    
    /**
     * Gibt alle verfügbaren Datenbanktypen als Array zurück.
     */
    public static DatabaseType[] getAvailableTypes() {
        return values();
    }
    
    /**
     * Prüft, ob der Datenbanktyp eine echte Datenbankverbindung benötigt.
     */
    public boolean requiresConnection() {
        return this != LOKAL;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
