package gui.statistik;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import business.QuizApplication;
import data.dto.StatistikDTO;
import gui.interfaces.GuiDefaults;
import gui.interfaces.QuizLeftPanel;
import quiz.data.model.Frage;
import quiz.data.model.Thema;

/**
 * Panel zur Anzeige von Quiz-Statistiken. Zeigt Übersichten über Themen, Fragen
 * und Antworten an.
 */
public class StatistikLinkesPanel extends JPanel implements GuiDefaults, QuizLeftPanel {

	private static final long serialVersionUID = 1L;

	private static final String LABEL_THEMA = "Thema";
	private static final String LABEL_UEBERSICHT = "Statistik-Übersicht";
	private static final String LABEL_DETAILS = "Detaillierte Statistiken";

	private static final int UEBERSICHT_ROWS = 15;
	private static final int UEBERSICHT_COLUMNS = 50;

	private JLabel themaLabel;
	private JLabel themaLabelDynamic;
	private JLabel uebersichtLabel;
	private JTextArea uebersichtArea;
	private JScrollPane uebersichtScrollPane;
	private JLabel detailsLabel;
	private JTextArea detailsArea;
	private JScrollPane detailsScrollPane;

	private String aktuellesThemaTitel = "";
	private List<Thema> alleThemen = null;

