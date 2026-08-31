package airtrafficsimulator.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class IdleDetector {
	
	private final int TIMEOUT_SECONDS = 60;
	private final int WARNING_SECONDS = 5;
	private JFrame parent;
	private Timer timer;
	private int elapsed = 0;
	private JDialog warningDialog;
	private JLabel countdownLabel;
	private boolean isPaused = false;
	
	public IdleDetector(JFrame parent) {
		this.parent = parent;
		
		Toolkit.getDefaultToolkit().addAWTEventListener(
	            event -> resetTimer(),
	            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
	        );
		
		timer = new Timer(1000, this::tick);
		timer.start();
	}
	
	public void pause() {
		isPaused = true;
		if (!timer.isRunning()) return;
		timer.stop();
		if (warningDialog != null) {
			warningDialog.dispose();
			warningDialog = null;
			elapsed = 0;
		}
	}
	
	public void resume() {
		isPaused = false;
		if (timer.isRunning()) return;
		resetTimer();
	}
	
	private void resetTimer() {
		if (isPaused) {
			return;
		}
		elapsed = 0;
		if (warningDialog != null && warningDialog.isVisible()) {
			warningDialog.dispose();
			warningDialog = null;
		}
		timer.start();
	}
	
	
	private void tick(ActionEvent ae) {
		elapsed++;
		
		if (elapsed >= TIMEOUT_SECONDS) {
			timer.stop();
			if (warningDialog != null) {
				warningDialog.dispose();
			}
			System.exit(0);
		}
		else if (elapsed >= TIMEOUT_SECONDS - WARNING_SECONDS) {
			updateWarningDialog();
		}
	}
	
	private void updateWarningDialog() {
		if (warningDialog == null) {
			warningDialog = new JDialog(parent, "Upozorenje", false);
			countdownLabel = new JLabel("", SwingConstants.CENTER);
			
			warningDialog.setLayout(new BorderLayout(10, 10));
			warningDialog.add(countdownLabel, BorderLayout.CENTER);
			warningDialog.setSize(500, 150);
			warningDialog.setLocationRelativeTo(parent);
			warningDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		}
		countdownLabel.setText("Zbog neaktivnost, program se gasi za " + (TIMEOUT_SECONDS - elapsed) + " sekundi.");
		warningDialog.setVisible(true);
		
	}
}
