
package quiz.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Thema implements Serializable {
	private static final long serialVersionUID = 2L;
	private long id;
	private String titel;
	private String information;
	private List<Frage> fragen = new ArrayList<>();

	public Thema(String titel, String information) {
		this.titel = titel;
		this.information = information;
	}

	@Override
	public String toString() {
		return titel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Thema thema = (Thema) o;
		return id == thema.id && id != 0;
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

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public List<Frage> getFragen() {
		return fragen;
	}

	public void setFragen(List<Frage> fragen) {
		this.fragen = fragen;
	}
}