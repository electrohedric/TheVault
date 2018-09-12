package constants;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import util.output.Log;
import util.output.Sound;

public class Sounds {
	
	static long device;
	static long context;
	
	public static List<Integer> buffers = new ArrayList<>();
	public static Map<String, Sound> list = new HashMap<>();
	private static Sound nullSound;
	
	private static void addSound(String name) {
		list.put(name, new Sound(name + ".ogg"));
	}
	
	public static void init() {
		String defaultDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDevice);
		context = alcCreateContext(device, new int[] {0});
		alcMakeContextCurrent(context);
		ALCCapabilities alc = ALC.createCapabilities(device);
		AL.createCapabilities(alc);
		alListener3f(AL_POSITION, 0, 0, 0);
		alListener3f(AL_VELOCITY, 0, 0, 0);
		
		nullSound = new Sound("null.ogg"); // a 550 Hz square wave.
		// initialize all of our game's sounds here, not need for them to belong to a particular class, this game isn't too big
		addSound("up");
		addSound("down");
		
	}
	
	public static void play(String name) {
		get(name).play();
	}
	
	public static Sound get(String name) {
		if(list.containsKey(name))
			return list.get(name);
		Log.warn("Sound '" + name + "' does not exist!");
		return nullSound;
	}
	
	public static void destroy() {
		for(int buf : buffers)
			alDeleteBuffers(buf);
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
}
