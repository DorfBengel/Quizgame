package gui.controller;

import java.awt.Component;

import business.event.EventListener;
import business.event.EventManager;
import gui.util.DialogService;

/**
 * Abstrakte Basis-Klasse für alle Controller.
 * Implementiert gemeinsame Funktionalität für das MVC-Pattern.
 */
public abstract class AbstractController implements EventListener {
    
    protected final EventManager eventManager;
    protected Component parentComponent;
    
    protected AbstractController() {
        this.eventManager = EventManager.getInstance();
    }
    
    /**
     * Setzt die übergeordnete Komponente für Dialoge.
     */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    /**
     * Zeigt eine Informationsmeldung an.
     */
    protected void showInfo(String message) {
        if (parentComponent != null) {
            DialogService.info(parentComponent, message);
        }
    }
    
    /**
     * Zeigt eine Warnmeldung an.
     */
    protected void showWarning(String message) {
        if (parentComponent != null) {
            DialogService.warn(parentComponent, message);
        }
    }
    
    /**
     * Zeigt eine Fehlermeldung an.
     */
    protected void showError(String message) {
        if (parentComponent != null) {
            DialogService.error(parentComponent, message);
        }
    }
    
    /**
     * Zeigt eine Bestätigungsmeldung an.
     */
    protected boolean showConfirm(String message, String title) {
        if (parentComponent != null) {
            return DialogService.confirm(parentComponent, message, title);
        }
        return false;
    }
    
    /**
     * Behandelt einen Fehler.
     */
    protected void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        showError(message + ": " + e.getMessage());
    }
    
    /**
     * Registriert den Controller für Events.
     */
    protected void registerForEvents(String... eventTypes) {
        for (String eventType : eventTypes) {
            eventManager.addEventListener(eventType, this);
        }
    }
    
    /**
     * Entfernt den Controller von Events.
     */
    protected void unregisterFromEvents(String... eventTypes) {
        for (String eventType : eventTypes) {
            eventManager.removeEventListener(eventType, this);
        }
    }
}
