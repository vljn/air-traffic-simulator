package airtrafficsimulator.logic;

import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;
import java.util.ArrayList;
import java.util.List;

public class AirTrafficRepository {
	
	private final ArrayList<Airport> airports = new ArrayList<>();
    private final ArrayList<Flight> flights = new ArrayList<>();
    
    public List<Airport> getAirports() {
    	return airports;
    }
    
    public Airport findAirport(String code) {
    	for (Airport a : airports) {
    		if (a.getCodeName().equals(code)) {
    			return a;
    		}
    	}
    	return null;
    }
    
    public void addAirport(Airport airport) {
        if (findAirport(airport.getCodeName()) != null) {
            throw new IllegalArgumentException(
                "Aerodrom sa kodom " + airport.getCodeName() + " već postoji.");
        }
        airports.add(airport);
    }
    
    public void addAirports(List<Airport> airport) {
    	for (Airport a : airport) {
    		addAirport(a);
    	}
    }
    
    public void removeAirport(Airport airport) {
    	boolean flightExists = flights.stream().anyMatch(f -> 
    		f.getDepartureAirport().equals(airport) || f.getDestinationAirport().equals(airport)
    	);
    	if (flightExists) {
    		throw new IllegalArgumentException("Aerodrom se koristi u nekom od postojećih letova.");
    	}
    	airports.remove(airport);
    }
    
    public List<Flight> getFlights() {
    	return flights;
    }
    
    public void addFlight(Flight flight) {
    	flights.add(flight);
    }
    
    public void addFlights(List<Flight> flights) {
    	for (Flight f : flights) {
    		addFlight(f);
    	}
    }
    
    public void removeFlight(Flight flight) {
    	flights.remove(flight);
    }
    
    public void clear() {
    	flights.clear();
    	airports.clear();
    }
}
