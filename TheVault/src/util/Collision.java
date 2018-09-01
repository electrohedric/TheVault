package util;

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
		if(pX < bX) return false; 
		if(pY < bY) return false;
		if(pX > bX + bW) return false;
		if(pY > bY + bH) return false;
		return true;
	}
	
}
