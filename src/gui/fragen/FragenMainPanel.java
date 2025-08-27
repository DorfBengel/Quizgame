package gui.fragen;

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
 * Hauptpanel für die Fragen-Verwaltung in der neuen Struktur.
 * Ersetzt View2QuizfragenMainPanel.
 */
public class FragenMainPanel extends AbstractMainPanel 
        implements PropertyChangeListener,
                   AbstractMainPanelWithWorker.HasThemenUpdate,
                   AbstractMainPanelWithWorker.HasFragenUpdate {

    private static final long serialVersionUID = 1L;
    private final QuizDataProvider dataProvider;
    
    // Mixin für SwingWorker-Funktionalität
    private final AbstractMainPanelWithWorker workerMixin = new AbstractMainPanelWithWorker() {};

    public FragenMainPanel(QuizDataProvider dataProvider) {
        super(new QuizfragenLinkesPanel(), new QuizfragenRechtesPanel(), new QuizfragenActionPanel());
        this.dataProvider = dataProvider;

        // Standard-Panel-Setup mit den neuen Hilfsklassen
        PanelInitializer.setupMainPanel(this, getPanelLinks(), getPanelRechts(), getPanelUnten(), dataProvider);

        // Fragen-spezifische Verbindungen
        getPanelRechts().setLinkesPanel(getPanelLinks());
        getPanelUnten().setMainPanel(this);
        
        // Button-Text aktualisieren, wenn eine Frage ausgewählt wird
        getPanelRechts().getFragenScrollPane().getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                getPanelUnten().updateButtonText();
            }
        });

        ladeAlleThemen();
    }

    public QuizfragenLinkesPanel getPanelLinks() {
        return (QuizfragenLinkesPanel) panelLinks;
    }

    public QuizfragenRechtesPanel getPanelRechts() {
        return (QuizfragenRechtesPanel) panelRechts;
    }

    public QuizfragenActionPanel getPanelUnten() {
        return (QuizfragenActionPanel) panelUnten;
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
                System.out.println("FragenMainPanel: " + themen.size() + " Themen geladen, aktualisiere rechtes Panel");
                getPanelRechts().setThemen(themen);
                System.out.println("FragenMainPanel: Rechtes Panel aktualisiert");
            },
            "FragenMainPanel",
            this
        );
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println("FragenMainPanel: PropertyChange empfangen: " + propertyName);

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
                System.out.println("FragenMainPanel: " + fragen.size() + " Fragen geladen, aktualisiere rechtes Panel");
                getPanelRechts().aktualisiereFragenFuerThema(
                    getPanelRechts().getSelectedThemaFromCombo(), 
                    fragen
                );
                System.out.println("FragenMainPanel: Rechtes Panel aktualisiert");
            },
            "FragenMainPanel",
            this
        );
    }
}
