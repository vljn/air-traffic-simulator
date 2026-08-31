package airtrafficsimulator.model;

import java.time.LocalTime;

public class ActiveFlight {
	
	public enum Status {
		WAITING_FOR_TAKEOFF,
		FLYING,
		LANDED
	}
	
	private final Flight flight;

	private final LocalTime actualDepartureTime;
	private Status status;
	private double currentX;
	private double currentY;
	
	public ActiveFlight(Flight flight, LocalTime actualDepartureTime) {
        this.flight = flight;
        this.actualDepartureTime = actualDepartureTime;
        this.status = Status.WAITING_FOR_TAKEOFF;
        
        this.currentX = flight.getDepartureAirport().getPosition().getX();
        this.currentY = flight.getDepartureAirport().getPosition().getY();
    }
	
	public Flight getFlight() {
        return flight;
    }

    public LocalTime getActualDepartureTime() {
        return actualDepartureTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }
    
    public void updatePosition(double x, double y) {
        this.currentX = x;
        this.currentY = y;
    }
}
