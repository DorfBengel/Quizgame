package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lädt und verwaltet die Anwendungskonfiguration aus Properties-Dateien.
 */
public class PropertiesLoader {

	private static final String PROPERTIES_FILE = "application.properties";
	private static Properties properties;

	static {
		loadProperties();
	}

	private static void loadProperties() {
		properties = new Properties();
		
		// Versuche zuerst, die Datei aus dem Projektstammverzeichnis zu laden
		File projectRoot = new File(".").getAbsoluteFile().getParentFile();
		File configFile = new File(projectRoot, PROPERTIES_FILE);
		
		if (configFile.exists()) {
			try (InputStream input = new FileInputStream(configFile)) {
				properties.load(input);
				System.out.println("Properties geladen aus: " + configFile.getAbsolutePath());
				return;
			} catch (IOException e) {
				System.err.println("Fehler beim Laden der Properties aus " + configFile.getAbsolutePath() + ": " + e.getMessage());
			}
		}
		
		// Fallback: Versuche über ClassLoader
		try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
			if (input != null) {
				properties.load(input);
				System.out.println("Properties geladen über ClassLoader");
				return;
			}
		} catch (IOException e) {
			System.err.println("Fehler beim Laden der Properties über ClassLoader: " + e.getMessage());
		}
		
		// Wenn keine Datei gefunden wurde, verwende Standardwerte
		System.err.println("Konnte " + PROPERTIES_FILE + " nicht finden. Verwende Standardwerte.");
		setDefaultProperties();
	}

	private static void setDefaultProperties() {
		// Datenbank-Konfiguration über Enums
		properties.setProperty("quiz.database.type", "lokal"); // Standard: Lokale Speicherung
		properties.setProperty("quiz.database.config", "development"); // Standard: Entwicklungsumgebung
		properties.setProperty("quiz.database.sqlite.file", "quiz_datenbank.db");
		properties.setProperty("quiz.database.mariadb.host", "localhost");
		properties.setProperty("quiz.database.mariadb.port", "3306");
		properties.setProperty("quiz.database.mariadb.name", "quiz_db");
		properties.setProperty("quiz.database.mariadb.user", "root");
		properties.setProperty("quiz.database.mariadb.password", ""); // Leer für Sicherheit
		
		// UI-Konfiguration
		properties.setProperty("quiz.ui.scale", "2.0");
		properties.setProperty("quiz.ui.window.width", "900");
		properties.setProperty("quiz.ui.window.height", "600");
		
		// Validierung
		properties.setProperty("quiz.validation.thema.titel.maxlength", "100");
		properties.setProperty("quiz.validation.frage.titel.maxlength", "100");
		properties.setProperty("quiz.validation.frage.text.maxlength", "500");
		properties.setProperty("quiz.validation.antwort.text.maxlength", "200");
		
		// Logging
		properties.setProperty("quiz.logging.level", "INFO");
		properties.setProperty("quiz.logging.file", "quiz.log");
	}

	/**
	 * Gibt den Wert einer Property zurück.
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Gibt den Wert einer Property mit Standardwert zurück.
	 */
	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Gibt eine Property als Integer zurück.
	 */
	public static int getIntProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt(properties.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Gibt eine Property als Double zurück.
	 */
	public static double getDoubleProperty(String key, double defaultValue) {
		try {
			return Double.parseDouble(properties.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Gibt eine Property als Boolean zurück.
	 */
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * Gibt die Fensterbreite aus der Konfiguration zurück.
	 */
	public static int getWindowWidth() {
		return getIntProperty("quiz.ui.window.width", 900);
	}

	/**
	 * Gibt die Fensterhöhe aus der Konfiguration zurück.
	 */
	public static int getWindowHeight() {
		return getIntProperty("quiz.ui.window.height", 600);
	}

	/**
	 * Gibt den UI-Skalierungsfaktor aus der Konfiguration zurück.
	 */
	public static double getUIScale() {
		return getDoubleProperty("quiz.ui.scale", 2.0);
	}

	/**
	 * Lädt die Properties neu.
	 */
	public static void reload() {
		loadProperties();
	}
	
	/**
	 * Gibt den aktuellen Datenbanktyp als Enum zurück.
	 */
	public static DatabaseType getDatabaseType() {
		try {
			System.out.println("PropertiesLoader: Bestimme Datenbanktyp...");
			
			// Versuche zuerst den expliziten Datenbanktyp zu lesen
			String dbType = getProperty("quiz.database.type");
			System.out.println("PropertiesLoader: quiz.database.type = " + dbType);
			
			if (dbType != null) {
				System.out.println("PropertiesLoader: Verwende expliziten Datenbanktyp: " + dbType);
				return DatabaseType.fromConfigKey(dbType);
			}
			
			// Fallback: Leite den Typ aus der Konfiguration ab
			System.out.println("PropertiesLoader: Lade Datenbankkonfiguration...");
			DatabaseConfig config = getDatabaseConfig();
			System.out.println("PropertiesLoader: Konfiguration geladen: " + config.getDisplayName());
			System.out.println("PropertiesLoader: Standard-Datenbanktyp: " + config.getDefaultDatabaseType().getDisplayName());
			return config.getDefaultDatabaseType();
		} catch (Exception e) {
			System.err.println("Fehler beim Bestimmen des Datenbanktyps: " + e.getMessage());
			e.printStackTrace();
			System.err.println("Verwende Standard-Datenbanktyp: LOKAL");
			return DatabaseType.LOKAL;
		}
	}
	
	/**
	 * Gibt die aktuelle Datenbankkonfiguration als Enum zurück.
	 */
	public static DatabaseConfig getDatabaseConfig() {
		try {
			String configKey = getProperty("quiz.database.config", "development");
			System.out.println("PropertiesLoader: quiz.database.config = " + configKey);
			DatabaseConfig config = DatabaseConfig.fromConfigKey(configKey);
			System.out.println("PropertiesLoader: Konfiguration gefunden: " + config.getDisplayName());
			return config;
		} catch (Exception e) {
			System.err.println("Fehler beim Laden der Datenbankkonfiguration: " + e.getMessage());
			System.err.println("Verwende Standard-Konfiguration: DEVELOPMENT");
			return DatabaseConfig.DEVELOPMENT;
		}
	}
	
	/**
	 * Prüft, ob alle erforderlichen Datenbank-Properties verfügbar sind.
	 */
	public static boolean validateDatabaseConfiguration() {
		try {
			DatabaseType dbType = getDatabaseType();
			for (String requiredProperty : dbType.getRequiredProperties()) {
				String value = getProperty(requiredProperty);
				if (value == null || value.trim().isEmpty()) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Gibt eine Zusammenfassung der aktuellen Datenbankkonfiguration zurück.
	 */
	public static String getDatabaseConfigurationSummary() {
		try {
			DatabaseType dbType = getDatabaseType();
			DatabaseConfig config = getDatabaseConfig();
			
			StringBuilder summary = new StringBuilder();
			summary.append("Datenbank: ").append(dbType.getDisplayName()).append("\n");
			summary.append("Konfiguration: ").append(config.getDisplayName()).append("\n");
			summary.append("Status: ").append(validateDatabaseConfiguration() ? "OK" : "Fehlerhaft");
			
			return summary.toString();
		} catch (Exception e) {
			return "Konfiguration konnte nicht geladen werden: " + e.getMessage();
		}
	}
}
