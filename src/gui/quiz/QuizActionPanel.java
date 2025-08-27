package gui.quiz;

import java.util.ArrayList;
import java.util.List;

import gui.AbstractActionPanel;
import gui.util.DialogService;
import gui.fragen.QuizfragenRechtesPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

/**
 * Action-Panel für das Quiz-Spiel. Enthält die Buttons für das eigentliche
 * Quiz: Antwort zeigen, Antwort abgeben, nächste Frage.
 */
public class QuizActionPanel extends AbstractActionPanel {

	private static final long serialVersionUID = 1L;
	private QuizMainPanel mainPanel;

	// Quiz-Status
	private boolean antwortZeigt = false;
	private boolean antwortAbgegeben = false;
	private int aktuelleFragenIndex = 0;
	private List<Frage> aktuelleFragen = new ArrayList<>();
	private int punkte = 0;
	private int gesamtFragen = 0;

	// Quiz-Timing
	private long quizStartZeit;
	private long antwortStartZeit;

	// Nachrichten-Konstanten
	private static final String MSG_KEINE_FRAGE_AUSGEWAEHLT = "Bitte wählen Sie eine Frage aus.";
	private static final String MSG_KEINE_ANTWORT_AUSGEWAEHLT = "Bitte wählen Sie mindestens eine Antwort aus.";
	private static final String MSG_ANTWORT_BEREITS_ABGEGEBEN = "Sie haben bereits eine Antwort abgegeben.";
	private static final String MSG_ANTWORT_RICHTIG = "Richtig! +1 Punkt";
	private static final String MSG_ANTWORT_FALSCH = "Falsch! Keine Punkte";
	private static final String MSG_QUIZ_BEENDET = "Quiz beendet! Ihr Ergebnis: %d/%d Punkten";

	public QuizActionPanel() {
		super();
		configureButtons();
		initListeners();
	}

