package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

import de.fruitfly.ovr.HMDInfo;
import de.fruitfly.ovr.OculusRift;
import de.fruitfly.vr.InputHandler.GamepadState;

public class Main {
	
	public static InputHandler input;
	public static Player player;
	
	private static BarrelDistortionRenderer renderer;
	private static DisplayMode windowedDisplayMode;
	private static DisplayMode fullscreenDisplayMode;
	private static Point defaultWindowLocaton;
	private static boolean windowIsAtExtendedScreenLocation = false;
	
	public static OculusRift hmd;
	
	private static DisplayMode getFullScreenMode() {
		DisplayMode[] modes = null;
		try {
			modes = Display.getAvailableDisplayModes();
			for (DisplayMode dm : modes) {
				if (dm.getWidth() == Constants.ScreenWidth
						&& dm.getHeight() == Constants.ScreenHeight
						&& dm.isFullscreenCapable()
						&& dm.getBitsPerPixel() == 32
						&& dm.getFrequency() == 60) {
					return dm;
				}
			}
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	private static void toggleFullscreenMode() {
		try {
			if (Display.getDisplayMode() == windowedDisplayMode) {
				Display.setDisplayModeAndFullscreen(fullscreenDisplayMode);
			}
			else if (Display.getDisplayMode() == fullscreenDisplayMode) {
				Display.setDisplayMode(windowedDisplayMode);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private static long tickAccumulator = 0;
	private static long lastNanos;
	public static long tick = 0;
	
	public static void main(String[] args) {
        try {
        	fullscreenDisplayMode = getFullScreenMode();
        	windowedDisplayMode = new DisplayMode(Constants.ScreenWidth,
					Constants.ScreenHeight);
			Display.setDisplayMode(windowedDisplayMode);
			Display.setTitle("VRSandbox");
			defaultWindowLocaton = new Point(50, 50);
			Display.setLocation(defaultWindowLocaton.x, defaultWindowLocaton.y);
			
			PixelFormat pixelFormat = new PixelFormat(8, 8, 8);
			Display.create(pixelFormat);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

        renderer = new BarrelDistortionRenderer(Constants.ScreenWidth, Constants.ScreenHeight);
        
        hmd = new OculusRift();
		hmd.init();
		
		HMDInfo hmdInfo = hmd.getHMDInfo();
		System.out.println("HMD initialized? " + hmd.isInitialized());
		System.out.println(hmdInfo);

        input = new InputHandler();
		if (hmd.isInitialized()) {
			input.setHeadTracker(new HeadTracker(hmd));
		}
        
		Model m = new Model("/world");
		player = new Player(input);

		lastNanos = System.nanoTime();
		
		while (!Display.isCloseRequested()) {
			input.fetchEvents();
			
			if (input.isKeyDown(Keyboard.KEY_ESCAPE)) {
				//System.exit(0);
				Mouse.setGrabbed(false);
			}
			
			// If monitors are mirroring, use this to get into fullscreen
			if (input.isKeyPressed(Keyboard.KEY_F1)) {
				toggleFullscreenMode();
			}
			
			// If rift is a secondary/extended monitor,
			// use this to move window to this monitor
			if (input.isKeyPressed(Keyboard.KEY_F5)) {
				toggleMoveWindowToRiftAsSecondaryScreen();
			}
			
			if (input.isKeyPressed(Keyboard.KEY_F2)) {
				renderer.toggleShader();
			}
			
			if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
				Mouse.setGrabbed(true);
			}
			
			// Update World
			long nanos = System.nanoTime();
			long deltaNanos = nanos - lastNanos;
		
			tickAccumulator += deltaNanos;
			while (tickAccumulator >= Constants.nanoSecondsPerTick) {
				tick();
				tickAccumulator-=Constants.nanoSecondsPerTick;
				tick++;
			}
			lastNanos = nanos;
			
			// Render World
			renderer.beginOffScreenRenderPass();
			
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glEnable(GL_TEXTURE_2D);

			glViewport(0, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			player.setupOpenGLMVP(Player.LeftEye);
			m.render();
			
			glViewport(Constants.ScreenWidth/2, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			player.setupOpenGLMVP(Player.RightEye);
			m.render();
			renderer.endOffScreenRenderPass();
			
			renderer.renderToScreen();
			
			//Display.sync(60);
			Display.update();
		}
	}

	private static void tick() {
		player.update();
	}

	private static void toggleMoveWindowToRiftAsSecondaryScreen() {
		if (!windowIsAtExtendedScreenLocation) {
			Display.setLocation(1917, -22); // Make this general; at the moment assumes my monitor with 1920 pixel width and title bar of height 22 pixel
			windowIsAtExtendedScreenLocation = true;
		}
		else {
			Display.setLocation(defaultWindowLocaton.x, defaultWindowLocaton.y);
			windowIsAtExtendedScreenLocation = false;
		}
	}
}
