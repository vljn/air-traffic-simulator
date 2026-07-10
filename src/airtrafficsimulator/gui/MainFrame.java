package airtrafficsimulator.gui;

import javax.swing.*;
import airtrafficsimulator.logic.AirTrafficRepository;

public class MainFrame extends JFrame {
	
	private AirTrafficRepository repository;
	private MenuBar menuBar;
	private final int WINDOW_HEIGHT = 800;
	private final int WINDOW_WIDTH = 800;
	
	public MainFrame(AirTrafficRepository atc) {
		super("Simulator avionskog saobraćaja");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        repository = atc;
        menuBar = new MenuBar(repository, this);
        setJMenuBar(menuBar);
		super.setVisible(true);
	}

}
