package gui.common;

import java.awt.Component;
import gui.AbstractMainPanel;
import gui.interfaces.QuizLeftPanel;

/**
 * Hilfsklasse für die gemeinsame Panel-Initialisierung.
 * Reduziert Code-Duplikation bei der Einrichtung von MainPanels.
 */
public class PanelInitializer {
    
    /**
     * Richtet ein MainPanel mit den Standard-Verbindungen ein.
     * 
     * @param mainPanel Das zu konfigurierende MainPanel
     * @param panelLinks Das linke Panel
     * @param panelRechts Das rechte Panel
     * @param panelUnten Das untere Panel
     * @param dataProvider Der DataProvider
     */
    public static void setupMainPanel(
            AbstractMainPanel mainPanel,
            Component panelLinks,
            Component panelRechts,
            Component panelUnten,
            Object dataProvider) {
        
        // Panel-Gewichtungen setzen
        mainPanel.setPanelWeights(0.8, 0.2);
        
        // Verbindung zwischen linkem und rechtem Panel
        if (panelRechts instanceof HasLinkesPanel) {
            ((HasLinkesPanel) panelRechts).setLinkesPanel(panelLinks);
        }
        
        // MainPanel-Referenz setzen
        if (panelUnten instanceof HasMainPanel) {
            ((HasMainPanel) panelUnten).setMainPanel(mainPanel);
        }
        
        // PropertyChange-Listener registrieren
        registerPropertyChangeListeners(mainPanel, dataProvider);
    }
    
    /**
     * Registriert PropertyChange-Listener für den DataProvider.
     */
    private static void registerPropertyChangeListeners(AbstractMainPanel mainPanel, Object dataProvider) {
        if (dataProvider instanceof quiz.logic.ServiceBackedDataProvider) {
            quiz.logic.ServiceBackedDataProvider provider = (quiz.logic.ServiceBackedDataProvider) dataProvider;
            
            // Erstelle PropertyChangeHandler mit Builder-Pattern
            PropertyChangeHandler handler = new PropertyChangeHandler.Builder(mainPanel.getClass().getSimpleName())
                .addHandler("themenChanged", () -> {
                    if (mainPanel instanceof AbstractMainPanelWithWorker.HasThemenUpdate) {
                        ((AbstractMainPanelWithWorker.HasThemenUpdate) mainPanel).ladeAlleThemen();
                    }
                })
                .addHandler("fragenChanged", () -> {
                    if (mainPanel instanceof AbstractMainPanelWithWorker.HasFragenUpdate) {
                        ((AbstractMainPanelWithWorker.HasFragenUpdate) mainPanel).aktualisiereFragenAnzeige();
                    }
                })
                .build();
            
            // Listener registrieren
            provider.addPropertyChangeListener(quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED, handler);
            provider.addPropertyChangeListener(quiz.logic.ServiceBackedDataProvider.FRAGEN_CHANGED, handler);
        }
    }
    
    // Marker-Interfaces für bessere Typsicherheit
    public interface HasLinkesPanel {
        void setLinkesPanel(Component linkesPanel);
    }
    
    public interface HasMainPanel {
        void setMainPanel(AbstractMainPanel mainPanel);
    }
}
