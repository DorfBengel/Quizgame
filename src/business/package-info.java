/**
 * Business-Logik-Schicht der Quiz-Anwendung.
 * 
 * <p>Dieses Package enthält die zentrale Geschäftslogik der Quiz-Anwendung.
 * Es implementiert das Service-Layer-Pattern und stellt eine Abstraktionsschicht
 * zwischen der GUI und der Datenpersistierung dar.</p>
 * 
 * <p>Hauptkomponenten:</p>
 * <ul>
 *   <li><strong>Services</strong> - Geschäftslogik für Themen, Fragen und Statistiken</li>
 *   <li><strong>Events</strong> - Event-System für die lose Kopplung zwischen Schichten</li>
 *   <li><strong>QuizApplication</strong> - Zentrale Anwendungsklasse mit Singleton-Pattern</li>
 * </ul>
 * 
 * <p>Architektur-Prinzipien:</p>
 * <ul>
 *   <li>Separation of Concerns - Klare Trennung von Geschäftslogik und Datenzugriff</li>
 *   <li>Dependency Injection - Services erhalten ihre Abhängigkeiten über Konstruktoren</li>
 *   <li>Event-driven Architecture - Lose Kopplung durch Event-basierte Kommunikation</li>
 *   <li>Business Rule Validation - Alle Eingabedaten werden vor dem Speichern validiert</li>
 * </ul>
 * 
 * <p>Die Business-Logik implementiert die Geschäftsregeln und validiert die
 * Eingabedaten, bevor sie an die Daten-Schicht weitergegeben werden. Sie
 * stellt sicher, dass alle Geschäftsregeln eingehalten werden und die
 * Datenintegrität gewährleistet ist.</p>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * QuizApplication app = QuizApplication.getInstance();
 * ThemaService themaService = app.getThemaService();
 * FrageService frageService = app.getFrageService();
 * 
 * // Thema erstellen
 * ThemaDTO thema = themaService.createThema("Java", "Java-Programmierung");
 * 
 * // Frage erstellen
 * FrageDTO frage = frageService.createFrage("Was ist Java?", "Beschreibung...", antworten, thema.getId());
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see business.event
 * @see data.dto
 * @see data.repository
 */
package business;
