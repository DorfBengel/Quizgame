
package gui.fragen;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import gui.interfaces.FrageSelectionDelegate;
import gui.interfaces.GuiDefaults;
import gui.interfaces.QuizLeftPanel;
import quiz.data.model.Frage;

public class QuizfragenLinkesPanel extends JPanel implements GuiDefaults, FrageSelectionDelegate, QuizLeftPanel {
	private static final long serialVersionUID = 1L;

	private static final String LABEL_THEMA = "Thema";
	private static final String LABEL_TITEL = "Frage-Titel";
	private static final String LABEL_FRAGE = "Frage";
	private static final String LABEL_ANTWORTWAHL = "Antwortwahl";
	private static final String LABEL_RICHTIG = "Richtig";

	private static final String TOOLTIP_TITEL = "Titel der Frage";
	private static final String TOOLTIP_FRAGE = "Formulieren Sie hier die Frage (mehrzeilig möglich)";
	private static final String TOOLTIP_ANTWORT = "Antworttext";
	private static final String TOOLTIP_RICHTIG = "Markieren, wenn diese Antwort richtig ist";

	private static final int ANZAHL_ANTWORTEN = 4;
	private static final int TITEL_COLUMNS = 35;
	private static final int FRAGE_ROWS = 8;
	private static final int FRAGE_COLUMNS = 35;
	private static final int ANTWORT_COLUMNS = 35;

	// Längenlimits
	private static final int MAX_TITEL_LEN = 100;
	private static final int MAX_FRAGE_LEN = 500;
	private static final int MAX_ANTWORT_LEN = 200;

	private JLabel themaLabel;
	private JLabel themaLabelDynamic;
	private JLabel titelLabel;
	private JTextField titelField;
	private JLabel frageLabel;
	private JTextArea frageArea;
	private JScrollPane frageScrollPane;
	private JLabel antwortwahlLabel;
	private JLabel richtigLabel;
	private JTextField[] antwortFields;
	private JCheckBox[] richtigBoxes;

	private boolean hatUngespeicherteAenderungen = false;

	// Default-Rahmen merken, um nach Fehler-Markierung wiederherzustellen
	private Border defaultTextFieldBorder;
	private Border defaultTextAreaBorder;
	private final Border errorBorder = BorderFactory.createLineBorder(Color.RED);

