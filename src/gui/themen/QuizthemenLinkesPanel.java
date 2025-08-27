
package gui.themen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.interfaces.GuiDefaults;
import quiz.data.model.Thema;

/**
 * Panel zur Eingabe und Bearbeitung von Quiz-Themen. Ermöglicht das Erstellen
 * neuer Themen und das Bearbeiten bestehender Themen.
 */
public class QuizthemenLinkesPanel extends JPanel implements GuiDefaults {

	private static final long serialVersionUID = 1L;

	// Konstanten für bessere Wartbarkeit
	private static final String LABEL_NEUES_THEMA = "Neues Thema";
	private static final String LABEL_TITEL = "Titel:";
	private static final String LABEL_INFORMATIONEN = "Informationen zum Thema:";
	private static final String TOOLTIP_TITEL = "Der Titel des Quiz-Themas (max. 100 Zeichen)";
	private static final String TOOLTIP_INFO = "Beschreibung und Informationen zum Quiz-Thema";

	private static final int TITEL_FIELD_COLUMNS = 25;
	private static final int INFO_AREA_ROWS = 6;
	private static final int INFO_AREA_COLUMNS = 25;
	private static final int MAX_TITEL_LAENGE = 100;

	private JLabel themaLabel;
	private JLabel titelLabel;
	private JTextField titelField;
	private JLabel infoLabel;
	private JTextArea infoThemaArea;
	private JScrollPane infoScrollPane;

	// Referenz auf das aktuell angezeigte Thema
	private Thema aktuellesThema;

	// Flag für ungespeicherte Änderungen
	private boolean hatUngespeicherteAenderungen = false;

	// DocumentListener für Eingabeänderungen
	private final DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			markAsChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			markAsChanged();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			markAsChanged();
		}
	};

	public QuizthemenLinkesPanel() {
		super();
		setLayout(new GridBagLayout());
		initComponents();
		initLayout();
		setupDocumentListeners();
	}

	private void initComponents() {
		themaLabel = new JLabel(LABEL_NEUES_THEMA);
		themaLabel.setFont(HEADER_FONT);

		titelLabel = new JLabel(LABEL_TITEL);
		titelLabel.setFont(LABEL_FONT);

		titelField = new JTextField(TITEL_FIELD_COLUMNS);
		titelField.setToolTipText(TOOLTIP_TITEL);

		infoLabel = new JLabel(LABEL_INFORMATIONEN);
		infoLabel.setFont(LABEL_FONT);

		infoThemaArea = new JTextArea(INFO_AREA_ROWS, INFO_AREA_COLUMNS);
		infoThemaArea.setLineWrap(true);
		infoThemaArea.setWrapStyleWord(true);
		infoThemaArea.setToolTipText(TOOLTIP_INFO);

		infoScrollPane = new JScrollPane(infoThemaArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}

	private void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = DEFAULT_INSETS;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		// Thema Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(themaLabel, gbc);

		// Titel Label
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		add(titelLabel, gbc);

		// Titel Field - flexibel in der Breite
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(titelField, gbc);

		// Info Label
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(infoLabel, gbc);

		// Info Thema Area (mit ScrollPane) - flexibel in beiden Dimensionen
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(infoScrollPane, gbc);

		// Minimale Größen für bessere Responsivität
		titelField.setMinimumSize(new java.awt.Dimension(150, 25));
		infoThemaArea.setMinimumSize(new java.awt.Dimension(200, 100));
	}

	private void setupDocumentListeners() {
		titelField.getDocument().addDocumentListener(documentListener);
		infoThemaArea.getDocument().addDocumentListener(documentListener);
	}

	/**
	 * Gibt den aktuellen Titel-Text zurück.
	 *
	 * @return Der Titel-Text oder leerer String, falls kein Text eingegeben wurde
	 */
	public String getTitel() {
		return titelField.getText() != null ? titelField.getText().trim() : "";
	}

	/**
	 * Gibt den aktuellen Informations-Text zurück.
	 *
	 * @return Der Informations-Text oder leerer String, falls kein Text eingegeben
	 *         wurde
	 */
	public String getInfoThema() {
		return infoThemaArea.getText() != null ? infoThemaArea.getText().trim() : "";
	}

	/**
	 * Zeigt ein Thema zur Bearbeitung an oder setzt die Felder zurück.
	 *
	 * @param thema Das anzuzeigende Thema oder null für ein neues Thema
	 */
	public void zeigeThema(Thema thema) {
		// Temporär DocumentListener entfernen, um ungewollte Änderungen zu vermeiden
		titelField.getDocument().removeDocumentListener(documentListener);
		infoThemaArea.getDocument().removeDocumentListener(documentListener);

		this.aktuellesThema = thema;

		if (thema != null) {
			titelField.setText(thema.getTitel() != null ? thema.getTitel() : "");
			infoThemaArea.setText(thema.getInformation() != null ? thema.getInformation() : "");
			themaLabel.setText("Thema bearbeiten: " + thema.getTitel());
		} else {
			titelField.setText("");
			infoThemaArea.setText("");
			themaLabel.setText(LABEL_NEUES_THEMA);
		}

		// DocumentListener wieder hinzufügen
		titelField.getDocument().addDocumentListener(documentListener);
		infoThemaArea.getDocument().addDocumentListener(documentListener);

		// Änderungen zurücksetzen
		hatUngespeicherteAenderungen = false;

		// Fokus auf das erste Eingabefeld setzen
		SwingUtilities.invokeLater(() -> titelField.requestFocusInWindow());
	}

	/**
	 * Setzt alle Eingabefelder zurück.
	 */
	public void resetFields() {
		zeigeThema(null);
	}

	/**
	 * Überprüft, ob der eingegebene Titel gültig ist.
	 *
	 * @return true, wenn der Titel gültig ist, false sonst
	 */
	public boolean isTitelValid() {
		String titel = getTitel();
		return titel != null && !titel.isEmpty() && titel.length() <= MAX_TITEL_LAENGE;
	}

	/**
	 * Überprüft, ob es ungespeicherte Änderungen gibt.
	 *
	 * @return true, wenn es ungespeicherte Änderungen gibt, false sonst
	 */
	public boolean hatUngespeicherteAenderungen() {
		return hatUngespeicherteAenderungen;
	}

	/**
	 * Markiert das Panel als geändert.
	 */
	private void markAsChanged() {
		hatUngespeicherteAenderungen = true;
	}

	/**
	 * Markiert alle Änderungen als gespeichert.
	 */
	public void markAsSaved() {
		hatUngespeicherteAenderungen = false;
	}

	/**
	 * Gibt das aktuell angezeigte Thema zurück.
	 *
	 * @return Das aktuelle Thema oder null, falls keins angezeigt wird
	 */
	public Thema getAktuellesThema() {
		return aktuellesThema;
	}

	/**
	 * Überprüft, ob ein neues Thema erstellt wird.
	 *
	 * @return true, wenn ein neues Thema erstellt wird, false sonst
	 */
	public boolean isNeuesThema() {
		return aktuellesThema == null;
	}
}
