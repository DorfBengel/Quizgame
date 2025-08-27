package gui.interfaces;

import quiz.data.model.Frage;
import quiz.data.model.Thema;
import data.dto.StatistikDTO;
import java.util.List;

/**
 * Interface für Statistik-Views im MVC-Pattern.
 * Definiert die Operationen, die eine Statistik-View unterstützen muss.
 */
public interface StatistikView {
    
    /**
     * Zeigt eine Liste von Themen zur Auswahl an.
     */
    void zeigeThemen(List<Thema> themen);
    
    /**
     * Zeigt eine Liste von Fragen für ein bestimmtes Thema an.
     */
    void zeigeFragenFuerThema(Thema thema, List<Frage> fragen);
    
    /**
     * Zeigt die Gesamtstatistik an.
     */
    void zeigeGesamtstatistik(StatistikDTO statistik);
    
    /**
     * Zeigt die Statistik für ein bestimmtes Thema an.
     */
    void zeigeThemastatistik(Thema thema, StatistikDTO statistik);
    
    /**
     * Zeigt die Statistik für eine bestimmte Frage an.
     */
    void zeigeFragestatistik(Frage frage, StatistikDTO statistik);
    
    /**
     * Zeigt eine Liste aller verfügbaren Statistiken an.
     */
    void zeigeAlleStatistiken(List<StatistikDTO> statistiken);
    
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
     * Gibt die aktuell ausgewählte Frage zurück.
     */
    Frage getAusgewählteFrage();
    
    /**
     * Aktualisiert die Statistik-Anzeige.
     */
    void aktualisiereStatistikAnzeige();
    
    /**
     * Zeigt einen Ladeindikator an.
     */
    void zeigeLadeindikator(boolean anzeigen);
    
    /**
     * Zeigt Fortschritt beim Export an.
     */
    void zeigeExportfortschritt(int prozent);
    
    /**
     * Zeigt die Hilfe für Statistiken an.
     */
    void zeigeStatistikHilfe(String hilfeText);
    
    /**
     * Leert die Statistik-Anzeige.
     */
    void leereStatistikAnzeige();
    
    /**
     * Aktualisiert die Anzeige nach Datenänderungen.
     */
    void aktualisiereAnzeige();
}
