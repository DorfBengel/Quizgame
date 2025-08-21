package gui.controller;

import javax.swing.SwingUtilities;

import business.QuizStatistikService;
import business.event.DataChangedEvent;
import gui.quiz.QuizMainPanel;
import gui.quiz.QuizActionPanel;
import gui.quiz.QuizspielPanelLinks;
import gui.fragen.QuizfragenRechtesPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

import java.util.List;
import java.util.ArrayList;

/**
 * Controller für das Quiz-System.
 * Verwaltet Quiz-Sessions, Antworten und Ergebnisse.
 */
public class QuizController extends AbstractController {
    
    private final QuizStatistikService statistikService;
    
    // View-Referenzen
    private QuizMainPanel mainPanel;
    private QuizActionPanel actionPanel;
    private QuizspielPanelLinks linkesPanel;
    private QuizfragenRechtesPanel rechtesPanel;
    
    // Quiz-Status
    private List<Frage> aktuelleFragen = new ArrayList<>();
    private int aktuellerFragenIndex = 0;
    private int punkte = 0;
    private long quizStartZeit;
    private long antwortStartZeit;
    private boolean quizAktiv = false;
    
    public QuizController(QuizStatistikService statistikService) {
        super();
        this.statistikService = statistikService;
        
        // Event-Listener registrieren
        registerForEvents("FRAGE_*", "THEMA_*");
    }
    