	public void setMainPanel(QuizMainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void initListeners() {
		button1.addActionListener(e -> zeigeAntwort());
		button2.addActionListener(e -> gebeAntwortAb());
		button3.addActionListener(e -> naechsteFrage());
	}

	/**
	 * Zeigt die richtige Antwort an (ohne Punkte zu geben).
	 */
	private void zeigeAntwort() {
		if (mainPanel == null) {
			return;
		}

		Frage ausgewaehlteFrage = getSelectedFrage();
		if (ausgewaehlteFrage == null) {
			DialogService.warn(this, MSG_KEINE_FRAGE_AUSGEWAEHLT);
			return;
		}

		if (antwortZeigt) {
			DialogService.info(this, "Die Antwort wird bereits angezeigt.");
			return;
		}

		// Richtige Antwort(en) markieren
		markiereRichtigeAntworten(ausgewaehlteFrage);
		antwortZeigt = true;

		// Button-Status aktualisieren
		button1.setEnabled(false); // "Antwort Zeigen" deaktivieren
		button2.setEnabled(true); // "Antwort abgeben" aktivieren
		button3.setEnabled(false); // "Nächste Frage" deaktivieren

		DialogService.info(this,
				"Die richtige Antwort wird angezeigt. Sie können jetzt Ihre Antwort abgeben (ohne Punkte).");
	}

	/**
	 * Gibt die ausgewählte Antwort ab und vergibt Punkte.
	 */
	private void gebeAntwortAb() {
		if (mainPanel == null) {
			return;
		}

		if (antwortAbgegeben) {
			DialogService.warn(this, MSG_ANTWORT_BEREITS_ABGEGEBEN);
			return;
		}

		Frage ausgewaehlteFrage = getSelectedFrage();
		if (ausgewaehlteFrage == null) {
			DialogService.warn(this, MSG_KEINE_FRAGE_AUSGEWAEHLT);
			return;
		}

		// Prüfen, ob Antworten ausgewählt wurden
		if (!hatAntwortenAusgewaehlt()) {
			DialogService.warn(this, MSG_KEINE_ANTWORT_AUSGEWAEHLT);
			return;
		}

		// Antwort bewerten
		boolean richtig = istAntwortRichtig(ausgewaehlteFrage);
		if (richtig && !antwortZeigt) {
			// Nur Punkte geben, wenn die Antwort nicht vorher angezeigt wurde
			punkte++;
			DialogService.info(this, MSG_ANTWORT_RICHTIG);
		} else if (richtig && antwortZeigt) {
			DialogService.info(this, "Richtig, aber keine Punkte (Antwort wurde vorher angezeigt).");
		} else {
			DialogService.info(this, MSG_ANTWORT_FALSCH);
		}

		antwortAbgegeben = true;

		// Quiz-Ergebnis speichern
		speichereQuizErgebnis(ausgewaehlteFrage, richtig);

		// Punkte anzeigen
		updatePunkteAnzeige();

		// Button-Status aktualisieren
		button2.setEnabled(false); // "Antwort abgeben" deaktivieren
		button3.setEnabled(true); // "Nächste Frage" aktivieren
	}

	/**
	 * Geht zur nächsten Frage oder beendet das Quiz.
	 */
	private void naechsteFrage() {
		if (mainPanel == null) {
			return;
		}

		// Aktuell ausgewähltes Thema und dessen Fragen laden
		Thema ausgewaehltesThema = mainPanel.getPanelRechts().getSelectedThemaFromCombo();
		mainPanel.getPanelRechts();
		if (ausgewaehltesThema == null || ausgewaehltesThema == QuizfragenRechtesPanel.ALLE_THEMEN) {
			DialogService.warn(this, "Bitte wählen Sie ein konkretes Thema aus.");
			return;
		}

		try {
			aktuelleFragen = mainPanel.getDataProvider().findeFragenFuerThema(ausgewaehltesThema.getId());
			gesamtFragen = aktuelleFragen.size();

			if (aktuelleFragen.isEmpty()) {
				DialogService.warn(this, "Keine Fragen für dieses Thema verfügbar.");
				return;
			}

			aktuelleFragenIndex++;

			if (aktuelleFragenIndex >= aktuelleFragen.size()) {
				// Quiz beendet
				quizBeenden();
			} else {
				// Nächste Frage anzeigen
				naechsteFrageAnzeigen();
			}

		} catch (Exception e) {
			DialogService.error(this, "Fehler beim Laden der Fragen: " + e.getMessage());
		}
	}

	/**
	 * Wird aufgerufen, wenn ein Thema ausgewählt wird. Aktiviert die
	 * Quiz-Funktionalität.
	 */
	public void themaAusgewaehlt() {
		if (mainPanel != null) {
			// Alle Quiz-Buttons aktivieren
			button1.setEnabled(true); // "Antwort Zeigen" (Hilfe)
			button2.setEnabled(true); // "Antwort abgeben" (Hauptfunktion)
			button3.setEnabled(false); // "Nächste Frage" (erst nach Antwort abgeben)
			messageLabel.setText(
					"Wählen Sie eine Frage aus und beginnen Sie das Quiz. Wählen Sie Ihre Antwort(en) aus und klicken Sie 'Antwort abgeben'.");
		}
	}

	/**
	 * Wird aufgerufen, wenn eine Frage ausgewählt wird. Startet das Quiz für die
	 * ausgewählte Frage.
	 */
	public void starteQuiz(Frage frage) {
		if (mainPanel == null || frage == null) {
			return;
		}

		// Quiz-Status zurücksetzen
		antwortZeigt = false;
		antwortAbgegeben = false;

		// Timing starten
		antwortStartZeit = System.currentTimeMillis();

		// Button-Status aktualisieren
		button1.setEnabled(true); // "Antwort Zeigen" aktivieren (Hilfe)
		button2.setEnabled(true); // "Antwort abgeben" aktivieren (Hauptfunktion)
		button3.setEnabled(false); // "Nächste Frage" deaktivieren

		messageLabel.setText(
				"Quiz gestartet! Wählen Sie Ihre Antwort(en) aus und klicken Sie 'Antwort abgeben' für Punkte oder 'Antwort zeigen' für Hilfe.");
	}

	/**
	 * Zeigt die aktuelle Frage an.
	 */
	private void zeigeAktuelleFrage() {
		if (aktuelleFragenIndex < aktuelleFragen.size()) {
			Frage frage = aktuelleFragen.get(aktuelleFragenIndex);
			mainPanel.getPanelLinks().zeigeFrage(frage);

			// Frage in der rechten Liste auswählen
			mainPanel.getPanelRechts().selectFrageById(frage.getId());
		}
	}

	/**
	 * Markiert die richtigen Antworten in der GUI.
	 */
	private void markiereRichtigeAntworten(Frage frage) {
		if (mainPanel == null || frage == null) {
			return;
		}
		mainPanel.getPanelLinks().zeigeKorrekteAntworten(frage);
	}

	/**
	 * Prüft, ob der Benutzer Antworten ausgewählt hat.
	 */
	private boolean hatAntwortenAusgewaehlt() {
		if (mainPanel == null) {
			return false;
		}
		boolean[] auswahl = mainPanel.getPanelLinks().getRichtig();
		for (boolean b : auswahl) {
			if (b) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft, ob die ausgewählte Antwort richtig ist.
	 */
	private boolean istAntwortRichtig(Frage frage) {
		if (mainPanel == null) {
			return false;
		}
		boolean[] auswahl = mainPanel.getPanelLinks().getRichtig();
		// Vollständiger Vergleich: Jede richtige muss gewählt sein, keine falsche darf
		// gewählt sein
		int n = Math.min(auswahl.length, frage.getAntworten().size());
		for (int i = 0; i < n; i++) {
			if (auswahl[i] != frage.getAntworten().get(i).istRichtig()) {
				return false;
			}
		}
		// Falls Frage weniger als 4 Antworten hat: sicherstellen, dass restliche nicht
		// gewählt sind
		for (int i = n; i < auswahl.length; i++) {
			if (auswahl[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Aktualisiert die Punkteanzeige.
	 */
	private void updatePunkteAnzeige() {
		String message = String.format("Punkte: %d/%d", punkte, aktuelleFragenIndex + 1);
		messageLabel.setText(message);
	}

	/**
	 * Beendet das Quiz und zeigt das Endergebnis.
	 */
	private void quizBeenden() {
		String ergebnis = String.format(MSG_QUIZ_BEENDET, punkte, gesamtFragen);
		DialogService.info(this, ergebnis);

		// Quiz-Status zurücksetzen
		resetQuizStatus();

		// Alle Buttons deaktivieren
		button1.setEnabled(false);
		button2.setEnabled(false);
		button3.setEnabled(false);

		messageLabel.setText("Quiz beendet. Wählen Sie ein Thema aus, um ein neues Quiz zu starten.");
	}

	/**
	 * Zeigt die nächste Frage an.
	 */
	private void naechsteFrageAnzeigen() {
		zeigeAktuelleFrage();

		// Quiz-Status für neue Frage zurücksetzen
		antwortZeigt = false;
		antwortAbgegeben = false;

		// Button-Status aktualisieren
		button1.setEnabled(true); // "Antwort Zeigen" aktivieren (Hilfe)
		button2.setEnabled(true); // "Antwort abgeben" aktivieren (Hauptfunktion)
		button3.setEnabled(false); // "Nächste Frage" deaktivieren

		// Punkte anzeigen
		updatePunkteAnzeige();

		messageLabel.setText(
				"Neue Frage geladen. Wählen Sie Ihre Antwort(en) aus und klicken Sie 'Antwort abgeben' für Punkte oder 'Antwort zeigen' für Hilfe.");
	}

	/**
	 * Speichert das Quiz-Ergebnis in der Datenbank.
	 */
	private void speichereQuizErgebnis(Frage frage, boolean richtig) {
		if (mainPanel == null || frage == null) {
			return;
		}

		try {
			// Aktuell ausgewähltes Thema ermitteln
			quiz.data.model.Thema ausgewaehltesThema = mainPanel.getPanelRechts().getSelectedThemaFromCombo();
			mainPanel.getPanelRechts();
			if (ausgewaehltesThema == null || ausgewaehltesThema == QuizfragenRechtesPanel.ALLE_THEMEN) {
				return;
			}

			// Antwortzeit berechnen
			long antwortZeit = System.currentTimeMillis() - antwortStartZeit;
			int antwortZeitSekunden = (int) (antwortZeit / 1000);

			// Quiz-Ergebnis über den Service speichern
			business.QuizApplication.getInstance().getQuizStatistikService().saveQuizErgebnis(
					ausgewaehltesThema.getId(), frage.getId(), richtig, antwortZeigt, antwortZeitSekunden,
					richtig && !antwortZeigt ? 1 : 0);

		} catch (Exception e) {
			System.err.println("Fehler beim Speichern des Quiz-Ergebnisses: " + e.getMessage());
		}
	}

	/**
	 * Setzt den Quiz-Status zurück.
	 */
	private void resetQuizStatus() {
		antwortZeigt = false;
		antwortAbgegeben = false;
		aktuelleFragenIndex = 0;
		punkte = 0;
		gesamtFragen = 0;

		// Button-Status zurücksetzen
		button1.setEnabled(false); // "Antwort Zeigen"
		button2.setEnabled(false); // "Antwort abgeben"
		button3.setEnabled(false); // "Nächste Frage"

		messageLabel.setText("Wählen Sie ein Thema aus, um ein Quiz zu beginnen.");
	}

	/**
	 * Holt die aktuell ausgewählte Frage.
	 */
	private Frage getSelectedFrage() {
		return mainPanel.getPanelRechts().getSelectedFrage();
	}

	@Override
	protected void configureButtons() {
		button1.setText("Antwort Zeigen");
		button2.setText("Antwort abgeben");
		button3.setText("Nächste Frage");

		// Alle Buttons initial deaktiviert
		button1.setEnabled(false);
		button2.setEnabled(false);
		button3.setEnabled(false);

		messageLabel.setText("Wählen Sie ein Thema aus, um ein Quiz zu beginnen.");
	}
}
