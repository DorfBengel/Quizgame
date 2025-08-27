package gui.common;

import gui.AbstractMainPanel;

/**
 * Gemeinsames Interface für alle Action-Panels.
 * Definiert die Standard-Methoden, die jedes Action-Panel implementieren muss.
 */
public interface ActionPanel {
    
    /**
     * Setzt die Referenz auf das Hauptpanel.
     */
    void setMainPanel(AbstractMainPanel mainPanel);
    
    /**
     * Konfiguriert die Buttons des Panels.
     */
    void configureButtons();
    
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
}
