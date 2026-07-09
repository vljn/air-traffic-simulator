package airtrafficsimulator.model;

import java.util.Objects;

public class Airport {
	
	private String name;
	private String codeName;
	private Position position;
	
    public Airport(String codeName, String name, Position position) {
        setCodeName(codeName);
    	setName(name);
        setPosition(position);
    }
	
	public String getName() {
		return name;
	}
	
	public final void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Ime aerodroma mora biti zadato.");
		}
		this.name = name;
	}
	
	public String getCodeName() {
		return codeName;
	}
	
	public final void setCodeName(String codeName) {
		if(codeName == null) {
			throw new IllegalArgumentException("Kod aerodroma mora biti zadat.");
		}
        if(!codeName.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Kod aerodroma mora biti sačinjen od 3 velika slova.");
        }
        this.codeName = codeName;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public final void setPosition(Position position) {
		if(position == null) {
			throw new IllegalArgumentException("Pozicija aerodroma mora biti zadata");
		}
        this.position = position;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codeName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Airport))
			return false;
		Airport other = (Airport) obj;
		return Objects.equals(codeName, other.codeName);
	}
	
	@Override
	public String toString() {
		return name + " (" + codeName + ") {" + position.getX() + ", " + position.getY() + "}";
	}
	
}
