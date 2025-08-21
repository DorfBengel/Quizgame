/**
 * Daten-Schicht der Quiz-Anwendung.
 * 
 * <p>Dieses Package enthält alle Komponenten für den Datenzugriff und die
 * Datenpersistierung der Quiz-Anwendung. Es implementiert das Repository-Pattern
 * und stellt eine saubere API für den Datenzugriff bereit.</p>
 * 
 * <p>Hauptkomponenten:</p>
 * <ul>
 *   <li><strong>dto</strong> - Data Transfer Objects für die UI-Kommunikation</li>
 *   <li><strong>repository</strong> - Repository-Interfaces für den Datenzugriff</li>
 *   <li><strong>persistence</strong> - Implementierungen der Persistierung</li>
 * </ul>
 * 
 * <p>Architektur-Prinzipien:</p>
 * <ul>
 *   <li>Data Access Object Pattern - Abstraktion der Datenzugriffslogik</li>
 *   <li>Repository Pattern - Einheitliche API für verschiedene Datenquellen</li>
 *   <li>DTO Pattern - Übertragung von Daten zwischen Schichten</li>
 *   <li>Factory Pattern - Erstellung von Repository-Implementierungen</li>
 * </ul>
 * 
 * <p>Unterstützte Datenquellen:</p>
 * <ul>
 *   <li>SQLite-Datenbank - Für lokale Entwicklung und Tests</li>
 *   <li>MariaDB/MySQL - Für Produktionsumgebungen</li>
 *   <li>Lokale Dateispeicherung - Für Offline-Betrieb</li>
 * </ul>
 * 
 * <p>Die Daten-Schicht ist von der Business-Logik und der UI getrennt und stellt
 * eine saubere API für den Datenzugriff bereit. Sie unterstützt verschiedene
 * Persistierungsstrategien und kann einfach erweitert werden.</p>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * // Repository über Factory erstellen
 * QuizRepository repository = RepositoryFactory.createRepository();
 * 
 * // Daten über Repository abrufen
 * List<ThemaDTO> themen = repository.findAllThemen();
 * ThemaDTO thema = repository.findThemaById(1L);
 * 
 * // Daten über Repository speichern
 * repository.saveThema(thema);
 * repository.deleteThema(1L);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see business
 * @see gui
 * @see config
 */
package data;
