package gui.statistik;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import gui.AbstractMainPanel;
import gui.common.AbstractMainPanelWithWorker;
import gui.common.PanelInitializer;
import quiz.data.model.Frage;
import quiz.data.model.Thema;
import quiz.logic.interfaces.QuizDataProvider;

/**
 * Hauptpanel für die Statistik-Anzeige in der neuen Struktur.
 */
public class StatistikMainPanel extends AbstractMainPanel 
        implements PropertyChangeListener,
                   AbstractMainPanelWithWorker.HasThemenUpdate,
                   AbstractMainPanelWithWorker.HasFragenUpdate {

    private static final long serialVersionUID = 1L;
    private final QuizDataProvider dataProvider;
    
    // Mixin für SwingWorker-Funktionalität
    private final AbstractMainPanelWithWorker workerMixin = new AbstractMainPanelWithWorker() {};

    public StatistikMainPanel(QuizDataProvider dataProvider) {
        super(new StatistikLinkesPanel(), new StatistikRechtesPanel(), new StatistikActionPanel());
        this.dataProvider = dataProvider;

        // Standard-Panel-Setup mit den neuen Hilfsklassen
        PanelInitializer.setupMainPanel(this, getPanelLinks(), getPanelRechts(), getPanelUnten(), dataProvider);

        // Statistik-spezifische Verbindungen
        getPanelRechts().setLinkesPanel(getPanelLinks());
        getPanelUnten().setMainPanel(this);

        ladeAlleThemen();
    }

    public StatistikLinkesPanel getPanelLinks() {
        return (StatistikLinkesPanel) panelLinks;
    }

    public StatistikRechtesPanel getPanelRechts() {
        return (StatistikRechtesPanel) panelRechts;
    }

    public StatistikActionPanel getPanelUnten() {
        return (StatistikActionPanel) panelUnten;
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
                System.out.println("StatistikMainPanel: " + themen.size() + " Themen geladen, aktualisiere rechtes Panel");
                getPanelRechts().setThemen(themen);
                getPanelLinks().setAlleThemen(themen);
                System.out.println("StatistikMainPanel: Rechtes Panel aktualisiert");
            },
            "StatistikMainPanel",
            this
        );
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println("StatistikMainPanel: PropertyChange empfangen: " + propertyName);

        // Unterstützt ServiceBackedDataProvider
        if (quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED.equals(propertyName)) {
            ladeAlleThemen();
        } else if (quiz.logic.ServiceBackedDataProvider.FRAGEN_CHANGED.equals(propertyName)) {
            aktualisiereFragenAnzeige();
        }
    }

    @Override
    public void aktualisiereFragenAnzeige() {
        // Verwende die neue abstrakte SwingWorker-Logik
        workerMixin.loadDataAsyncWithDefaultErrorHandling(
            () -> {
                Thema selectedThema = getPanelRechts().getSelectedThemaFromCombo();
                if (selectedThema != null && !selectedThema.equals(getPanelRechts().ALLE_THEMEN)) {
                    return dataProvider.findeFragenFuerThema(selectedThema.getId());
                }
                return List.<Frage>of();
            },
            (List<Frage> fragen) -> {
                System.out.println("StatistikMainPanel: " + fragen.size() + " Fragen geladen, aktualisiere rechtes Panel");
                getPanelRechts().aktualisiereFragenFuerThema(
                    getPanelRechts().getSelectedThemaFromCombo(), 
                    fragen
                );
                System.out.println("StatistikMainPanel: Rechtes Panel aktualisiert");
            },
            "StatistikMainPanel",
            this
        );
    }
}
