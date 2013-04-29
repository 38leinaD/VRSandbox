package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

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
	private static BarrelDistortionRenderer renderer;
	private static DisplayMode windowedDisplayMode;
	private static DisplayMode fullscreenDisplayMode;
	
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
	
	private static void toggleDisplayMode() {
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
	
	public static void main(String[] args) {
        try {
        	fullscreenDisplayMode = getFullScreenMode();
        	windowedDisplayMode = new DisplayMode(Constants.ScreenWidth,
					Constants.ScreenHeight);
			Display.setDisplayMode(windowedDisplayMode);
			Display.setTitle("VRSandbox");
			//Display.setLocation(2000, 200);
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
		Player p = new Player(input);

		while (!Display.isCloseRequested()) {
			input.fetchEvents();
			
			if (input.isKeyDown(Keyboard.KEY_ESCAPE)) {
				//System.exit(0);
				Mouse.setGrabbed(false);
			}
			
			if (input.isKeyPressed(Keyboard.KEY_F1)) {
				toggleDisplayMode();
			}
			
			if (input.isKeyPressed(Keyboard.KEY_F5)) {
				Display.setLocation(1917, -22);
			}
			
			if (input.isKeyPressed(Keyboard.KEY_F2)) {
				renderer.toggleShader();
			}
			
			if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
				Mouse.setGrabbed(true);
			}
			
			p.update();

			renderer.beginOffScreenRenderPass();
			
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glEnable(GL_TEXTURE_2D);

			glViewport(0, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			p.setupOpenGLMVP(Player.LeftEye);
			m.render();
			
			glViewport(Constants.ScreenWidth/2, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			p.setupOpenGLMVP(Player.RightEye);
			m.render();
			renderer.endOffScreenRenderPass();
			
			renderer.renderToScreen();
			
			Display.sync(60);
			Display.update();
		}
	}
}
