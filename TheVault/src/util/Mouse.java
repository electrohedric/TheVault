package util;

import static org.lwjgl.glfw.GLFW.*;

import staindev.Game;

public class Mouse {
	
	public static int x;
	public static int y;
	private static double[] xbuf = new double[1];
	private static double[] ybuf = new double[1];
	
	// give aliases to GLFW mouse buttons constants
	public static final int LEFT = GLFW_MOUSE_BUTTON_LEFT;
	public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
	public static final int MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;
	
	public static void getUpdate() {
		glfwGetCursorPos(Game.window, xbuf, ybuf);
		x = (int) xbuf[0];
		y = (int) (Game.HEIGHT - ybuf[0]);
	}
	
	public boolean isDown(int button) {
		return glfwGetMouseButton(Game.window, button) == 1;
	}
	
}
