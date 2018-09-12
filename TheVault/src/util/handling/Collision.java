package util.handling;

import objects.GameObject;

public class Collision {
	
	/**
	 * Collides a point with an axis-aligned bounding box
	 * @param pX Point X
	 * @param pY Point Y
	 * @param bX AABB X
	 * @param bY AABB Y
	 * @param bW AABB Width
	 * @param bH AABB Height
	 * @return <code>true</code> if the given point collides with the given AABB
	 */
	public static boolean pointCollidesAABB(float pX, float pY, float bX, float bY, float bW, float bH) {
		if(pX < bX || pY < bY || pX > bX + bW || pY > bY + bH) return false; // return false if point outside the rect
		return true;
	}
	
	/**
	 * Collides a point with an axis-aligned GameObject
	 * <p> *NOTE* <br>If the GameObject is not Axis-Aligned (i.e. rotation is not 0), results may be futile </p>
	 * @param pX Point X
	 * @param pY Point Y
	 * @param object GameObject to collide
	 * @return <code>true</code> if the given point collides with the given GameObject
	 */
	public static boolean pointCollideAAGameObject(float pX, float pY, GameObject obj) {
		float trueScale = obj.getTrueScale();
		int width = obj.getActiveTexture().getWidth();
		int height = obj.getActiveTexture().getHeight();
		return pointCollidesAABB(pX, pY, obj.x - width * trueScale / 2, obj.y - height * trueScale / 2, width * trueScale, height * trueScale);
	}
	
}
