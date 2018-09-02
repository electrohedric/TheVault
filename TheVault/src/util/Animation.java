package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import constants.Resources;
import gl.Texture;
import main.Game;
import objects.Surface;

public class Animation extends Surface {
	
	/** list of all animations that wish to be updated */
	public static List<Animation> queue = new ArrayList<>();;
	
	private List<Texture> frames;
	private int currentFrame;
	
	/** time between frames. Equivalent to 1/fps */
	private float frameDelta;
	
	/** Time that has passed since last call to <code>update</code>*/
	private float frameElapsedTime;
	private boolean running;
	private int maxRunTimes;
	private int runTimes;
	private int animationSlot;
	private int numFrames;
	private boolean canStart;
	
	/**
	 * A utility class which helps to display the a sequence of textures in a timed manner
	 * @param pathFormat The path to the frames from relative location <strong>res/</strong> where each frame is distinguished by <strong>&ltn&gt</strong> and where 
	 *                   <strong>n</strong> is the amount of padding. e.g. "texture/frame&lt3&gt.png" indicates a sequence of images such as frame000.png, frame001.png, 
	 *                   etc. in the folder <strong>res/textures/</strong>
	 * @param fps The framerate, in frames per second, to animate at. If <= 0, the animation cannot be started. Must call <code>progress()</code>
	 * @param loopTimes Max number of run times before automatically stopping. If <= 0, the animation will loop indefinetely
	 * 
	 * @see Texture#Texture(String, float, float, float) Texture
	 */
	public Animation(String pathFormat, int fps, int loopTimes, float centerX, float centerY, float quarterTurns) {
		super();
		this.frames = new ArrayList<>();
		this.currentFrame = 0;
		if(fps > 0) {
			this.frameDelta = 1.0f / fps;
			this.canStart = true;
		}
		else {
			this.frameDelta = Float.MAX_VALUE; // about 11 septillion millenia
			this.canStart = false;
		}
		this.frameElapsedTime = 0;
		this.maxRunTimes = loopTimes;
		this.running = false;
		this.numFrames = 0;
		this.runTimes = 0;
		
		String format = pathFormat.replace("<", "%0").replace(">", "d"); // convert <n> to %0nd
		while(true) {
			String name = String.format(format, numFrames);
			if(new File(Resources.TEXTURES_PATH + Texture.getLocalPath() + name).exists()) {
				frames.add(new Texture(name, centerX, centerY, quarterTurns)); // create a new texture for that file
				numFrames++;
			} else break;
		}
		if(frames.isEmpty())
			Log.warn("Couldn't find any texture(s) at '" + Resources.TEXTURES_PATH + Texture.getLocalPath() + pathFormat + "'");
	}
	
	/**
	 * Calls {@link #Animation(String, int, int, float, float, float) Animation} with no offsets
	 */
	public Animation(String pathFormat, int fps, int loopTimes) {
		this(pathFormat, fps, loopTimes, 0, 0, 0);
	}
	
	public void start() {
		if(!running && canStart) {
			running = true;
			queue.add(this);
		}
	}
	
	public void stop() {
		if(running) {
			running = false;
			queue.remove(this);
		}
	}
	
	/** Updates the texture index when the next frame of animation is needed */
	public void update() {
		frameElapsedTime += Game.delta;
		while(frameElapsedTime >= frameDelta && running) { // need to move to the next frame
			// should, 99% of the time, loop once, but just in case more time has passed, we need to increment the right amount of frames
			frameElapsedTime -= frameDelta;
			progress();
		}
	}
	
	/** Forces progression of 1 frame */
	public void progress() {
		if(++currentFrame >= numFrames) { // need to loop
			if(++runTimes < maxRunTimes) // OK to loop again
				currentFrame = 0; // just in case it progressed more than
			else {
				stop();
				currentFrame = numFrames - 1; // just keep rendering the last frame
			}
		}
	}
	
	@Override
	public void bind() {
		bind(animationSlot);
	}
	
	@Override
	public void bind(int slot) {
		frames.get(currentFrame).bind(slot);
	}
	
	@Override
	public void delete() {
		for(Texture t : frames)
			t.delete();
	}

	@Override
	public int getWidth() {
		return frames.get(currentFrame).getWidth();
	}

	@Override
	public int getHeight() {
		return frames.get(currentFrame).getHeight();
	}

	@Override
	public float getOffsetX() {
		return frames.get(currentFrame).getOffsetX();
	}

	@Override
	public float getOffsetY() {
		return frames.get(currentFrame).getOffsetY();
	}

	@Override
	public float getOffsetRot() {
		return frames.get(currentFrame).getOffsetRot();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getRunTimes() {
		return runTimes;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getNumFrames() {
		return numFrames;
	}
	
	public boolean isFinished() {
		return runTimes >= maxRunTimes;
	}
	
}
