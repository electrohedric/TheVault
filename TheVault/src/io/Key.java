package io;

import static org.lwjgl.glfw.GLFW.*;

import main.Main;

public class Key {
	
	public static boolean down(int key) {
		return glfwGetKey(Main.window, key) == 1;
	}
}
