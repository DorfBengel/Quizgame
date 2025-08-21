package gui.mvc;

import business.QuizApplication;
import gui.controller.ThemenController;
import gui.controller.FragenController;
import gui.controller.QuizController;
import gui.controller.StatistikController;

/**
 * Factory für die Erstellung und Verwaltung aller MVC-Controller.
 * Implementiert das Singleton-Pattern für zentrale Controller-Verwaltung.
 */
public class ControllerFactory {
    
    private static ControllerFactory instance;
    
    // Controller-Instanzen
    private ThemenController themenController;
    private FragenController fragenController;
    private QuizController quizController;
    private StatistikController statistikController;
    
    // Services
    private final QuizApplication quizApplication;
    
    private ControllerFactory() {
        this.quizApplication = QuizApplication.getInstance();
        initializeControllers();
    }
    
    /**
     * Singleton-Instanz der ControllerFactory.
     */
    public static synchronized ControllerFactory getInstance() {
        if (instance == null) {
            instance = new ControllerFactory();
        }
        return instance;
    }
    
    /**
     * Initialisiert alle Controller.
     */
    private void initializeControllers() {
        // ThemenController erstellen
        themenController = new ThemenController(quizApplication.getThemaService());
        
        // FragenController erstellen
        fragenController = new FragenController(quizApplication.getFrageService());
        
        // QuizController erstellen
        quizController = new QuizController(quizApplication.getQuizStatistikService());
        
        // StatistikController erstellen
        statistikController = new StatistikController(quizApplication.getQuizStatistikService());
    }
    
    /**
     * Gibt den ThemenController zurück.
     */
    public ThemenController getThemenController() {
        return themenController;
    }
    
    /**
     * Gibt den FragenController zurück.
     */
    public FragenController getFragenController() {
        return fragenController;
    }
    
    /**
     * Gibt den QuizController zurück.
     */
    public QuizController getQuizController() {
        return quizController;
    }
    
    /**
     * Gibt den StatistikController zurück.
     */
    public StatistikController getStatistikController() {
        return statistikController;
    }
    
    /**
     * Verknüpft alle Controller mit ihren entsprechenden Views.
     * Diese Methode sollte nach der Erstellung aller Views aufgerufen werden.
     */
    public void connectControllersWithViews(
            gui.themen.ThemenMainPanel themenMainPanel,
            gui.fragen.FragenMainPanel fragenMainPanel,
            gui.quiz.QuizMainPanel quizMainPanel,
            gui.statistik.StatistikMainPanel statistikMainPanel) {
        
        // ThemenController mit Views verknüpfen
        themenController.setViews(
            themenMainPanel,
            themenMainPanel.getPanelUnten(),
            themenMainPanel.getPanelLinks(),
            themenMainPanel.getPanelRechts()
        );
        
        // FragenController mit Views verknüpfen
        fragenController.setViews(
            fragenMainPanel,
            fragenMainPanel.getPanelUnten(),
            fragenMainPanel.getPanelLinks(),
            fragenMainPanel.getPanelRechts()
        );
        
        // QuizController mit Views verknüpfen
        quizController.setViews(
            quizMainPanel,
            quizMainPanel.getPanelUnten(),
            quizMainPanel.getPanelLinks(),
            quizMainPanel.getPanelRechts()
        );
        
        // StatistikController mit Views verknüpfen
        statistikController.setViews(
            statistikMainPanel,
            statistikMainPanel.getPanelUnten(),
            statistikMainPanel.getPanelLinks(),
            statistikMainPanel.getPanelRechts()
        );
    }
    
    /**
     * Startet alle Controller (lädt initiale Daten).
     */
    public void startAllControllers() {
        // Themen laden
        themenController.ladeAlleThemen();
        
        // Fragen-Themen laden
        fragenController.ladeAlleThemen();
        
        // Quiz-Themen laden
        quizController.ladeAlleThemen();
        
        // Statistik-Themen laden
        statistikController.ladeAlleThemen();
        statistikController.aktualisiereStatistiken();
    }
    
    /**
     * Beendet alle Controller und räumt Ressourcen auf.
     */
    public void shutdownAllControllers() {
        // Cleanup für alle Controller
        // Da wir MVCController nicht verwenden, ist hier nichts zu tun
        
        // Event-Listener entfernen
        if (themenController != null) {
            // EventManager cleanup würde hier stattfinden
        }
        
        // Instance zurücksetzen
        instance = null;
    }
    
    /**
     * Registriert Inter-Controller-Kommunikation.
     * Controller können sich gegenseitig über Änderungen benachrichtigen.
     */
    private void setupInterControllerCommunication() {
        // Beispiel: Wenn sich Themen ändern, sollten auch andere Controller benachrichtigt werden
        // Dies würde über das Event-System geschehen, das bereits implementiert ist
    }
    
    /**
     * Gibt Debugging-Informationen über alle Controller aus.
     */
    public String getControllerStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Controller Factory Status:\n");
        status.append("=========================\n");
        status.append("ThemenController: ").append(themenController != null ? "initialized" : "null").append("\n");
        status.append("FragenController: ").append(fragenController != null ? "initialized" : "null").append("\n");
        status.append("QuizController: ").append(quizController != null ? "initialized" : "null").append("\n");
        status.append("StatistikController: ").append(statistikController != null ? "initialized" : "null").append("\n");
        
        return status.toString();
    }
}
