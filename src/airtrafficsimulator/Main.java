package airtrafficsimulator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import airtrafficsimulator.gui.MainFrame;
import airtrafficsimulator.logic.AirTrafficRepository;

public class Main {

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
		SwingUtilities.invokeLater(() -> {
	        AirTrafficRepository atr = new AirTrafficRepository();
	        new MainFrame(atr);
	    });
	}

}
