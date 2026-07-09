package airtrafficsimulator.model;

import java.util.Objects;

public class Position {
	
	private double x;
	private double y;

	public Position(double x, double y) {
		setX(x);
		setY(y);
	}

	public double getX() {
		return x;
	}

	public final void setX(double x) {
		if (x < -180.0 || x > 180.0) {
	        throw new IllegalArgumentException("Vrednost x koordinate mora biti između -180 i 180.");
	    }
	    this.x = x;
	}

	public double getY() {
		return y;
	}

	public final void setY(double y) {
		if (y < -90.0 || y > 90.0) {
	        throw new IllegalArgumentException("Vrednost y koordinate mora biti između -90 i 90.");
	    }
		this.y = y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Position))
			return false;
		Position other = (Position) obj;
		return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
}
