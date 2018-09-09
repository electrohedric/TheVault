package objects;

import guis.Plane;
import objects.Map.Square;
import util.Camera;

public class Tile {

	private Plane renderablePlane;
	private Square type;
	private int gridX;
	private int gridY;
	
	public Tile(int gridX, int gridY, Square type, Plane renderablePlane) { // TODO we probably want to add textures for each square instead of a color, but for now...
		this.gridX = gridX;
		this.gridY = gridY;
		this.type = type;
		this.renderablePlane = renderablePlane;
	}
	
	public void render(Camera camera) {
		renderablePlane.render(camera);
	}
	
	public Square getType() {
		return type;
	}
	
	public int getGridX() {
		return gridX;
	}
	
	public int getGridY() {
		return gridY;
	}
	
	public int getPosX() {
		return (int) renderablePlane.getCX();
	}
	
	public int getPosY() {
		return (int) renderablePlane.getCY();
	}
	
	public int getAbsoluteX(Camera camera) {
		return (int) (getPosX() - camera.x);
	}
	
	public int getAbsoluteY(Camera camera) {
		return (int) (getPosY() - camera.y);
	}
	
	@Override
	public String toString() {
		return String.format("Tile: type=%s, grid=(%d, %d)", type, getGridX(), getGridY());
	}
	
}
