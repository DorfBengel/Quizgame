
package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import gui.interfaces.GuiDefaults;

public abstract class AbstractMainPanel extends JPanel implements GuiDefaults {

	private static final long serialVersionUID = 1L;

	protected Component panelLinks;
	protected Component panelRechts;
	protected Component panelUnten;

	private double weightxLinks = 0.7; // Standard: 70% der Breite für das linke Panel
	private double weightxRechts = 0.3; // Standard: 30% der Breite für das rechte Panel

	// Minimale Panel-Größen für bessere Responsivität
	private static final int MIN_PANEL_WIDTH = 200;
	private static final int MIN_PANEL_HEIGHT = 150;
	private static final int PREFERRED_PANEL_WIDTH = 300;
	private static final int PREFERRED_PANEL_HEIGHT = 200;

	public AbstractMainPanel(Component panelLinks, Component panelRechts, Component panelUnten) {
		super();
		this.panelLinks = panelLinks;
		this.panelRechts = panelRechts;
		this.panelUnten = panelUnten;
		setLayout(new GridBagLayout());

		// Minimale Größen für die Panels setzen
		setupPanelConstraints();
		initLayout();
	}

	/**
	 * Setzt die minimalen und bevorzugten Größen für alle Panels.
	 */
	private void setupPanelConstraints() {
		if (panelLinks instanceof JPanel) {
			((JPanel) panelLinks).setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
			((JPanel) panelLinks).setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH, PREFERRED_PANEL_HEIGHT));
		}
		if (panelRechts instanceof JPanel) {
			((JPanel) panelRechts).setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
			((JPanel) panelRechts).setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH, MIN_PANEL_HEIGHT));
		}
		if (panelUnten instanceof JPanel) {
			// Action-Panels: Höhe für Buttons + Abstände
			((JPanel) panelUnten).setMinimumSize(new Dimension(MIN_PANEL_WIDTH, 60));
			((JPanel) panelUnten).setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH, 70));
		}
	}

	protected void initLayout() {
		GridBagConstraints gbc = new GridBagConstraints();

		// Linkes Panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(8, 8, 4, 4); // Bessere Abstände
		gbc.weightx = weightxLinks;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		add(panelLinks, gbc);

		// Rechtes Panel
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(8, 4, 4, 8); // Bessere Abstände
		gbc.weightx = weightxRechts;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		add(panelRechts, gbc);

		// Unteres Panel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 8, 8, 8); // Reduzierte obere Abstände
		gbc.weightx = 1.0;
		gbc.weighty = 0.0; // Kein vertikales Gewicht für Action-Panels
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		add(panelUnten, gbc);
	}

	/**
	 * Setzt das Verhältnis der Breite von linkem und rechtem Panel (z.B. 0.6, 0.4)
	 */
	public void setPanelWeights(double weightLinks, double weightRechts) {
		this.weightxLinks = weightLinks;
		this.weightxRechts = weightRechts;
		initLayout();
		revalidate();
		repaint();
	}

	/**
	 * Passt die Panel-Gewichtungen automatisch an die Fensterbreite an.
	 */
	public void adjustPanelWeightsForWindowSize(int windowWidth) {
		if (windowWidth < 800) {
			// Kleine Fenster: Mehr Platz für das rechte Panel
			setPanelWeights(0.5, 0.5);
		} else if (windowWidth < 1200) {
			// Mittlere Fenster: Standard-Verhältnis
			setPanelWeights(0.7, 0.3);
		} else {
			// Große Fenster: Mehr Platz für das linke Panel
			setPanelWeights(0.8, 0.2);
		}
	}
}
