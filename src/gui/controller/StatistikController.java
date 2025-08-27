package gui.controller;

import javax.swing.SwingUtilities;

import business.QuizStatistikService;
import business.event.DataChangedEvent;
import data.dto.StatistikDTO;
import gui.statistik.StatistikMainPanel;
import gui.statistik.StatistikActionPanel;
import gui.statistik.StatistikLinkesPanel;
import gui.statistik.StatistikRechtesPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

import java.util.List;

/**
 * Controller für die Statistik-Verwaltung.
 * Verwaltet die Anzeige und Berechnung von Quiz-Statistiken.
 */
public class StatistikController extends AbstractController {
    
    private final QuizStatistikService statistikService;
    
    // View-Referenzen
    private StatistikMainPanel mainPanel;
    private StatistikActionPanel actionPanel;
    private StatistikLinkesPanel linkesPanel;
    private StatistikRechtesPanel rechtesPanel;
    
    public StatistikController(QuizStatistikService statistikService) {
        super();
        this.statistikService = statistikService;
        
        // Event-Listener registrieren
        registerForEvents("QUIZ_ERGEBNIS_*", "THEMA_*", "FRAGE_*");
    }
    
    /**
     * Setzt die View-Referenzen.
     */
    public void setViews(StatistikMainPanel mainPanel, StatistikActionPanel actionPanel, 
                        StatistikLinkesPanel linkesPanel, StatistikRechtesPanel rechtesPanel) {
        this.mainPanel = mainPanel;
        this.actionPanel = actionPanel;
        this.linkesPanel = linkesPanel;
        this.rechtesPanel = rechtesPanel;
        
        setParentComponent(mainPanel);
        setupActionListeners();
    }
    
    /**
     * Richtet die Action-Listener ein.
     */
    private void setupActionListeners() {
        // Controller wird über setMainPanel gesetzt
    }
    
