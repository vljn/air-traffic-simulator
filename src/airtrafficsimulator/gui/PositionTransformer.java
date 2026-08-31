package airtrafficsimulator.gui;

import java.awt.Point;

import airtrafficsimulator.model.Position;

public class PositionTransformer {

	private int panelWidth, panelHeight;
	
	public PositionTransformer(int panelWidth, int panelHeight) {
		this.panelHeight = panelHeight;
		this.panelWidth = panelWidth;
	}
	
	public Point transform(Position position) {
		int x = (int) ((position.getX() + 180) / 360.0 * panelWidth);
		int y = (int) ((90 - position.getY()) / 180.0 * panelHeight);
		return new Point(x, y);
	}
	
}
