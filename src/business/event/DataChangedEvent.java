package business.event;

/**
 * Generisches Event für Datenänderungen.
 * 
 * <p>Diese Klasse ersetzt die spezifischen Event-Klassen für einfachere Wartung.
 * Sie implementiert das Observer-Pattern und ermöglicht die lose Kopplung zwischen
 * verschiedenen Komponenten bei Datenänderungen.</p>
 * 
 * <p>Event-Typen:</p>
 * <ul>
 *   <li>CREATED - Neue Daten wurden erstellt</li>
 *   <li>UPDATED - Bestehende Daten wurden aktualisiert</li>
 *   <li>DELETED - Daten wurden gelöscht</li>
 * </ul>
 * 
 * <p>Entitäts-Typen:</p>
 * <ul>
 *   <li>THEMA - Themen-bezogene Änderungen</li>
 *   <li>FRAGE - Fragen-bezogene Änderungen</li>
 *   <li>QUIZ_ERGEBNIS - Quiz-Ergebnis-Änderungen</li>
 * </ul>
 * 
 * <p>Verwendung:</p>
 * <pre>
 * DataChangedEvent event = new DataChangedEvent(
 *     "FrageService", 
 *     DataChangedEvent.ChangeType.CREATED,
 *     DataChangedEvent.EntityType.FRAGE, 
 *     frage
 * );
 * EventManager.getInstance().fireEvent(event);
 * </pre>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizEvent
 * @see EventManager
 * @see EventListener
 */
public class DataChangedEvent extends QuizEvent {

	/**
	 * Enum für die Art der Datenänderung.
	 */
	public enum ChangeType {
		/** Neue Daten wurden erstellt */
		CREATED, 
		/** Bestehende Daten wurden aktualisiert */
		UPDATED, 
		/** Daten wurden gelöscht */
		DELETED
	}

	/**
	 * Enum für den Typ der geänderten Entität.
	 */
	public enum EntityType {
		/** Themen-bezogene Änderungen */
		THEMA, 
		/** Fragen-bezogene Änderungen */
		FRAGE, 
		/** Quiz-Ergebnis-Änderungen */
		QUIZ_ERGEBNIS
	}

	private final ChangeType changeType;
	private final EntityType entityType;
	private final Object entity;

	/**
	 * Erstellt ein neues DataChangedEvent.
	 * 
	 * @param source Der Name der Komponente, die das Event feuert
	 * @param changeType Die Art der Datenänderung
	 * @param entityType Der Typ der geänderten Entität
	 * @param entity Das geänderte Datenobjekt
	 */
	public DataChangedEvent(String source, ChangeType changeType, EntityType entityType, Object entity) {
		super(source);
		this.changeType = changeType;
		this.entityType = entityType;
		this.entity = entity;
	}

	/**
	 * Gibt den Typ der Datenänderung zurück.
	 * 
	 * @return Die Art der Datenänderung (CREATED, UPDATED, DELETED)
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	/**
	 * Gibt den Typ der geänderten Entität zurück.
	 * 
	 * @return Der Typ der Entität (THEMA, FRAGE, QUIZ_ERGEBNIS)
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * Gibt das geänderte Datenobjekt zurück.
	 * 
	 * @return Das Datenobjekt, das geändert wurde
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * Gibt den Event-Typ als String zurück.
	 * 
	 * <p>Der Event-Typ wird aus dem Entity-Typ und dem Change-Type zusammengesetzt,
	 * z.B. "FRAGE_CREATED" oder "THEMA_UPDATED".</p>
	 * 
	 * @return Der Event-Typ als String
	 */
	@Override
	public String getEventType() {
		return entityType.name() + "_" + changeType.name();
	}
}
