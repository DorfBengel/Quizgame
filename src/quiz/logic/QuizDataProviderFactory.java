package quiz.logic;

import quiz.logic.interfaces.QuizDataProvider;

/**
 * Einfache Factory für den QuizDataProvider. Verwendet den
 * ServiceBackedDataProvider als einzige Implementierung.
 */
public class QuizDataProviderFactory {

	private static QuizDataProvider instance;

	public static QuizDataProvider getInstance() {
		if (instance == null) {
			instance = new ServiceBackedDataProvider();
		}
		return instance;
	}
}
