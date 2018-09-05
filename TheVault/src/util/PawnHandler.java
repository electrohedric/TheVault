package util;

import constants.Mode;
import main.Game;
import objects.Pawn;
import objects.Tile;

/**
 * Class to handle clicks for all pawns in all needed modes
 */
public class PawnHandler implements ClickListener {

	public static PawnHandler instance;
	
	public PawnHandler() {
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
		for(Pawn p : Game.pawns) {
			if(p.canGrab() && p.AACollideMouse()) { // if clicked on, grab it
				p.grabbed = true;
				p.removeFromTile();
				return; // max of 1 pawn at a time will be grabbed
			}
		}
	}

	@Override
	public void handleRelease(int button) {
		for(Pawn p : Game.pawns) { // release all pawns
			if(p.grabbed) {
				p.grabbed = false;
				// try to gridlock this pawn that was dropped to the square we're on
				Tile mouseTile = Game.map.getTileAtMouse();
				if(mouseTile != null)
					p.placeOnTile(mouseTile);
				return; // should only be one anyway
			}
		}
	}
}
