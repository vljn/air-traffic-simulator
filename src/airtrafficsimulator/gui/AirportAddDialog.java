package airtrafficsimulator.gui;

import javax.swing.*;
import java.awt.*;
import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Position;

public class AirportAddDialog extends JDialog {
    
    private boolean confirmed = false;
    private Airport newAirport;

    public AirportAddDialog(JFrame parent) {
        super(parent, "Novi aerodrom", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        JTextField txtName = new JTextField();
        JTextField txtCode = new JTextField();
        JTextField txtX = new JTextField();
        JTextField txtY = new JTextField();

        add(new JLabel(" Naziv:")); add(txtName);
        add(new JLabel(" Troslovni kod:")); add(txtCode);
        add(new JLabel(" X koordinata (-180 - 180):")); add(txtX);
        add(new JLabel(" Y koordinata (-90 - 90):")); add(txtY);

        JButton btnSave = new JButton("Sačuvaj");
        JButton btnCancel = new JButton("Odustani");

        btnSave.addActionListener(ae -> {
            try {
                String name = txtName.getText();
                String code = txtCode.getText();
                double x = Double.parseDouble(txtX.getText());
                double y = Double.parseDouble(txtY.getText());
                
                if (name.isEmpty() || code.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Sva polja su obavezna!", "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                newAirport = new Airport(code, name, new Position(x, y));
                confirmed = true;
                dispose();
            } 
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Koordinate moraju biti brojevi", "Greška", JOptionPane.ERROR_MESSAGE);
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
    public Airport getNewAirport() { return newAirport; }
}