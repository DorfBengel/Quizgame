package gui.interfaces;

import quiz.data.model.Frage;
import quiz.data.model.Thema;
import java.util.List;

/**
 * Interface für Quiz-Views im MVC-Pattern.
 * Definiert die Operationen, die eine Quiz-View unterstützen muss.
 */
public interface QuizView {
    
    /**
     * Zeigt eine Liste von Themen zur Auswahl an.
     */
    void zeigeThemen(List<Thema> themen);
    
    /**
     * Zeigt eine Liste von Fragen für ein bestimmtes Thema an.
     */
    void zeigeFragenFuerThema(Thema thema, List<Frage> fragen);
    
    /**
     * Zeigt eine Frage im Quiz-Modus an.
     */
    void zeigeFrageImQuiz(Frage frage);
    
    /**
     * Zeigt die korrekten Antworten für eine Frage an.
     */
    void zeigeKorrekteAntworten(Frage frage);
    
    /**
     * Startet den Quiz-Modus.
     */
    void starteQuizModus();
    
    /**
     * Beendet den Quiz-Modus.
     */
    void beendeQuizModus();
    
    /**
     * Zeigt eine Erfolgsmeldung an.
     */
    void zeigeErfolg(String nachricht);
    
    /**
     * Zeigt eine Fehlermeldung an.
     */
    void zeigeFehler(String nachricht);
    
    /**
     * Zeigt eine Warnmeldung an.
     */
    void zeigeWarnung(String nachricht);
    
    /**
     * Aktiviert oder deaktiviert die Benutzerinteraktion.
     */
    void setBenutzerInteraktionAktiv(boolean aktiv);
    
    /**
     * Gibt die aktuell ausgewählte Frage zurück.
     */
    Frage getAusgewählteFrage();
    
    /**
     * Gibt das aktuell ausgewählte Thema zurück.
     */
    Thema getAusgewählteThema();
    
    /**
     * Gibt die ausgewählten Antworten zurück.
     */
    boolean[] getAusgewählteAntworten();
    
    /**
     * Aktualisiert die Punkteanzeige.
     */
    void aktualisierePunkteanzeige(int punkte, int maxPunkte);
    
    /**
     * Aktualisiert die Fragenfortschritt-Anzeige.
     */
    void aktualisiereFragenfortschritt(int aktuelleFrage, int gesamtFragen);
    
    /**
     * Leert die Antwortauswahl.
     */
    void leereAntwortauswahl();
    
    /**
     * Aktualisiert die Anzeige nach Datenänderungen.
     */
    void aktualisiereAnzeige();
    
    /**
     * Aktiviert oder deaktiviert Quiz-spezifische Buttons.
     */
    void setQuizButtonsAktiv(boolean aktiv);
}
