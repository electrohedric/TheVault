package guis;

import java.util.ArrayList;
import java.util.List;

import constants.Mode;
import constants.Textures;
import guis.elements.Button;
import main.Game;
import util.ClickListener;
import util.Log;
import util.Mouse;

public class SetupScreen extends Gui implements ClickListener {
	
	private static SetupScreen instance;
	private List<Button> toAnimate;
	private List<Button> toUnanimate;
	private List<Button> elements_pawnSelection;
	private List<Button> elements_placement;
	private float animateFinalY;
	private float pawnButtonInitialY;
	private SetupMode setupMode;
	private List<String> pawnsChosen;
	private float halfDistance;
	
	private Plane outPlane;
	private Plane inPlane;
	
	private SetupScreen() {
		super(Textures.get("shaded"));
		
		this.toAnimate = new ArrayList<>();
		this.toUnanimate = new ArrayList<>();
		this.elements_pawnSelection = new ArrayList<>();
		this.elements_placement = new ArrayList<>();
		this.pawnsChosen = new ArrayList<>();
		
		this.animateFinalY = Game.HEIGHT * 0.5f;
		this.setupMode = SetupMode.PAWN_SELECTION;
		this.pawnButtonInitialY = Game.HEIGHT * 0.8f;
		float pawnButtonSize = 0.1f;
		float pawnButtonLeftX = Game.WIDTH * 0.2f;
		float pawnButtonRightX = Game.WIDTH * 0.8f;
		float pawnButtonDX = (pawnButtonRightX - pawnButtonLeftX) / 3; // 4 buttons = 3 spaces
		this.halfDistance = Math.abs(pawnButtonInitialY - animateFinalY) / 2;
		
		Button b_red = new Button(pawnButtonLeftX + pawnButtonDX * 0, pawnButtonInitialY, pawnButtonSize, Textures.get("pawn_red"), Mode.SETUP, false, null);
		b_red.setCallback(() -> {
			doCallBack(b_red, "red");
		});
		Button b_blue = new Button(pawnButtonLeftX + pawnButtonDX * 1, pawnButtonInitialY, pawnButtonSize, Textures.get("pawn_blue"), Mode.SETUP, false, null);
		b_blue.setCallback(() -> {
			doCallBack(b_blue, "blue");
		});
		Button b_green = new Button(pawnButtonLeftX + pawnButtonDX * 2, pawnButtonInitialY, pawnButtonSize, Textures.get("pawn_green"), Mode.SETUP, false, null);
		b_green.setCallback(() -> {
			doCallBack(b_green, "green");
		});
		Button b_yellow = new Button(pawnButtonLeftX + pawnButtonDX * 3, pawnButtonInitialY, pawnButtonSize, Textures.get("pawn_yellow"), Mode.SETUP, false, null);
		b_yellow.setCallback(() -> {
			doCallBack(b_yellow, "yellow");
		});
		elements_pawnSelection.add(b_red);
		elements_pawnSelection.add(b_blue);
		elements_pawnSelection.add(b_green);
		elements_pawnSelection.add(b_yellow);
		elements_pawnSelection.add(new Button(Game.WIDTH * 0.88f, Game.HEIGHT * 0.3f, 0.2f, Textures.get("button_next"), Mode.SETUP, true, () -> { // next to pawn placement
			setupMode = SetupMode.PLACEMENT;
			elements.clear();
			elements.addAll(elements_placement);
			for(Button b : elements_pawnSelection)
				b.disable();
			for(Button b : elements_placement)
				b.enable();
		}));
		
		elements_placement.add(new Button(Game.WIDTH * 0.12f, Game.HEIGHT * 0.3f, 0.2f, Textures.get("button_back"), Mode.SETUP, true, () -> { // back to pawn selection
			setupMode = SetupMode.PAWN_SELECTION;
			elements.clear();
			elements.addAll(elements_pawnSelection);
			for(Button b : elements_placement)
				b.disable();
			for(Button b : elements_pawnSelection)
				b.enable();
		}));
		
		outPlane = new Plane((pawnButtonLeftX + pawnButtonRightX) / 2, pawnButtonInitialY, Game.WIDTH, (pawnButtonSize + 0.05f) * Game.HEIGHT, 117, 0, 0, 127);
		inPlane = new Plane((pawnButtonLeftX + pawnButtonRightX) / 2, animateFinalY, Game.WIDTH, (pawnButtonSize + 0.05f) * Game.HEIGHT, 0, 117, 0, 127);
		elements.addAll(elements_pawnSelection);
		ClickListener.addToCallback(this, Mode.SETUP);

		instance = this;
	}
	
	private void doCallBack(Button b, String name) {
		b.disable();
		if(Math.abs(animateFinalY - b.y) > halfDistance) { // we're not at the final position
			toAnimate.add(b);
			pawnsChosen.add(name);
		} else {
			toUnanimate.add(b);
			pawnsChosen.remove(name);
		}
	}
	
	public static SetupScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new SetupScreen();
	}
	
	@Override
	public void update() {
		super.update();
		switch(setupMode) {
		case PAWN_SELECTION:
			for(int i = toAnimate.size() - 1; i >= 0; i--) {
				Button b = toAnimate.get(i);
				if(Math.abs(animateFinalY - b.y) < 2) { // max threshold is 1 pixel before jumping to final location
					b.y = animateFinalY;
					toAnimate.remove(i); // this is why we iterate backwards
					b.enable();
				} else
					b.y += (animateFinalY - b.y) * 0.2f; // lerp by 20%
			}
			for(int i = toUnanimate.size() - 1; i >= 0; i--) {
				Button b = toUnanimate.get(i);
				if(Math.abs(pawnButtonInitialY - b.y) < 2) { // max threshold is 1 pixel before jumping to final location
					b.y = pawnButtonInitialY;
					toUnanimate.remove(i); // this is why we iterate backwards
					b.enable();
				} else
					b.y += (pawnButtonInitialY - b.y) * 0.2f; // lerp by 20%
			}
			break;
		case PLACEMENT:
			
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
			super.render();
			Game.map.render();
			break;
		default:
			break;
		}
	}

	@Override
	public void switchTo() {
		Game.mode = Mode.SETUP;
	}
	

	@Override
	public void handleClick(int button) {}

	@Override
	public void handleRelease(int button) {
		if(button == Mouse.LEFT) {
			Log.log(Game.map.getTileAtMouse());
		}
	}

	private enum SetupMode {
		PAWN_SELECTION, PLACEMENT
	}
}
