package airtrafficsimulator.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;
import airtrafficsimulator.model.Position;

class AirportDto {
	String code;
	String name;
	int x;
	int y;
}

class FlightDto {
	String from;
	String to;
	String departure;
	int duration;
}

class AirTrafficRepositoryDto {
	List<AirportDto> airports;
	List<FlightDto> flights;
}

public class JsonIo {
	public static ParsingResult parseJson(File file) throws IOException, ParsingException {
		Gson gson = new Gson();
		AirTrafficRepositoryDto dto;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			dto = gson.fromJson(reader, AirTrafficRepositoryDto.class);
		}
		catch (JsonSyntaxException e) {
			throw new ParsingException("Fajl nije validan JSON: " + e.getMessage());
		}
		
		if (dto == null) {
			throw new ParsingException("Fajl je prazan ili nije validan JSON.");
		}
		
		List<Airport> airports = new ArrayList<>();
        Map<String, Airport> byCode = new HashMap<>();

        if (dto.airports != null) {
            for (AirportDto a : dto.airports) {
                try {
                    Airport airport = new Airport(a.code, a.name, new Position(a.x, a.y));
                    if (byCode.containsKey(airport.getCodeName())) {
                        throw new ParsingException("Aerodrom je već dodat: " + airport.getCodeName());
                    }
                    airports.add(airport);
                    byCode.put(airport.getCodeName(), airport);
                } catch (IllegalArgumentException e) {
                    throw new ParsingException("Greška kod aerodroma " + a.code + ": " + e.getMessage());
                }
            }
        }
        
        
        List<Flight> flights = new ArrayList<>();
        if (dto.flights != null) {
            for (FlightDto f : dto.flights) {
                Airport from = byCode.get(f.from);
                Airport to = byCode.get(f.to);
                if (from == null) throw new ParsingException("Polazni aerodrom leta nije pronađen '" + f.from + "'.");
                if (to == null) throw new ParsingException("Odredišni aerodrom leta nije pronađen '" + f.to + "'.");
                try {
                    LocalTime time = LocalTime.parse(f.departure, DateTimeFormatter.ofPattern("H:m"));
                    flights.add(new Flight(from, to, time, f.duration));
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    throw new ParsingException("Greška kod leta " + f.from + "->" + f.to + ": " + e.getMessage());
                }
            }
        }

        return new ParsingResult(airports, flights);
	}
	
	public static void writeJson(File file, List<Airport> airports, List<Flight> flights) throws IOException {
		AirTrafficRepositoryDto dto = new AirTrafficRepositoryDto();
	    dto.airports = new ArrayList<>();
	    for (Airport a : airports) {
	        AirportDto d = new AirportDto();
	        d.code = a.getCodeName();
	        d.name = a.getName();
	        d.x = (int) a.getPosition().getX();
	        d.y = (int) a.getPosition().getY();
	        dto.airports.add(d);
	    }
	    
	    dto.flights = new ArrayList<>();
	    for (Flight f : flights) {
	    	FlightDto d = new FlightDto();
	    	d.from = f.getDepartureAirport().getCodeName();
	        d.to = f.getDestinationAirport().getCodeName();
	        d.departure = f.getDepartureTime().format(DateTimeFormatter.ofPattern("H:m"));
	        d.duration = f.getDuration();
	        dto.flights.add(d);
	    }
	    
	    Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	        gson.toJson(dto, writer);
	    }
	}
	
}
