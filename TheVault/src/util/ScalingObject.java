package util;
import objects.GameObject;
import util.Animator.AnimateType;

public class ScalingObject {
		
		private GameObject object;
		private float initialSize;
		private float destSize;
		private AnimateType animateType;
		private float[] k;
		private Runnable callback;
		
		/**
		 * Helper object for Animator to track the object and its moving position.
		 * Adds itself to the Animator.moving list to be updated
		 * @param object GameObject to track
		 * @param destSize size destination for this object to scale to
		 * @param k Constants to determine things like lerp amount, speed, or whatever the animation algorithm calls for
		 */
		public ScalingObject(GameObject object, Runnable callback, float destSize, AnimateType animateType, float...k) {
			this.object = object;
			this.callback = callback;
			this.initialSize = object.scale;
			this.destSize = destSize;
			this.animateType = animateType;
			this.k = k;
			Animator.scaling.add(this);
		}

		/**
		 * @return true if the object is done animating
		 */
		public boolean update() {
			switch(animateType) {
			case BELL:
				// TODO implement if needed
				break;
			case LERP:
				float distSize = destSize - object.scale;
				if(Math.abs(distSize) < 0.002) { // about 2 pixels
					object.scale = destSize;
					return true;
				}
				object.scale += distSize * k[0];
				return false;
			case LINEAR:
				// TODO implement if needed
				break;
			default:
				break;
			}
			return true; // if something gets through we don't want it animating forever
		}

		public GameObject getObject() {
			return object;
		}
		
		public void callback() {
			if(callback != null)
				callback.run();
		}

		public float getInitialSize() {
			return initialSize;
		}

		public float getDestSize() {
			return destSize;
		}
		
	}