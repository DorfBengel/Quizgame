
package gui.fragen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import gui.QuizScrollPane;
import gui.interfaces.GuiDefaults;
import gui.interfaces.QuizLeftPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

public class QuizfragenRechtesPanel extends JPanel implements GuiDefaults {

	private static final long serialVersionUID = 1L;

	// UI-Konstanten
	private static final String LABEL_THEMEN = "Themenauswahl";
	private static final String TOOLTIP_COMBO = "Thema wählen, um Fragen anzuzeigen";
	private static final String TOOLTIP_LISTE = "Frage aus der Liste auswählen";

	private final JLabel fragenLabel;
	final JComboBox<Thema> fragenComboBox;
	final QuizScrollPane<Frage> fragenScrollPane;
	private QuizLeftPanel linkesPanel; // Referenz auf das linke Panel (Interface)
	List<Thema> aktuelleThemen = new ArrayList<>();

	public static final Thema ALLE_THEMEN = new Thema("Alle Themen", "");

	/**
	 * Aktualisiert die Fragen für ein spezifisches Thema ohne alle Themen neu zu
	 * laden.
	 *
	 * @param thema  Das Thema, dessen Fragen aktualisiert werden sollen
	 * @param fragen Die neuen Fragen für das Thema
	 */
	public void aktualisiereFragenFuerThema(Thema thema, List<Frage> fragen) {
		System.out.println("QuizfragenRechtesPanel: Aktualisiere Fragen für Thema '" + thema.getTitel() + "'");

		// Fragen für das aktuelle Thema aktualisieren
		if (thema != null && thema != ALLE_THEMEN) {
			// Aktuell ausgewähltes Thema in der ComboBox finden und aktualisieren
			for (int i = 0; i < fragenComboBox.getItemCount(); i++) {
				Thema comboThema = fragenComboBox.getItemAt(i);
				if (comboThema != null && comboThema.getId() == thema.getId()) {
					// Fragen im Thema aktualisieren
					comboThema.setFragen(fragen);
					System.out.println("QuizfragenRechtesPanel: " + fragen.size() + " Fragen für Thema '"
							+ thema.getTitel() + "' aktualisiert");
					break;
				}
			}
		}

		// Fragen-Liste neu laden
		ladeFragenZumThema();
		
		// WICHTIG: Wenn eine Frage ausgewählt ist, diese neu anzeigen
		Frage selectedFrage = getSelectedFrage();
		if (selectedFrage != null && linkesPanel != null) {
			// Aktualisierte Frage aus dem aktualisierten Thema holen
			Thema aktuellesThema = getSelectedThemaFromCombo();
			if (aktuellesThema != null && aktuellesThema != ALLE_THEMEN) {
				// Frage mit aktualisierten Daten finden
				for (Frage frage : aktuellesThema.getFragen()) {
					if (frage.getId() == selectedFrage.getId()) {
						System.out.println("QuizfragenRechtesPanel: Aktualisierte Frage im linken Panel anzeigen");
						linkesPanel.zeigeFrage(frage);
						break;
					}
				}
			}
		}
	}

