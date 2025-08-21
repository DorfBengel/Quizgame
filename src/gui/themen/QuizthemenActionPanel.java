
// src/gui/view1/quizthemen/subpanels/QuizthemenActionPanel.java
package gui.themen;

import java.util.List;

import javax.swing.SwingUtilities;

import gui.AbstractActionPanel;
import gui.themen.ThemenMainPanel;
import gui.util.DialogService;
import quiz.data.model.Thema;

public class QuizthemenActionPanel extends AbstractActionPanel {
	private static final long serialVersionUID = 1L;

	// Konstanten für bessere Wartbarkeit
	private static final String MSG_KEIN_THEMA_AUSGEWAEHLT = "Kein Thema ausgewählt.";
	private static final String MSG_THEMA_GELOESCHT = "Thema erfolgreich gelöscht.";
	private static final String MSG_THEMA_GESPEICHERT = "Thema erfolgreich gespeichert.";
	private static final String MSG_THEMA_EXISTIERT = "Ein Thema mit diesem Titel existiert bereits.\nMöchten Sie es überschreiben?";
	private static final String MSG_TITEL_LEER = "Der Titel darf nicht leer sein.";
	private static final String MSG_TITEL_ZU_LANG = "Der Titel darf maximal 100 Zeichen lang sein.";

	private static final int MAX_TITEL_LAENGE = 100;

	private ThemenMainPanel mainPanel;

	public QuizthemenActionPanel() {
		super();
		configureButtons();
		initListeners();
	}

