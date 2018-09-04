package objects;

import constants.Textures;
import gl.Texture;
import main.Game;
import util.Animator;
import util.Mouse;

public class Pawn extends GameObject {

	public boolean grabbed;
	private boolean grabable;
	private Color color;
	private Tile onTile;
	
	public Pawn(float x, float y, float scale, Color color) {
		super(x, y, 0.0f, scale);
		this.grabbed = false;
		this.grabable = false;
		this.color = color;
		this.onTile = null;
		setActiveTexture(color.texture);
	}
	
	public void update() {
		if(grabbed) {
			x = Mouse.x;
			y = Mouse.y;
		}
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
	
	public Tile getTile() {
		return onTile;
	}
	
	public void placeOnTile(Tile t) {
		
		switch(Game.mode) {
		case PLAY:
			
			break;
		case SETUP:
			// FIXME check if the square is a valid starting square or if another pawn has already taken it. if valid add to order list
			break;
		default:
			break;
		}
		setGrabable(false); // disable grabbing until animation has finished
		Animator.lerp(this, t.getAbsoluteX(Game.map.getCamera()), t.getAbsoluteY(Game.map.getCamera()), 0.1f, () -> {
			setGrabable(true);
		});
		onTile = t;
	}
	
	public void removeFromTile() {
		switch(Game.mode) {
		case PLAY:
			
			break;
		case SETUP:
			// FIXME remove from place order list
			break;
		default:
			break;
		}
		onTile = null;
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
