package util;

import static org.lwjgl.openal.AL10.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;

import constants.Resources;
import constants.Sounds;

public class Sound {
	
	protected int buffer;
	private int[] source;
	private float volume;
	public static float mixerVolume = 1.0f;
	
	/**
	 * Creates a new Sound which can be played with multiple players
	 * @param path Path of the sound relative to res/sounds. Must be an ogg file
	 * @param volume Default sound to play at. 1.0f is max volume. 0.0f is silent
	 * @param maxPlayers
	 */
	public Sound(String path, float volume, int maxPlayers) {
		IntBuffer channelsBuf = BufferUtils.createIntBuffer(1);
		IntBuffer rateBuf = BufferUtils.createIntBuffer(1);
		ShortBuffer audioBuf = STBVorbis.stb_vorbis_decode_filename(Resources.SOUNDS_PATH + path, channelsBuf, rateBuf);
		int channels = channelsBuf.get();
		int rate = rateBuf.get();
		this.volume = volume;
		
		int format = -1;
		if(channels == 1) {
		    format = AL_FORMAT_MONO16;
		} else if(channels == 2) {
		    format = AL_FORMAT_STEREO16;
		} else throw new IllegalArgumentException("Couldn't Identify number of channels for: " + path);
		
		this.buffer = alGenBuffers();
		alBufferData(buffer, format, audioBuf, rate);
		Sounds.buffers.add(buffer);
		
		if(maxPlayers < 0)
			throw new IllegalArgumentException("There must be at least one player: " + path);
		
		this.source = new int[maxPlayers];
		alGenSources(source);
		
		// specifically for more than one souce and no looping, but hey it works for everything.
		// it wouldn't make much sense to have the same sound playing on a loop and one not, so this works out
		for(int src : source) {
			alSourcef(src, AL_GAIN, volume * Sound.mixerVolume);
			alSourcef(src, AL_PITCH, 1.0f);
			alSource3f(src, AL_POSITION, 0, 0, 0);
			alSource3f(src, AL_VELOCITY, 0, 0, 0);
			alSourcei(src, AL_BUFFER, buffer);
		}
	}
	
	/**
	 * Calls Sound with volume 1, and 1 player. Good for sounds not intended to overlay or for music tracks.
	 * See constructor {@link #Sound(String, float, int) Sound}
	 */
	public Sound(String path) {
		this(path, 1.0f, 1);
	}
	
	/** Plays the sound in the buffer. If no player is available, no action is taken unless forced
	 * @param force if <code>true</code>, the sound will play even if no player is available
	 * @return <code>true</code> if the sound was able to be played
	 */
	private boolean play(boolean force) {
		boolean available = false;
		int nextSrc = 0;
		for(int src : source) {
			if(isStopped(src)) {
				nextSrc = src;
				available = true;
				break;
			}
		}
		if(available) { // if there's a slot available, use it
			playBuffer(nextSrc);
			return true;
		} else {
			if(force) // if we're forcing, play anyway
				playBuffer(source[0]);
			return force;
		}
	}
	
	/**
	 * Plays the sound in the buffer. Not forced.
	 * @return <code>true</code> if the sound was able to be played (i.e. maxPlayers not reached)
	 */
	public boolean play() {
		return play(false);
	}
	
	/**
	 * Plays the sound in the buffer. Forced.
	 * @return <code>true</code>
	 */
	public boolean forcePlay() {
		return play(true);
	}
	
	public void playBuffer(int src) {
		alSourcef(src, AL_GAIN, volume * Sound.mixerVolume);
		alSourcePlay(src);
	}
	
	public void pauseAll() {
		for(int src : source)
			if(isPlaying(src))
				alSourcePause(src);
	}
	
	public void unpauseAll() {
		for(int src : source)
			if(isPaused(src))
				alSourcePlay(src);
	}
	
	public void stopAll() {
//		for(int src : source)
//			if(!isStopped(src))
//				alSourceStop(src);
	}
	
	public void checkError() {
		Log.log(alGetError());
	}
	
	/** 
	 * @return <code>true</code> if and only if sound is playing. i.e. NOT paused and NOT stopped
	 */
	public boolean isPlaying(int src) {
		return alGetSourcei(src, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	/** 
	 * @return <code>true</code> if and only if sound is paused. i.e. NOT stopped and NOT playing
	 */
	public boolean isPaused(int src) {
		return alGetSourcei(src, AL_SOURCE_STATE) == AL_PAUSED;
	}
	
	/** 
	 * @return <code>true</code> if and only if sound is stopped or never used. i.e. NOT paused and NOT playing
	 */
	public boolean isStopped(int src) {
		int srcstate = alGetSourcei(src, AL_SOURCE_STATE);
		return srcstate == AL_INITIAL || srcstate == AL_STOPPED;
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
}
