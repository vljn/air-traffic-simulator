package airtrafficsimulator.io;

import airtrafficsimulator.model.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

public class CsvIo {
	public static ParsingResult parseCsv(File file) throws FileNotFoundException, IOException, ParsingException {
		List<Airport> airports = new ArrayList<>();
        List<Flight> flights = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        	String line;
        	int current = 0;
        	int lineNumber = -1;
        	while ((line = reader.readLine()) != null) {
        		lineNumber++;
        		String trimmed = line.trim();
        		if (trimmed.isEmpty()) continue;
        		if (trimmed.equalsIgnoreCase("# AIRPORTS")) { current = 1; continue; }
                if (trimmed.equalsIgnoreCase("# FLIGHTS"))  { current = 2;  continue; }
                if (trimmed.equalsIgnoreCase("CODE,NAME,X,Y")) continue;
                if (trimmed.equalsIgnoreCase("FROM,TO,DEPARTURE,DURATION")) continue;
                
                switch (current) {
                case 1: {
                	Airport airport = parseAirport(line, lineNumber);
                	if (airports.stream().anyMatch(a -> 
                		a.equals(airport))) {
                		throw new ParsingException(lineNumber, line, "Aerodrom je već unet.");
                	}
                	airports.add(airport);
                	break;
                }
                case 2: {
                	flights.add(parseFlight(line, lineNumber, airports));
                	break;
                }
                case 0: {
                	throw new ParsingException(lineNumber, line, "Linija nije u adekvatnoj sekciji (#AIRPORTS, #FLIGHTS).");
                }
                }
        	}
        }
        return new ParsingResult(airports, flights);
	}
	
	private static Airport parseAirport(String line, int lineNumber) throws ParsingException {
		String[] splitted = Stream.of(line.split(",")).map(s -> s.trim()).toArray(String[]::new);
		if (splitted.length != 4) {
			throw new ParsingException(lineNumber, line, "Format nije validan. (CODE,NAME,X,Y)");
		}
		try {
			double x = Double.parseDouble(splitted[2]);
			double y = Double.parseDouble(splitted[3]);
			return new Airport(splitted[0], splitted[1], new Position(x, y));
		}
		catch (NumberFormatException e) {
			throw new ParsingException(lineNumber, line, "Numerički podatak nije validan.");
		}
		catch (IllegalArgumentException e) {
			throw new ParsingException(lineNumber, line, e.getMessage());
		}
	}
	
	private static Flight parseFlight(String line, int lineNumber, List<Airport> airports) throws ParsingException {
		String[] splitted = Stream.of(line.split(",")).map(s -> s.trim()).toArray(String[]::new);
		if (splitted.length != 4) {
			throw new ParsingException(lineNumber, line, "Format nije validan. (FROM,TO,DEPARTURE,DUARATION)");
		}
		try {
			LocalTime departure = LocalTime.parse(splitted[2], DateTimeFormatter.ofPattern("H:m"));
			int duration = Integer.parseInt(splitted[3]);
			Optional<Airport> from = airports.stream().filter(a -> a.getCodeName().equals(splitted[0])).findFirst();
			if (!from.isPresent()) {
				throw new ParsingException(lineNumber, line, "Polazni aerodrom leta nije pronađen.");
			}
			Optional<Airport> to = airports.stream().filter(a -> a.getCodeName().equals(splitted[1])).findFirst();
			if (!to.isPresent()) {
				throw new ParsingException(lineNumber, line, "Odredišni aerodrom leta nije pronađen.");
			}
			
			return new Flight(from.get(), to.get(), departure, duration);
		}
		catch (DateTimeParseException e) {
			throw new ParsingException(lineNumber, line, "Format vremena nije validan. (H:m)");
		}
		catch (NumberFormatException e) {
			throw new ParsingException(lineNumber, line, "Numerički podatak nije validan.");
		}
		catch (IllegalArgumentException e) {
			throw new ParsingException(lineNumber, line, e.getMessage());
		}
	}
	
	public static void writeCsv(File file, List<Airport> airports, List<Flight> flights) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			if (!airports.isEmpty()) {
				writer.append("# AIRPORTS\n");
			}
			for (Airport a : airports) {
				writer.append(a.getCodeName()
						+ "," + a.getName()
						+ "," + (long) a.getPosition().getX()
						+ "," + (long) a.getPosition().getY() + '\n');
			}
			
			if (!flights.isEmpty()) {
				writer.append("# FLIGHTS\n");
			}
			for (Flight f : flights) {
				writer.append(
						f.getDepartureAirport().getCodeName()
						+ "," + f.getDestinationAirport().getCodeName()
						+ "," + f.getDepartureTime()
						+ "," + f.getDuration() + '\n');
			}
		}
	}
	
}
