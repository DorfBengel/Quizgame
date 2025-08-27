package gui.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Hilfsklasse für die Behandlung von PropertyChangeEvents.
 * Reduziert Code-Duplikation bei der Event-Behandlung.
 */
public class PropertyChangeHandler implements PropertyChangeListener {
    
    private final Map<String, Consumer<PropertyChangeEvent>> eventHandlers;
    private final String handlerName;
    
    /**
     * Erstellt einen neuen PropertyChangeHandler.
     * 
     * @param eventHandlers Map von Property-Namen zu Event-Handlern
     * @param handlerName Name des Handlers für Logging
     */
    public PropertyChangeHandler(Map<String, Consumer<PropertyChangeEvent>> eventHandlers, String handlerName) {
        this.eventHandlers = eventHandlers;
        this.handlerName = handlerName;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println(handlerName + ": PropertyChange empfangen: " + propertyName);
        
        // Normalisiere Property-Namen für ServiceBackedDataProvider
        String normalizedPropertyName = normalizePropertyName(propertyName);
        
        // Führe entsprechenden Handler aus
        Consumer<PropertyChangeEvent> handler = eventHandlers.get(normalizedPropertyName);
        if (handler != null) {
            handler.accept(evt);
        } else {
            System.out.println(handlerName + ": Kein Handler für Property '" + normalizedPropertyName + "' gefunden");
        }
    }
    
    /**
     * Normalisiert Property-Namen für bessere Kompatibilität.
     */
    private String normalizePropertyName(String propertyName) {
        // Unterstützt ServiceBackedDataProvider
        if ("themenChanged".equals(propertyName) || 
            "quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED".equals(propertyName)) {
            return "themenChanged";
        } else if ("fragenChanged".equals(propertyName) || 
                   "quiz.logic.ServiceBackedDataProvider.FRAGEN_CHANGED".equals(propertyName)) {
            return "fragenChanged";
        }
        return propertyName;
    }
    
    /**
     * Erstellt einen PropertyChangeHandler mit vordefinierten Handlern.
     */
    public static class Builder {
        private final Map<String, Consumer<PropertyChangeEvent>> handlers = new java.util.HashMap<>();
        private final String handlerName;
        
        public Builder(String handlerName) {
            this.handlerName = handlerName;
        }
        
        public Builder addHandler(String propertyName, Consumer<PropertyChangeEvent> handler) {
            handlers.put(propertyName, handler);
            return this;
        }
        
        public Builder addHandler(String propertyName, Runnable action) {
            handlers.put(propertyName, evt -> action.run());
            return this;
        }
        
        public PropertyChangeHandler build() {
            return new PropertyChangeHandler(handlers, handlerName);
        }
    }
}