	public QuizfragenRechtesPanel() {
		super();
		setLayout(new GridBagLayout());

		fragenLabel = new JLabel(LABEL_THEMEN);
		fragenLabel.setFont(HEADER_FONT);

		fragenComboBox = new JComboBox<>();
		fragenComboBox.setToolTipText(TOOLTIP_COMBO);
		fragenScrollPane = new QuizScrollPane<>();
		fragenScrollPane.getList().setToolTipText(TOOLTIP_LISTE);
		fragenScrollPane.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initLayout();

		fragenComboBox.addActionListener(e -> ladeFragenZumThema());
		fragenScrollPane.getList().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && linkesPanel != null) {
				Frage selected = fragenScrollPane.getList().getSelectedValue();
				linkesPanel.zeigeFrage(selected);
			}
		});
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(fragenLabel, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(2, 8, 8, 8); // Reduzierte Abstände
		add(fragenComboBox, gbc);

		gbc.gridy = 2;
		gbc.insets = DEFAULT_INSETS; // Normale Abstände wiederherstellen
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		add(fragenScrollPane, gbc);

		// ComboBox-Listener für Themenauswahl
		fragenComboBox.addActionListener(e -> {
			Thema selectedThema = getSelectedThemaFromCombo();
			if (linkesPanel != null && selectedThema != null && selectedThema != ALLE_THEMEN) {
				// Benachrichtige das linke Panel über die Themenauswahl
				linkesPanel.setThemaTitel(selectedThema.getTitel());
			} else if (linkesPanel != null && (selectedThema == null || selectedThema == ALLE_THEMEN)) {
				linkesPanel.setThemaTitel("");
			}
		});
	}

	public void setThemen(List<Thema> themen) {
		fragenComboBox.removeAllItems();
		aktuelleThemen.clear();

		if (themen != null) {
			aktuelleThemen.addAll(themen);
			fragenComboBox.addItem(ALLE_THEMEN);
			themen.forEach(fragenComboBox::addItem);

			if (!themen.isEmpty()) {
				fragenComboBox.setSelectedItem(themen.get(0));
				if (linkesPanel != null) {
					linkesPanel.setThemaTitel(themen.get(0).getTitel());
				}
			}
		}

		ladeFragenZumThema();
	}

	private void ladeFragenZumThema() {
		Thema thema = (Thema) fragenComboBox.getSelectedItem();
		fragenScrollPane.getListModel().clear();

		if (thema == ALLE_THEMEN) {
			for (Thema t : aktuelleThemen) {
				for (Frage frage : t.getFragen()) {
					fragenScrollPane.getListModel().addElement(frage);
				}
			}
		} else if (thema != null) {
			for (Frage frage : thema.getFragen()) {
				fragenScrollPane.getListModel().addElement(frage);
			}
		}
		updateLabel();
	}

	public void setLinkesPanel(QuizLeftPanel linkesPanel) {
		this.linkesPanel = linkesPanel;
	}

	public QuizScrollPane<Frage> getFragenScrollPane() {
		return fragenScrollPane;
	}

	public void clearSelection() {
		fragenScrollPane.getList().clearSelection();
	}

	public Thema getSelectedThemaFromCombo() {
		Object item = fragenComboBox.getSelectedItem();
		return (item instanceof Thema) ? (Thema) item : null;
	}

	public Frage getSelectedFrage() {
		return fragenScrollPane.getList().getSelectedValue();
	}

	public void selectThema(Thema thema) {
		if (thema == null) {
			fragenComboBox.setSelectedItem(ALLE_THEMEN);
			if (linkesPanel != null) {
				linkesPanel.setThemaTitel("");
			}
			return;
		}
		fragenComboBox.setSelectedItem(thema);
		if (linkesPanel != null) {
			linkesPanel.setThemaTitel(thema.getTitel());
		}
	}

	public void selectFrageById(long frageId) {
		if (frageId <= 0) {
			clearSelection();
			return;
		}

		for (int i = 0; i < fragenScrollPane.getListModel().getSize(); i++) {
			Frage frage = fragenScrollPane.getListModel().getElementAt(i);
			if (frage.getId() == frageId) {
				fragenScrollPane.getList().setSelectedIndex(i);
				fragenScrollPane.getList().ensureIndexIsVisible(i);
				return;
			}
		}

		clearSelection();
	}

	public List<Thema> getAktuelleThemen() {
		return new ArrayList<>(aktuelleThemen);
	}

	private void updateLabel() {
		Object item = fragenComboBox.getSelectedItem();
		int count = fragenScrollPane.getListModel().getSize();
		if (item == ALLE_THEMEN) {
			fragenLabel.setText(LABEL_THEMEN + " (Alle Themen: " + count + " Fragen)");
		} else if (item instanceof Thema) {
			fragenLabel.setText(LABEL_THEMEN + " (" + ((Thema) item).getTitel() + ": " + count + " Fragen)");
		} else {
			fragenLabel.setText(LABEL_THEMEN);
		}
	}
}
