
package gui.fragen;

import java.util.ArrayList;
import java.util.List;

import gui.AbstractActionPanel;
import gui.common.ValidationHelper;
import gui.fragen.FragenMainPanel;
import gui.util.DialogService;
import quiz.data.model.Antwort;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

public class QuizfragenActionPanel extends AbstractActionPanel {
	private static final long serialVersionUID = 1L;

	private FragenMainPanel mainPanel;

	// Nachrichten-Konstanten
	private static final String MSG_KEINE_FRAGE_AUSGEWAEHLT = "Keine Frage ausgewählt.";
	private static final String MSG_FRAGE_GELOESCHT = "Frage gelöscht.";
	private static final String MSG_FRAGE_GESPEICHERT = "Frage gespeichert.";
	private static final String MSG_BESTAETIGE_LOESCHEN_TITEL = "Frage löschen bestätigen";
	private static final String MSG_BESTAETIGE_LOESCHEN_TEXT = "Möchten Sie die ausgewählte Frage wirklich löschen?";
	private static final String MSG_THEMA_AUSWAEHLEN = "Bitte wählen Sie ein konkretes Thema aus, bevor Sie eine Frage speichern.";
	private static final String MSG_TITEL_LEER = "Der Fragetitel darf nicht leer sein.";
	private static final String MSG_FRAGETEXT_LEER = "Der Fragetext darf nicht leer sein.";
	private static final String MSG_ANTWORTEN_LEER = "Bitte geben Sie mindestens eine Antwort ein.";
	private static final String MSG_KEINE_RICHTIGE = "Bitte markieren Sie mindestens eine Antwort als richtig.";
	private static final String MSG_RICHTIG_OHNE_TEXT = "Eine als richtig markierte Antwort ist leer. Bitte Text eingeben oder Markierung entfernen.";

	public QuizfragenActionPanel() {
		super();
		configureButtons();
		initListeners();
	}

