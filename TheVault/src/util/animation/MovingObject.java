package util.animation;
import objects.GameObject;
import util.animation.Animator.AnimateType;

public class MovingObject {
		
		private GameObject object;
		private float initialX;
		private float initialY;
		private float destX;
		private float destY;
		private AnimateType animateType;
		private float[] k;
		private Runnable callback;
		
		/**
		 * Helper object for Animator to track the object and its moving position.
		 * Adds itself to the Animator.moving list to be updated
		 * @param object GameObject to track
		 * @param destX X destination for this object to move to
		 * @param destY Y destination for this object to move to
		 * @param k Constants to determine things like lerp amount, speed, or whatever the animation algorithm calls for
		 */
		public MovingObject(GameObject object, Runnable callback, float destX, float destY, AnimateType animateType, float...k) {
			this.object = object;
			this.callback = callback;
			this.initialX = object.x;
			this.initialY = object.y;
			this.destX = destX;
			this.destY = destY;
			this.animateType = animateType;
			this.k = k;
			Animator.moving.add(this);
		}
		
		/**
		 * @return true if the object is done animating
		 */
		public boolean update() {
			switch(animateType) {
			case BELL:
				// XXX implement if needed
				break;
			case LERP:
				float distX = destX - object.x;
				float distY = destY - object.y;
				if(Math.abs(distX) < 1 && Math.abs(distY) < 1) { // less than a pixel, snap to destination; otherwise it'd take too long
					object.x = destX;
					object.y = destY;
					return true;
				}
				object.x += distX * k[0];
				object.y += distY * k[0];
				return false;
			case LINEAR:
				// XXX implement if needed
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

		public float getInitialX() {
			return initialX;
		}

		public float getInitialY() {
			return initialY;
		}

		public float getDestX() {
			return destX;
		}

		public float getDestY() {
			return destY;
		}
		
	}