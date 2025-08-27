package gui.quiz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import gui.AbstractMainPanel;
import gui.common.AbstractMainPanelWithWorker;
import gui.common.PanelInitializer;
import gui.fragen.QuizfragenRechtesPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;
import quiz.logic.interfaces.QuizDataProvider;

/**
 * Hauptpanel für das Quiz-Spiel. Ermöglicht das Beantworten von Quiz-Fragen mit
 * Punktezählung.
 */
public class QuizMainPanel extends AbstractMainPanel 
        implements PropertyChangeListener,
                   AbstractMainPanelWithWorker.HasThemenUpdate, 
                   AbstractMainPanelWithWorker.HasFragenUpdate {

    private static final long serialVersionUID = 1L;
    private final QuizDataProvider dataProvider;
    
    // Mixin für SwingWorker-Funktionalität
    private final AbstractMainPanelWithWorker workerMixin = new AbstractMainPanelWithWorker() {};

    public QuizMainPanel(QuizDataProvider dataProvider) {
        super(new QuizspielPanelLinks(), // Linkes Panel für Fragen-Anzeige
                new QuizfragenRechtesPanel(), // Rechtes Panel für Themen-Auswahl
                new QuizActionPanel() // Neues Action-Panel für Quiz-Funktionen
        );
        this.dataProvider = dataProvider;

        // Standard-Panel-Setup mit den neuen Hilfsklassen
        PanelInitializer.setupMainPanel(this, getPanelLinks(), getPanelRechts(), getPanelUnten(), dataProvider);

        // WICHTIG: QuizActionPanel mit MainPanel verbinden
        getPanelUnten().setMainPanel(this);

        // Quiz-spezifische Verbindung: Wenn Frage ausgewählt wird, Frage anzeigen und Quiz ermöglichen
        getPanelRechts().getFragenScrollPane().getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Frage selectedFrage = getPanelRechts().getSelectedFrage();
                if (selectedFrage != null) {
                    // Frage im linken Panel anzeigen
                    getPanelLinks().zeigeFrage(selectedFrage);
                    // Quiz-Funktionalität aktivieren
                    getPanelUnten().themaAusgewaehlt();
                    // Quiz für diese Frage starten
                    getPanelUnten().starteQuiz(selectedFrage);
                }
            }
        });

        // PropertyChange-Listener beim DataProvider registrieren
        if (dataProvider instanceof quiz.logic.ServiceBackedDataProvider) {
            quiz.logic.ServiceBackedDataProvider provider = (quiz.logic.ServiceBackedDataProvider) dataProvider;
            provider.addPropertyChangeListener(quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED, this);
            provider.addPropertyChangeListener(quiz.logic.ServiceBackedDataProvider.FRAGEN_CHANGED, this);
        }

        ladeAlleThemen();
    }

    public QuizspielPanelLinks getPanelLinks() {
        return (QuizspielPanelLinks) panelLinks;
    }

    public QuizfragenRechtesPanel getPanelRechts() {
        return (QuizfragenRechtesPanel) panelRechts;
    }

    public QuizActionPanel getPanelUnten() {
        return (QuizActionPanel) panelUnten;
    }

    public final QuizDataProvider getDataProvider() {
        return dataProvider;
    }

    @Override
    public void ladeAlleThemen() {
        // Verwende die neue abstrakte SwingWorker-Logik
        workerMixin.loadDataAsyncWithDefaultErrorHandling(
            () -> dataProvider.getAlleThemen(),
            themen -> {
                System.out.println("QuizMainPanel: " + themen.size() + " Themen geladen, aktualisiere rechtes Panel");
                getPanelRechts().setThemen(themen);
                System.out.println("QuizMainPanel: Rechtes Panel aktualisiert");
            },
            "QuizMainPanel",
            this
        );
    }

    @Override
    public void aktualisiereFragenAnzeige() {
        System.out.println("QuizMainPanel: Aktualisiere Fragen-Anzeige...");
        // Aktuell ausgewähltes Thema beibehalten
        quiz.data.model.Thema aktuellesThema = getPanelRechts().getSelectedThemaFromCombo();
        if (aktuellesThema != null && aktuellesThema != QuizfragenRechtesPanel.ALLE_THEMEN) {
            // Fragen für das aktuelle Thema neu laden
            try {
                List<quiz.data.model.Frage> fragen = dataProvider.findeFragenFuerThema(aktuellesThema.getId());
                getPanelRechts().aktualisiereFragenFuerThema(aktuellesThema, fragen);
                System.out.println("QuizMainPanel: Fragen für Thema '" + aktuellesThema.getTitel() + "' aktualisiert");
            } catch (Exception e) {
                System.err.println("QuizMainPanel: Fehler beim Aktualisieren der Fragen: " + e.getMessage());
                // Fallback: Vollständiges Update
                ladeAlleThemen();
            }
        } else {
            // Bei "Alle Themen" alle Fragen neu laden
            ladeAlleThemen();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println("QuizMainPanel: Aktualisiere Fragen-Anzeige: " + propertyName);

        // Unterstützt ServiceBackedDataProvider
        if (quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED.equals(propertyName)) {
            ladeAlleThemen();
        } else if (quiz.logic.ServiceBackedDataProvider.FRAGEN_CHANGED.equals(propertyName)) {
            aktualisiereFragenAnzeige();
        }
    }
}
