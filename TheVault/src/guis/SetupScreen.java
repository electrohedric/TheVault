package guis;

import java.util.ArrayList;
import java.util.List;

import constants.Mode;
import constants.Textures;
import gl.Texture;
import guis.elements.Button;
import main.Game;
import objects.GameObject;
import objects.Pawn;
import objects.Pawn.Color;
import objects.Tile;
import util.Animator;
import util.Log;

public class SetupScreen extends Gui {
	
	private static SetupScreen instance;
	
	private List<Button> elementsPawnSelection;
	private List<Button> elementsPlacement;
	private float pawnFinalY;
	private float pawnInitialY;
	private SetupMode setupMode;
	
	/** Easy recognition for which pawns to add **/
	private List<String> pawnStringsChosen;
	/** Buttons that need to animate on placement mode **/
	private List<Button> pawnButtonsChosen;
	private float halfDistance;
	private Tile[] validStartingTiles;
	private GameObject[] possibleSquares;
	
	/** red background for pawns that are not included **/
	private Plane outPlane;
	/** green background for pawns that are included **/
	private Plane inPlane;
	
	private SetupScreen() {
		super(Textures.get("shaded"));
		
		this.elementsPawnSelection = new ArrayList<>();
		this.elementsPlacement = new ArrayList<>();
		this.pawnStringsChosen = new ArrayList<>();
		this.pawnButtonsChosen = new ArrayList<>();
		this.validStartingTiles = new Tile[] {
				Game.map.getTileAt(7, 4),
				Game.map.getTileAt(7, 10),
				Game.map.getTileAt(4, 7),
				Game.map.getTileAt(10, 7)};
		this.possibleSquares = new GameObject[validStartingTiles.length];
		
		this.pawnFinalY = Game.HEIGHT * 0.5f; // down position
		this.setupMode = SetupMode.PAWN_SELECTION;
		this.pawnInitialY = Game.HEIGHT * 0.8f; // up position
		float pawnButtonSize = 0.1f;
		float pawnFinalSize = 0.045f; // size after shrinking from selection -> placing mode
		float pawnButtonLeftX = Game.WIDTH * 0.2f; // selection screen left button position
		float pawnButtonRightX = Game.WIDTH * 0.8f; // selection screen right button position
		float pawnButtonDX = (pawnButtonRightX - pawnButtonLeftX) / 3; // 4 buttons = 3 spaces
		this.halfDistance = Math.abs(pawnInitialY - pawnFinalY) / 2;
		
		// create pawn buttons and set up callbacks to animate them
		Button b_red = new Button(pawnButtonLeftX + pawnButtonDX * 0, pawnInitialY, pawnButtonSize, Textures.get("pawn_red"), Mode.SETUP, false, null);
		b_red.setCallback(() -> {
			doCallBack(b_red, "red");
		});
		Button b_blue = new Button(pawnButtonLeftX + pawnButtonDX * 1, pawnInitialY, pawnButtonSize, Textures.get("pawn_blue"), Mode.SETUP, false, null);
		b_blue.setCallback(() -> {
			doCallBack(b_blue, "blue");
		});
		Button b_green = new Button(pawnButtonLeftX + pawnButtonDX * 2, pawnInitialY, pawnButtonSize, Textures.get("pawn_green"), Mode.SETUP, false, null);
		b_green.setCallback(() -> {
			doCallBack(b_green, "green");
		});
		Button b_yellow = new Button(pawnButtonLeftX + pawnButtonDX * 3, pawnInitialY, pawnButtonSize, Textures.get("pawn_yellow"), Mode.SETUP, false, null);
		b_yellow.setCallback(() -> {
			doCallBack(b_yellow, "yellow");
		});
		// add separately so we can toggle different sections of elements
		elementsPawnSelection.add(b_red);
		elementsPawnSelection.add(b_blue);
		elementsPawnSelection.add(b_green);
		elementsPawnSelection.add(b_yellow);
		
		int centerY = (int) (Game.HEIGHT * 0.85f);
		int distance = (int) (Game.WIDTH * 0.04f); // space between pawns
		
		 // NEXT to pawn placement
		elementsPawnSelection.add(new Button(Game.WIDTH * 0.88f, Game.HEIGHT * 0.3f, 0.2f, Textures.get("button_next"), Mode.SETUP, true, () -> {
			if(pawnButtonsChosen.size() < 2) {
				Log.log("You must have at least 2 players");  // TODO add sound for this
				return;
			}
			setupMode = SetupMode.PLACEMENT;
			elements.clear(); // clear visual elements
			elements.addAll(elementsPlacement); // load elements for next page
			for(Button b : elementsPawnSelection) // disable previous page buttons
				b.disable();
			for(Button b : elementsPlacement) // enable next page buttons
				b.enable();
			int centerX = (int) (Game.WIDTH * 0.03f); // on placement screen this is the button x after animated top-leftward;
			for(Button b : pawnButtonsChosen) { // create pawn for each button and animate the pawns to the side of the screen
				Pawn pawn = new Pawn(b.x, b.y, pawnButtonSize, Color.fromTexture((Texture) b.getActiveTexture()));
				Game.pawns.add(pawn);
				Animator.lerp(pawn, centerX, centerY, 0.1f, () -> {
					pawn.setGrabable(true); // once the animation finishes, allow grabbing
				});
				Animator.lerpSize(pawn, pawnFinalSize, 0.1f, null);
				centerX += distance;
			}
		}));
		
		// git gud!
		// git: 'gud!' is not a git command. See 'git --help'.
		
		 // BACK to pawn selection
		elementsPlacement.add(new Button(Game.WIDTH * 0.12f, Game.HEIGHT * 0.3f, 0.2f, Textures.get("button_back"), Mode.SETUP, true, () -> {
			setupMode = SetupMode.PAWN_SELECTION;
			elements.clear(); // kill rendering elements
			Game.pawns.clear(); // kill pawns
			elements.addAll(elementsPawnSelection);
			for(Button b : elementsPlacement)
				b.disable();
			for(Button b : elementsPawnSelection)
				b.enable();
		}));
		elementsPlacement.add(new Button(Game.WIDTH * 0.88f, Game.HEIGHT * 0.3f, 0.2f, Textures.get("button_startgame"), Mode.SETUP, true, () -> {
			for(Pawn p : Game.pawns)
				if(p.getTile() == null) return; // if any pawn does not have a tile, then don't move on
			// a pawn will only be able to be on a tile if it's on a valid square, so we don't have to worry about that
			PlayScreen.getInstance().switchTo();
		}));
		
		for(int i = 0; i < validStartingTiles.length; i++) {
			Tile t = validStartingTiles[i];
			possibleSquares[i] = Game.map.createHighlight(t, Textures.get("haze_purple")); // add all the purple hazes here so they know where they can place their pieces
		}

		// add red and green rects
		outPlane = new Plane((pawnButtonLeftX + pawnButtonRightX) / 2, pawnInitialY, Game.WIDTH, (pawnButtonSize + 0.05f) * Game.HEIGHT, 117, 0, 0, 127);
		inPlane = new Plane((pawnButtonLeftX + pawnButtonRightX) / 2, pawnFinalY, Game.WIDTH, (pawnButtonSize + 0.05f) * Game.HEIGHT, 0, 117, 0, 127);
		elements.addAll(elementsPawnSelection);

		instance = this;
	}
	
