package gui.quiz;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import gui.interfaces.GuiDefaults;
import gui.interfaces.QuizLeftPanel;
import quiz.data.model.Antwort;
import quiz.data.model.Frage;

/**
 * Panel zur Anzeige von Quiz-Fragen (nicht editierbar, außer
 * Antwort-Checkboxen). Speziell für das Quiz-Spiel entwickelt.
 */
public class QuizspielPanelLinks extends JPanel implements GuiDefaults, QuizLeftPanel {

	private static final long serialVersionUID = 1L;

	private static final String LABEL_THEMA = "Thema";
	private static final String LABEL_TITEL = "Frage-Titel";
	private static final String LABEL_FRAGE = "Frage";
	private static final String LABEL_ANTWORTWAHL = "Antwortwahl";

	private static final int ANZAHL_ANTWORTEN = 4;
	private static final int TITEL_COLUMNS = 35;
	private static final int FRAGE_ROWS = 8;
	private static final int FRAGE_COLUMNS = 35;
	private static final int ANTWORT_COLUMNS = 35;

	private JLabel themaLabel;
	private JLabel themaLabelDynamic;
	private JLabel titelLabel;
	private JTextField titelField;
	private JLabel frageLabel;
	private JTextArea frageArea;
	private JScrollPane frageScrollPane;
	private JLabel antwortwahlLabel;
	private JTextField[] antwortFields;
	private JCheckBox[] richtigBoxes;

	private String aktuellesThemaTitel = "";

	public QuizspielPanelLinks() {
		super();
		setLayout(new GridBagLayout());
		initComponents();
		initLayout();
		setReadOnlyMode();
	}

	private void initComponents() {
		themaLabel = new JLabel(LABEL_THEMA + ":");
		themaLabel.setFont(LABEL_FONT);

		themaLabelDynamic = new JLabel("");
		themaLabelDynamic.setFont(LABEL_FONT);

		titelLabel = new JLabel(LABEL_TITEL + ":");
		titelLabel.setFont(LABEL_FONT);

		titelField = new JTextField(TITEL_COLUMNS);
		titelField.setToolTipText("Titel der Frage (nur Anzeige)");

		frageLabel = new JLabel(LABEL_FRAGE + ":");
		frageLabel.setFont(LABEL_FONT);

		frageArea = new JTextArea(FRAGE_ROWS, FRAGE_COLUMNS);
		frageArea.setLineWrap(true);
		frageArea.setWrapStyleWord(true);
		frageArea.setToolTipText("Frage-Text (nur Anzeige)");

		frageScrollPane = new JScrollPane(frageArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		antwortwahlLabel = new JLabel(LABEL_ANTWORTWAHL + ":");
		antwortwahlLabel.setFont(LABEL_FONT);

		antwortFields = new JTextField[ANZAHL_ANTWORTEN];
		richtigBoxes = new JCheckBox[ANZAHL_ANTWORTEN];

		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			antwortFields[i] = new JTextField(ANTWORT_COLUMNS);
			antwortFields[i].setToolTipText("Antworttext (nur Anzeige)");

			richtigBoxes[i] = new JCheckBox();
			richtigBoxes[i].setToolTipText("Auswahl Ihrer Antwort");
		}
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		// Thema Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		add(themaLabel, gbc);

		// Thema Label (dynamisch)
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(themaLabelDynamic, gbc);

		// Titel Label
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		add(titelLabel, gbc);

		// Titel Field
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(titelField, gbc);

		// Frage Label
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(frageLabel, gbc);

		// Frage Area (mit ScrollPane)
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(frageScrollPane, gbc);

		// Antwortwahl Label
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(antwortwahlLabel, gbc);

		// Antworten und Checkboxen
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			// Antwort Label
			gbc.gridx = 0;
			gbc.gridy = 5 + i;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			add(new JLabel("Antwort " + (i + 1) + ":"), gbc);

			// Antwort Field
			gbc.gridx = 1;
			gbc.gridy = 5 + i;
			gbc.gridwidth = 1;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(antwortFields[i], gbc);

			// Richtig Checkbox (vom Benutzer nutzbar)
			gbc.gridx = 2;
			gbc.gridy = 5 + i;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			add(richtigBoxes[i], gbc);
		}

		// Minimale Größen
		titelField.setMinimumSize(new java.awt.Dimension(150, 25));
		frageArea.setMinimumSize(new java.awt.Dimension(200, 100));
		for (JTextField antwortField : antwortFields) {
			antwortField.setMinimumSize(new java.awt.Dimension(150, 25));
		}
	}

	private void setReadOnlyMode() {
		titelField.setEditable(false);
		titelField.setBackground(new Color(240, 240, 240));

		frageArea.setEditable(false);
		frageArea.setBackground(new Color(240, 240, 240));

		for (JTextField antwortField : antwortFields) {
			antwortField.setEditable(false);
			antwortField.setBackground(new Color(240, 240, 240));
		}
		// Checkboxes bleiben ENABLED – Benutzer trifft Auswahl hier
	}

	@Override
	public void zeigeFrage(Frage frage) {
		if (frage == null) {
			clearFields();
			return;
		}

		titelField.setText(frage.getFrageTitel() != null ? frage.getFrageTitel() : "");
		frageArea.setText(frage.getFrageText() != null ? frage.getFrageText() : "");

		// Antworten anzeigen, Checkboxen NICHT vorauswählen
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			if (i < frage.getAntworten().size()) {
				Antwort antwort = frage.getAntworten().get(i);
				antwortFields[i].setText(antwort.getText() != null ? antwort.getText() : "");
				richtigBoxes[i].setSelected(false);
			} else {
				antwortFields[i].setText("");
				richtigBoxes[i].setSelected(false);
			}
		}
	}

	public void zeigeKorrekteAntworten(Frage frage) {
		if (frage == null) {
			return;
		}
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			if (i < frage.getAntworten().size()) {
				richtigBoxes[i].setSelected(frage.getAntworten().get(i).istRichtig());
			} else {
				richtigBoxes[i].setSelected(false);
			}
		}
	}

	private void clearFields() {
		titelField.setText("");
		frageArea.setText("");
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			antwortFields[i].setText("");
			richtigBoxes[i].setSelected(false);
		}
	}

	@Override
	public void setThemaTitel(String titel) {
		this.aktuellesThemaTitel = titel != null ? titel : "";
		themaLabelDynamic.setText(this.aktuellesThemaTitel);
	}

	public boolean[] getRichtig() {
		boolean[] richtig = new boolean[ANZAHL_ANTWORTEN];
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			richtig[i] = richtigBoxes[i].isSelected();
		}
		return richtig;
	}
}
