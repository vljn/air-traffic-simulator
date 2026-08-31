package airtrafficsimulator.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;

public class ListPanel extends JPanel {

	private MainFrame parent;
	
	private JPanel airportsTab;
	private List<JCheckBox> checkboxes;
	private List<Airport> airports;
	
	private JPanel flightsTab;
	private JTable flightsTable;
	private DefaultTableModel tableModel;
	
	public ListPanel(MainFrame parent) {
		this.parent = parent;
		setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel wrapper = new JPanel(new BorderLayout());
		
		airportsTab = new JPanel();
		airportsTab.setLayout(new BoxLayout(airportsTab, BoxLayout.Y_AXIS));
		airports = new ArrayList<Airport>();
		checkboxes = new ArrayList<JCheckBox>();
		setClearedText("Trenutno nema uvezenih aerodroma");
		
		wrapper.add(new JScrollPane(airportsTab), BorderLayout.CENTER);
		JButton btnAddAirport = new JButton("Dodaj aerodrom");
		btnAddAirport.addActionListener(e -> parent.openAirportAddDialog());
		wrapper.add(btnAddAirport, BorderLayout.SOUTH);
		
		tabbedPane.addTab("Aerodromi", wrapper);
		
		flightsTab = new JPanel(new BorderLayout());
		String[] columnNames = {"Od", "Do", "Vreme", "Trajanje (min)"};
		
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (getRowCount() > 0) {
					return getValueAt(0, columnIndex).getClass();
				}
				return super.getColumnClass(columnIndex);
			}
		};
		flightsTable = new JTable(tableModel);
		flightsTable.setAutoCreateRowSorter(true);
		flightsTab.add(new JScrollPane(flightsTable), BorderLayout.CENTER);
		
		JButton btnAddFlight = new JButton("Dodaj let");
		btnAddFlight.addActionListener(e -> parent.openFlightAddDialog());
		flightsTab.add(btnAddFlight, BorderLayout.SOUTH);
		
		tabbedPane.addTab("Letovi", flightsTab);
		add(tabbedPane, BorderLayout.CENTER);
		
	}
	
	private void setClearedText(String text) {
		airportsTab.add(new JLabel(text));
	}
	
	public List<Airport> getSelected() {
		List<Airport> selected = new ArrayList<>();
		for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i).isSelected()) {
				selected.add(airports.get(i));
			}
		}
		return selected;
	}
	
	public void updateAirports(List<Airport> airports) {
		airportsTab.removeAll();
		checkboxes.clear();
		this.airports = airports;
		if (airports.isEmpty()) {
			setClearedText("Trenutno nema uvezenih aerodroma");
		}
		for (Airport a : airports) {
			JCheckBox cb = new JCheckBox(a.getName() + " (" + a.getCodeName() + ")", true);
			cb.addActionListener((e) -> {
				 parent.setSelectedAirports(getSelected());
			});
			checkboxes.add(cb);
			airportsTab.add(cb);
		}
		
		revalidate();
		repaint();
	}
	
	public void updateFlights(List<Flight> flights) {
		tableModel.setRowCount(0);
		
		for (Flight f : flights) {
			Object[] rowData = {
				f.getDepartureAirport().getCodeName(),
				f.getDestinationAirport().getCodeName(),
				f.getDepartureTime().toString(),
				f.getDuration()
			};
			tableModel.addRow(rowData);
		}
	}
}