	public void setMainPanel(FragenMainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void initListeners() {
		button1.addActionListener(e -> loescheFrage());
		button2.addActionListener(e -> speichereFrage());
		button3.addActionListener(e -> neueFrage());
	}

	private void neueFrage() {
		if (mainPanel == null) {
			return;
		}
		resetLinkesPanel();
		resetRechtesPanelAuswahl();
	}

	private void resetLinkesPanel() {
		mainPanel.getPanelLinks().zeigeFrage(null);
	}

	private void resetRechtesPanelAuswahl() {
		mainPanel.getPanelRechts().clearSelection();
	}

	private void loescheFrage() {
		if (mainPanel == null) {
			return;
		}

		Thema ausgewaehltesThema = mainPanel.getPanelRechts().getSelectedThemaFromCombo();
		Frage frage = mainPanel.getPanelRechts().getSelectedFrage();

		if (ausgewaehltesThema == QuizfragenRechtesPanel.ALLE_THEMEN && frage != null) {
			// Bei "Alle Themen" das konkrete Thema der Frage finden
			ausgewaehltesThema = mainPanel.getPanelRechts().getAktuelleThemen().stream()
					.filter(t -> t.getFragen().contains(frage)).findFirst().orElse(null);
		}

		if (frage == null || ausgewaehltesThema == null) {
			DialogService.warn(this, MSG_KEINE_FRAGE_AUSGEWAEHLT);
			return;
		}

		boolean bestaetigung = DialogService.confirm(this, MSG_BESTAETIGE_LOESCHEN_TEXT, MSG_BESTAETIGE_LOESCHEN_TITEL);
		if (!bestaetigung) {
			return;
		}

		try {
			mainPanel.getDataProvider().loescheFrage(frage);
			mainPanel.ladeAlleThemen();

			// Thema nach dem Neuladen wieder auswählen
			mainPanel.getPanelRechts().selectThema(ausgewaehltesThema);

			// Auswahl zurücksetzen
			mainPanel.getPanelLinks().zeigeFrage(null);
			mainPanel.getPanelRechts().clearSelection();

			DialogService.info(this, MSG_FRAGE_GELOESCHT);
		} catch (Exception ex) {
			DialogService.error(this, "Fehler beim Löschen der Frage: " + ex.getMessage());
		}
	}

	// Speichern/Updaten einer Frage
	private void speichereFrage() {
		if (mainPanel == null) {
			return;
		}

		Thema ausgewaehltesThema = mainPanel.getPanelRechts().getSelectedThemaFromCombo();

		if (ausgewaehltesThema == QuizfragenRechtesPanel.ALLE_THEMEN) {
			DialogService.warn(this, MSG_THEMA_AUSWAEHLEN);
			return;
		}

		Frage ausgewaehlteFrage = mainPanel.getPanelRechts().getSelectedFrage();

		String titel = ValidationHelper.trimOrEmpty(mainPanel.getPanelLinks().getTitel());
		String frageText = ValidationHelper.trimOrEmpty(mainPanel.getPanelLinks().getFrageText());
		String[] antworten = mainPanel.getPanelLinks().getAntworten();
		boolean[] richtig = mainPanel.getPanelLinks().getRichtig();

		// Validierung
		String validierungsFehler = pruefeEingaben(titel, frageText, antworten, richtig);
		if (validierungsFehler != null) {
			DialogService.warn(this, validierungsFehler);
			return;
		}

		try {
			Frage frage;
			
			// Prüfen, ob eine bestehende Frage bearbeitet wird
			if (ausgewaehlteFrage != null && ausgewaehlteFrage.getId() > 0) {
				// Bestehende Frage bearbeiten
				frage = ausgewaehlteFrage;
				frage.setFrageTitel(titel);
				frage.setFrageText(frageText);
				
				// Antworten aktualisieren - immer alle Antworten neu erstellen
				List<Antwort> antwortList = new ArrayList<>();
				for (int i = 0; i < antworten.length; i++) {
					String antwortText = ValidationHelper.trimOrEmpty(antworten[i]);
					if (!antwortText.isEmpty()) {
						// Neue Antwort erstellen (ID wird vom Repository gesetzt)
						Antwort neueAntwort = new Antwort(antwortText, richtig[i]);
						antwortList.add(neueAntwort);
					}
				}
				frage.setAntworten(antwortList);
				
				// Bestehende Frage aktualisieren
				mainPanel.getDataProvider().speichereFrage(frage, ausgewaehltesThema.getId());
			} else {
				// Neue Frage erstellen
				frage = new Frage(frageText);
				frage.setFrageTitel(titel);
				List<Antwort> antwortList = new ArrayList<>();
				for (int i = 0; i < antworten.length; i++) {
					String antwortText = ValidationHelper.trimOrEmpty(antworten[i]);
					if (!antwortText.isEmpty()) {
						antwortList.add(new Antwort(antwortText, richtig[i]));
					}
				}
				frage.setAntworten(antwortList);
				
				// Neue Frage speichern
				mainPanel.getDataProvider().speichereFrage(frage, ausgewaehltesThema.getId());
			}
			mainPanel.ladeAlleThemen();

			// Thema nach dem Neuladen wieder auswählen
			mainPanel.getPanelRechts().selectThema(ausgewaehltesThema);

			// Frage nach dem Neuladen wieder auswählen (falls noch vorhanden)
			if (ausgewaehlteFrage != null) {
				// Versuche die gespeicherte Frage wiederzufinden
				List<Frage> fragen = ausgewaehltesThema.getFragen();
				for (int i = 0; i < fragen.size(); i++) {
					if (fragen.get(i).getFrageTitel().equals(titel)) {
						mainPanel.getPanelRechts().getFragenScrollPane().getList().setSelectedIndex(i);
						break;
					}
				}
			}

			DialogService.info(this, MSG_FRAGE_GESPEICHERT);
		} catch (IllegalArgumentException ex) {
			DialogService.warn(this, ex.getMessage());
		} catch (Exception ex) {
			DialogService.error(this, "Ein technischer Fehler ist aufgetreten: " + ex.getMessage());
		}
	}

	private String pruefeEingaben(String titel, String frageText, String[] antworten, boolean[] richtig) {
		// Verwende die neue ValidationHelper-Klasse
		if (!ValidationHelper.validateNotEmpty(titel, "Titel")) {
			return MSG_TITEL_LEER;
		}
		if (!ValidationHelper.validateNotEmpty(frageText, "Fragetext")) {
			return MSG_FRAGETEXT_LEER;
		}
		if (!ValidationHelper.validateNoEmptyCorrect(richtig, antworten)) {
			return MSG_RICHTIG_OHNE_TEXT;
		}
		if (!ValidationHelper.validateAtLeastOneCorrect(richtig, antworten)) {
			return MSG_KEINE_RICHTIGE;
		}
		
		// Prüfe, ob mindestens eine Antwort vorhanden ist
		boolean hatMindestensEineAntwort = false;
		for (String antwort : antworten) {
			if (ValidationHelper.trimOrEmpty(antwort).length() > 0) {
				hatMindestensEineAntwort = true;
				break;
			}
		}
		if (!hatMindestensEineAntwort) {
			return MSG_ANTWORTEN_LEER;
		}
		
		return null;
	}

	@Override
	protected void configureButtons() {
		button1.setText("Frage löschen");
		button2.setText("Frage speichern");
		button3.setText("Neue Frage");
	}
	
	/**
	 * Aktualisiert den Button-Text basierend auf dem ausgewählten Thema.
	 */
	public void updateButtonText() {
		if (mainPanel != null) {
			Thema ausgewaehltesThema = mainPanel.getPanelRechts().getSelectedThemaFromCombo();
			Frage ausgewaehlteFrage = mainPanel.getPanelRechts().getSelectedFrage();
			
			if (ausgewaehlteFrage != null && ausgewaehlteFrage.getId() > 0) {
				button2.setText("Frage aktualisieren");
			} else {
				button2.setText("Frage speichern");
			}
		}
	}
}
