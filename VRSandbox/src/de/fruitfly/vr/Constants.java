package de.fruitfly.vr;

public class Constants {
	public static int ScreenWidth = 1280;
	public static int ScreenHeight = 800;
	
	public static float ScreenWidthMeters = 0.14976f;
	public static float ScreenHeightMeters = 0.0936f;
	
	public static float InterpupillaryDistance = 0.058f;
	public static float LensSeperationDistance = InterpupillaryDistance;

	public static float AspectRatio = ScreenWidth * 0.5f / ScreenHeight;
	public static float EyeToScreenDistance = 0.041f;
	public static float FieldOfViewY = 2.0f * (float)Math.atan2(ScreenHeightMeters * 0.5f, EyeToScreenDistance); // in radians; not degrees!

    public static float K0 = 1.0f;
    public static float K1 = 0.22f;
    public static float K2 = 0.24f;
    public static float K3 = 0.0f;
    
    public static float h_meters = ScreenHeightMeters/4.0f - InterpupillaryDistance/2.0f;
    public static float h = 4.0f * h_meters / ScreenHeightMeters;
}
