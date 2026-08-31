package airtrafficsimulator.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.*;
import airtrafficsimulator.logic.AirTrafficRepository;
import airtrafficsimulator.logic.SimulationEngine;
import airtrafficsimulator.model.Airport;

public class MainFrame extends JFrame {

	private final int WINDOW_HEIGHT = 800;
	private final int WINDOW_WIDTH = 1200;
	private AirTrafficRepository repository;
	private MenuBar menuBar;
	private MapPanel mapPanel;
	private ListPanel listPanel;
	private IdleDetector idleDetector;
	private SimulationEngine simulationEngine;
    private JLabel timeLabel;
	
	public MainFrame(AirTrafficRepository atc) {
		super("Simulator avionskog saobraćaja");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        repository = atc;
        menuBar = new MenuBar(repository, this);
        setJMenuBar(menuBar);
        idleDetector = new IdleDetector(this);
        
        mapPanel = new MapPanel(this);
        
        JPanel westPanel = new JPanel(new BorderLayout());
        listPanel = new ListPanel(this);
        westPanel.add(listPanel, BorderLayout.NORTH);
        westPanel.add(createControlPanel(), BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, mapPanel);
        splitPane.setDividerLocation(300);
        splitPane.setContinuousLayout(true);
        
        add(splitPane);
        
        setupSimulationEngine();
        
		setVisible(true);
	}
	
	void pauseIdleDetector() {
		idleDetector.pause();
	}
	
	void resumeIdleDetector() {
		idleDetector.resume();
	}
	
	public void openAirportAddDialog() {
	    AirportAddDialog dialog = new AirportAddDialog(this);
	    dialog.setVisible(true);
	    
	    if (dialog.isConfirmed()) {
	    	try {
		        repository.addAirport(dialog.getNewAirport());
		        refreshRepository();
	    	}
	    	catch (IllegalArgumentException e) {
	    		JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
	    	}
	    }
	}
	
	public void openFlightAddDialog() {
	    if (repository.getAirports().size() < 2) {
	        JOptionPane.showMessageDialog(this, "Potrebno je dodati barem dva aerodroma.", "Info", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }
	    
	    FlightAddDialog dialog = new FlightAddDialog(this, repository.getAirports());
	    dialog.setVisible(true);
	    
	    if (dialog.isConfirmed()) {
	        repository.addFlight(dialog.getNewFlight());
	        refreshRepository();
	    }
	}
	
	void setSelectedAirports(List<Airport> airports) {
		mapPanel.update(airports);
	}
	
	void refreshRepository() {
		mapPanel.update(repository.getAirports());
		listPanel.updateAirports(repository.getAirports());
		listPanel.updateFlights(repository.getFlights());
		
		if (simulationEngine != null) {
            simulationEngine.resetSimulation();
        }
	}
	
	private void setupSimulationEngine() {
        Runnable onTick = () -> {
            SwingUtilities.invokeLater(() -> {
                timeLabel.setText("Vreme: " + simulationEngine.getSimulatedTime().toString());
                timeLabel.setAlignmentX(CENTER_ALIGNMENT);
                mapPanel.updateActiveFlights(simulationEngine.getActiveFlights());
            });
        };
        
        Runnable onFinish = () -> {
        	SwingUtilities.invokeLater(() -> {
                resumeIdleDetector();
                JOptionPane.showMessageDialog(this, 
                    "Svi letovi su se uspešno završili!", 
                    "Simulacija završena", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
        };
        
        simulationEngine = new SimulationEngine(repository, onTick, onFinish);
    }
	
	private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 0, 5));
        
        JButton btnStart = new JButton("Start");
        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(1, 2, 5, 0));
        JButton btnPause = new JButton("Pause");
        JButton btnReset = new JButton("Reset");
        timeLabel = new JLabel("Vreme: 00:00", SwingConstants.CENTER);
        timeLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 24));
        
        btnStart.addActionListener(e -> {
            if (simulationEngine != null) {
            	if (simulationEngine.isFinished()) {
                    simulationEngine.resetSimulation();
                }
            	
                simulationEngine.start();
                btnPause.setEnabled(true);
                pauseIdleDetector();
            }
        });
        
        btnPause.addActionListener(e -> {
            if (simulationEngine != null) {
                simulationEngine.pause();
                btnPause.setEnabled(false);
                resumeIdleDetector();
            }
        });
        
        btnReset.addActionListener(e -> {
            if (simulationEngine != null) {
                simulationEngine.resetSimulation();
                resumeIdleDetector();
            }
        });

        panel.add(timeLabel);
        panel.add(btnStart);
        btns.add(btnPause);
        btns.add(btnReset);
        panel.add(btns);
        
        return panel;
    }

}
