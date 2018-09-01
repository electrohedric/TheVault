package util;

public class Camera {
	
	public float x;
	public float y;
	
	public Camera(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setMouseXY(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public int getMouseX() {
		return (int) (Mouse.x + x);
	}
	
	public int getMouseY() {
		return (int) (Mouse.y + y);
	}
	
}
