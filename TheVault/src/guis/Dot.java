package guis;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import constants.Shaders;
import gl.Shader;
import objects.Point;
import staindev.Game;
import util.Camera;

public class Dot {
	
	private float x;
	private float y;
	private float size;
	private Vector4f color;
	
	private Shader program;
	
	// preallocations
	private Matrix4f proj = new Matrix4f();
	private Matrix4f model = new Matrix4f();
	private Matrix4f mvp = new Matrix4f();
	
	/**
	 * Creates a Point with a size
	 * @param x X of point 1
	 * @param y Y of point 1
	 * @param size Diameter of point
	 * @param color Color to color the color in normalized RGBA
	 */
	public Dot(float x, float y, float size, int r, int g, int b, int a) {
		setPos(x, y);
		this.size = size;
		this.color = new Vector4f();
		setColor(r, g, b, a);
		this.program = Shaders.COLOR;
	}

	public void render(Camera camera) {
		program.bind();
		Point.bind(); // binds the VAO
		program.set("u_Color", color);
		// just translate
		proj.set(Game.proj);
		model = model.translation(x + 0.5f - camera.x, y - camera.y, 0);
		mvp = proj.mul(model); // M x V x P
		program.set("u_MVP", mvp);
		glPointSize(size);
		glDrawElements(GL_POINTS, Point.ibo.length, GL_UNSIGNED_INT, 0);
	}
	
	public void setPos(float x1, float y1) {
		x = x1;
		y = y1;
	}
	
	public void setColor(int r, int g, int b, int a) {
		color.set(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}
	
}
