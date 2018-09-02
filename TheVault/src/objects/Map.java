package objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import constants.Resources;
import guis.Plane;
import main.Game;
import util.Camera;
import util.Log;

public class Map {
	
	private int width;
	private int height;
	private Square[] squares;
	private Plane[] planes;
	private Plane backgroundPlane;
	private Camera camera;
	
	public Map(String name) {
		String fullPath = Resources.DATA_PATH + name + ".png";
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(fullPath));
		} catch (IOException e) {
			Log.err("Cannot open file: " + fullPath);
		}
		
		this.camera = new Camera(-1000, -500); // FIXME figure out what to change this to and add methods to change it
		this.width = image.getWidth();
		this.height = image.getHeight();
		int size = width * height * 4; // width * height * channels
		int[] store = new int[size];
		image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), store);
		this.squares = new Square[width * height];
		this.planes = new Plane[width * height];
		
		int SPACING = (int) (Game.HEIGHT * 0.005f); // 5 pixels on a 1080p screen
		int TOTAL_HEIGHT = (int) (Game.HEIGHT * 0.8f); // 80% of screen
		int TOTAL_WIDTH = (int) (TOTAL_HEIGHT * height / width);
		int SQUARE_SIZE = (int) ((TOTAL_HEIGHT - (height + 1) * SPACING) / height); // calculate square size to maximize space but contrained to height
		int SQUARE_DISTANCE = SPACING + SQUARE_SIZE;
		TOTAL_HEIGHT = SQUARE_DISTANCE * height + SPACING; // recalculate since everything got trimmed slightly being rounded down and all
		TOTAL_WIDTH = SQUARE_DISTANCE * width + SPACING;
		int INITIAL_CENTER_X = -TOTAL_WIDTH / 2 + SPACING + SQUARE_SIZE / 2;
		int INITIAL_CENTER_Y = -TOTAL_HEIGHT / 2 + SPACING + SQUARE_SIZE / 2;
		int x = INITIAL_CENTER_X;
		int y = INITIAL_CENTER_Y;
		this.backgroundPlane = new Plane(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT, 0, 0, 0, 255);
		
		int index = 0;
		for(int i = 0; i < store.length; i += 4) {
			int r = store[i + 0];
			int g = store[i + 1];
			int b = store[i + 2];
			// (we dont care about alpha)
			int color = (r << 020) | (g << 010) | b; // merge into a single color so it's easier to check
			
			switch(color) {
			case 0xFFFFFF: // white
				squares[index] = Square.NORMAL;
				planes[index] = new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 255, 255, 255); // TODO we probably want to add textures for each square instead of a color, but for now...
				break;
			case 0xFF0000: // red
				squares[index] = Square.FORGE;
				planes[index] = new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 0, 0, 255);
				break;
			case 0x0000FF: // blue
				squares[index] = Square.GEM;
				planes[index] = new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 0, 0, 255, 255);
				break;
			case 0xFFFF00: // yellow
				squares[index] = Square.CONSUMABLE;
				planes[index] = new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 255, 255, 0, 255);
				break;
			default: // other (black)
				squares[index] = Square.NONE;
				planes[index] = new Plane(x, y, SQUARE_SIZE, SQUARE_SIZE, 0, 0, 0, 255); // not stricly nessessary but null isn't great either
				break;
			}
			index++;
			if(index % width == 0) { // we just wrapped. increment y and set x to initial
				x = INITIAL_CENTER_X;
				y += SQUARE_DISTANCE;
			} else
				x += SQUARE_DISTANCE;
		}
	}
	
	public void render() {
		backgroundPlane.render(camera);
		for(Plane rect : planes)
			rect.render(camera);
	}
	
	public Square getSquareAt(int x, int y) {
		return squares[y * width + x];
	}
	
	public enum Square {
		NONE, NORMAL, FORGE, GEM, CONSUMABLE;
	}
}
