package constants;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

public class Sounds {
	
	static long device;
	static long context;
	
	public static List<Integer> buffers = new ArrayList<>();
	
	public static void init() {
		String defaultDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDevice);
		context = alcCreateContext(device, new int[] {0});
		alcMakeContextCurrent(context);
		ALCCapabilities alc = ALC.createCapabilities(device);
		AL.createCapabilities(alc);
		alListener3f(AL_POSITION, 0, 0, 0);
		alListener3f(AL_VELOCITY, 0, 0, 0);
		
		// initialize all of our game's sounds here, not need for them to belong to a particular class, this game isn't too big
		
	}
	
	public static void destroy() {
		for(int buf : buffers)
			alDeleteBuffers(buf);
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
}
