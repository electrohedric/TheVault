package guis;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import constants.Shaders;
import gl.Shader;
import main.Game;
import objects.Rect;
import util.Camera;

//TODO make this implement a Renderable class which has the matrices, program, slot, x, and y set up, and render() abstract method
public class Plane {
	
	private float cx;
	private float cy;
	private float width;
	private float height;
	private Vector4f defaultColor;
	private Vector4f color;
	
	private Shader program;
	
	// preallocations
	private Matrix4f proj = new Matrix4f();
	private Matrix4f model = new Matrix4f();
	private Matrix4f mvp = new Matrix4f();
	
	/**
	 * Creates a plane with a color
	 * @param cx Center X
	 * @param cy Center Y
	 * @param width Width
	 * @param height Height
	 * @param r Red 0-255
	 * @param g Green 0-255
	 * @param b Blue 0-255
	 * @param a Alpha 0-255. 0 is transparent. 255 is opaque
	 */
	public Plane(float cx, float cy, float width, float height, int r, int g, int b, int a) {
		this.cx = cx;
		this.cy = cy;
		this.width = width;
		this.height = height;
		this.color = new Vector4f();
		setColor(r, g, b, a);
		this.defaultColor = new Vector4f(color); // duplicate color
		this.program = Shaders.COLOR;
	}
	
	/**
	 * Like {@link #Plane(float, float, float, float, int, int, int, int) Segment} except takes in parameter color
	 * @param color Color using normalized rgba components
	 */
	public Plane(float cx, float cy, float width, float height, Vector4f color) {
		this.cx = cx;
		this.cy = cy;
		this.width = width;
		this.height = height;
		this.color = new Vector4f(color);
		this.defaultColor = new Vector4f(color); // duplicate color
		this.program = Shaders.COLOR;
	}

	/**
	 * Renders this segment to the screen using a camera
	 * @param camera
	 */
	public void render(Camera camera) {
		program.bind();
		Rect.bind(); // binds the VAO
		program.set("u_Color", color);
		// scale to length, rotate, then translate
		proj.set(Game.proj);
		model = model.translation(cx - camera.x, cy - camera.y, 0).scale(width, height, 1.0f);
		mvp = proj.mul(model); // M x V x P
		program.set("u_MVP", mvp);
		glDrawElements(GL_TRIANGLES, Rect.ibo.length, GL_UNSIGNED_INT, 0);
	}
	
	/**
	 * Renders this segment to the screen using absolute positioning
	 */
	public void render() {
		render(Game.nullCamera);
	}
	
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float width) {
		this.width = width;
	}

	public void setColor(int r, int g, int b, int a) {
		color.set(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}
	
	public void resetColor() {
		color.set(defaultColor);
	}
	
	/**
	 * @return <code>true</code> if this PLane has the same center x, center y, width, and height as the <strong>other</strong> does
	 */
	public boolean equals(Plane other) {
		return cx == other.cx && cy == other.cy && width == other.width && height == other.height;
	}
	
	public float getCX() {
		return cx;
	}

	public float getCY() {
		return cy;
	}

}
