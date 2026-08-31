package airtrafficsimulator.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import airtrafficsimulator.model.ActiveFlight;
import airtrafficsimulator.model.Airport;
import airtrafficsimulator.model.Position;

public class MapPanel extends JPanel {
	
	private static Color BACKGROUND_COLOR = Color.GRAY;
	private static Color SQUARE_COLOR = Color.DARK_GRAY;
	private static Color BLINK_COLOR = Color.RED;
	private static final Color PLANE_COLOR = Color.BLUE;
	private static int SQUARE_SIZE = 8;
	private static final int PLANE_SIZE = 10;
	private static int FONT_SIZE = 12;
	private List<Airport> airports;
	private Timer blinkTimer;
	private boolean blinkOn = false;
	private Airport selectedAirport;
	private MainFrame parent;
	
	private List<ActiveFlight> activeFlights = new ArrayList<>();
	
	public MapPanel(MainFrame parent) {
		setBackground(BACKGROUND_COLOR);
		this.parent = parent;
		airports = new ArrayList<Airport>();
		blinkTimer = new Timer(500, e -> {
			blinkOn = !blinkOn;
			repaint();
		});
		
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });
	}
	
	private void handleClick(Point clickPoint) {
		PositionTransformer transformer = new PositionTransformer(getWidth(), getHeight());
        Airport clicked = null;

        for (Airport a : airports) {
            Point p = transformer.transform(a.getPosition());
            Rectangle bounds = new Rectangle(
                p.x - SQUARE_SIZE / 2, p.y - SQUARE_SIZE / 2, SQUARE_SIZE, SQUARE_SIZE);
            if (bounds.contains(clickPoint)) {
                clicked = a;
                break;
            }
        }

        if (clicked == null) {
        	deselect();
        	return;
        }

        if (clicked.equals(selectedAirport)) {
            deselect();
        } else {
            select(clicked);
        }
	}
	
	private void select(Airport a) {
		selectedAirport = a;
		blinkOn = true;
		blinkTimer.start();
		parent.pauseIdleDetector();
		repaint();
	}
	
	private void deselect() {
		selectedAirport = null;
		blinkOn = false;
		blinkTimer.stop();
		parent.resumeIdleDetector();
		repaint();
	}
	
	public void update(List<Airport> airports) {
		this.airports = airports;
		revalidate();
		repaint();
	}
	
	public void updateActiveFlights(List<ActiveFlight> flights) {
	    this.activeFlights = flights;
	    repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		PositionTransformer transformer = new PositionTransformer(getWidth(), getHeight());
		
		for (Airport a : airports) {
			Point position = transformer.transform(a.getPosition());
			
			if (blinkOn && a == selectedAirport) {
				g.setColor(BLINK_COLOR);
			} else {				
				g.setColor(SQUARE_COLOR);
			}
			g.fillRect(position.x - SQUARE_SIZE / 2, position.y - SQUARE_SIZE / 2, SQUARE_SIZE, SQUARE_SIZE);
			
			setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
			g.drawString(a.getCodeName(), position.x + SQUARE_SIZE, position.y + SQUARE_SIZE);
		}
		for (ActiveFlight af : activeFlights) {
	        if (af.getStatus() == ActiveFlight.Status.FLYING) {
	            Position tempPos = new Position(af.getCurrentX(), af.getCurrentY());
	            Point p = transformer.transform(tempPos);
	            
	            g.setColor(PLANE_COLOR);
	            g.fillOval(p.x - PLANE_SIZE / 2, p.y - PLANE_SIZE / 2, PLANE_SIZE, PLANE_SIZE);
	        }
	    }
	}
	
}
