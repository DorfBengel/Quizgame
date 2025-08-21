package gui.statistik;

import gui.AbstractActionPanel;
import gui.statistik.StatistikMainPanel;
import gui.util.DialogService;

/**
 * Action-Panel für die Statistik-Ansicht. Enthält Buttons für verschiedene
 * Statistik-Funktionen.
 */
public class StatistikActionPanel extends AbstractActionPanel {

	private static final long serialVersionUID = 1L;
	private StatistikMainPanel mainPanel;

	// Nachrichten-Konstanten
	private static final String MSG_KEIN_THEMA_AUSGEWAEHLT = "Kein Thema ausgewählt.";

	public StatistikActionPanel() {
		super();
		configureButtons();
		initListeners();
	}

	public void setMainPanel(StatistikMainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void initListeners() {
		button1.addActionListener(e -> aktualisiereStatistiken());
		button2.addActionListener(e -> exportiereStatistiken());
		button3.addActionListener(e -> zeigeHilfe());
	}

	/**
	 * Aktualisiert alle Statistiken.
	 */
	private void aktualisiereStatistiken() {
		if (mainPanel == null) {
			return;
		}

		try {
			mainPanel.ladeAlleThemen();
		} catch (Exception e) {
			DialogService.error(this, "Fehler beim Aktualisieren der Statistiken: " + e.getMessage());
		}
	}

	/**
	 * Exportiert die aktuellen Statistiken.
	 */
	private void exportiereStatistiken() {
		if (mainPanel == null) {
			return;
		}

		// Hier könnte eine Export-Funktionalität implementiert werden
		// Für den Moment zeigen wir nur eine Info-Nachricht
		DialogService.info(this, "Export-Funktionalität wird in einer zukünftigen Version verfügbar sein.");
	}

	/**
	 * Zeigt Hilfe für die Statistik-Ansicht an.
	 */
	private void zeigeHilfe() {
		String hilfeText = "=== STATISTIK-HILFE ===\n\n"
				+ "• Wählen Sie ein Thema aus der ComboBox, um spezifische Statistiken zu sehen\n"
				+ "• Wählen Sie 'Alle Themen' für eine Gesamtübersicht\n"
				+ "• Klicken Sie auf eine Frage in der rechten Liste für detaillierte Informationen\n"
				+ "• Verwenden Sie 'Aktualisieren' um die neuesten Daten zu laden\n"
				+ "• Die Statistiken zeigen Anzahl von Themen, Fragen und Antworten\n\n"
				+ "Die linke Seite zeigt eine Übersicht, die rechte Seite detaillierte Informationen.";

		DialogService.info(this, hilfeText);
	}

	@Override
	protected void configureButtons() {
		button1.setText("Aktualisieren");
		button2.setText("Exportieren");
		button3.setText("Hilfe");

		// Alle Buttons aktiv
		button1.setEnabled(true);
		button2.setEnabled(true);
		button3.setEnabled(true);

		messageLabel.setText(
				"Wählen Sie ein Thema aus, um Statistiken anzuzeigen. Verwenden Sie 'Aktualisieren' für die neuesten Daten.");
	}
}
