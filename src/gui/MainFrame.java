
package gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

import business.QuizApplication;
import quiz.logic.QuizDataProviderFactory;
import quiz.logic.interfaces.QuizDataProvider;
import gui.themen.ThemenMainPanel;
import gui.fragen.FragenMainPanel;
import gui.quiz.QuizMainPanel;
import gui.statistik.StatistikMainPanel;
import gui.mvc.ControllerFactory;
import config.PropertiesLoader;

/**
 * Hauptfenster der Quiz-Anwendung.
 * 
 * <p>Diese Klasse repräsentiert das Hauptfenster der Quiz-Anwendung und dient
 * als zentraler Einstiegspunkt für die Benutzeroberfläche. Sie implementiert
 * das Tabbed-Interface mit verschiedenen Funktionsbereichen.</p>
 * 
 * <p>Hauptfunktionen:</p>
 * <ul>
 *   <li>Zentrale Fenster-Verwaltung mit konfigurierbaren Dimensionen</li>
 *   <li>Tabbed-Interface für verschiedene Funktionsbereiche</li>
 *   <li>Responsive Design mit automatischer Größenanpassung</li>
 *   <li>MVC-Controller-Integration</li>
 *   <li>Performance-optimierte Resize-Behandlung</li>
 * </ul>
 * 
 * <p>Verfügbare Tabs:</p>
 * <ul>
 *   <li>Quizthemen - Verwaltung von Quiz-Themen</li>
 *   <li>Quizfragen - Verwaltung von Quiz-Fragen</li>
 *   <li>Quiz - Durchführung von Quiz-Spielen</li>
 *   <li>Statistiken - Anzeige von Quiz-Statistiken</li>
 * </ul>
 * 
 * <p>Konfiguration:</p>
 * <ul>
 *   <li>Fenstergröße aus application.properties</li>
 *   <li>UI-Skalierung für verschiedene Bildschirmauflösungen</li>
 *   <li>Moderne FlatLaf-Benutzeroberfläche</li>
 * </p>
 * 
 * @author TvT
 * @version 1.0
 * @since 1.0
 * @see QuizTabPane
 * @see ThemenMainPanel
 * @see FragenMainPanel
 * @see QuizMainPanel
 * @see StatistikMainPanel
 * @see ControllerFactory
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final QuizDataProvider dataProvider;
	private final QuizApplication quizApp;

	// Performance-Optimierung für Resize-Events
	private final ScheduledExecutorService resizeScheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> resizeTask;
	private int lastProcessedWidth = -1;
	private static final int RESIZE_DEBOUNCE_DELAY = 100;

	/**
	 * Erstellt ein neues Hauptfenster der Quiz-Anwendung.
	 * 
	 * <p>Initialisiert alle Komponenten, lädt die Konfiguration und startet
	 * die Anwendung. Das Fenster wird zentriert angezeigt und mit der
	 * konfigurierten Größe versehen.</p>
	 */
	public MainFrame() {
		super();
		setTitle("Quizzzzz");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Fenstergröße aus application.properties laden
		int windowWidth = PropertiesLoader.getWindowWidth();
		int windowHeight = PropertiesLoader.getWindowHeight();
		setSize(windowWidth, windowHeight);
		
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(700, 500));

		quizApp = QuizApplication.getInstance();
		quizApp.start();
		dataProvider = QuizDataProviderFactory.getInstance();

		init();
		setupResizeListener();
	}

	/**
	 * Richtet den Resize-Listener für das Fenster ein.
	 * 
	 * <p>Implementiert eine debounced Resize-Behandlung, um Performance-Probleme
	 * bei häufigen Größenänderungen zu vermeiden. Die tatsächliche Verarbeitung
	 * erfolgt erst nach einer kurzen Verzögerung.</p>
	 */
	private void setupResizeListener() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int currentWidth = getWidth();

				if (currentWidth == lastProcessedWidth) {
					return;
				}

				if (resizeTask != null && !resizeTask.isDone()) {
					resizeTask.cancel(false);
				}

				resizeTask = resizeScheduler.schedule(() -> {
					if (getWidth() == currentWidth) {
						lastProcessedWidth = currentWidth;

						SwingUtilities.invokeLater(() -> {
							System.out.println("MainFrame: Verarbeite Resize auf " + currentWidth + "px");

							// Panel-Gewichtungen anpassen
							if (getContentPane().getComponent(0) instanceof QuizTabPane) {
								QuizTabPane tabPane = (QuizTabPane) getContentPane().getComponent(0);
								tabPane.adjustForWindowSize(currentWidth);

								// Panel-Gewichtungen in den Tabs anpassen
								for (int i = 0; i < tabPane.getTabCount(); i++) {
									if (tabPane.getComponentAt(i) instanceof AbstractMainPanel) {
										AbstractMainPanel panel = (AbstractMainPanel) tabPane.getComponentAt(i);
										panel.adjustPanelWeightsForWindowSize(currentWidth);
									}
								}
							}
						});
					}
				}, RESIZE_DEBOUNCE_DELAY, TimeUnit.MILLISECONDS);
			}
		});
	}

	/**
	 * Initialisiert die Hauptkomponenten des Fensters.
	 * 
	 * <p>Erstellt alle Panels, fügt sie als Tabs hinzu und verbindet sie
	 * mit den entsprechenden MVC-Controllern.</p>
	 */
	void init() {
		QuizTabPane tabPane = new QuizTabPane();

		// Panels erstellen
		ThemenMainPanel themenMainPanel = new ThemenMainPanel(dataProvider);
		FragenMainPanel fragenMainPanel = new FragenMainPanel(dataProvider);
		QuizMainPanel quizMainPanel = new QuizMainPanel(dataProvider);
		StatistikMainPanel statistikMainPanel = new StatistikMainPanel(dataProvider);

		// Tabs hinzufügen
		tabPane.addTab("Quizthemen", themenMainPanel);
		tabPane.addTab("Quizfragen", fragenMainPanel);
		tabPane.addTab("Quiz", quizMainPanel);
		tabPane.addTab("Statistiken", statistikMainPanel);
		
		// MVC-Controller mit Views verbinden
		try {
			ControllerFactory controllerFactory = ControllerFactory.getInstance();
			controllerFactory.connectControllersWithViews(
				themenMainPanel, 
				fragenMainPanel, 
				quizMainPanel, 
				statistikMainPanel
			);
			controllerFactory.startAllControllers();
		} catch (Exception e) {
			System.err.println("Fehler beim Verbinden der MVC-Controller: " + e.getMessage());
			e.printStackTrace();
		}

		add(tabPane);
		setVisible(true);
	}

	/**
	 * Hauptmethode der Anwendung.
	 * 
	 * <p>Konfiguriert die UI-Skalierung, setzt das Look-and-Feel und
	 * startet die Anwendung im Event-Dispatch-Thread.</p>
	 * 
	 * @param args Kommandozeilenargumente (werden nicht verwendet)
	 */
	public static void main(String args[]) {

		// UI-Skalierung aus application.properties laden
		double uiScale = PropertiesLoader.getUIScale();
		System.setProperty("sun.java2d.uiScale", String.valueOf(uiScale));
		
		try {
			UIManager.setLookAndFeel(new FlatLightLaf()); // helles Design
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(MainFrame::new);
	}

	/**
	 * Bereinigt Ressourcen beim Schließen des Fensters.
	 * 
	 * <p>Stoppt die Quiz-Anwendung und bereinigt alle Scheduler-Ressourcen
	 * vor dem endgültigen Schließen des Fensters.</p>
	 */
	@Override
	public void dispose() {
		if (quizApp != null) {
			quizApp.stop();
		}

		cleanupResizeScheduler();
		super.dispose();
	}

	/**
	 * Bereinigt den Resize-Scheduler.
	 * 
	 * <p>Beendet alle laufenden Resize-Tasks und fährt den Scheduler
	 * ordnungsgemäß herunter.</p>
	 */
	private void cleanupResizeScheduler() {
		if (resizeTask != null && !resizeTask.isDone()) {
			resizeTask.cancel(false);
		}

		if (!resizeScheduler.isShutdown()) {
			resizeScheduler.shutdown();
			try {
				if (!resizeScheduler.awaitTermination(500, TimeUnit.MILLISECONDS)) {
					resizeScheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				resizeScheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}
