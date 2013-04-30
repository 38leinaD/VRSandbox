package de.fruitfly.vr;

public class Constants {
	public static long nanoSecondsPerTick = (long) (1e9/60);
	
	public static int HResolution = 1280;
	public static int VResolution = 800;
	
	public static float RenderSurfaceScale = 1.25f;
	
	public static float HScreenSize = 0.14976f;
	public static float VScreenSize = 0.0936f;
	
	public static float InterpupillaryDistance = 0.058f;
	public static float LensSeperationDistance = InterpupillaryDistance;

	public static float AspectRatio = HResolution * 0.5f / VResolution;
	public static float EyeToScreenDistance = 0.041f;
	public static float FieldOfViewY = 2.0f * (float)Math.atan2(VScreenSize * 0.5f, EyeToScreenDistance); // in radians; not degrees!

	public static float LensCenter =  1 - 2 * LensSeperationDistance / HScreenSize;
	
    public static float K0 = 1.0f;
    public static float K1 = 0.22f;
    public static float K2 = 0.24f;
    public static float K3 = 0.0f;
    
    public static float h_meters = HScreenSize/4.0f - InterpupillaryDistance/2.0f;
    public static float h = 4.0f * h_meters / HScreenSize;
}
