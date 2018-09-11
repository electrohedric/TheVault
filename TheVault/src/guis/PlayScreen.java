package guis;

import constants.Mode;
import constants.Textures;
import guis.elements.Button;
import main.Game;
import objects.Pawn;
import objects.Tile;
import util.Log;
import util.PawnHandler;

public class PlayScreen extends Gui {
	
	private static PlayScreen instance;
	private Button consumableButton;
	private Button forgeButton;
	private Button gemButton;
	private Button doneButton;
	// TODO do text rendering (mostly for cards.) not particularly in this class but just do it
	// FIXME NEXT add buttons that may pop up if they're on a square that can do an action
	// such stuff like forges would then bring up card lists to choose from.
	// Gem squares and such would add a button that when clicked gives them a random card
	
	public PlayScreen() {
		super(Textures.get("shaded"));
		
		int actionBX = (int) (Game.WIDTH * 0.13f);
		int actionBY = (int) (Game.HEIGHT * 0.85f);
		float actionBScale = 0.25f;
		this.consumableButton = new Button(actionBX, actionBY, actionBScale, Textures.get("button_drawconsumable"), Mode.PLAY, true, () -> {
			// TODO add cards here
		});
		this.forgeButton = new Button(actionBX, actionBY, actionBScale, Textures.get("button_useforge"), Mode.PLAY, true, () -> {
			
		});
		this.gemButton = new Button(actionBX, actionBY, actionBScale, Textures.get("button_drawgem"), Mode.PLAY, true, () -> {
			
		});
		this.doneButton = new Button(Game.WIDTH * 0.88f, Game.HEIGHT * 0.1f, 0.15f, Textures.get("button_endturn"), Mode.PLAY, true, () -> {
			PawnHandler.getInstance().nextTurn();
		});
		consumableButton.disable();
		forgeButton.disable();
		gemButton.disable();
		instance = this;
	}
	
	public static PlayScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new PlayScreen();
	}

	@Override
	public void switchTo() {
		Game.mode = Mode.PLAY;
		for(Pawn p : Game.pawns) // disable all pawns initially 
			p.setGrabable(false);
		PawnHandler.getInstance().getPawn().resetTurn(); // enable first pawn to act
	}
	
	@Override
	public void update() {
		for(Pawn p : Game.pawns)
			p.update();
		consumableButton.update();
		forgeButton.update();
		gemButton.update();
		doneButton.update();
	}
	
	public Button getConsumableButton() {
		return consumableButton;
	}

	public Button getForgeButton() {
		return forgeButton;
	}

	public Button getGemButton() {
		return gemButton;
	}
	
	public Button getDoneButton() {
		return doneButton;
	}

	public void showCorrespondingButton(Tile t) {
		if(t == null) {
			Log.err("Tried to show button, but tile was null... this shouldn't happen.");
			return;
		}
		switch(t.getType()) { // enable the cooresponding button which allows them to do the action of the square they're standing on
		case CONSUMABLE:
			// TODO check they can actually hold the card
			consumableButton.enable();
			break;
		case FORGE:
			// TODO check they have enough cards
			forgeButton.enable();
			break;
		case GEM:
			// TODO check they can hold the card
			gemButton.enable();
			break;
		default:
			break;
		}
	}
	
	public void hideActionButtons() {
		consumableButton.disable();
		forgeButton.disable();
		gemButton.disable();
		doneButton.disable();
	}

	private void conditionalRender(Button b) {
		if(b.isEnabled())
			b.render();
	}
	
	@Override
	public void render() {
		super.renderBackground();
		Game.map.render();
		PawnHandler.getInstance().renderPawns();
		conditionalRender(consumableButton); // render only if they are enabled, making it easy to see what's available
		conditionalRender(forgeButton);
		conditionalRender(gemButton);
		doneButton.render();
	}
	
}
