package guis;

import main.Game;
import objects.GameObject;
import objects.Surface;

public class Screen extends GameObject {

	public Screen(Surface texture, float x, float y, float scale) {
		super(x, y, 0, scale);
		setActiveTexture(texture);
	}
	
	public Screen(Surface texture) {
		this(texture, Game.WIDTH / 2, Game.HEIGHT / 2, 1.0f);
	}
	
}
