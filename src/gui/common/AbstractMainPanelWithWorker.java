package gui.common;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import gui.util.DialogService;

/**
 * Abstrakte Basis-Klasse für MainPanels mit gemeinsamer SwingWorker-Logik.
 * Reduziert Code-Duplikation bei asynchronen Datenladungen.
 */
public abstract class AbstractMainPanelWithWorker {
    
    protected final AtomicBoolean updateInProgress = new AtomicBoolean(false);
    
    /**
     * Lädt Daten asynchron mit einem SwingWorker.
     * 
     * @param <T> Der Typ der zu ladenden Daten
     * @param dataLoader Der DataLoader für die Hintergrundarbeit
     * @param onSuccess Callback bei erfolgreichem Laden
     * @param onError Callback bei Fehlern
     * @param operationName Name der Operation für Logging
     */
    public <T> void loadDataAsync(
            DataLoader<T> dataLoader,
            DataSuccessCallback<T> onSuccess,
            DataErrorCallback onError,
            String operationName) {
        
        if (updateInProgress.get()) {
            System.out.println(operationName + ": Update läuft bereits, überspringe...");
            return;
        }
        
        updateInProgress.set(true);
        System.out.println(operationName + ": Starte asynchrones Laden...");
        
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                System.out.println(operationName + ": Lade Daten im Hintergrund...");
                return dataLoader.loadData();
            }
            
            @Override
            protected void done() {
                try {
                    T data = get();
                    System.out.println(operationName + ": Daten erfolgreich geladen, aktualisiere UI...");
                    onSuccess.onSuccess(data);
                    System.out.println(operationName + ": UI erfolgreich aktualisiert");
                } catch (Exception e) {
                    System.err.println(operationName + ": Fehler beim Laden der Daten: " + e.getMessage());
                    e.printStackTrace();
                    onError.onError(e);
                } finally {
                    updateInProgress.set(false);
                }
            }
        }.execute();
    }
    
    /**
     * Lädt Daten asynchron mit Standard-Fehlerbehandlung.
     */
    public <T> void loadDataAsyncWithDefaultErrorHandling(
            DataLoader<T> dataLoader,
            DataSuccessCallback<T> onSuccess,
            String operationName,
            Component parentComponent) {
        
        loadDataAsync(
            dataLoader,
            onSuccess,
            error -> {
                DialogService.error(parentComponent, 
                    "Fehler beim Laden der Daten:\n" + error.getMessage());
            },
            operationName
        );
    }
    
    // Funktionale Interfaces für Callbacks
    @FunctionalInterface
    public interface DataLoader<T> {
        T loadData() throws Exception;
    }
    
    @FunctionalInterface
    public interface DataSuccessCallback<T> {
        void onSuccess(T data);
    }
    
    @FunctionalInterface
    public interface DataErrorCallback {
        void onError(Exception error);
    }
    
    // Marker-Interfaces für MainPanels
    public interface HasThemenUpdate {
        void ladeAlleThemen();
    }
    
    public interface HasFragenUpdate {
        void aktualisiereFragenAnzeige();
    }
}
