package airtrafficsimulator.logic;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import airtrafficsimulator.model.ActiveFlight;
import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;

public class SimulationEngine implements Runnable {
	
	private volatile boolean running = false;
	private volatile boolean paused = false;
	private volatile boolean finished = false;
	
	private Thread simulationThread;
	private LocalTime simulatedTime;
	
	private final List<ActiveFlight> activeFlights = new ArrayList<>();

	private Runnable onTick;
	private Runnable onFinish;
	private AirTrafficRepository repository;
	
	private static int SLEEP_DURATION = 50;
	
	public SimulationEngine(AirTrafficRepository repository, Runnable onTick, Runnable onFinish) {
        this.repository = repository;
        this.onTick = onTick;
        this.onFinish = onFinish;
        resetSimulation();
    }
	
	public void resetSimulation() {
        stop();
        finished = false;
        simulatedTime = LocalTime.MIDNIGHT;        
        synchronized (activeFlights) {
            activeFlights.clear();
            
            Map<Airport, LocalTime> nextAvailableTakeoff = new HashMap<>();
            
            List<Flight> allFlights = new ArrayList<>(repository.getFlights());
            allFlights.sort(Comparator.comparing(Flight::getDepartureTime));
            
            for (Flight f : allFlights) {
                Airport depAirport = f.getDepartureAirport();
                LocalTime scheduledTime = f.getDepartureTime();
                LocalTime availableTime = nextAvailableTakeoff.getOrDefault(depAirport, LocalTime.MIDNIGHT);
                
                LocalTime actualDeparture;
                if (scheduledTime.isBefore(availableTime)) {
                    actualDeparture = availableTime;
                } else {
                    actualDeparture = scheduledTime;
                }
                
                activeFlights.add(new ActiveFlight(f, actualDeparture));

                nextAvailableTakeoff.put(depAirport, actualDeparture.plusMinutes(10));
            }
        }
        
        if (onTick != null) onTick.run(); 
    }
	
	public void start() {
        if (!running) {
            running = true;
            paused = false;
            simulationThread = new Thread(this, "SimulationThread");
            simulationThread.setDaemon(true);
            simulationThread.start();
        } else if (paused) {
            paused = false;
        }
    }

    public void pause() {
        paused = true;
    }

    public void stop() {
        running = false;
        paused = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }
    
    public List<ActiveFlight> getActiveFlights() {
        synchronized (activeFlights) {
            return new ArrayList<>(activeFlights); 
        }
    }
    
    public LocalTime getSimulatedTime() {
        return simulatedTime;
    }
	
    @Override
    public void run() {
        while (running) {
            try {
                if (!paused) {
                    processTick();
                }
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void processTick() {
        simulatedTime = simulatedTime.plusMinutes(1);
        
        synchronized (activeFlights) {
            for (ActiveFlight af : activeFlights) {
                
                if (af.getStatus() == ActiveFlight.Status.WAITING_FOR_TAKEOFF) {
                    if (!simulatedTime.isBefore(af.getActualDepartureTime())) {
                        af.setStatus(ActiveFlight.Status.FLYING);
                    }
                }
                
                if (af.getStatus() == ActiveFlight.Status.FLYING) {
                    Flight flight = af.getFlight();
                    
                    long elapsedMinutes = ChronoUnit.MINUTES.between(af.getActualDepartureTime(), simulatedTime);
                    if (elapsedMinutes < 0) {
                        elapsedMinutes += 1440; 
                    }

                    int totalDuration = flight.getDuration(); 
                    
                    double progress = (double) elapsedMinutes / totalDuration;
                    
                    if (progress >= 1.0) {
                        af.setStatus(ActiveFlight.Status.LANDED);
                        af.updatePosition(flight.getDestinationAirport().getPosition().getX(),
                                          flight.getDestinationAirport().getPosition().getY());
                    } else {
                        double startX = flight.getDepartureAirport().getPosition().getX();
                        double startY = flight.getDepartureAirport().getPosition().getY();
                        double endX = flight.getDestinationAirport().getPosition().getX();
                        double endY = flight.getDestinationAirport().getPosition().getY();
                        
                        double currentX = startX + (endX - startX) * progress;
                        double currentY = startY + (endY - startY) * progress;
                        
                        af.updatePosition(currentX, currentY);
                    }
                }
            }
        }
        
        boolean allLanded = true;
        synchronized (activeFlights) {
            for (ActiveFlight af : activeFlights) {
                if (af.getStatus() != ActiveFlight.Status.LANDED) {
                    allLanded = false;
                    break;
                }
            }
        }
        
        if (allLanded && !activeFlights.isEmpty() && !finished) {
            finished = true;
            stop();
            if (onFinish != null) {
                onFinish.run();
            }
        }
        
        if (onTick != null) {
            onTick.run();
        }
    }

	public boolean isFinished() {
		return finished;
	}
	
}
