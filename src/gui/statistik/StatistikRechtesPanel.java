package gui.statistik;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gui.QuizScrollPane;
import gui.interfaces.GuiDefaults;
import gui.interfaces.QuizLeftPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

/**
 * Rechtes Panel für die Statistik-Ansicht. Ermöglicht die Auswahl von Themen
 * und Fragen für detaillierte Statistiken.
 */
public class StatistikRechtesPanel extends JPanel implements GuiDefaults {

	private static final long serialVersionUID = 1L;

	private static final String LABEL_THEMEN = "Themenauswahl";
	private static final String LABEL_FRAGEN = "Fragen für detaillierte Statistiken";
	private static final String TOOLTIP_COMBO = "Thema wählen, um Statistiken anzuzeigen";
	private static final String TOOLTIP_LISTE = "Frage auswählen für detaillierte Statistik";

	private final JLabel themenLabel;
	private final JLabel fragenLabel;
	final JComboBox<Thema> themenComboBox;
	final QuizScrollPane<Frage> fragenScrollPane;
	private QuizLeftPanel linkesPanel; // Referenz auf das linke Panel (Interface)
	List<Thema> aktuelleThemen = new ArrayList<>();

	public static final Thema ALLE_THEMEN = new Thema("Alle Themen", "");

	public StatistikRechtesPanel() {
		super();
		setLayout(new GridBagLayout());

		themenLabel = new JLabel(LABEL_THEMEN + ":");
		themenLabel.setFont(LABEL_FONT);

		themenComboBox = new JComboBox<>();
		themenComboBox.setToolTipText(TOOLTIP_COMBO);

		fragenLabel = new JLabel(LABEL_FRAGEN + ":");
		fragenLabel.setFont(LABEL_FONT);

		fragenScrollPane = new QuizScrollPane<>();

		initLayout();
		setupComboBoxListener();
		setupListSelectionListener();
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		// Themen Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		add(themenLabel, gbc);

		// Themen ComboBox
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(themenComboBox, gbc);

		// Fragen Label
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(fragenLabel, gbc);

		// Fragen ScrollPane
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(fragenScrollPane, gbc);
	}

	private void setupComboBoxListener() {
		themenComboBox.addActionListener(e -> {
			if (linkesPanel != null) {
				Thema selectedThema = (Thema) themenComboBox.getSelectedItem();
				if (selectedThema != null) {
					linkesPanel.setThemaTitel(selectedThema.getTitel());
					if (selectedThema != ALLE_THEMEN) {
						ladeFragenZumThema();
					}
				}
			}
		});
	}

	private void setupListSelectionListener() {
		fragenScrollPane.getList().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && linkesPanel != null) {
					Frage selectedFrage = fragenScrollPane.getList().getSelectedValue();
					if (selectedFrage != null) {
						linkesPanel.zeigeFrage(selectedFrage);
					}
				}
			}
		});
	}

	public void setThemen(List<Thema> themen) {
		this.aktuelleThemen = new ArrayList<>(themen);

		// ComboBox leeren und "Alle Themen" hinzufügen
		themenComboBox.removeAllItems();
		themenComboBox.addItem(ALLE_THEMEN);

		// Alle Themen hinzufügen
		for (Thema thema : themen) {
			themenComboBox.addItem(thema);
		}

		// Erstes Thema auswählen
		if (!themen.isEmpty()) {
			themenComboBox.setSelectedItem(themen.get(0));
			if (linkesPanel != null) {
				linkesPanel.setThemaTitel(themen.get(0).getTitel());
			}
		}

		updateLabel();
	}

	private void ladeFragenZumThema() {
		Thema selectedThema = (Thema) themenComboBox.getSelectedItem();
		if (selectedThema == null || selectedThema == ALLE_THEMEN) {
			fragenScrollPane.getListModel().clear();
			return;
		}

		List<Frage> fragen = selectedThema.getFragen();
		fragenScrollPane.getListModel().clear();
		for (Frage frage : fragen) {
			fragenScrollPane.getListModel().addElement(frage);
		}
	}

	public void aktualisiereFragenFuerThema(Thema thema, List<Frage> fragen) {
		if (thema == null) {
			return;
		}

		// Fragen zum Thema hinzufügen
		thema.setFragen(fragen);

		// Wenn dieses Thema aktuell ausgewählt ist, Fragen neu laden
		if (themenComboBox.getSelectedItem() == thema) {
			ladeFragenZumThema();
		}
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
		return (Thema) themenComboBox.getSelectedItem();
	}

	public Frage getSelectedFrage() {
		return fragenScrollPane.getList().getSelectedValue();
	}

	public void selectThema(Thema thema) {
		if (thema != null) {
			themenComboBox.setSelectedItem(thema);
			if (linkesPanel != null) {
				linkesPanel.setThemaTitel(thema.getTitel());
			}
			ladeFragenZumThema();
		}
	}

	public void selectFrageById(long frageId) {
		for (int i = 0; i < fragenScrollPane.getListModel().getSize(); i++) {
			Frage frage = fragenScrollPane.getListModel().getElementAt(i);
			if (frage.getId() == frageId) {
				fragenScrollPane.getList().setSelectedIndex(i);
				fragenScrollPane.getList().ensureIndexIsVisible(i);
				break;
			}
		}
	}

	public List<Thema> getAktuelleThemen() {
		return new ArrayList<>(aktuelleThemen);
	}

	private void updateLabel() {
		int anzahlThemen = aktuelleThemen.size();
		themenLabel.setText(LABEL_THEMEN + " (" + anzahlThemen + "):");
	}
}
