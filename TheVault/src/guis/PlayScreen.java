package guis;

import constants.Mode;
import constants.Textures;
import gl.Texture;
import main.Game;
import util.Animation;

public class PlayScreen extends Gui {
	
	private static PlayScreen instance;
	
	public PlayScreen(Texture background) {
		super(background);
		
		instance = this;
	}
	
	public static PlayScreen getInstance() {
		if(instance != null)
			return instance;
		else
			return new PlayScreen(Textures.get("shaded"));
	}

	@Override
	public void switchTo() {
		Game.mode = Mode.PLAY;
	}
	
	@Override
	public void update() {
		PlayScreen.getInstance().update();
		for(int i = Animation.queue.size() - 1; i >= 0; i--)
			Animation.queue.get(i).update();
	}
	
	@Override
	public void render() {
		
	}
	
	public void loadMap() {
		
	}
	

}
