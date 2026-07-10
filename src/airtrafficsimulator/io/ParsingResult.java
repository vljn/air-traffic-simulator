package airtrafficsimulator.io;

import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;
import java.util.List;

public class ParsingResult {
	private final List<Airport> airports;
    private final List<Flight> flights;

    public ParsingResult(List<Airport> airports, List<Flight> flights) {
        this.airports = airports;
        this.flights = flights;
    }

    public List<Airport> getAirports() { return airports; }
    public List<Flight> getFlights() { return flights; }
    
    @Override
    public String toString() {
    	String str = "";
    	for (Airport a : airports) {
    		str += a + "\n";
    	}
    	for (Flight f : flights) {
    		str += f + "\n";
    	}
    	return str;
    }
}
