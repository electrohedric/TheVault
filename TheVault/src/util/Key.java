package util;

import static org.lwjgl.glfw.GLFW.*;

import main.Game;

public class Key {
	
	public static boolean down(int key) {
		return glfwGetKey(Game.window, key) == 1;
	}
}
