package quiz.data.dao;

import java.util.List;

import quiz.data.model.Frage;

public interface IFrageDAO {
	void speichereFrage(Frage frage, long themaId);

	void updateFrage(Frage frage);

	void loescheFrage(Frage frage);

	List<Frage> findeFragenFuerThema(long themaId);
}
