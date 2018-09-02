package objects;

import guis.Plane;
import objects.Map.Square;
import util.Camera;

public class Tile {

	private Plane renderablePlane;
	private Square type;
	
	public Tile(Square type, Plane renderablePlane) { // TODO we probably want to add textures for each square instead of a color, but for now...
		this.type = type;
		this.renderablePlane = renderablePlane;
	}
	
	public void render(Camera camera) {
		renderablePlane.render(camera);
	}
	
	public Square getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "Tile: type=" + type;
	}
	
}
