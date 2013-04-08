package de.fruitfly.vr;

public class MathUtil {
	public static float d2r(float degree) {
		return (float) (degree/360.0f * 2 * Math.PI);
	}
	
	public static float r2d(float rad) {
		return (float) (rad/(2 * Math.PI) * 360.0f);
	}
	
	private float normDeg(float angle) {
		while (angle >= 360.0f) angle-=360.0f;
		while (angle < 0.0f) angle += 360.0f;
		return angle;
	}
	
	private float normRad(float angle) {
		while (angle >= 2 * Math.PI) angle-=2 * Math.PI;
		while (angle < 0.0f) angle += 2 * Math.PI;
		return angle;
	}
}