    /**
     * Setzt die View-Referenzen.
     */
    public void setViews(QuizMainPanel mainPanel, QuizActionPanel actionPanel, 
                        QuizspielPanelLinks linkesPanel, QuizfragenRechtesPanel rechtesPanel) {
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
     * Lädt alle Themen für die Quiz-Ansicht.
     */
    public void ladeAlleThemen() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (mainPanel != null) {
                    List<Thema> themen = mainPanel.getDataProvider().getAlleThemen();
                    rechtesPanel.setThemen(themen);
                }
            } catch (Exception e) {
                handleError("Fehler beim Laden der Themen", e);
            }
        });
    }
    
    /**
     * Startet ein Quiz mit einer ausgewählten Frage.
     */
    public void starteQuiz(Frage startFrage) {
        try {
            if (startFrage == null) {
                showWarning("Bitte wählen Sie eine Frage zum Starten aus");
                return;
            }
            
            // Quiz-Status initialisieren
            resetQuizStatus();
            quizStartZeit = System.currentTimeMillis();
            quizAktiv = true;
            
            // Fragen für das Thema laden (vereinfacht)
            aktuelleFragen = new ArrayList<>();
            aktuelleFragen.add(startFrage);
            aktuellerFragenIndex = 0;
            
            // Erste Frage anzeigen
            zeigeAktuelleFrage();
            
            showInfo("Quiz gestartet!");
            
        } catch (Exception e) {
            handleError("Fehler beim Starten des Quiz", e);
        }
    }
    
    /**
     * Verarbeitet eine abgegebene Antwort.
     */
    public void gebeAntwortAb(boolean[] ausgewählteAntworten) {
        try {
            if (!quizAktiv) {
                showWarning("Kein aktives Quiz");
                return;
            }
            
            Frage aktuelleFrage = aktuelleFragen.get(aktuellerFragenIndex);
            boolean antwortRichtig = istAntwortRichtig(aktuelleFrage, ausgewählteAntworten);
            
            // Antwortzeit berechnen
            long antwortZeit = System.currentTimeMillis() - antwortStartZeit;
            int antwortZeitSekunden = (int) (antwortZeit / 1000);
            
            // Punkte vergeben
            int gewonnenePunkte = 0;
            if (antwortRichtig) {
                gewonnenePunkte = 1;
                punkte += gewonnenePunkte;
                showInfo("Richtig! +1 Punkt");
            } else {
                showInfo("Falsch! Keine Punkte");
            }
            
            // Ergebnis speichern
            speichereQuizErgebnis(aktuelleFrage, antwortRichtig, antwortZeitSekunden, gewonnenePunkte);
            
            // Richtige Antworten anzeigen
            linkesPanel.zeigeKorrekteAntworten(aktuelleFrage);
            
            // Nächste Frage oder Quiz beenden
            aktuellerFragenIndex++;
            if (aktuellerFragenIndex < aktuelleFragen.size()) {
                // Nächste Frage vorbereiten
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(2000); // 2 Sekunden warten
                        naechsteFrageAnzeigen();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                });
            } else {
                quizBeenden();
            }
            
        } catch (Exception e) {
            handleError("Fehler beim Verarbeiten der Antwort", e);
        }
    }
    
    /**
     * Zeigt die korrekte Antwort für eine Frage an.
     */
    public void zeigeAntwort() {
        try {
            if (!quizAktiv || aktuellerFragenIndex >= aktuelleFragen.size()) {
                showWarning("Keine aktive Frage verfügbar");
                return;
            }
            
            Frage aktuelleFrage = aktuelleFragen.get(aktuellerFragenIndex);
            
            // Antwort als "vorher gezeigt" markieren und speichern
            long antwortZeit = System.currentTimeMillis() - antwortStartZeit;
            int antwortZeitSekunden = (int) (antwortZeit / 1000);
            
            speichereQuizErgebnis(aktuelleFrage, false, antwortZeitSekunden, 0, true);
            
            // Korrekte Antworten anzeigen
            linkesPanel.zeigeKorrekteAntworten(aktuelleFrage);
            
            showInfo("Antwort angezeigt - keine Punkte");
            
        } catch (Exception e) {
            handleError("Fehler beim Anzeigen der Antwort", e);
        }
    }
    
    /**
     * Zeigt eine Frage in der Detailansicht an.
     */
    public void zeigeFrage(Frage frage) {
        if (linkesPanel != null && frage != null) {
            linkesPanel.zeigeFrage(frage);
            antwortStartZeit = System.currentTimeMillis();
        }
    }
    
    /**
     * Zeigt die aktuelle Frage im Quiz an.
     */
    private void zeigeAktuelleFrage() {
        if (aktuellerFragenIndex < aktuelleFragen.size()) {
            Frage aktuelleFrage = aktuelleFragen.get(aktuellerFragenIndex);
            zeigeFrage(aktuelleFrage);
            antwortStartZeit = System.currentTimeMillis();
        }
    }
    
    /**
     * Geht zur nächsten Frage über.
     */
    private void naechsteFrageAnzeigen() {
        if (aktuellerFragenIndex < aktuelleFragen.size()) {
            zeigeAktuelleFrage();
        }
    }
    
    /**
     * Beendet das Quiz.
     */
    private void quizBeenden() {
        quizAktiv = false;
        
        String ergebnis = String.format("Quiz beendet! Ihr Ergebnis: %d/%d Punkten", 
                                       punkte, aktuelleFragen.size());
        showInfo(ergebnis);
        
        resetQuizStatus();
    }
    
    /**
     * Setzt den Quiz-Status zurück.
     */
    private void resetQuizStatus() {
        aktuelleFragen.clear();
        aktuellerFragenIndex = 0;
        punkte = 0;
        quizAktiv = false;
        quizStartZeit = 0;
        antwortStartZeit = 0;
    }
    
    /**
     * Prüft, ob die abgegebene Antwort richtig ist.
     */
    private boolean istAntwortRichtig(Frage frage, boolean[] ausgewählteAntworten) {
        if (frage.getAntworten().size() != ausgewählteAntworten.length) {
            return false;
        }
        
        for (int i = 0; i < frage.getAntworten().size(); i++) {
            boolean sollRichtigSein = frage.getAntworten().get(i).istRichtig();
            boolean istAusgewählt = ausgewählteAntworten[i];
            
            if (sollRichtigSein != istAusgewählt) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Speichert ein Quiz-Ergebnis.
     */
    private void speichereQuizErgebnis(Frage frage, boolean richtig, int antwortZeitSekunden, int punkte) {
        speichereQuizErgebnis(frage, richtig, antwortZeitSekunden, punkte, false);
    }
    
    /**
     * Speichert ein Quiz-Ergebnis mit Zusatzinformationen.
     */
    private void speichereQuizErgebnis(Frage frage, boolean richtig, int antwortZeitSekunden, 
                                     int punkte, boolean antwortVorherGezeigt) {
        try {
            // Thema-ID ermitteln (vereinfacht über DataProvider)
            long themaId = 0; // Würde normalerweise über Frage->Thema-Relation ermittelt
            
            statistikService.saveQuizErgebnis(themaId, frage.getId(), richtig, 
                                           antwortVorherGezeigt, antwortZeitSekunden, punkte);
            
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern des Quiz-Ergebnisses: " + e.getMessage());
            // Fehler nicht an Benutzer weiterleiten, da das Quiz weiterlaufen soll
        }
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
    
    @Override
    public void onEvent(business.event.QuizEvent event) {
        if (event instanceof DataChangedEvent) {
            DataChangedEvent dataEvent = (DataChangedEvent) event;
            
            switch (dataEvent.getEntityType()) {
                case FRAGE:
                    aktualisiereFragenAnzeige();
                    break;
                case THEMA:
                    ladeAlleThemen();
                    break;
                default:
                    // Ignorieren
                    break;
            }
        }
    }
}
