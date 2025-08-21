
package quiz.data.model;

import java.io.Serializable;
import java.util.Objects;

public class Antwort implements Serializable {
	private static final long serialVersionUID = 2L;
	private long id;
	private String text;
	private boolean istRichtig;

	public Antwort(String text, boolean istRichtig) {
		this.text = text;
		this.istRichtig = istRichtig;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Antwort antwort = (Antwort) o;
		return id == antwort.id && id != 0;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean istRichtig() {
		return istRichtig;
	}

	public void setIstRichtig(boolean istRichtig) {
		this.istRichtig = istRichtig;
	}
}
