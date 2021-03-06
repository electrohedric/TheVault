package guis;

import constants.Mode;
import constants.Textures;
import guis.elements.Button;
import main.Main;

public class TitleScreen extends Gui {
	
	private static TitleScreen instance;
	
	private TitleScreen() {
		super(Textures.get("shaded"));
		elements.add(new Button(Main.WIDTH * 0.5f, Main.HEIGHT * 0.6f, 0.15f, Textures.get("button_newgame"), Mode.TITLE, true, () ->  {
			SetupScreen.getInstance().switchTo();
		}));
		elements.add(new Button(Main.WIDTH * 0.5f, Main.HEIGHT * 0.5f, 0.15f, Textures.get("button_continue"), Mode.TITLE, true, () ->  {
			SetupScreen.getInstance().switchTo();
		}));

		instance = this;
	}
	
	public static TitleScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new TitleScreen();
	}
	
	@Override
	public void update() {
		super.update();
	}

	@Override
	public void switchTo() {
		Main.mode = Mode.TITLE;
		
	}

}
