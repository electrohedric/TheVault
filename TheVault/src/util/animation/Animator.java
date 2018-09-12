package util.animation;

import java.util.ArrayList;
import java.util.List;

import objects.GameObject;

public class Animator {

	protected static List<MovingObject> moving = new ArrayList<>();
	protected static List<ScalingObject> scaling = new ArrayList<>();
	
	// technically all these are just factories for MovingObject and MovingScale
	
	/**
	 * Moves an object from its current position to another with linear interpolation
	 * @see MovingObject#MovingObject(GameObject, float, float, float...) MovingObject constructor
	 * @param movingObject
	 * @param destX
	 * @param destY
	 * @param lerpAmount From (0,1]. % distance to cover with each iteration. 0.1-0.4 is recommended
	 */
	public static void lerp(GameObject object, float destX, float destY, float lerpAmount, Runnable callback) {
		new MovingObject(object, callback, destX, destY, AnimateType.LERP, lerpAmount);
	}
	
	/**
	 * Like {@link Animator#lerp(GameObject, float, float, float, Runnable) lerp} except changes size instead of position
	 * @param object
	 * @param destSize
	 * @param lerpAmount
	 * @param callback
	 */
	public static void lerpSize(GameObject object, float destSize, float lerpAmount, Runnable callback) {
		new ScalingObject(object, callback, destSize, AnimateType.LERP, lerpAmount);
	}
	
	public static void update() {
		// update movement
		for(int i = moving.size() - 1; i >= 0; i--)
			if(moving.get(i).update()) { // if the object is finished then callback and delete it
				moving.get(i).callback();
				moving.remove(i);
			}
		// update scale
		for(int i = scaling.size() - 1; i >= 0; i--)
			if(scaling.get(i).update()) {
				scaling.get(i).callback();
				scaling.remove(i);
			}
	}
	
	public static enum AnimateType {
		 LINEAR, LERP, BELL
	}
}
