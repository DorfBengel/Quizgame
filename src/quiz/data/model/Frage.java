
package quiz.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Frage implements Serializable {
	private static final long serialVersionUID = 2L;
	private long id;
	private String frageText;
	private String frageTitel;
	private List<Antwort> antworten = new ArrayList<>();

	public Frage(String frageText) {
		this.frageText = frageText;
	}

	@Override
	public String toString() {
		// Einfache String-Repräsentation für Debugging/Logging
		// Die UI-Anzeige erfolgt über den ListSeparatorRenderer
		return "Frage[id=" + id + ", titel=" + frageTitel + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Frage frage = (Frage) o;
		return id == frage.id && id != 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	// --- Getter und Setter ---
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFrageText() {
		return frageText;
	}

	public void setFrageText(String frageText) {
		this.frageText = frageText;
	}

	public List<Antwort> getAntworten() {
		return antworten;
	}

	public void setAntworten(List<Antwort> antworten) {
		this.antworten = antworten;
	}

	public String getFrageTitel() {
		return frageTitel;
	}

	public void setFrageTitel(String frageTitel) {
		this.frageTitel = frageTitel;
	}
}