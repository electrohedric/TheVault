package guis;

import main.Main;
import objects.GameObject;
import objects.Surface;

public class Screen extends GameObject {

	public Screen(Surface texture, float x, float y, float scale) {
		super(x, y, 0, scale);
		setActiveTexture(texture);
	}
	
	public Screen(Surface texture) {
		this(texture, Main.WIDTH / 2, Main.HEIGHT / 2, 1.0f);
	}
	
}
