package objects;

import static org.lwjgl.opengl.GL11.*;

import constants.Textures;

/**
 * Basically just a tie between Animation and Texture
 */
public abstract class Surface {
	
	public Surface() {
		Textures.allTextures.add(this);
	}
	
	public abstract void bind(int slot);
	public abstract void bind();
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public abstract void delete();
	
	public abstract int getWidth();
	public abstract int getHeight();
	
	public abstract float getOffsetX();
	public abstract float getOffsetY();
	public abstract float getOffsetRot();
}
