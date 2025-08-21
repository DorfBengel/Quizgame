package gui.common;

/**
 * Gemeinsames Interface für alle Inhalts-Panels.
 * Definiert die Standard-Methoden für linke und rechte Panels.
 */
public interface ContentPanel {
    
    /**
     * Löscht den Inhalt des Panels.
     */
    void clearContent();
    
    /**
     * Setzt den Lese-Modus des Panels.
     */
    void setReadOnly(boolean readOnly);
    
    /**
     * Gibt den Namen des Panels zurück.
     */
    String getPanelName();
    
    /**
     * Wird aufgerufen, wenn das Panel aktiviert wird.
     */
    default void onActivate() {
        // Standard-Implementierung: nichts tun
    }
    
    /**
     * Wird aufgerufen, wenn das Panel deaktiviert wird.
     */
    default void onDeactivate() {
        // Standard-Implementierung: nichts tun
    }
    
    /**
     * Aktualisiert die Anzeige des Panels.
     */
    default void refresh() {
        // Standard-Implementierung: nichts tun
    }
}
