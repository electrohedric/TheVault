package guis;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

import gl.Texture;
import guis.elements.Button;
import main.Game;
import util.Cursors;

public abstract class Gui {

	Screen background;
	List<Button> elements; // TODO should be an element list for elements, not just buttons
	private static long currentCursor = Cursors.POINTER;
	
	public Gui(Texture background) {
		this.background = new Screen(background);
		this.elements = new ArrayList<>();
	}

	public void update() {
		for(Button b : elements)
			b.update();
	}
	
	protected void renderBackground() {
		background.render();
	}
	
	protected void renderElements() {
		for(Button b : elements)
			b.render();
	}
	
	public void render() {
		renderBackground();
		renderElements();
	};
	
	protected static void setMousePointer(long cursor) {
		glfwSetCursor(Game.window, cursor);
	}
	
	protected static long getMousePointer() {
		return currentCursor;
	}
	
	public abstract void switchTo();
	
}
