package util;

import static org.lwjgl.openal.AL10.*;

import main.Game;

public class Music {

	private static int source;
	private static int looping;
	private static float currentFadeTime;
	private static int fadeDir;
	private static float fadeForTime;
	private static Runnable fadeCallback;
	private static float volume;
	/** for internal use of quick response whether sound is supposed to be playing **/
	private static boolean playing;
	public static float mixerVolume = 0.0f; // set to 0.0f to mute. 1.0f default
	
	/**
	 * Creates a new Sound which can be played with multiple players
	 * @param path
	 * @param volume
	 * @param loop
	 * @param maxPlayers
	 */
	public static void init() {
		source = alGenSources();
		looping = 0; // null
		currentFadeTime = 0;
		fadeDir = 0;
		fadeForTime = 0;
		fadeCallback = null;
		playing = false;
		setVolume(Music.mixerVolume);
		alSourcef(source, AL_PITCH, 1.0f);
		alSource3f(source, AL_POSITION, 0, 0, 0);
		alSource3f(source, AL_VELOCITY, 0, 0, 0);
	}
	
	/**
	 * Starts the play the music in the queue
	 * @param sound the buffer to play
	 */
	public static void play() {
		alSourcePlay(source);
		playing = true;
	}
	
	/**
	 * Identical to <code>queue()</code> except the music piece is recognized to loop indefinetely instead of being removed from the queue
	 * until <code>stop()</code> is called.
	 * @param nextSound the loop to play after this intro sound
	 */
	public static void queueLoop(Sound sound) {
		do {
			queue(sound);
		} while(buffersQueued() < 2);
		looping = sound.buffer;
	}
	
	/** queues a music selection to play next. music is removed from queue after it's played
	 * @param sound Sound to queue
	 */
	public static void queue(Sound sound) {
		alSourceQueueBuffers(source, sound.buffer);
	}
	
	private static void unqueue() {
		alSourceUnqueueBuffers(source);
	}
	
	public static void pause() {
		alSourcePause(source);
		playing = false;
	}
	
	public static void unpause() {
		alSourcePlay(source);
		playing = true;
	}
	
	/**
	 * Stops the music and completely clears the queue ready for a new track to play
	 */
	public static void stop() {
		alSourceStop(source);
		while(buffersQueued() > 0) // sometimes unqueue doesn't unqueue everything
			unqueue();
		playing = false;
	}
	
	/**
	 * Clears the queue and adds the specified looping sound to the end of the queue if needed to continue looping
	 */
	public static void update() {
		if(looping != 0 && playing) { //buffer not null and we need to loop
			unqueue(); // remove processed buffers
			while(buffersQueued() < 2) // always have 2 buffers in the queue when looping
				alSourceQueueBuffers(source, looping);
		}
		if(fadeDir != 0) { // need to fade
			currentFadeTime += Game.delta;
			if(currentFadeTime >= fadeForTime) { // we hit max fade time
				if(fadeDir > 0) { // just faded in
					setVolume(Music.mixerVolume);
					fadeDir = 0;
				} else { // just faded out
					setVolume(0.0f);
					looping = 0;
					stop();
					fadeCallback.run();
					currentFadeTime = 0;
					fadeDir = 1;
					fadeCallback = null;
				}
			} else { // change volume
				float timeLeft = fadeForTime - currentFadeTime;
				float volumeLeft = fadeDir > 0 ? Music.mixerVolume - volume : volume; // volume left based on fade direction
				float dvdt = volumeLeft / timeLeft * Game.delta;
				setVolume(volume + dvdt * fadeDir);
			}
		}
	}
	
	/**
	 * Fades out the music, runs the callback, then fades back in
	 * @param fadeTime Time to fade out, in seconds. The fade in will also take this amount of time
	 * @param callback Runnable method to call in the middle of the transition. Setup the next music queue here. Music will not play automatically unless 
	 * 				   <code>play()</code> is called in callback
	 */
	public static void transition(float fadeTime, Runnable callback) {
		currentFadeTime = 0;
		fadeDir = -1;
		fadeForTime = fadeTime;
		fadeCallback = callback;
	}
	
	private static int buffersQueued() {
		return alGetSourcei(source, AL_BUFFERS_QUEUED);
	}
	
	/** 
	 * Polls OpenAl's source to ask if it's playing
	 * @return <code>true</code> if and only if sound is playing. i.e. NOT paused and NOT stopped
	 */
	public static boolean isPlaying() {
		return alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	/** 
	 * @return <code>true</code> if and only if sound is paused. i.e. NOT stopped and NOT playing
	 */
	public static boolean isPaused() {
		return alGetSourcei(source, AL_SOURCE_STATE) == AL_PAUSED;
	}
	
	/** 
	 * @return <code>true</code> if and only if sound is stopped or never used. i.e. NOT paused and NOT playing
	 */
	public static boolean isStopped() {
		int srcstate = alGetSourcei(source, AL_SOURCE_STATE);
		return srcstate == AL_INITIAL || srcstate == AL_STOPPED;
	}
	
	public static void setVolume(float volume) {
		Music.volume = volume;
		alSourcef(source, AL_GAIN, volume * Music.mixerVolume);
	}
}
