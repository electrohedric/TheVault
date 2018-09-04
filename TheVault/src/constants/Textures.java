package constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gl.Texture;
import objects.Surface;
import util.Log;

public class Textures {
	
	public static List<Surface> allTextures = new ArrayList<>(); // handled within Surface
	public static Map<String, Texture> list = new HashMap<>();
	private static Texture devNull;
	
	private static void addTexture(String name) {
		list.put(name, new Texture(name + ".png"));
	}
	
	public static void init() {
		Texture.setLocalPath("misc/");
			devNull = new Texture("devnull.png");
			addTexture("haze_purple");
			addTexture("haze_aqua");
		Texture.setLocalPath("pieces/");
			addTexture("pawn_red");
			addTexture("pawn_blue");
			addTexture("pawn_green");
			addTexture("pawn_yellow");
		Texture.setLocalPath("backgrounds/");
			addTexture("shaded");
		Texture.setLocalPath("elements/");
			addTexture("button");
			addTexture("button_newgame");
			addTexture("button_continue");
			addTexture("button_next");
			addTexture("button_back");
	}
	
	public static void destroy() {
		for(Surface s : allTextures)
			s.delete();
	}
	
	public static Texture get(String name) {
		if(list.containsKey(name))
			return list.get(name);
		else
			Log.warn("Could not find texture '" + name + "' !");
		return devNull;
	}
	
}
