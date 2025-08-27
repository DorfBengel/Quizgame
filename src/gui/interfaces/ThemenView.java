package gui.interfaces;

import quiz.data.model.Thema;
import java.util.List;

/**
 * Interface für Themen-Views im MVC-Pattern.
 * Definiert die Operationen, die eine Themen-View unterstützen muss.
 */
public interface ThemenView {
    
    /**
     * Zeigt eine Liste von Themen an.
     */
    void zeigeThemen(List<Thema> themen);
    
    /**
     * Zeigt ein bestimmtes Thema in der Detailansicht an.
     */
    void zeigeThema(Thema thema);
    
    /**
     * Leert die Eingabefelder für ein neues Thema.
     */
    void bereiteFuerNeuesThema();
    
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
     * Gibt das aktuell ausgewählte Thema zurück.
     */
    Thema getAusgewählteThema();
    
    /**
     * Gibt den eingegebenen Titel zurück.
     */
    String getEingabeTitel();
    
    /**
     * Gibt die eingegebene Information zurück.
     */
    String getEingabeInformation();
    
    /**
     * Setzt den Fokus auf das erste Eingabefeld.
     */
    void fokusAufEingabe();
    
    /**
     * Aktualisiert die Anzeige nach Datenänderungen.
     */
    void aktualisiereAnzeige();
}
