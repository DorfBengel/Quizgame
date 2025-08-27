package gui.controller;

import java.util.List;

import javax.swing.SwingUtilities;

import business.ThemaService;
import business.event.DataChangedEvent;
import data.dto.ThemaDTO;
import exception.ValidationException;
import gui.themen.ThemenMainPanel;
import gui.themen.QuizthemenActionPanel;
import gui.themen.QuizthemenLinkesPanel;
import gui.themen.QuizthemenRechtesPanel;
import quiz.data.model.Thema;

/**
 * Controller für die Themen-Verwaltung.
 * Implementiert das MVC-Pattern durch klare Trennung von Logik und Präsentation.
 */
public class ThemenController extends AbstractController {
    
    private final ThemaService themaService;
    
    // View-Referenzen
    private ThemenMainPanel mainPanel;
    private QuizthemenActionPanel actionPanel;
    private QuizthemenLinkesPanel linkesPanel;
    private QuizthemenRechtesPanel rechtesPanel;
    
    public ThemenController(ThemaService themaService) {
        super();
        this.themaService = themaService;
        
        // Event-Listener registrieren
        registerForEvents("THEMA_*");
    }
    
    /**
     * Setzt die View-Referenzen.
     */
    public void setViews(ThemenMainPanel mainPanel, QuizthemenActionPanel actionPanel, 
                        QuizthemenLinkesPanel linkesPanel, QuizthemenRechtesPanel rechtesPanel) {
        this.mainPanel = mainPanel;
        this.actionPanel = actionPanel;
        this.linkesPanel = linkesPanel;
        this.rechtesPanel = rechtesPanel;
        
        // Action-Listener registrieren
        setupActionListeners();
    }
    
    /**
     * Richtet die Action-Listener ein.
     */
    private void setupActionListeners() {
        // Controller wird über setMainPanel gesetzt
    }
    
    /**
     * Lädt alle Themen und aktualisiert die View.
     */
    public void ladeAlleThemen() {
        try {
            List<ThemaDTO> themen = themaService.findAllThemen();
            if (rechtesPanel != null) {
                rechtesPanel.setThemen(mapToThemaList(themen));
            }
        } catch (Exception e) {
            handleError("Fehler beim Laden der Themen", e);
        }
    }
    
    /**
     * Erstellt ein neues Thema.
     */
    public void erstelleThema(String titel, String information) {
        try {
            // Validierung
            validateThemaData(titel, information);
            
            // Thema erstellen
            ThemaDTO thema = themaService.createThema(titel, information);
            
            // View aktualisieren
            refreshViews();
            
            // Erfolgsmeldung
            showInfo("Thema erfolgreich erstellt");
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Erstellen des Themas", e);
        }
    }
    
    /**
     * Aktualisiert ein bestehendes Thema.
     */
    public void aktualisiereThema(Thema thema) {
        try {
            if (thema == null || thema.getId() <= 0) {
                throw new ValidationException("Ungültiges Thema");
            }
            
            // Thema aktualisieren
            themaService.updateThema(thema.getId(), thema.getTitel(), thema.getInformation());
            
            // View aktualisieren
            refreshViews();
            
            // Erfolgsmeldung
            showInfo("Thema erfolgreich aktualisiert");
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Aktualisieren des Themas", e);
        }
    }
    
    /**
     * Löscht ein Thema.
     */
    public void loescheThema(Thema thema) {
        try {
            if (thema == null || thema.getId() <= 0) {
                throw new ValidationException("Ungültiges Thema");
            }
            
            // Thema löschen
            themaService.deleteThema(thema.getId());
            
            // View aktualisieren
            refreshViews();
            
            // Erfolgsmeldung
            showInfo("Thema erfolgreich gelöscht");
            
        } catch (ValidationException e) {
            showWarning("Validierungsfehler: " + e.getMessage());
        } catch (Exception e) {
            handleError("Fehler beim Löschen des Themas", e);
        }
    }
    
    /**
     * Zeigt ein Thema in der View an.
     */
    public void zeigeThema(Thema thema) {
        if (linkesPanel != null) {
            linkesPanel.zeigeThema(thema);
        }
    }
    
    /**
     * Validiert Themen-Daten.
     */
    private void validateThemaData(String titel, String information) {
        if (titel == null || titel.trim().isEmpty()) {
            throw new ValidationException("Der Titel darf nicht leer sein");
        }
        if (titel.trim().length() > 100) {
            throw new ValidationException("Der Titel darf maximal 100 Zeichen lang sein");
        }
        if (information == null || information.trim().isEmpty()) {
            throw new ValidationException("Die Information darf nicht leer sein");
        }
    }
    
    /**
     * Aktualisiert alle Views.
     */
    private void refreshViews() {
        ladeAlleThemen();
        if (linkesPanel != null) {
            linkesPanel.resetFields();
        }
    }
    
    /**
     * Behandelt Fehler.
     */
    protected void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        showError(message + ": " + e.getMessage());
    }
    
    /**
     * Mappt DTOs zu Domain-Objekten.
     */
    private List<Thema> mapToThemaList(List<ThemaDTO> themenDTO) {
        return themenDTO.stream()
            .map(dto -> {
                Thema thema = new Thema(dto.getTitel(), dto.getInformation());
                thema.setId(dto.getId());
                return thema;
            })
            .toList();
    }
    
    @Override
    public void onEvent(business.event.QuizEvent event) {
        if (event instanceof DataChangedEvent) {
            DataChangedEvent dataEvent = (DataChangedEvent) event;
            if (dataEvent.getEntityType() == DataChangedEvent.EntityType.THEMA) {
                // View bei Themen-Änderungen aktualisieren
                SwingUtilities.invokeLater(this::refreshViews);
            }
        }
    }
}