	private void doCallBack(Button b, String name) {
		b.disable();
		if(Math.abs(pawnFinalY - b.y) > halfDistance) { // we're in the up position, so animate it do the down position
			Animator.lerp(b, b.x, pawnFinalY, 0.2f, () -> {
				b.enable(); // when done animating, enable the button
			});
			pawnStringsChosen.add(name);
			pawnButtonsChosen.add(b);
		} else { // and vice-versa
			Animator.lerp(b, b.x, pawnInitialY, 0.2f, () -> {
				b.enable();
			});
			pawnStringsChosen.remove(name);
			pawnButtonsChosen.remove(b);
		}
	}
	
	public static SetupScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new SetupScreen();
	}
	
	public Tile[] getValidStartingTiles() {
		return validStartingTiles;
	}
	
	@Override
	public void update() {
		super.update();
		switch(setupMode) {
		case PAWN_SELECTION:
			
			break;
		case PLACEMENT:
			for(Pawn p : Game.pawns)
				p.update();
			break;
		default:
			break;
		
		}
		
	}
	
	@Override
	public void render() {
		switch(setupMode) {
		case PAWN_SELECTION:
			super.renderBackground();
			outPlane.render();
			inPlane.render();
			super.renderElements();
			break;
		case PLACEMENT:
			super.renderBackground();
			Game.map.render();
			for(GameObject haze : possibleSquares)
				haze.render(Game.map.getCamera());
			super.renderElements();
			for(Pawn p : Game.pawns)
				p.render();
			break;
		default:
			break;
		}
	}

	@Override
	public void switchTo() {
		Game.mode = Mode.SETUP;
	}

	private enum SetupMode {
		PAWN_SELECTION, PLACEMENT
	}
}
