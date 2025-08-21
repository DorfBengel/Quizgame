package gui.helper;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.MatteBorder;

import quiz.data.model.Frage;
import quiz.data.model.Thema;

public class ListSeparatorRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		// Untere Linie als Trennlinie (z.B. 1px, hellgrau)
		label.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

		// Korrekte Anzeige basierend auf dem Objekttyp (bessere MVC-Trennung)
		if (value instanceof Frage) {
			Frage frage = (Frage) value;
			String titel = frage.getFrageTitel();
			if (titel != null && !titel.trim().isEmpty()) {
				label.setText(titel);
			} else {
				// Fallback: Erste 50 Zeichen des Frage-Texts
				String text = frage.getFrageText();
				if (text != null && !text.trim().isEmpty()) {
					text = text.trim();
					label.setText(text.length() > 50 ? text.substring(0, 47) + "..." : text);
				} else {
					label.setText("Unbenannte Frage");
				}
			}
		} else if (value instanceof Thema) {
			Thema thema = (Thema) value;
			label.setText(thema.getTitel());
		}
		// Für andere Objekttypen wird toString() verwendet

		return label;
	}
}