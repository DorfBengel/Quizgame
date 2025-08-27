package quiz.logic.interfaces;

import java.util.List;

import quiz.data.model.Frage;
import quiz.data.model.Thema;

public interface QuizDataProvider {
	List<Thema> getAlleThemen();

	List<Frage> findeFragenFuerThema(long themaId);

	void speichereThema(String titel, String information, boolean forceOverwrite);

	void updateThema(Thema thema);

	void loescheThema(Thema thema);

	void loescheFrage(Frage frage);

	void speichereFrage(Frage frage, long themaId);
}