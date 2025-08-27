package gui;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import gui.helper.ListSeparatorRenderer;

public class QuizScrollPane<T> extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private DefaultListModel<T> listModel;
	private JList<T> list;

	// Minimale Größen für bessere Responsivität
	private static final int MIN_WIDTH = 150;
	private static final int MIN_HEIGHT = 100;
	private static final int PREFERRED_WIDTH = 250;
	private static final int PREFERRED_HEIGHT = 200;

	public QuizScrollPane() {
		listModel = new DefaultListModel<>();
		list = new JList<>(listModel);
		setViewportView(list);
		list.setCellRenderer(new ListSeparatorRenderer());
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

		// Responsive Größen setzen
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		// Bessere Scroll-Performance
		list.setFixedCellHeight(25);
		list.setVisibleRowCount(8);
	}

	public DefaultListModel<T> getListModel() {
		return listModel;
	}

	public JList<T> getList() {
		return list;
	}

	/**
	 * Passt die Größe der ScrollPane an die verfügbare Breite an.
	 */
	public void adjustSizeForWidth(int availableWidth) {
		if (availableWidth < MIN_WIDTH) {
			setPreferredSize(new Dimension(MIN_WIDTH, PREFERRED_HEIGHT));
		} else if (availableWidth < PREFERRED_WIDTH) {
			setPreferredSize(new Dimension(availableWidth, PREFERRED_HEIGHT));
		} else {
			setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		}
		revalidate();
	}
}