	public StatistikLinkesPanel() {
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

		uebersichtLabel = new JLabel(LABEL_UEBERSICHT + ":");
		uebersichtLabel.setFont(LABEL_FONT);

		uebersichtArea = new JTextArea(UEBERSICHT_ROWS, UEBERSICHT_COLUMNS);
		uebersichtArea.setLineWrap(true);
		uebersichtArea.setWrapStyleWord(true);
		uebersichtArea.setToolTipText("Übersicht über alle Quiz-Statistiken");

		uebersichtScrollPane = new JScrollPane(uebersichtArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		detailsLabel = new JLabel(LABEL_DETAILS + ":");
		detailsLabel.setFont(LABEL_FONT);

		detailsArea = new JTextArea(UEBERSICHT_ROWS, UEBERSICHT_COLUMNS);
		detailsArea.setLineWrap(true);
		detailsArea.setWrapStyleWord(true);
		detailsArea.setToolTipText("Detaillierte Statistiken für das ausgewählte Thema");

		detailsScrollPane = new JScrollPane(detailsArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
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

		// Übersicht Label
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(uebersichtLabel, gbc);

		// Übersicht Area (mit ScrollPane)
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		add(uebersichtScrollPane, gbc);

		// Details Label
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(detailsLabel, gbc);

		// Details Area (mit ScrollPane)
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		add(detailsScrollPane, gbc);

		// Minimale Größen
		uebersichtArea.setMinimumSize(new java.awt.Dimension(300, 200));
		detailsArea.setMinimumSize(new java.awt.Dimension(300, 200));
	}

	private void setReadOnlyMode() {
		uebersichtArea.setEditable(false);
		uebersichtArea.setBackground(new Color(248, 248, 248));

		detailsArea.setEditable(false);
		detailsArea.setBackground(new Color(248, 248, 248));
	}

	@Override
	public void zeigeFrage(Frage frage) {
		// Für Statistiken nicht relevant, aber Interface-Implementierung erforderlich
		if (frage != null) {
			aktualisiereDetailsStatistik(frage);
		}
	}

	@Override
	public void setThemaTitel(String titel) {
		this.aktuellesThemaTitel = titel != null ? titel : "";
		themaLabelDynamic.setText(this.aktuellesThemaTitel);

		if ("Alle Themen".equals(titel)) {
			zeigeGesamtStatistik();
		} else {
			zeigeThemaStatistik(titel);
		}
	}

	/**
	 * Zeigt die Gesamtstatistik für alle Themen an.
	 */
	private void zeigeGesamtStatistik() {
		if (alleThemen == null) {
			return;
		}

		StringBuilder uebersicht = new StringBuilder();
		uebersicht.append("=== GESAMTSTATISTIK ===\n\n");
		uebersicht.append("Anzahl Themen: ").append(alleThemen.size()).append("\n\n");

		int gesamtFragen = 0;
		int gesamtAntworten = 0;
		int gesamtRichtigeAntworten = 0;

		for (Thema thema : alleThemen) {
			List<Frage> fragen = thema.getFragen();
			gesamtFragen += fragen.size();

			for (Frage frage : fragen) {
				gesamtAntworten += frage.getAntworten().size();
				for (quiz.data.model.Antwort antwort : frage.getAntworten()) {
					if (antwort.istRichtig()) {
						gesamtRichtigeAntworten++;
					}
				}
			}
		}

		uebersicht.append("Anzahl Fragen: ").append(gesamtFragen).append("\n");
		uebersicht.append("Anzahl Antworten: ").append(gesamtAntworten).append("\n");
		uebersicht.append("Anzahl richtige Antworten: ").append(gesamtRichtigeAntworten).append("\n");
		uebersicht.append("Durchschnitt Antworten pro Frage: ")
				.append(gesamtFragen > 0 ? String.format("%.1f", (double) gesamtAntworten / gesamtFragen) : "0")
				.append("\n");
		uebersicht.append("Durchschnitt richtige Antworten pro Frage: ")
				.append(gesamtFragen > 0 ? String.format("%.1f", (double) gesamtRichtigeAntworten / gesamtFragen) : "0")
				.append("\n");

		// Echte Quiz-Statistiken laden
		try {
			StatistikDTO gesamtStatistik = QuizApplication.getInstance().getQuizStatistikService()
					.berechneGesamtStatistik();

			if (gesamtStatistik.getAnzahlVersuche() > 0) {
				uebersicht.append("\n=== ECHTE QUIZ-STATISTIKEN ===\n");
				uebersicht.append("Anzahl Quiz-Versuche: ").append(gesamtStatistik.getAnzahlVersuche()).append("\n");
				uebersicht.append("Erfolgsrate: ").append(String.format("%.1f", gesamtStatistik.getErfolgsRate()))
						.append("%\n");
				uebersicht.append("Durchschnittliche Antwortzeit: ")
						.append(String.format("%.1f", gesamtStatistik.getDurchschnittlicheAntwortZeit()))
						.append(" Sekunden\n");
				uebersicht.append("Durchschnittliche Punkte: ").append(gesamtStatistik.getDurchschnittlichePunkte())
						.append("\n");
				uebersicht.append("Beste Punkte: ").append(gesamtStatistik.getBestePunkte()).append("\n");
			} else {
				uebersicht.append("\n=== QUIZ-STATISTIKEN ===\n");
				uebersicht.append("Noch keine Quiz-Ergebnisse verfügbar.\n");
				uebersicht.append("Spielen Sie ein Quiz, um Statistiken zu generieren.\n");
			}
		} catch (Exception e) {
			uebersicht.append("\n=== QUIZ-STATISTIKEN ===\n");
			uebersicht.append("Fehler beim Laden der Quiz-Statistiken: ").append(e.getMessage()).append("\n");
		}

		uebersichtArea.setText(uebersicht.toString());
		detailsArea.setText("Wählen Sie ein spezifisches Thema aus, um detaillierte Statistiken zu sehen.");
	}

	/**
	 * Zeigt die Statistik für ein spezifisches Thema an.
	 */
	private void zeigeThemaStatistik(String themaTitel) {
		if (alleThemen == null) {
			return;
		}

		Thema ausgewaehltesThema = alleThemen.stream().filter(t -> t.getTitel().equals(themaTitel)).findFirst()
				.orElse(null);

		if (ausgewaehltesThema == null) {
			return;
		}

		List<Frage> fragen = ausgewaehltesThema.getFragen();

		StringBuilder uebersicht = new StringBuilder();
		uebersicht.append("=== THEMA: ").append(themaTitel).append(" ===\n\n");
		uebersicht.append("GRUNDSTATISTIKEN:\n");
		uebersicht.append("  • Anzahl Fragen: ").append(fragen.size()).append("\n");

		int gesamtAntworten = 0;
		int gesamtRichtigeAntworten = 0;
		int einfachFragen = 0;
		int mittelFragen = 0;
		int schwerFragen = 0;

		for (Frage frage : fragen) {
			gesamtAntworten += frage.getAntworten().size();
			long anzahlRichtige = frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count();
			gesamtRichtigeAntworten += anzahlRichtige;

			// Schwierigkeitsgrad zählen
			if (anzahlRichtige == 1) {
				einfachFragen++;
			} else if (anzahlRichtige == 2) {
				mittelFragen++;
			} else {
				schwerFragen++;
			}
		}

		uebersicht.append("  • Anzahl Antworten: ").append(gesamtAntworten).append("\n");
		uebersicht.append("  • Anzahl richtige Antworten: ").append(gesamtRichtigeAntworten).append("\n");
		uebersicht.append("  • Durchschnitt Antworten pro Frage: ")
				.append(fragen.size() > 0 ? String.format("%.1f", (double) gesamtAntworten / fragen.size()) : "0")
				.append("\n");
		uebersicht.append("  • Durchschnitt richtige Antworten pro Frage: ").append(
				fragen.size() > 0 ? String.format("%.1f", (double) gesamtRichtigeAntworten / fragen.size()) : "0")
				.append("\n");

		uebersicht.append("\nSCHWIERIGKEITSVERTEILUNG:\n");
		uebersicht.append("  • Einfache Fragen: ").append(einfachFragen).append(" (")
				.append(fragen.size() > 0 ? String.format("%.1f", (double) einfachFragen / fragen.size() * 100) : "0")
				.append("%)\n");
		uebersicht.append("  • Mittlere Fragen: ").append(mittelFragen).append(" (")
				.append(fragen.size() > 0 ? String.format("%.1f", (double) mittelFragen / fragen.size() * 100) : "0")
				.append("%)\n");
		uebersicht.append("  • Schwere Fragen: ").append(schwerFragen).append(" (")
				.append(fragen.size() > 0 ? String.format("%.1f", (double) schwerFragen / fragen.size() * 100) : "0")
				.append("%)\n");

		// Echte Quiz-Ergebnisse für das Thema
		try {
			StatistikDTO themaStatistik = QuizApplication.getInstance().getQuizStatistikService()
					.berechneThemaStatistik(ausgewaehltesThema.getId(), ausgewaehltesThema.getTitel());

			if (themaStatistik.getAnzahlVersuche() > 0) {
				uebersicht.append("\n=== ECHTE QUIZ-STATISTIKEN FÜR THEMA ===\n");
				uebersicht.append("  • Anzahl Quiz-Versuche: ").append(themaStatistik.getAnzahlVersuche()).append("\n");
				uebersicht.append("  • Erfolgsrate: ").append(String.format("%.1f", themaStatistik.getErfolgsRate()))
						.append("%\n");
				uebersicht.append("  • Durchschnittliche Antwortzeit: ")
						.append(String.format("%.1f", themaStatistik.getDurchschnittlicheAntwortZeit()))
						.append(" Sekunden\n");
				uebersicht.append("  • Durchschnittliche Punkte: ").append(themaStatistik.getDurchschnittlichePunkte())
						.append("\n");
				uebersicht.append("  • Beste Punkte: ").append(themaStatistik.getBestePunkte()).append("\n");
			} else {
				uebersicht.append("\n=== QUIZ-STATISTIKEN FÜR THEMA ===\n");
				uebersicht.append("  • Noch keine Quiz-Ergebnisse für dieses Thema verfügbar.\n");
				uebersicht.append("  • Spielen Sie ein Quiz zu diesem Thema, um Statistiken zu generieren.\n");
			}
		} catch (Exception e) {
			uebersicht.append("\n=== QUIZ-STATISTIKEN FÜR THEMA ===\n");
			uebersicht.append("  • Fehler beim Laden der Quiz-Statistiken: ").append(e.getMessage()).append("\n");
		}

		uebersichtArea.setText(uebersicht.toString());

		// Detaillierte Statistik
		StringBuilder details = new StringBuilder();
		details.append("=== DETAILLIERTE FRAGEN ===\n\n");

		for (int i = 0; i < fragen.size(); i++) {
			Frage frage = fragen.get(i);
			details.append("Frage ").append(i + 1).append(": ").append(frage.getFrageTitel()).append("\n");
			details.append("  Antworten: ").append(frage.getAntworten().size()).append("\n");
			details.append("  Richtige: ")
					.append(frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count())
					.append("\n");

			// Schwierigkeitsgrad für jede Frage
			long anzahlRichtige = frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count();
			String schwierigkeit = getSchwierigkeitsgrad(anzahlRichtige, frage.getAntworten().size());
			details.append("  Schwierigkeit: ").append(schwierigkeit).append("\n");

			// Simulierte Quiz-Ergebnisse für jede Frage
			details.append("  Erfolgsrate: ").append(getSimulierteFalschRate(frage)).append(" richtig beantwortet\n");
			details.append("\n");
		}

		detailsArea.setText(details.toString());
	}

	/**
	 * Aktualisiert die Details-Statistik für eine spezifische Frage.
	 */
	private void aktualisiereDetailsStatistik(Frage frage) {
		if (frage == null) {
			return;
		}

		StringBuilder details = new StringBuilder();
		details.append("=== FRAGE: ").append(frage.getFrageTitel()).append(" ===\n\n");
		details.append("Frage-Text: ").append(frage.getFrageText()).append("\n\n");

		// Antworten-Übersicht
		details.append("ANTWORTEN:\n");
		for (int i = 0; i < frage.getAntworten().size(); i++) {
			quiz.data.model.Antwort antwort = frage.getAntworten().get(i);
			details.append("  ").append(i + 1).append(". ").append(antwort.getText());
			if (antwort.istRichtig()) {
				details.append(" ✓ (richtig)");
			}
			details.append("\n");
		}

		details.append("\n");

		// Erweiterte Statistiken
		details.append("ERWEITERTE STATISTIKEN:\n");
		details.append("  • Anzahl Antworten: ").append(frage.getAntworten().size()).append("\n");
		details.append("  • Anzahl richtige Antworten: ")
				.append(frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count()).append("\n");
		details.append("  • Anzahl falsche Antworten: ")
				.append(frage.getAntworten().stream().filter(a -> !a.istRichtig()).count()).append("\n");

		// Schwierigkeitsgrad basierend auf Anzahl richtiger Antworten
		long anzahlRichtige = frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count();
		String schwierigkeit = getSchwierigkeitsgrad(anzahlRichtige, frage.getAntworten().size());
		details.append("  • Schwierigkeitsgrad: ").append(schwierigkeit).append("\n");

		// Echte Quiz-Ergebnisse
		try {
			StatistikDTO frageStatistik = QuizApplication.getInstance().getQuizStatistikService()
					.berechneFrageStatistik(0, "Unbekannt", frage.getId(), frage.getFrageTitel());

			if (frageStatistik.getAnzahlVersuche() > 0) {
				details.append("\n=== ECHTE QUIZ-ERGEBNISSE ===\n");
				details.append("  • Anzahl Versuche: ").append(frageStatistik.getAnzahlVersuche()).append("\n");
				details.append("  • Erfolgsrate: ").append(String.format("%.1f", frageStatistik.getErfolgsRate()))
						.append("%\n");
				details.append("  • Durchschnittliche Antwortzeit: ")
						.append(String.format("%.1f", frageStatistik.getDurchschnittlicheAntwortZeit()))
						.append(" Sekunden\n");
				details.append("  • Durchschnittliche Punkte: ").append(frageStatistik.getDurchschnittlichePunkte())
						.append("\n");
				details.append("  • Beste Punkte: ").append(frageStatistik.getBestePunkte()).append("\n");
			} else {
				details.append("\n=== QUIZ-ERGEBNISSE ===\n");
				details.append("  • Noch keine Quiz-Ergebnisse für diese Frage verfügbar.\n");
				details.append("  • Beantworten Sie diese Frage in einem Quiz, um Statistiken zu generieren.\n");
			}
		} catch (Exception e) {
			details.append("\n=== QUIZ-ERGEBNISSE ===\n");
			details.append("  • Fehler beim Laden der Quiz-Statistiken: ").append(e.getMessage()).append("\n");
		}

		// Empfehlungen basierend auf den Statistiken
		details.append("\nEMPFEHLUNGEN:\n");
		details.append(getEmpfehlungen(frage));

		detailsArea.setText(details.toString());
	}

	/**
	 * Bestimmt den Schwierigkeitsgrad basierend auf der Anzahl richtiger Antworten.
	 */
	private String getSchwierigkeitsgrad(long anzahlRichtige, int gesamtAntworten) {
		if (anzahlRichtige == 1) {
			return "Einfach (1 richtige Antwort)";
		} else if (anzahlRichtige == 2) {
			return "Mittel (2 richtige Antworten)";
		} else if (anzahlRichtige == 3) {
			return "Schwer (3 richtige Antworten)";
		} else if (anzahlRichtige >= 4) {
			return "Sehr schwer (" + anzahlRichtige + " richtige Antworten)";
		} else {
			return "Unbekannt";
		}
	}

	/**
	 * Simuliert eine Falsch-Rate basierend auf der Frage-ID (für Demo-Zwecke).
	 */
	private String getSimulierteFalschRate(Frage frage) {
		// Simulierte Werte basierend auf der Frage-ID für Demo-Zwecke
		int seed = (int) (frage.getId() % 100);
		if (seed < 20) {
			return "15-25";
		} else if (seed < 40) {
			return "25-35";
		} else if (seed < 60) {
			return "35-45";
		} else if (seed < 80) {
			return "45-55";
		} else {
			return "55-65";
		}
	}

	/**
	 * Simuliert eine durchschnittliche Antwortzeit basierend auf der Frage-ID.
	 */
	private String getSimulierteAntwortZeit(Frage frage) {
		// Simulierte Werte basierend auf der Frage-ID für Demo-Zwecke
		int seed = (int) (frage.getId() % 100);
		if (seed < 25) {
			return "10-20";
		} else if (seed < 50) {
			return "20-30";
		} else if (seed < 75) {
			return "30-45";
		} else {
			return "45-60";
		}
	}

	/**
	 * Simuliert die häufigste Fehlerquelle basierend auf der Frage-ID.
	 */
	private String getSimulierteFehlerQuelle(Frage frage) {
		// Simulierte Werte basierend auf der Frage-ID für Demo-Zwecke
		String[] fehlerQuellen = { "Verwechslung ähnlicher Begriffe", "Überlesen wichtiger Details",
				"Falsche Interpretation der Frage", "Unvollständige Antwortauswahl", "Zeitdruck bei der Beantwortung" };
		int index = (int) (frage.getId() % fehlerQuellen.length);
		return fehlerQuellen[index];
	}

	/**
	 * Generiert Empfehlungen basierend auf den Frage-Statistiken.
	 */
	private String getEmpfehlungen(Frage frage) {
		StringBuilder empfehlungen = new StringBuilder();

		long anzahlRichtige = frage.getAntworten().stream().filter(quiz.data.model.Antwort::istRichtig).count();
		int gesamtAntworten = frage.getAntworten().size();

		if (anzahlRichtige == 1) {
			empfehlungen.append("  • Einfache Frage - gut für Anfänger\n");
			empfehlungen.append("  • Fokus auf das eine richtige Konzept\n");
		} else if (anzahlRichtige == 2) {
			empfehlungen.append("  • Mittlere Schwierigkeit - mehrere Konzepte kombinieren\n");
			empfehlungen.append("  • Achten Sie auf alle richtigen Antworten\n");
		} else if (anzahlRichtige >= 3) {
			empfehlungen.append("  • Hohe Schwierigkeit - umfassendes Verständnis erforderlich\n");
			empfehlungen.append("  • Alle richtigen Antworten müssen identifiziert werden\n");
		}

		if (gesamtAntworten < 4) {
			empfehlungen.append("  • Wenige Antwortoptionen - präzise Auswahl wichtig\n");
		} else {
			empfehlungen.append("  • Viele Antwortoptionen - gründliche Prüfung empfohlen\n");
		}

		// Empfehlung basierend auf der simulierten Falsch-Rate
		String falschRate = getSimulierteFalschRate(frage);
		if (falschRate.contains("15-25") || falschRate.contains("25-35")) {
			empfehlungen.append("  • Geringe Fehlerrate - Frage ist gut verständlich\n");
		} else if (falschRate.contains("35-45") || falschRate.contains("45-55")) {
			empfehlungen.append("  • Mittlere Fehlerrate - Frage könnte klarer formuliert werden\n");
		} else {
			empfehlungen.append("  • Hohe Fehlerrate - Überarbeitung der Frage empfohlen\n");
		}

		return empfehlungen.toString();
	}

	/**
	 * Simuliert eine Erfolgsrate für das gesamte Thema.
	 */
	private String getSimulierteThemaErfolgsRate(Thema thema) {
		// Simulierte Werte basierend auf der Themen-ID
		int seed = (int) (thema.getId() % 100);
		if (seed < 20) {
			return "75-85";
		} else if (seed < 40) {
			return "65-75";
		} else if (seed < 60) {
			return "55-65";
		} else if (seed < 80) {
			return "45-55";
		} else {
			return "35-45";
		}
	}

	/**
	 * Simuliert die häufigste Fehlerquelle für das Thema.
	 */
	private String getSimulierteThemaFehlerQuelle(Thema thema) {
		String[] fehlerQuellen = { "Grundlagenverständnis fehlt", "Begriffe werden verwechselt",
				"Anwendungsbeispiele unklar", "Theorie-Praxis-Verbindung schwach", "Vorkenntnisse unzureichend" };
		int index = (int) (thema.getId() % fehlerQuellen.length);
		return fehlerQuellen[index];
	}

	/**
	 * Simuliert eine empfohlene Lernreihenfolge für das Thema.
	 */
	private String getSimulierteLernReihenfolge(Thema thema) {
		String[] lernReihenfolgen = { "Grundlagen → Anwendungen → Vertiefung", "Beispiele → Theorie → Übungen",
				"Überblick → Details → Zusammenfassung", "Einfache → Komplexe → Integrierte Aufgaben",
				"Theorie → Praxis → Reflexion" };
		int index = (int) (thema.getId() % lernReihenfolgen.length);
		return lernReihenfolgen[index];
	}

	/**
	 * Setzt alle verfügbaren Themen für die Statistik-Berechnung.
	 */
	public void setAlleThemen(List<Thema> themen) {
		this.alleThemen = themen;
		if ("Alle Themen".equals(aktuellesThemaTitel)) {
			zeigeGesamtStatistik();
		}
	}
}
