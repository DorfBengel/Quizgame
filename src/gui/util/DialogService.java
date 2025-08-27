package gui.util;

import java.awt.Component;

import javax.swing.JOptionPane;

public final class DialogService {

	private DialogService() {
	}

	public static void info(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void warn(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Hinweis", JOptionPane.WARNING_MESSAGE);
	}

	public static void error(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Fehler", JOptionPane.ERROR_MESSAGE);
	}

	public static boolean confirm(Component parent, String message, String title) {
		int res = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return res == JOptionPane.YES_OPTION;
	}
}
