package config;

/**
 * Enum für vordefinierte Datenbankkonfigurationen.
 * Bietet verschiedene Umgebungen (Development, Production, Testing) mit
 * entsprechenden Einstellungen.
 */
public enum DatabaseConfig {
    
    DEVELOPMENT("development", DatabaseType.SQLITE, "Entwicklungsumgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.sqlite.file": return "quiz_dev.db";
                case "quiz.database.type": return "sqlite";
                case "quiz.ui.scale": return "1.0";
                case "quiz.logging.level": return "DEBUG";
                default: return null;
            }
        }
    },
    
    PRODUCTION("production", DatabaseType.MARIADB, "Produktionsumgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.type": return "mariadb";
                case "quiz.database.mariadb.host": return "prod-db.example.com";
                case "quiz.database.mariadb.port": return "3306";
                case "quiz.database.mariadb.name": return "quiz_prod";
                case "quiz.database.mariadb.user": return "quiz_user";
                case "quiz.database.mariadb.password": return "secure_password";
                case "quiz.ui.scale": return "1.0";
                case "quiz.logging.level": return "WARN";
                default: return null;
            }
        }
    },
    
    TESTING("testing", DatabaseType.SQLITE, "Testumgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.sqlite.file": return "quiz_test.db";
                case "quiz.database.type": return "sqlite";
                case "quiz.ui.scale": return "1.0";
                case "quiz.logging.level": return "INFO";
                default: return null;
            }
        }
    },
    
    LOCAL("local", DatabaseType.LOKAL, "Lokale Umgebung") {
        @Override
        public String getProperty(String key) {
            switch (key) {
                case "quiz.database.type": return "lokal";
                case "quiz.ui.scale": return "2.0";
                case "quiz.logging.level": return "INFO";
                default: return null;
            }
        }
    };
    
    private final String configKey;
    private final DatabaseType defaultDatabaseType;
    private final String displayName;
    
    DatabaseConfig(String configKey, DatabaseType defaultDatabaseType, String displayName) {
        this.configKey = configKey;
        this.defaultDatabaseType = defaultDatabaseType;
        this.displayName = displayName;
    }
    
    /**
     * Gibt den Wert für eine bestimmte Konfigurationsschlüssel zurück.
     */
    public abstract String getProperty(String key);
    
    /**
     * Gibt den Standard-Datenbanktyp für diese Konfiguration zurück.
     */
    public DatabaseType getDefaultDatabaseType() {
        return defaultDatabaseType;
    }
    
    /**
     * Gibt den Konfigurationsschlüssel zurück.
     */
    public String getConfigKey() {
        return configKey;
    }
    
    /**
     * Gibt den Anzeigenamen zurück.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gibt die Konfiguration basierend auf dem Schlüssel zurück.
     */
    public static DatabaseConfig fromConfigKey(String configKey) {
        if (configKey == null) {
            return DEVELOPMENT; // Standard
        }
        
        for (DatabaseConfig config : values()) {
            if (config.configKey.equalsIgnoreCase(configKey)) {
                return config;
            }
        }
        
        throw new IllegalArgumentException("Unbekannte Datenbankkonfiguration: " + configKey);
    }
    
    /**
     * Gibt alle verfügbaren Konfigurationen zurück.
     */
    public static DatabaseConfig[] getAvailableConfigs() {
        return values();
    }
    
    /**
     * Prüft, ob diese Konfiguration für eine bestimmte Umgebung geeignet ist.
     */
    public boolean isSuitableFor(String environment) {
        if (environment == null) return false;
        
        switch (environment.toLowerCase()) {
            case "dev":
            case "development":
                return this == DEVELOPMENT;
            case "prod":
            case "production":
                return this == PRODUCTION;
            case "test":
            case "testing":
                return this == TESTING;
            case "local":
                return this == LOCAL;
            default:
                return false;
        }
    }
    
    /**
     * Gibt eine Zusammenfassung der Konfiguration zurück.
     */
    public String getSummary() {
        return String.format("%s (%s) - %s", 
            displayName, 
            configKey, 
            defaultDatabaseType.getDisplayName());
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
