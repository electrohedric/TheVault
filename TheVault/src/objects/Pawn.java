package objects;

import constants.Mode;
import constants.Textures;
import gl.Texture;
import util.ClickListener;
import util.Mouse;

public class Pawn extends GameObject implements ClickListener {

	private boolean grabbed;
	private boolean grabable;
	private Color color;
	
	public Pawn(float x, float y, float scale, Color color) {
		super(x, y, 0.0f, scale);
		this.grabbed = false;
		this.grabable = false;
		this.color = color;
		setActiveTexture(color.texture);
		
		ClickListener.addToCallback(this, Mode.SETUP);
		ClickListener.addToCallback(this, Mode.PLAY);
	}
	
	public void update() {
		if(grabbed) {
			x = Mouse.x;
			y = Mouse.y;
		}
	}

	@Override
	public void handleClick(int button) {
		if(grabable && AACollideMouse()) // if clicked on, grab it
			grabbed = true;
	}

	@Override
	public void handleRelease(int button) {
		grabbed = false;
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean canGrab() {
		return grabable;
	}
	
	public void setGrabable(boolean grabable) {
		this.grabable = grabable;
		if(!grabable) // if not grabable anymore, drop it
			grabbed = false;
	}
	
	public static enum Color {		
		RED("pawn_red"), GREEN("pawn_green"), BLUE("pawn_blue"), YELLOW("pawn_yellow");
		
		private Texture texture;
		
		private Color(String textureName) {
			this.texture = Textures.get(textureName);
		}
		
		public static Color fromTexture(Texture texture) {
			for(Color c : Color.values())
				if(c.texture == texture)
					return c;
			return null;
		}
	}

}
