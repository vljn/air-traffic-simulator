package airtrafficsimulator.gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Flight;

public class FlightAddDialog extends JDialog {
    
    private boolean confirmed = false;
    private Flight newFlight;

    public FlightAddDialog(JFrame parent, List<Airport> availableAirports) {
        super(parent, "Novi let", true);
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        JComboBox<Airport> cbDeparture = new JComboBox<>(availableAirports.toArray(new Airport[0]));
        JComboBox<Airport> cbDestination = new JComboBox<>(availableAirports.toArray(new Airport[0]));
        JTextField txtTime = new JTextField("12:00");
        JTextField txtDuration = new JTextField("60");

        add(new JLabel(" Polazni aerodrom:")); add(cbDeparture);
        add(new JLabel(" Odredišni aerodrom:")); add(cbDestination);
        add(new JLabel(" Vreme (H:m):")); add(txtTime);
        add(new JLabel(" Trajanje (min):")); add(txtDuration);

        JButton btnSave = new JButton("Sačuvaj");
        JButton btnCancel = new JButton("Odustani");

        btnSave.addActionListener(ae -> {
            try {
                Airport dep = (Airport) cbDeparture.getSelectedItem();
                Airport dest = (Airport) cbDestination.getSelectedItem();
                
                if (dep.equals(dest)) {
                    JOptionPane.showMessageDialog(this, "Polazni i odredišni aerodrom se moraju razlikovati", "Greška", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalTime time = LocalTime.parse(txtTime.getText());
                int duration = Integer.parseInt(txtDuration.getText());

                newFlight = new Flight(dep, dest, time, duration);
                confirmed = true;
                dispose();
            } 
            catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Vreme mora biti u formatu H:m (npr. 01:10)", "Greška", JOptionPane.ERROR_MESSAGE);
            } 
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Trajanje leta mora biti broj", "Greška", JOptionPane.ERROR_MESSAGE);
            }
            catch (IllegalArgumentException e) {
            	JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        add(btnSave);
        add(btnCancel);
    }

    public boolean isConfirmed() { return confirmed; }
    public Flight getNewFlight() { return newFlight; }
}