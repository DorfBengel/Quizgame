package gui.interfaces;

import java.awt.Font;
import java.awt.Insets;

public interface GuiDefaults {
	// Standard-Insets für Panels
	Insets DEFAULT_INSETS = new Insets(6, 8, 6, 8);
	// Schriftarten für Labels und Überschriften
	Font HEADER_FONT = new Font("Serif", Font.BOLD, 20);
	Font LABEL_FONT = new Font("Serif", Font.BOLD, 16);
	// Für Meldungen und Buttons
	Font MESSAGE_FONT = new Font("Serif", Font.PLAIN, 16);
	Font BUTTON_FONT = new Font("Serif", Font.BOLD, 14);
	//
	Insets DEFAULT_BUTTON_INSETS = new Insets(6, 8, 6, 20);

}