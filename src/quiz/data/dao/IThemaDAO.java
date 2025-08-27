package quiz.data.dao;

import java.util.List;
import java.util.Optional;

import quiz.data.model.Thema;

public interface IThemaDAO {
	void speichereThema(Thema thema);

	void loescheThema(Thema thema);

	void updateThema(Thema thema);

	List<Thema> getAlleThemen();

	Optional<Thema> findeThemaById(long id);
}