package gl;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	
	public static void setClearColor(int r, int g, int b) {
		glClearColor(r / 255.0f, g / 255.0f, b / 255.0f, 0); // probably doesn't matter
	}
}
