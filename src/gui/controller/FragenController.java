package gui.controller;

import javax.swing.SwingUtilities;

import business.FrageService;
import business.event.DataChangedEvent;
import data.dto.FrageDTO;
import exception.ValidationException;
import gui.fragen.FragenMainPanel;
import gui.fragen.QuizfragenActionPanel;
import gui.fragen.QuizfragenLinkesPanel;
import gui.fragen.QuizfragenRechtesPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

import java.util.List;
import java.util.ArrayList;

/**
 * Controller für die Fragen-Verwaltung.
 * Implementiert das MVC-Pattern durch klare Trennung von Logik und Präsentation.
 */
public class FragenController extends AbstractController {
    
    private final FrageService frageService;
    
    // View-Referenzen
    private FragenMainPanel mainPanel;
    private QuizfragenActionPanel actionPanel;
    private QuizfragenLinkesPanel linkesPanel;
    private QuizfragenRechtesPanel rechtesPanel;
    
    public FragenController(FrageService frageService) {
        super();
        this.frageService = frageService;
        
        // Event-Listener registrieren
        registerForEvents("FRAGE_*", "THEMA_*");
    }
    
    /**
     * Setzt die View-Referenzen.
     */
    public void setViews(FragenMainPanel mainPanel, QuizfragenActionPanel actionPanel, 
                        QuizfragenLinkesPanel linkesPanel, QuizfragenRechtesPanel rechtesPanel) {
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
     * Lädt alle Themen für die Fragen-Ansicht.
     */
    public void ladeAlleThemen() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Hier würde normalerweise über ThemaService geladen
                // Für jetzt nutzen wir den DataProvider des MainPanels
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
     * Erstellt eine neue Frage.
     */
    public void erstelleFrage(String titel, String text, List<String> antworten, 
                             List<Boolean> richtigFlags, long themaId) {
        try {
            validateFrageData(titel, text, antworten, richtigFlags);
            
            // DTO-Konvertierung
            List<data.dto.AntwortDTO> antwortDTOs = new ArrayList<>();
            for (int i = 0; i < antworten.size(); i++) {
                if (antworten.get(i) != null && !antworten.get(i).trim().isEmpty()) {
                    antwortDTOs.add(new data.dto.AntwortDTO(0, antworten.get(i), richtigFlags.get(i)));
                }
            }
            
            FrageDTO neueFrageDTO = frageService.createFrage(titel, text, antwortDTOs, themaId);
            
            // Erfolgsmeldung
            showInfo("Frage erfolgreich erstellt");
            
            // Views aktualisieren
            refreshViews();
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Erstellen der Frage", e);
        }
    }
    
    /**
     * Aktualisiert eine bestehende Frage.
     */
    public void aktualisiereFrage(Frage frage, String titel, String text, 
                                 List<String> antworten, List<Boolean> richtigFlags, long themaId) {
        try {
            validateFrageData(titel, text, antworten, richtigFlags);
            
            // DTO-Konvertierung
            List<data.dto.AntwortDTO> antwortDTOs = new ArrayList<>();
            for (int i = 0; i < antworten.size(); i++) {
                if (antworten.get(i) != null && !antworten.get(i).trim().isEmpty()) {
                    antwortDTOs.add(new data.dto.AntwortDTO(0, antworten.get(i), richtigFlags.get(i)));
                }
            }
            
            FrageDTO aktualisierteFrageDTO = frageService.updateFrage(
                frage.getId(), titel, text, antwortDTOs, themaId);
            
            // Erfolgsmeldung
            showInfo("Frage erfolgreich aktualisiert");
            
            // Views aktualisieren
            refreshViews();
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Aktualisieren der Frage", e);
        }
    }
    
    /**
     * Löscht eine Frage.
     */
    public void loescheFrage(Frage frage) {
        try {
            if (frage == null || frage.getId() <= 0) {
                throw new ValidationException("Keine gültige Frage ausgewählt");
            }
            
            if (!showConfirm("Möchten Sie die Frage wirklich löschen?", "Frage löschen")) {
                return;
            }
            
            frageService.deleteFrage(frage.getId());
            
            // Erfolgsmeldung
            showInfo("Frage erfolgreich gelöscht");
            
            // Views aktualisieren
            refreshViews();
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Löschen der Frage", e);
        }
    }
    
    /**
     * Zeigt eine Frage in der Detailansicht an.
     */
    public void zeigeFrage(Frage frage) {
        if (linkesPanel != null) {
            linkesPanel.zeigeFrage(frage);
        }
        
        if (actionPanel != null) {
            actionPanel.updateButtonText();
        }
    }
    
    /**
     * Validiert Fragen-Daten.
     */
    private void validateFrageData(String titel, String text, List<String> antworten, List<Boolean> richtigFlags) {
        if (titel == null || titel.trim().isEmpty()) {
            throw new ValidationException("Der Fragetitel darf nicht leer sein");
        }
        
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException("Der Fragetext darf nicht leer sein");
        }
        
        if (antworten == null || antworten.isEmpty()) {
            throw new ValidationException("Bitte geben Sie mindestens eine Antwort ein");
        }
        
        boolean hatMindestensEineRichtige = false;
        for (int i = 0; i < antworten.size(); i++) {
            String antwort = antworten.get(i);
            boolean istRichtig = richtigFlags.get(i);
            
            if (antwort != null && !antwort.trim().isEmpty() && istRichtig) {
                hatMindestensEineRichtige = true;
                break;
            }
        }
        
        if (!hatMindestensEineRichtige) {
            throw new ValidationException("Bitte markieren Sie mindestens eine Antwort als richtig");
        }
    }
    
    /**
     * Aktualisiert alle Views.
     */
    private void refreshViews() {
        SwingUtilities.invokeLater(() -> {
            if (mainPanel != null) {
                mainPanel.ladeAlleThemen();
                mainPanel.aktualisiereFragenAnzeige();
            }
        });
    }
    
    /**
     * Behandelt Fehler.
     */
    @Override
    protected void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        showError(message + ": " + e.getMessage());
    }
    
    @Override
    public void onEvent(business.event.QuizEvent event) {
        if (event instanceof DataChangedEvent) {
            DataChangedEvent dataEvent = (DataChangedEvent) event;
            
            switch (dataEvent.getEntityType()) {
                case FRAGE:
                    SwingUtilities.invokeLater(() -> {
                        if (mainPanel != null) {
                            mainPanel.aktualisiereFragenAnzeige();
                        }
                    });
                    break;
                case THEMA:
                    SwingUtilities.invokeLater(() -> {
                        if (mainPanel != null) {
                            mainPanel.ladeAlleThemen();
                        }
                    });
                    break;
                default:
                    // Ignorieren
                    break;
            }
        }
    }
}
