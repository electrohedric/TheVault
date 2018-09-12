package objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import constants.Resources;
import guis.Plane;
import main.Game;
import util.handling.Camera;
import util.output.Log;

public class Map {
	
	private int width;
	private int height;
	private Tile[] tiles;
	private Plane backgroundPlane;
	private Camera camera;
	
	private int SPACING;
	private int TOTAL_HEIGHT;
	private int TOTAL_WIDTH;
	private int SQUARE_SIZE;
	private int SQUARE_DISTANCE;
	
	public Map(String name, float centerX, float centerY) {
		String fullPath = Resources.DATA_PATH + name + ".png";
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(fullPath));
		} catch (IOException e) {
			Log.err("Cannot open file: " + fullPath);
		}
		
		this.width = image.getWidth();
		this.height = image.getHeight();
		int size = width * height * 4; // width * height * channels
		int[] store = new int[size];
		image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), store);
		this.tiles = new Tile[width * height];
		
		this.SPACING = (int) (Game.HEIGHT * 0.005f); // 5 pixels on a 1080p screen
		this.TOTAL_HEIGHT = (int) (Game.HEIGHT * 0.8f); // 80% of screen
		this.TOTAL_WIDTH = (int) (TOTAL_HEIGHT * height / width);
		this.SQUARE_SIZE = (int) ((TOTAL_HEIGHT - (height + 1) * SPACING) / height); // calculate square size to maximize space but contrained to height
		this.SQUARE_DISTANCE = SPACING + SQUARE_SIZE;
		TOTAL_HEIGHT = SQUARE_DISTANCE * height + SPACING; // recalculate since everything got trimmed slightly being rounded down and all
		TOTAL_WIDTH = SQUARE_DISTANCE * width + SPACING;
		int INITIAL_CENTER_X = -TOTAL_WIDTH / 2 + SPACING + SQUARE_SIZE / 2;
		int INITIAL_CENTER_Y = TOTAL_HEIGHT / 2 - SPACING - SQUARE_SIZE / 2;
		int x = INITIAL_CENTER_X;
		int y = INITIAL_CENTER_Y;
		this.backgroundPlane = new Plane(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT, 0, 0, 0, 255);
		this.camera = new Camera(0, 0);
		setCenter(centerX, centerY);
		
		int index = 0;
		for(int i = 0; i < store.length; i += 4) {
			int r = store[i + 0];
			int g = store[i + 1];
			int b = store[i + 2];
			// (we dont care about alpha)
			int color = (r << 020) | (g << 010) | b; // merge into a single color so it's easier to check
			int gx = index % width;
			int gy = index / width;
			
			switch(color) {
			case 0xFFFFFF: // white
				tiles[index] = new Tile(gx, gy, Square.NORMAL, new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 255, 255, 255));
				break;
			case 0xFF0000: // red
				tiles[index] = new Tile(gx, gy, Square.FORGE, new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 100, 100, 255));
				break;
			case 0x0000FF: // blue
				tiles[index] = new Tile(gx, gy, Square.GEM, new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 100, 100, 255, 255));
				break;
			case 0xFFFF00: // yellow
				tiles[index] = new Tile(gx, gy, Square.CONSUMABLE, new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 255, 100, 255));
				break;
			default: // other (black)
				tiles[index] = new Tile(gx, gy, Square.NONE, new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 0, 0, 0, 255));
				break;
			}
			index++;
			if(index % width == 0) { // we just wrapped. increment y and set x to initial
				x = INITIAL_CENTER_X;
				y -= SQUARE_DISTANCE;
			} else
				x += SQUARE_DISTANCE;
		}
	}
	
	public void render() {
		backgroundPlane.render(camera);
		for(Tile tile : tiles)
			tile.render(camera);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Tile getTileAt(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) // we dont have a tile at that position if its outside these bounds
			return null;
		else
			return tiles[y * width + x];
	}
	
	public Tile getTileAtMouse() {
		// offset mouse to map top left
		int mouseX = camera.getMouseX() + TOTAL_WIDTH / 2;
		int mouseY = TOTAL_HEIGHT / 2 - camera.getMouseY();
		
		 // if outside the box, obviously not on a tile
		if(mouseX < SPACING || mouseX > TOTAL_WIDTH - SPACING || mouseY < SPACING || mouseY > TOTAL_HEIGHT - SPACING)
			return null;
		
		// calculate tile closest tile
		int tileX = mouseX / SQUARE_DISTANCE;
		int tileY = mouseY / SQUARE_DISTANCE;
		
		// thing is, this includes the spacing on top and left of square, so we have to check to be sure it's not between
		int tileStartX = SPACING + (tileX * SQUARE_DISTANCE);
		int tileStartY = SPACING + (tileY * SQUARE_DISTANCE);
		if(tileStartX <= mouseX && mouseX <= tileStartX + SQUARE_SIZE && tileStartY <= mouseY && mouseY <= tileStartY + SQUARE_SIZE)
			return getTileAt(tileX, tileY);
		
		// otherwise it must be in the spacing, we're not counting that
		return null;
	}
	
	public int getTileSize() {
		return SQUARE_SIZE;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public GameObject createHighlight(Tile t, Surface texture) {
		GameObject haze = new GameObject(t.getAbsoluteX(camera), t.getAbsoluteY(camera), 0.0f, SQUARE_SIZE * 1.8f / Game.HEIGHT); // haze scale is 1.5 tile size);
		haze.setActiveTexture(texture);
		return haze;
	}
	
	public void setCenter(float x, float y) {
		camera.x = -x;
		camera.y = -y;
	}
	
	public enum Square {
		NONE, NORMAL, FORGE, GEM, CONSUMABLE;
	}
}
