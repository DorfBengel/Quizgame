package gui.themen;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import gui.AbstractMainPanel;
import gui.common.AbstractMainPanelWithWorker;
import gui.common.PanelInitializer;
import gui.interfaces.ThemaSelectionDelegate;
import quiz.data.model.Thema;
import quiz.logic.interfaces.QuizDataProvider;

/**
 * Hauptpanel für die Themen-Verwaltung in der neuen Struktur.
 * Ersetzt View1QuizthemenMainPanel.
 */
public class ThemenMainPanel extends AbstractMainPanel 
        implements ThemaSelectionDelegate, 
                   AbstractMainPanelWithWorker.HasThemenUpdate {

    private static final long serialVersionUID = 1L;
    private final QuizDataProvider dataProvider;
    
    // Mixin für SwingWorker-Funktionalität
    private final AbstractMainPanelWithWorker workerMixin = new AbstractMainPanelWithWorker() {};

    public ThemenMainPanel() {
        this(null);
    }

    public ThemenMainPanel(QuizDataProvider dataProvider) {
        super(new QuizthemenLinkesPanel(), new QuizthemenRechtesPanel(), new QuizthemenActionPanel());
        this.dataProvider = dataProvider;

        // Standard-Panel-Setup mit den neuen Hilfsklassen
        PanelInitializer.setupMainPanel(this, getPanelLinks(), getPanelRechts(), getPanelUnten(), dataProvider);

        // WICHTIG: QuizthemenActionPanel mit MainPanel verbinden
        getPanelUnten().setMainPanel(this);

        // Themen-spezifische Verbindung
        getPanelRechts().setThemaSelectionDelegate(this);
        
        // Button-Text aktualisieren, wenn ein Thema ausgewählt wird
        getPanelRechts().getThemenScrollPane().getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                getPanelUnten().updateButtonText();
            }
        });

        ladeAlleThemen();
    }

    public final QuizDataProvider getDataProvider() {
        return dataProvider;
    }

    public void ladeAlleThemen() {
        // Verwende die neue abstrakte SwingWorker-Logik
        workerMixin.loadDataAsyncWithDefaultErrorHandling(
            () -> dataProvider.getAlleThemen(),
            themen -> {
                System.out.println("ThemenMainPanel: " + themen.size() + " Themen geladen, aktualisiere rechtes Panel");
                getPanelRechts().setThemen(themen);
                System.out.println("ThemenMainPanel: Rechtes Panel aktualisiert");
            },
            "ThemenMainPanel",
            this
        );
    }

    @Override
    public final void zeigeThema(Thema thema) {
        if (thema != null) {
            getPanelLinks().zeigeThema(thema);
            getPanelRechts().selectThema(thema);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println("ThemenMainPanel: PropertyChange empfangen: " + propertyName);

        // Unterstützt ServiceBackedDataProvider
        if (quiz.logic.ServiceBackedDataProvider.THEMEN_CHANGED.equals(propertyName)) {
            ladeAlleThemen();
        }
    }

    public final QuizthemenLinkesPanel getPanelLinks() {
        return (QuizthemenLinkesPanel) panelLinks;
    }

    public final QuizthemenRechtesPanel getPanelRechts() {
        return (QuizthemenRechtesPanel) panelRechts;
    }

    public final QuizthemenActionPanel getPanelUnten() {
        return (QuizthemenActionPanel) panelUnten;
    }
}
