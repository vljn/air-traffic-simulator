package airtrafficsimulator.model;

import java.time.LocalTime;
import java.util.Objects;

public class Flight {
	
	private Airport departureAirport;
	private Airport destinationAirport;
	private LocalTime departureTime;
	private int duration;
	
	public Flight(Airport departureAirport, Airport destinationAirport, LocalTime time, int duration) {
		setDepartureAirport(departureAirport);
		setDestinationAirport(destinationAirport);
		setDepartureTime(time);
		setDuration(duration);
	}
	
	public Airport getDepartureAirport() {
		return departureAirport;
	}
	
	public void setDepartureAirport(Airport departureAirport) {
		if (departureAirport == null) {
			throw new IllegalArgumentException("Početni aerodrom mora biti zadat.");
		}
		if (this.destinationAirport != null && departureAirport.equals(this.destinationAirport)) {
	        throw new IllegalArgumentException("Početni i krajnji aerodrom ne mogu biti isti.");
		}
		this.departureAirport = departureAirport;
	}
	
	public Airport getDestinationAirport() {
		return destinationAirport;
	}
	
	public void setDestinationAirport(Airport destinationAirport) {
		if (destinationAirport == null) {
			throw new IllegalArgumentException("Krajnji aerodrom mora biti zadat.");
		}
		if (this.departureAirport != null && destinationAirport.equals(this.departureAirport)) {
            throw new IllegalArgumentException("Početni i krajnji aerodrom ne mogu biti isti.");
        }
		this.destinationAirport = destinationAirport;
	}
	
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	
	public void setDepartureTime(LocalTime departureTime) {
		if (departureTime == null) {
            throw new IllegalArgumentException("Vreme poletanja mora biti zadato.");
        }
		this.departureTime = departureTime;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Trajanje leta mora biti veće od nule.");
		}
		this.duration = duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(departureAirport, departureTime, destinationAirport, duration);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Flight))
			return false;
		Flight other = (Flight) obj;
		return Objects.equals(departureAirport, other.departureAirport)
				&& Objects.equals(departureTime, other.departureTime)
				&& Objects.equals(destinationAirport, other.destinationAirport)
				&& duration == other.duration;
	}
	
	@Override
    public String toString() {
        return "Let: " + departureAirport.getCodeName() + " -> " + destinationAirport.getCodeName() + 
               " | Poleće: " + departureTime + " | Trajanje: " + duration + " min";
    }
	
}
