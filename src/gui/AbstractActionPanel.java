
// src/gui/AbstractActionPanel.java
package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.interfaces.GuiDefaults;

public abstract class AbstractActionPanel extends JPanel implements GuiDefaults {
	private static final long serialVersionUID = 1L;
	protected JLabel messageLabel;
	protected JButton button1;
	protected JButton button2;
	protected JButton button3;

	public AbstractActionPanel() {
		setLayout(new GridBagLayout());
		initComponents();
		initLayout();
		configureButtons();
	}

	private void initComponents() {
		// Meldungsfeld
		messageLabel = new JLabel(" ");
		messageLabel.setFont(MESSAGE_FONT);
		// Buttons
		button1 = new JButton();
		button1.setFont(BUTTON_FONT);
		button2 = new JButton();
		button2.setFont(BUTTON_FONT);
		button3 = new JButton();
		button3.setFont(BUTTON_FONT);
	}

	private void initLayout() {
		// Meldungsfeld
		GridBagConstraints gbcMessage = new GridBagConstraints();
		gbcMessage.gridx = 0;
		gbcMessage.gridy = 0;
		gbcMessage.gridwidth = 5;
		gbcMessage.insets = new Insets(4, 8, 4, 8); // Ausgewogene Abstände
		gbcMessage.fill = GridBagConstraints.HORIZONTAL;
		gbcMessage.weightx = 1.0;
		add(messageLabel, gbcMessage);

		GridBagConstraints gbcButton = new GridBagConstraints();
		gbcButton.gridy = 1;
		gbcButton.insets = new Insets(4, 8, 4, 20); // Ausgewogene Abstände

		// Button 1 (ganz links)
		gbcButton.gridx = 0;
		gbcButton.weightx = 0.0;
		gbcButton.anchor = GridBagConstraints.WEST;
		gbcButton.fill = GridBagConstraints.NONE;
		add(button1, gbcButton);

		// Abstandshalter zwischen Button1 und Button2
		gbcButton.gridx = 1;
		add(new JPanel() {
			{
				setPreferredSize(new Dimension(24, 1));
			}
		}, gbcButton);

		// Button 2 (Mitte)
		gbcButton.gridx = 2;
		gbcButton.anchor = GridBagConstraints.CENTER;
		add(button2, gbcButton);

		// Abstandshalter zwischen Button2 und Button3
		gbcButton.gridx = 3;
		gbcButton.weightx = 1.0;
		gbcButton.fill = GridBagConstraints.HORIZONTAL;
		add(new JPanel(), gbcButton);

		// Button 3 (ganz rechts)
		gbcButton.gridx = 4;
		gbcButton.weightx = 0.0;
		gbcButton.anchor = GridBagConstraints.EAST;
		gbcButton.fill = GridBagConstraints.NONE;
		add(button3, gbcButton);
	}

	/** Hier können die Unterklassen Beschriftungen und Aktionen setzen */
	protected abstract void configureButtons();

	public JLabel getMessageLabel() {
		return messageLabel;
	}

	public JButton getButton1() {
		return button1;
	}

	public JButton getButton2() {
		return button2;
	}

	public JButton getButton3() {
		return button3;
	}
}