	public void setMainPanel(ThemenMainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private void initListeners() {
		button1.addActionListener(e -> loescheThema());
		button2.addActionListener(e -> speichereThema());
		button3.addActionListener(e -> neuesThema());
	}

	private void loescheThema() {
		if (!isMainPanelValid()) {
			return;
		}

		Thema thema = getSelectedThema();
		if (thema == null) {
			showInfoMessage(MSG_KEIN_THEMA_AUSGEWAEHLT);
			return;
		}

		// Bestätigungsdialog für das Löschen
		boolean confirmed = DialogService.confirm(this,
				"Möchten Sie das Thema '" + thema.getTitel() + "' wirklich löschen?", "Thema löschen bestätigen");
		if (!confirmed) {
			return;
		}

		// SwingWorker für längere Operationen
		new javax.swing.SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				mainPanel.getDataProvider().loescheThema(thema);
				return null;
			}

			@Override
			protected void done() {
				try {
					get(); // Exception werfen falls aufgetreten
					refreshAllPanels();
					showInfoMessage(MSG_THEMA_GELOESCHT);
				} catch (Exception e) {
					showErrorMessage("Fehler beim Löschen des Themas: " + e.getMessage());
				}
			}
		}.execute();
	}

	private void speichereThema() {
		if (!isMainPanelValid()) {
			return;
		}

		String titel = getTitelFromLeftPanel();
		String information = getInfoFromLeftPanel();

		// Validierung der Eingaben
		if (!isTitelValid(titel)) {
			return;
		}

		// Prüfen, ob ein bestehendes Thema bearbeitet wird
		Thema ausgewaehltesThema = getSelectedThema();
		
		if (ausgewaehltesThema != null && ausgewaehltesThema.getId() > 0) {
			// Bestehendes Thema bearbeiten
			ausgewaehltesThema.setTitel(titel);
			ausgewaehltesThema.setInformation(information);
			
			// SwingWorker für längere Operationen
			new javax.swing.SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					mainPanel.getDataProvider().updateThema(ausgewaehltesThema);
					return null;
				}

				@Override
				protected void done() {
					try {
						get(); // Exception werfen falls aufgetreten

						// GUI aktualisieren
						refreshAllPanels();

						// Bearbeitetes Thema in der Liste auswählen
						selectNewlyCreatedThema(titel);

						showInfoMessage("Thema erfolgreich aktualisiert.");
					} catch (Exception e) {
						showErrorMessage("Fehler beim Aktualisieren: " + e.getMessage());
					}
				}
			}.execute();
		} else {
			// Neues Thema erstellen
			// Prüfen, ob Titel bereits existiert
			boolean existiert = isThemaExisting(titel);
			final boolean forceOverwrite = existiert
					? DialogService.confirm(this, MSG_THEMA_EXISTIERT, "Überschreiben bestätigen")
					: false;

			// SwingWorker für längere Operationen
			new javax.swing.SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					mainPanel.getDataProvider().speichereThema(titel, information, forceOverwrite);
					return null;
				}

				@Override
				protected void done() {
					try {
						get(); // Exception werfen falls aufgetreten

						// GUI aktualisieren
						refreshAllPanels();

						// Neues Thema in der Liste auswählen
						selectNewlyCreatedThema(titel);

						showInfoMessage(MSG_THEMA_GESPEICHERT);
					} catch (Exception e) {
						showErrorMessage("Fehler beim Speichern: " + e.getMessage());
					}
				}
			}.execute();
		}
	}

	/**
	 * Wählt das neu erstellte Thema in der Liste aus. Vereinfachte, robuste Lösung
	 * mit SwingUtilities.invokeLater.
	 */
	private void selectNewlyCreatedThema(String titel) {
		if (mainPanel == null) {
			return;
		}

		System.out.println("ActionPanel: Wähle neues Thema aus: " + titel);

		// Alle Themen neu laden und dann das neue auswählen
		mainPanel.ladeAlleThemen();

		// Mit SwingUtilities.invokeLater für bessere Performance
		SwingUtilities.invokeLater(() -> {
			List<quiz.data.model.Thema> themen = mainPanel.getDataProvider().getAlleThemen();
			themen.stream().filter(t -> t.getTitel().equals(titel)).findFirst().ifPresent(thema -> {
				System.out.println(
						"ActionPanel: Neues Thema gefunden: " + thema.getTitel() + " (ID: " + thema.getId() + ")");
				mainPanel.getPanelRechts().selectThemaById(thema.getId());
				mainPanel.getPanelLinks().zeigeThema(thema);
				System.out.println("ActionPanel: Neues Thema wurde ausgewählt und angezeigt");
			});
		});
	}

	private void neuesThema() {
		if (!isMainPanelValid()) {
			return;
		}

		resetLinkesPanel();
		// Lade alle Themen neu, um die Auswahl zurückzusetzen
		mainPanel.ladeAlleThemen();
	}

	private void resetLinkesPanel() {
		if (mainPanel != null && mainPanel.getPanelLinks() != null) {
			mainPanel.getPanelLinks().zeigeThema(null);
		}
	}

	private void syncQuizfragenPanel() {
		if (mainPanel != null) {
			mainPanel.ladeAlleThemen();
		}
	}

	// Hilfsmethoden für bessere Lesbarkeit
	private boolean isMainPanelValid() {
		if (mainPanel == null) {
			showErrorMessage("Hauptpanel ist nicht verfügbar.");
			return false;
		}
		return true;
	}

	private Thema getSelectedThema() {
		return mainPanel.getPanelRechts().getSelectedThema();
	}

	private String getTitelFromLeftPanel() {
		return mainPanel.getPanelLinks().getTitel();
	}

	private String getInfoFromLeftPanel() {
		return mainPanel.getPanelLinks().getInfoThema();
	}

	private boolean isTitelValid(String titel) {
		if (titel == null || titel.trim().isEmpty()) {
			showErrorMessage(MSG_TITEL_LEER);
			return false;
		}

		if (titel.trim().length() > MAX_TITEL_LAENGE) {
			showErrorMessage(MSG_TITEL_ZU_LANG);
			return false;
		}

		return true;
	}

	private boolean isThemaExisting(String titel) {
		return mainPanel.getDataProvider().getAlleThemen().stream()
				.anyMatch(t -> t.getTitel().equalsIgnoreCase(titel.trim()));
	}

	private void refreshAllPanels() {
		mainPanel.ladeAlleThemen();
		resetLinkesPanel();
		syncQuizfragenPanel();
	}

	private void showInfoMessage(String message) {
		DialogService.info(this, message);
	}

	private void showErrorMessage(String message) {
		DialogService.error(this, message);
	}

	@Override
	protected void configureButtons() {
		button1.setText("Thema löschen");
		button2.setText("Thema speichern");
		button3.setText("Neues Thema");
	}
	
	/**
	 * Aktualisiert den Button-Text basierend auf dem ausgewählten Thema.
	 */
	public void updateButtonText() {
		if (mainPanel != null) {
			Thema ausgewaehlteThema = getSelectedThema();
			
			if (ausgewaehlteThema != null && ausgewaehlteThema.getId() > 0) {
				button2.setText("Thema aktualisieren");
			} else {
				button2.setText("Thema speichern");
			}
		}
	}
}
