package de.fruitfly.vr;

public class Constants {
	public static int ScreenWidth = 1280;
	public static int ScreenHeight = 960;
	
	public static float ScreenWidthMeters = 0.14976f;
	public static float ScreenHeightMeters = 0.0935f;
	
	public static float InterpupillaryDistance = 0.64f;
	public static float AspectRatio = ScreenWidth * 0.5f / ScreenHeight;
	public static float EyeToScreenDistance = 0.04f;
	public static float FieldOfViewY = 2.0f * (float)Math.atan2(ScreenHeightMeters * 0.5f, EyeToScreenDistance); // in radians; not degrees!
}