	private final DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			markAsChanged();
			refreshValidationMarkers();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			markAsChanged();
			refreshValidationMarkers();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			markAsChanged();
			refreshValidationMarkers();
		}
	};

	public QuizfragenLinkesPanel() {
		super();
		setLayout(new GridBagLayout());
		initComponents();
		initLayout();
		setupDocumentListeners();
		applyInputLimits();
	}

	private void initComponents() {
		themaLabel = new JLabel(LABEL_THEMA);
		themaLabel.setFont(HEADER_FONT);

		themaLabelDynamic = new JLabel("Bitte Thema auswählen");
		themaLabelDynamic.setFont(LABEL_FONT);

		titelLabel = new JLabel(LABEL_TITEL);
		titelLabel.setFont(LABEL_FONT);

		titelField = new JTextField(TITEL_COLUMNS);
		titelField.setToolTipText(TOOLTIP_TITEL);

		frageLabel = new JLabel(LABEL_FRAGE);
		frageLabel.setFont(LABEL_FONT);

		frageArea = new JTextArea(FRAGE_ROWS, FRAGE_COLUMNS);
		frageArea.setLineWrap(true);
		frageArea.setWrapStyleWord(true);
		frageArea.setToolTipText(TOOLTIP_FRAGE);
		frageScrollPane = new JScrollPane(frageArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		antwortwahlLabel = new JLabel(LABEL_ANTWORTWAHL);
		antwortwahlLabel.setFont(LABEL_FONT);

		richtigLabel = new JLabel(LABEL_RICHTIG);
		richtigLabel.setFont(LABEL_FONT);

		antwortFields = new JTextField[ANZAHL_ANTWORTEN];
		richtigBoxes = new JCheckBox[ANZAHL_ANTWORTEN];
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			antwortFields[i] = new JTextField(ANTWORT_COLUMNS);
			antwortFields[i].setToolTipText(TOOLTIP_ANTWORT);
			richtigBoxes[i] = new JCheckBox();
			richtigBoxes[i].setToolTipText(TOOLTIP_RICHTIG);
		}

		// Default-Rahmen erfassen
		defaultTextFieldBorder = (new JTextField()).getBorder();
		defaultTextAreaBorder = (new JTextArea()).getBorder();
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0; // Standard: kein vertikales Gewicht

		// Zeile 0: Thema Label (spannt alle 3 Spalten)
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(themaLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		add(themaLabelDynamic, gbc);

		// Zeile 1: Titel (3 Spalten: Label | Eingabefeld | leer)
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		add(titelLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(titelField, gbc);

		// Zeile 2: Frage (3 Spalten: Label | TextArea | leer)
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		add(frageLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0; // Nur die Fragetext-Area bekommt vertikales Gewicht
		add(frageScrollPane, gbc);

		// Zeile 3: Antwortwahl-Header (3 Spalten: Label | leer | Checkbox-Label)
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0; // Zurücksetzen
		add(antwortwahlLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER; // Zentriert das "Richtig" Label
		add(richtigLabel, gbc);

		// Zeilen 4-7: Antworten (3 Spalten: Label | Eingabefeld | Checkbox)
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			gbc.gridy = 4 + i;

			// Antwort-Label (Spalte 0)
			gbc.gridx = 0;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST; // Labels links ausrichten
			JLabel antwortLabel = new JLabel("Antwort " + (i + 1));
			add(antwortLabel, gbc);

			// Antwort-Feld (Spalte 1) - bekommt den ganzen horizontalen Platz
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.WEST; // Eingabefelder links ausrichten
			add(antwortFields[i], gbc);

			// Richtig-Checkbox (Spalte 2) - zentral unter dem "Richtig" Label
			gbc.gridx = 2;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.CENTER; // Checkboxen zentral ausrichten
			add(richtigBoxes[i], gbc);
		}

		// Minimale Größen für bessere Responsivität
		titelField.setMinimumSize(new java.awt.Dimension(200, 25));
		frageArea.setMinimumSize(new java.awt.Dimension(250, 120));
		for (JTextField antwortField : antwortFields) {
			antwortField.setMinimumSize(new java.awt.Dimension(200, 25));
		}
	}

	private void setupDocumentListeners() {
		titelField.getDocument().addDocumentListener(documentListener);
		frageArea.getDocument().addDocumentListener(documentListener);
		for (JTextField antwortField : antwortFields) {
			antwortField.getDocument().addDocumentListener(documentListener);
		}
		for (JCheckBox box : richtigBoxes) {
			box.addItemListener(e -> refreshValidationMarkers());
		}
	}

	private void applyInputLimits() {
		((AbstractDocument) titelField.getDocument()).setDocumentFilter(new LengthFilter(MAX_TITEL_LEN));
		((AbstractDocument) frageArea.getDocument()).setDocumentFilter(new LengthFilter(MAX_FRAGE_LEN));
		for (JTextField antwortField : antwortFields) {
			((AbstractDocument) antwortField.getDocument()).setDocumentFilter(new LengthFilter(MAX_ANTWORT_LEN));
		}
	}

	@Override
	public void zeigeFrage(Frage frage) {
		if (frage == null) {
			themaLabelDynamic.setText("");
			titelField.setText("");
			frageArea.setText("");
			for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
				antwortFields[i].setText("");
				richtigBoxes[i].setSelected(false);
			}
			hatUngespeicherteAenderungen = false;
			refreshValidationMarkers();
			return;
		}
		// Thema kann hier ggf. über die MainPanel-Referenz gesetzt werden
		titelField.setText(frage.getFrageTitel());
		frageArea.setText(frage.getFrageText());
		for (int i = 0; i < ANZAHL_ANTWORTEN; i++) {
			if (i < frage.getAntworten().size()) {
				antwortFields[i].setText(frage.getAntworten().get(i).getText());
				richtigBoxes[i].setSelected(frage.getAntworten().get(i).istRichtig());
			} else {
				antwortFields[i].setText("");
				richtigBoxes[i].setSelected(false);
			}
		}
		hatUngespeicherteAenderungen = false;
		refreshValidationMarkers();
	}

	public String getFrageText() {
		return frageArea.getText();
	}

	public String getTitel() {
		return titelField.getText();
	}

	public String[] getAntworten() {
		String[] antworten = new String[antwortFields.length];
		for (int i = 0; i < antwortFields.length; i++) {
			antworten[i] = antwortFields[i].getText();
		}
		return antworten;
	}

	public boolean[] getRichtig() {
		boolean[] richtig = new boolean[richtigBoxes.length];
		for (int i = 0; i < richtigBoxes.length; i++) {
			richtig[i] = richtigBoxes[i].isSelected();
		}
		return richtig;
	}

	// Hilfsmethoden
	public void resetFields() {
		zeigeFrage(null);
	}

	@Override
	public void setThemaTitel(String titel) {
		// Dieses Bearbeitungs-Panel zeigt den Thema-Titel optional an; hier ignorieren
		// oder integrieren
		// Falls es ein Label gibt: themaLabelDynamic.setText(titel != null ? titel :
		// "");
	}

	public boolean hatUngespeicherteAenderungen() {
		return hatUngespeicherteAenderungen;
	}

	public void markAsSaved() {
		hatUngespeicherteAenderungen = false;
	}

	public boolean isTitelValid() {
		String t = getTitel();
		return t != null && !t.trim().isEmpty();
	}

	public boolean isFrageValid() {
		String f = getFrageText();
		return f != null && !f.trim().isEmpty();
	}

	public boolean hatMindestensEineAntwort() {
		for (String a : getAntworten()) {
			if (a != null && !a.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean hatMindestensEineRichtige() {
		boolean[] r = getRichtig();
		String[] a = getAntworten();
		for (int i = 0; i < r.length; i++) {
			if (r[i] && a[i] != null && !a[i].trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void markAsChanged() {
		hatUngespeicherteAenderungen = true;
	}

	private void refreshValidationMarkers() {
		setBorderForTextField(titelField, isTitelValid());
		setBorderForTextArea(frageArea, isFrageValid());
		String[] antworten = getAntworten();
		boolean[] richtig = getRichtig();
		for (int i = 0; i < antworten.length; i++) {
			boolean fieldOk = !(richtig[i] && (antworten[i] == null || antworten[i].trim().isEmpty()));
			setBorderForTextField(antwortFields[i], fieldOk);
		}
	}

	private void setBorderForTextField(JTextField field, boolean ok) {
		field.setBorder(ok ? defaultTextFieldBorder : errorBorder);
	}

	private void setBorderForTextArea(JTextArea area, boolean ok) {
		area.setBorder(ok ? defaultTextAreaBorder : errorBorder);
	}

	// DocumentFilter zur Begrenzung der Eingabelänge
	private static class LengthFilter extends DocumentFilter {
		private final int maxLen;

		private LengthFilter(int maxLen) {
			this.maxLen = maxLen;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if (string == null) {
				return;
			}
			if (fb.getDocument().getLength() + string.length() <= maxLen) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if (text == null) {
				return;
			}
			int currentLen = fb.getDocument().getLength();
			int newLen = currentLen - length + text.length();
			if (newLen <= maxLen) {
				super.replace(fb, offset, length, text, attrs);
			}
		}
	}
}
