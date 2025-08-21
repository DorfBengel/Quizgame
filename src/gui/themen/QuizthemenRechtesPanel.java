
package gui.themen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import gui.QuizScrollPane;
import gui.interfaces.GuiDefaults;
import gui.interfaces.ThemaSelectionDelegate;
import quiz.data.model.Thema;

/**
 * Panel zur Anzeige und Auswahl von Quiz-Themen. Zeigt eine scrollbare Liste
 * aller verfügbaren Themen an und ermöglicht die Auswahl.
 */
public class QuizthemenRechtesPanel extends JPanel implements GuiDefaults {

	private static final long serialVersionUID = 1L;

	// Konstanten für bessere Wartbarkeit
	private static final String LABEL_THEMENUEBERSICHT = "Themenübersicht";
	private static final String TOOLTIP_THEMEN_LISTE = "Klicken Sie auf ein Thema, um es zu bearbeiten";
	private static final String TOOLTIP_LABEL = "Übersicht aller verfügbaren Quiz-Themen";

	private final JLabel themenLabel;
	final QuizScrollPane<Thema> themenScrollPane;
	private ThemaSelectionDelegate delegate;
	List<Thema> aktuelleThemen = new ArrayList<>();

	public QuizthemenRechtesPanel() {
		super();
		setLayout(new GridBagLayout());

		themenLabel = new JLabel(LABEL_THEMENUEBERSICHT);
		themenLabel.setFont(HEADER_FONT);
		themenLabel.setToolTipText(TOOLTIP_LABEL);

		themenScrollPane = new QuizScrollPane<>();
		themenScrollPane.getListModel();

		// Tooltip für die Themenliste
		themenScrollPane.getList().setToolTipText(TOOLTIP_THEMEN_LISTE);

		// Einzelauswahl erzwingen
		themenScrollPane.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initLayout();
		setupListSelectionListener();
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridy = 0;
		add(themenLabel, gbc);

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		add(themenScrollPane, gbc);
	}

	private void setupListSelectionListener() {
		themenScrollPane.getList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && delegate != null) {
				Thema selectedThema = getSelectedThema();
				delegate.zeigeThema(selectedThema);
			}
		});
	}

	/**
	 * Setzt die Liste der anzuzeigenden Themen.
	 *
	 * @param themen Liste der Themen, die angezeigt werden sollen
	 */
	public void setThemen(List<Thema> themen) {
		if (themen == null) {
			themen = new ArrayList<>();
		}

		System.out.println("Debug: setThemen aufgerufen mit " + themen.size() + " Themen");
		for (int i = 0; i < themen.size(); i++) {
			Thema t = themen.get(i);
			System.out.println("Debug: Thema " + i + ": ID=" + t.getId() + ", Titel=" + t.getTitel());
		}

		themenScrollPane.getListModel().clear();
		aktuelleThemen.clear();
		aktuelleThemen.addAll(themen);

		for (Thema thema : themen) {
			if (thema != null) {
				themenScrollPane.getListModel().addElement(thema);
			}
		}

		System.out.println("Debug: JList hat jetzt " + themenScrollPane.getListModel().getSize() + " Einträge");
		System.out.println("Debug: aktuelleThemen hat " + aktuelleThemen.size() + " Einträge");

		// Aktualisiere das Label mit der Anzahl der Themen
		updateThemenLabel();
	}

	/**
	 * Setzt den Delegate für Thema-Auswahl-Events.
	 *
	 * @param delegate Der Delegate, der über Thema-Auswahlen informiert wird
	 */
	public void setThemaSelectionDelegate(ThemaSelectionDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Gibt das aktuell ausgewählte Thema zurück.
	 *
	 * @return Das ausgewählte Thema oder null, falls keins ausgewählt ist
	 */
	public Thema getSelectedThema() {
		return themenScrollPane.getList().getSelectedValue();
	}

	/**
	 * Löscht die aktuelle Auswahl.
	 */
	public void clearSelection() {
		themenScrollPane.getList().clearSelection();
	}

	/**
	 * Wählt ein spezifisches Thema aus.
	 *
	 * @param thema Das auszuwählende Thema
	 */
	public void selectThema(Thema thema) {
		if (thema == null) {
			clearSelection();
			return;
		}
		int index = aktuelleThemen.indexOf(thema);
		if (index >= 0) {
			themenScrollPane.getList().setSelectedIndex(index);
			themenScrollPane.getList().ensureIndexIsVisible(index);
		}
	}

	/**
	 * Wählt ein Thema anhand seiner ID aus.
	 *
	 * @param themaId Die ID des auszuwählenden Themas
	 */
	public void selectThemaById(long themaId) {
		if (themaId <= 0) {
			clearSelection();
			return;
		}

		// Durch alle Themen suchen
		for (int i = 0; i < aktuelleThemen.size(); i++) {
			if (aktuelleThemen.get(i).getId() == themaId) {
				themenScrollPane.getList().setSelectedIndex(i);
				themenScrollPane.getList().ensureIndexIsVisible(i);
				return;
			}
		}

		// Thema nicht gefunden - Auswahl löschen
		clearSelection();
	}

	/**
	 * Aktualisiert das Label mit der Anzahl der Themen.
	 */
	private void updateThemenLabel() {
		int anzahl = aktuelleThemen.size();
		if (anzahl == 0) {
			themenLabel.setText(LABEL_THEMENUEBERSICHT + " (keine Themen verfügbar)");
		} else if (anzahl == 1) {
			themenLabel.setText(LABEL_THEMENUEBERSICHT + " (1 Thema)");
		} else {
			themenLabel.setText(LABEL_THEMENUEBERSICHT + " (" + anzahl + " Themen)");
		}
	}

	/**
	 * Gibt die JList-Instanz zurück (für erweiterte Funktionalität).
	 *
	 * @return Die JList-Instanz
	 */
	public JList<Thema> getList() {
		return themenScrollPane.getList();
	}

	/**
	 * Gibt die QuizScrollPane-Instanz zurück.
	 *
	 * @return Die QuizScrollPane-Instanz
	 */
	public QuizScrollPane<Thema> getThemenScrollPane() {
		return themenScrollPane;
	}
}
