package objects;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

import constants.Shaders;
import gl.Shader;
import io.Mouse;
import main.Main;
import util.handling.Camera;
import util.handling.Collision;

// TODO this should probably be abstract eventually
public class GameObject {
	// assuming to be rectangle so we can use the Rect class for our buffers
	
	public float x;
	public float y;
	
	/** In radians **/
	public float rot;
	public float scale;
	/** changes brightness of texture. 0 is normal. 1 is fully white. -1 is fully black */
	public float brightScale;
	
	private int slot;
	private Shader program;
	private Surface activeTexture;
	
	// Preallocations
	private Matrix4f proj = new Matrix4f();
	private Matrix4f model = new Matrix4f();
	private Matrix4f mvp = new Matrix4f();
	
	/**
	 * Initializes a Game Object which is specifically only a textured quad
	 * @param x Initial X
	 * @param y Initial Y
	 * @param rot Initial rotation, in radians
	 * @param scale Scale of texture in relation to screen size. (e.g. 0.5 means no dimension of the texture will take up
	 *              more than 50% of its corresponding screen dimension)
	 */
	public GameObject(float x, float y, float rot, float scale) {
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.scale = scale;
		this.brightScale = 0.0f; // normal color
		this.slot = 0;
		this.program = Shaders.TEXTURE;
		this.activeTexture = null;
	}
	
	/** 
	 * Renders the <code>activeTexture</code> to the screen using its properties and this <code>GameObject</code>'s position.
	 * <code>null</code> is a valid <code>activeTexture</code> which renders nothing.
	 * */
	public void render(Camera camera) {
		if(activeTexture != null) {
			program.bind();
			activeTexture.bind(slot);
			Rect.bind(); // binds the VAO
			proj.set(Main.proj);
			float trueScale = getTrueScale();
			model = model.
					translation(x - camera.x, y - camera.y, 0).
					rotate(rot - activeTexture.getOffsetRot(), 0.0f, 0.0f, 1.0f).
					scale(trueScale, trueScale, 1.0f).
					translate(-activeTexture.getOffsetX(), activeTexture.getOffsetY(), 0).
					scale(activeTexture.getWidth(), activeTexture.getHeight(), 1.0f);
			mvp = proj.mul(model); // M x V x P
			
			//uniforms
			program.set("u_Texture", slot);
			program.set("u_MVP", mvp);
			program.set("u_BrightScale", brightScale);
			
			glDrawElements(GL_TRIANGLES, Rect.ibo.length, GL_UNSIGNED_INT, 0);
		}
	}
	
	/**
	 * Renders using absolute positioning
	 */
	public void render() {
		render(Main.nullCamera);
	}

	public void setActiveTexture(Surface activeTexture) {
		this.activeTexture = activeTexture;
	}
	
	public Surface getActiveTexture() {
		return activeTexture;
	}
	
	/**
	 * Calculates the true scale of an object with the current texture scale being proportional to the screen size.
	 * @return the corrected scale in number x the actual scale texture
	 */
	public float getTrueScale() {
		if(activeTexture != null) {
			float scaledWidthRatio = Main.WIDTH * scale / activeTexture.getWidth();
			float scaledHeightRatio = Main.HEIGHT * scale / activeTexture.getHeight();
			return Math.min(scaledWidthRatio, scaledHeightRatio);
		}
		return 0.0f;
	}
	
	/**
	 * Collides this GameObject with a point
	 * <p> *NOTE* <br>If the GameObject is not Axis-Aligned (i.e. rotation is not 0), results may be futile </p>
	 * @return <code>true</code> if the given point collides with this
	 */
	public boolean AACollideMouse() {
		return Collision.pointCollideAAGameObject(Mouse.x, Mouse.y, this);
	}
	
}
