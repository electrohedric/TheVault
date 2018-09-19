package util.handling;

import java.util.ArrayList;
import java.util.List;

import constants.Mode;
import constants.Textures;
import io.ClickListener;
import main.Main;
import objects.GameObject;
import objects.Map.Square;
import objects.Pawn;
import objects.Tile;

/**
 * Class to handle clicks for all pawns in all needed modes
 */
public class PawnHandler implements ClickListener {

	public static PawnHandler instance;
	public List<Pawn> turnOrder;
	private List<Tile> moveableTiles;
	private List<GameObject> highlights;
	private int currentTurn;
	private boolean[][] visited; // for highlighting tiles we can move to
	
	public PawnHandler() {
		this.turnOrder = new ArrayList<>();
		this.moveableTiles = new ArrayList<>();
		this.highlights = new ArrayList<>();
		this.currentTurn = 0;
		this.visited = new boolean[Main.map.getHeight()][Main.map.getWidth()];
		
		ClickListener.addToCallback(this, Mode.SETUP);
		ClickListener.addToCallback(this, Mode.PLAY);
		
		instance = this;
	}
	
	public static PawnHandler getInstance() {
		if(instance != null)
			return instance;
		else
			return new PawnHandler();
	}
	
	@Override
	public void handleClick(int button) {
		for(Pawn p : Main.pawns) {
			if(p.canGrab() && p.AACollideMouse()) { // if clicked on, grab it
				p.grabbed = true;
				p.removeFromTile();
				return; // max of 1 pawn at a time will be grabbed
				// TODO make this grab the closest pawn to it and possibly bring it the front of the rendering
			}
		}
	}

	@Override
	public void handleRelease(int button) {
		for(Pawn p : Main.pawns) { // release all pawns
			if(p.grabbed) {
				p.grabbed = false;
				// try to gridlock this pawn that was dropped to the square we're on
				Tile mouseTile = Main.map.getTileAtMouse();
				if(mouseTile != null)
					p.placeOnTile(mouseTile);
				return; // should only be one anyway
			}
		}
	}
	
	/**
	 * Progresses to the next player's turn and sets up their turn
	 */
	public void nextTurn() {
		getPawn().finishTurn(); // finish up this pawns turn
		currentTurn = (currentTurn + 1) % turnOrder.size(); // go to next player
		getPawn().resetTurn(); // prepare next pawn for play
	}
	
	/**
	 * @return The Pawn whose turn it is
	 */
	public Pawn getPawn() {
		return turnOrder.get(currentTurn);
	}
	
	/**
	 * Recursive function which calculates and adds all tiles within distance <code>d</code> the pawn can move to at initial tile <code>tile</code>
	 * @param tile Current tile from which to calculate.
	 * @param d Distance left. 0 means no surrounding tiles will be added, only the current tile
	 */
	private void setMoveableSurrounding(Tile tile, int d) {
		if(d > 0) {
			int currentX = tile.getGridX();
			int currentY = tile.getGridY();
			for(int y = currentY - 1; y <= currentY + 1; y++) { // 3x3 box with this tile at the center
				for(int x = currentX - 1; x <= currentX + 1; x++) {
					Tile t = Main.map.getTileAt(x, y);
					if(t != null) { // but make sure we can move there and its not a tile we've visited
						if(!visited[y][x] && possibleMove(t)) {
							visited[y][x] = true; // visit the cell
							moveableTiles.add(t);
							highlights.add(Main.map.createHighlight(t, Textures.get("haze_aqua")));
						}
						setMoveableSurrounding(t, d - 1); // recurse from this tile with the distance left, only if not null
					}
				}
			}
		}
	}
	
	public void calculateMoveableTiles() {
		// reset moveable cell list
		for(int y = 0; y < visited.length; y++) {
			for(int x = 0; x < visited[y].length; x++) {
				visited[y][x] = false;
			}
		}
		moveableTiles.clear();
		highlights.clear();
		setMoveableSurrounding(getPawn().getTile(), getPawn().getMovesLeft());
		
	}
	
	/**
	 * Determines whether or not this tile could theoretically be moved to.
	 * @param t Tile to test movement to
	 * @return <code>true</code> if the tile is not NONE and no other player is standing on that tile.
	 */
	private boolean possibleMove(Tile t) {
		Pawn currentPawn = getPawn();
		for(Pawn p : Main.pawns)
			if(p.getTile() == t && p != currentPawn)
				return false;
		return t.getType() != Square.NONE;
	}
	
	public boolean canMoveThere(Tile t) {
		for(Tile canMove : moveableTiles)
			if(t == canMove) return true; // if the tile is in moveable, then yes, they can move there.
		return false;
	}
	
	public void renderPawns() {
		for(Pawn p : Main.pawns)
			p.render();
	}
	
	public void renderMoveableTileHighlights() {
		for(GameObject highlight : highlights)
			highlight.render();
	}
}
