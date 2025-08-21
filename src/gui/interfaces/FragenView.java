package gui.interfaces;

import quiz.data.model.Frage;
import quiz.data.model.Thema;
import java.util.List;

/**
 * Interface für Fragen-Views im MVC-Pattern.
 * Definiert die Operationen, die eine Fragen-View unterstützen muss.
 */
public interface FragenView {
    
    /**
     * Zeigt eine Liste von Themen zur Auswahl an.
     */
    void zeigeThemen(List<Thema> themen);
    
    /**
     * Zeigt eine Liste von Fragen für ein bestimmtes Thema an.
     */
    void zeigeFragenFuerThema(Thema thema, List<Frage> fragen);
    
    /**
     * Zeigt eine bestimmte Frage in der Detailansicht an.
     */
    void zeigeFrage(Frage frage);
    
    /**
     * Leert die Eingabefelder für eine neue Frage.
     */
    void bereiteFuerNeueFrage();
    
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
     * Gibt den eingegebenen Fragetitel zurück.
     */
    String getEingabeTitel();
    
    /**
     * Gibt den eingegebenen Fragetext zurück.
     */
    String getEingabeText();
    
    /**
     * Gibt die eingegebenen Antworten zurück.
     */
    List<String> getEingabeAntworten();
    
    /**
     * Gibt die Markierungen für richtige Antworten zurück.
     */
    List<Boolean> getRichtigeAntworten();
    
    /**
     * Setzt den Fokus auf das erste Eingabefeld.
     */
    void fokusAufEingabe();
    
    /**
     * Aktualisiert die Anzeige nach Datenänderungen.
     */
    void aktualisiereAnzeige();
    
    /**
     * Aktualisiert den Text des Aktions-Buttons.
     */
    void aktualisiereButtonText(String text);
}
