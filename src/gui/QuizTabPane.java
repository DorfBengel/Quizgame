package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class QuizTabPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	public static final Color BG_COLOR = Color.WHITE;
	private static final Font FONT_TAB = new Font("Arial", Font.PLAIN, 16);

	// Responsive Größen
	private static final int MIN_WIDTH = 700;
	private static final int MIN_HEIGHT = 500;
	private static final int PREFERRED_WIDTH = 900;
	private static final int PREFERRED_HEIGHT = 600;

	/**
	 * Constructs a tab pane and set its appearance
	 */
	public QuizTabPane() {
		super(SwingConstants.TOP);
		setFont(FONT_TAB);
		setBackground(BG_COLOR);

		// Responsive Größen setzen
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		// Bessere Tab-Performance
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setOpaque(true);
	}

	/**
	 * Passt die Tab-Größen an die verfügbare Breite an.
	 */
	public void adjustForWindowSize(int windowWidth) {
		if (windowWidth < 800) {
			// Kleine Fenster: Kleinere Schrift
			setFont(new Font("Arial", Font.PLAIN, 14));
		} else if (windowWidth < 1200) {
			// Mittlere Fenster: Standard-Schrift
			setFont(FONT_TAB);
		} else {
			// Große Fenster: Größere Schrift
			setFont(new Font("Arial", Font.PLAIN, 18));
		}
		revalidate();
	}
}
