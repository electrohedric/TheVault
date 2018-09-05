package objects;

import constants.Textures;
import gl.Texture;
import guis.SetupScreen;
import main.Game;
import util.Animator;
import util.Mouse;

/**
 * This Game's equivalent of a Player
 */
public class Pawn extends GameObject {

	public boolean grabbed;
	private boolean grabable;
	private Color color;
	private Tile onTile;
	private GameObject ghost;
	
	// these are all properties of the pawn per turn
	private int movesPerTurn;
	private int actionsPerTurn;
	
	// these are all current turn properties
	private int movesLeft;
	private int actionsLeft;
	
	public Pawn(float x, float y, float scale, Color color) {
		super(x, y, 0.0f, scale);
		this.grabbed = false;
		this.grabable = false;
		this.color = color;
		this.onTile = null;
		setActiveTexture(color.texture);
		this.ghost = null;
		
		this.movesPerTurn = 3;
		this.actionsPerTurn = 2;
		
		this.movesLeft = 0;
		this.actionsLeft = 0;
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
	
	@Override
	public void render() {
		super.render();
		if(ghost != null)
			ghost.render();
	}
	
	public void resetTurn() {
		movesLeft = movesPerTurn;
		actionsLeft = actionsPerTurn;
		// TODO create squares they can move to are highlighted using recursion on surrounding squares
	}
	
	public void placeOnTile(Tile t) {
		
		switch(Game.mode) {
		case PLAY:
			//FIXME next set an order of players so one player at a time can move
			break;
		case SETUP:
			// check if t is one of the starting tiles
			boolean startingSquare = false;
			for(Tile tile : SetupScreen.getInstance().getValidStartingTiles()) {
				if(t == tile) {
					startingSquare = true; // found this is a good tile
					break;
				}
			}
			if(startingSquare) {
				// now we must check there are no other players on this tile
				for(Pawn p : Game.pawns) {
					if(p != this && p.getTile() == t)
						return; // found another pawn on this tile. no good
				}
			} else return;
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
			// TODO when they pick up a pawn, a ghost is placed where their pawn was
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