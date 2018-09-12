package objects;

import constants.Mode;
import constants.Sounds;
import constants.Textures;
import gl.Texture;
import guis.PlayScreen;
import guis.SetupScreen;
import main.Game;
import util.animation.Animator;
import util.handling.PawnHandler;
import util.input.Mouse;

/**
 * This Game's equivalent of a Player
 */
public class Pawn extends GameObject {

	public boolean grabbed;
	private boolean grabable;
	private Color color;
	private Tile onTile;
	private GameObject ghost;
	// TODO add cards
	
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
		this.ghost = new GameObject(0, 0, 0, 0); // not ready for rendering yet until resetTurn
		ghost.setActiveTexture(color.texture); // TODO instead of scaling down, decrease alpha
		
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
	
	public int getMovesLeft() {
		return movesLeft;
	}

	public int getActionsLeft() {
		return actionsLeft;
	}

	@Override
	public void render() {
		if(grabbed && Game.mode == Mode.PLAY) {
			PawnHandler.getInstance().renderMoveableTileHighlights();
			ghost.render();
		}
		super.render(); // render the pawn after the ghost after the highlights
	}
	
	private void setGhostHere() {
		ghost.x = onTile.getAbsoluteX(Game.map.getCamera());
		ghost.y = onTile.getAbsoluteY(Game.map.getCamera());
	}
	
	/**
	 * Should be called when the turn is about to begin to reset for play
	 */
	public void resetTurn() {
		movesLeft = movesPerTurn;
		actionsLeft = actionsPerTurn;
		setGrabable(true);
		PawnHandler.getInstance().calculateMoveableTiles();
		setGhostHere();
		ghost.scale = scale * 0.8f; // can't do this in constructor because they move and resize
		PlayScreen.getInstance().showCorrespondingButton(onTile); // reshow the tile if they're on one
		// TODO some display that lets people know it's their turn
	}
	
	public void finishTurn() {
		setGrabable(false);
		PlayScreen.getInstance().hideActionButtons();
	}
	
	public void placeOnTile(Tile t) {
		switch(Game.mode) {
		case PLAY:
			if(PawnHandler.getInstance().canMoveThere(t)) {
				PlayScreen.getInstance().showCorrespondingButton(t);
				PlayScreen.getInstance().getDoneButton().enable();
			} else return;
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
			PawnHandler.getInstance().turnOrder.add(0, this); // insert at the beginnning to do a live reverse
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
			PlayScreen.getInstance().hideActionButtons();
			PlayScreen.getInstance().getDoneButton().disable();
			break;
		case SETUP:
			PawnHandler.getInstance().turnOrder.remove(this);
			break;
		default:
			break;
		}
		onTile = null;
	}
	
	public GameObject getGhost() {
		return ghost;
	}
	
	@Override
	public String toString() {
		return String.format("Pawn: color=%s, tile=[%s]", color, getTile());
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