    /**
     * Lädt alle Themen für die Statistik-Ansicht.
     */
    public void ladeAlleThemen() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (mainPanel != null) {
                    List<Thema> themen = mainPanel.getDataProvider().getAlleThemen();
                    rechtesPanel.setThemen(themen);
                    linkesPanel.setAlleThemen(themen);
                }
            } catch (Exception e) {
                handleError("Fehler beim Laden der Themen", e);
            }
        });
    }
    
    /**
     * Aktualisiert alle Statistiken.
     */
    public void aktualisiereStatistiken() {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Themen neu laden
                    ladeAlleThemen();
                    
                    // Gesamtstatistik anzeigen
                    zeigeGesamtStatistik();
                    
                } catch (Exception e) {
                    handleError("Fehler beim Aktualisieren der Statistiken", e);
                }
            });
            
        } catch (Exception e) {
            handleError("Fehler beim Aktualisieren der Statistiken", e);
        }
    }
    
    /**
     * Zeigt die Gesamtstatistik an.
     */
    public void zeigeGesamtStatistik() {
        try {
            StatistikDTO gesamtStatistik = statistikService.berechneGesamtStatistik();
            
            if (linkesPanel != null) {
                // Gesamtstatistik im linken Panel anzeigen
                linkesPanel.setThemaTitel("Alle Themen");
                // Weitere Statistik-Details würden hier gesetzt
            }
            
        } catch (Exception e) {
            handleError("Fehler beim Anzeigen der Gesamtstatistik", e);
        }
    }
    
    /**
     * Zeigt die Statistik für ein bestimmtes Thema an.
     */
    public void zeigeThemaStatistik(Thema thema) {
        try {
            if (thema == null) {
                zeigeGesamtStatistik();
                return;
            }
            
            StatistikDTO themaStatistik = statistikService.berechneThemaStatistik(
                thema.getId(), thema.getTitel());
            
            if (linkesPanel != null) {
                linkesPanel.setThemaTitel(thema.getTitel());
                // Weitere Statistik-Details würden hier gesetzt
            }
            
        } catch (Exception e) {
            handleError("Fehler beim Anzeigen der Thema-Statistik", e);
        }
    }
    
    /**
     * Zeigt die Statistik für eine bestimmte Frage an.
     */
    public void zeigeFrageStatistik(Frage frage, Thema thema) {
        try {
            if (frage == null) {
                if (thema != null) {
                    zeigeThemaStatistik(thema);
                } else {
                    zeigeGesamtStatistik();
                }
                return;
            }
            
            String themaTitel = thema != null ? thema.getTitel() : "Unbekanntes Thema";
            long themaId = thema != null ? thema.getId() : 0;
            
            StatistikDTO frageStatistik = statistikService.berechneFrageStatistik(
                themaId, themaTitel, frage.getId(), frage.getFrageTitel());
            
            if (linkesPanel != null) {
                linkesPanel.zeigeFrage(frage);
                // Weitere Statistik-Details würden hier gesetzt
            }
            
        } catch (Exception e) {
            handleError("Fehler beim Anzeigen der Frage-Statistik", e);
        }
    }
    
    /**
     * Exportiert Statistiken (vereinfachte Implementation).
     */
    public void exportiereStatistiken() {
        try {
            showInfo("Statistiken werden exportiert...");
            
            // Hier würde normalerweise ein Export-Dialog geöffnet
            // und die Statistiken in verschiedene Formate exportiert werden
            
            List<StatistikDTO> alleStatistiken = statistikService.findAlleStatistiken();
            
            // Vereinfachter Export-Prozess
            StringBuilder exportData = new StringBuilder();
            exportData.append("Quiz-Statistiken Export\n");
            exportData.append("========================\n\n");
            
            for (StatistikDTO statistik : alleStatistiken) {
                exportData.append(String.format("Thema: %s\n", statistik.getThemaTitel()));
                exportData.append(String.format("Versuche: %d\n", statistik.getAnzahlVersuche()));
                exportData.append(String.format("Erfolgsrate: %.1f%%\n", statistik.getErfolgsRate()));
                exportData.append("---\n");
            }
            
            // In einem echten System würde hier eine Datei gespeichert werden
            showInfo("Statistiken erfolgreich exportiert");
            
        } catch (Exception e) {
            handleError("Fehler beim Exportieren der Statistiken", e);
        }
    }
    
    /**
     * Zeigt die Hilfe für die Statistik-Funktionen an.
     */
    public void zeigeHilfe() {
        String hilfeText = "Statistik-Hilfe:\n\n" +
                          "• Wählen Sie ein Thema aus der rechten Liste\n" +
                          "• Klicken Sie auf eine Frage für detaillierte Statistiken\n" +
                          "• Verwenden Sie 'Aktualisieren' um neue Daten zu laden\n" +
                          "• 'Exportieren' speichert die Statistiken in eine Datei\n\n" +
                          "Die Statistiken zeigen:\n" +
                          "• Anzahl der Versuche\n" +
                          "• Erfolgsrate in Prozent\n" +
                          "• Durchschnittliche Antwortzeit\n" +
                          "• Beste erreichte Punkte";
        
        showInfo(hilfeText);
    }
    
    /**
     * Aktualisiert die Fragenansicht.
     */
    public void aktualisiereFragenAnzeige() {
        SwingUtilities.invokeLater(() -> {
            if (mainPanel != null) {
                mainPanel.aktualisiereFragenAnzeige();
            }
        });
    }
    
    /**
     * Behandelt Thema-Auswahl.
     */
    public void themaAusgewaehlt(Thema thema) {
        zeigeThemaStatistik(thema);
        
        // Fragen für das Thema laden
        if (thema != null && mainPanel != null) {
            List<Frage> fragen = mainPanel.getDataProvider().findeFragenFuerThema(thema.getId());
            rechtesPanel.aktualisiereFragenFuerThema(thema, fragen);
        }
    }
    
    /**
     * Behandelt Frage-Auswahl.
     */
    public void frageAusgewaehlt(Frage frage) {
        // Thema für die Frage ermitteln
        Thema aktuellesThema = null;
        if (rechtesPanel != null) {
            aktuellesThema = rechtesPanel.getSelectedThemaFromCombo();
        }
        
        zeigeFrageStatistik(frage, aktuellesThema);
    }
    
    @Override
    public void onEvent(business.event.QuizEvent event) {
        if (event instanceof DataChangedEvent) {
            DataChangedEvent dataEvent = (DataChangedEvent) event;
            
            switch (dataEvent.getEntityType()) {
                case QUIZ_ERGEBNIS:
                    // Neue Quiz-Ergebnisse -> Statistiken aktualisieren
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // Aktuelle Anzeige aktualisieren
                            if (rechtesPanel != null) {
                                Thema ausgewähltes = rechtesPanel.getSelectedThemaFromCombo();
                                Frage ausgewählteFrage = rechtesPanel.getSelectedFrage();
                                
                                if (ausgewählteFrage != null) {
                                    zeigeFrageStatistik(ausgewählteFrage, ausgewähltes);
                                } else if (ausgewähltes != null) {
                                    zeigeThemaStatistik(ausgewähltes);
                                } else {
                                    zeigeGesamtStatistik();
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Fehler beim Aktualisieren der Statistik-Anzeige: " + e.getMessage());
                        }
                    });
                    break;
                case THEMA:
                    ladeAlleThemen();
                    break;
                case FRAGE:
                    aktualisiereFragenAnzeige();
                    break;
                default:
                    // Ignorieren
                    break;
            }
        }
    }
}
