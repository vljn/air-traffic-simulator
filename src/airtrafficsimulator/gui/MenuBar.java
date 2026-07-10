package airtrafficsimulator.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import airtrafficsimulator.io.CsvIo;
import airtrafficsimulator.io.ParsingException;
import airtrafficsimulator.io.ParsingResult;
import airtrafficsimulator.logic.AirTrafficRepository;
import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;

public class MenuBar extends JMenuBar {
	
	private AirTrafficRepository repository;
	private JFrame parent;
	
	public MenuBar(AirTrafficRepository atc, JFrame parent) {
		repository = atc;
		this.parent = parent;
		populate();
	}
	
	@FunctionalInterface
	interface ParserFunction {
	    ParsingResult parse(File file) throws IOException, ParsingException;
	}
	
	@FunctionalInterface
	interface WriterFunction {
		void write(File file, List<Airport> airports, List<Flight> flights) throws IOException;
	}
	
	private void fileImportHandler(FileNameExtensionFilter fileFilter, ParserFunction fn) {
		JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(fileFilter);
	    chooser.setMultiSelectionEnabled(false);
	    int returnVal = chooser.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File f = chooser.getSelectedFile();
	    	try {
				ParsingResult result = fn.parse(f);
				repository.clear();
				repository.addAirports(result.getAirports());
				repository.addFlights(result.getFlights());
			} catch (FileNotFoundException e) {
				String message = "Fajl " + f.getAbsolutePath() + " nije pronađen.";
				JOptionPane.showMessageDialog(parent, message, "Greška prilikom otvaranja fajla", JOptionPane.ERROR_MESSAGE, null);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(parent, f.getAbsolutePath(), "Greška prilikom otvaranja fajla", JOptionPane.ERROR_MESSAGE, null);
			} catch (ParsingException e) {
				JOptionPane.showMessageDialog(parent, e.getMessage(), "Greška prilikom parsiranja", JOptionPane.ERROR_MESSAGE, null);
			}
	    }
	}
	
	private void fileExportHandler(FileNameExtensionFilter fileFilter, String extension, WriterFunction fn) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(fileFilter);
	    chooser.setMultiSelectionEnabled(false);
	    int returnVal = chooser.showSaveDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File f = chooser.getSelectedFile();
	    	String path = f.getAbsolutePath();
	    	if (!f.getName().toLowerCase().endsWith(extension)) {
	    		path = path + extension;
	    	}
	    	File finalFile = new File(path);
	    	if (finalFile.exists()) {
	    		int result = JOptionPane.showConfirmDialog(
	    				parent, 
	    				"Fajl " + path + " već postoji. Da li želite da prepišete sadržaj fajla?", 
	    				"Fajl već postoji",
	    				JOptionPane.YES_NO_OPTION,
	    				JOptionPane.QUESTION_MESSAGE);
	    		if (result != JOptionPane.YES_OPTION) {
	    			return;
	    		}
	    	}
	    	try {
	    		fn.write(finalFile, repository.getAirports(), repository.getFlights());   		
	    	}
	    	catch (IOException e) {
	    		JOptionPane.showMessageDialog(parent, finalFile.getAbsolutePath(), "Greška prilikom upisa", JOptionPane.ERROR_MESSAGE, null);
	    	}
	    }
	}
	
	private void populate() {
		JMenu fileMenu = new JMenu("Fajl");
		
		JMenuItem importCsvItem = new JMenuItem("Uvezi iz CSV fajla...");
		importCsvItem.addActionListener((ae) -> {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
			fileImportHandler(filter, CsvIo::parseCsv);
		});
		fileMenu.add(importCsvItem);
		
		JMenuItem exportCsvItem = new JMenuItem("Izvezi kao CSV...");
		exportCsvItem.addActionListener((ae) -> {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
			fileExportHandler(filter, ".csv", CsvIo::writeCsv);
		});
		fileMenu.add(exportCsvItem);

		add(fileMenu);
	}
}
