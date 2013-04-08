package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

import de.fruitfly.vr.InputHandler.GamepadState;

public class Main {
	
	public static InputHandler input;
	
	public static void main(String[] args) {
        try {
            DisplayMode displayMode = new DisplayMode(Constants.ScreenWidth,
					Constants.ScreenHeight);
			Display.setDisplayMode(displayMode);
			Display.setTitle("VRSandbox");
			Display.setLocation(2000, 200);
			PixelFormat pixelFormat = new PixelFormat(8, 8, 8);
			Display.create(pixelFormat);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

        input = new InputHandler();
        
		Model m = new Model("/world");
		Player p = new Player(input);

		while (!Display.isCloseRequested()) {
			input.fetchEvents();
			
			if (input.isKeyDown(Keyboard.KEY_ESCAPE)) {
				//System.exit(0);
				Mouse.setGrabbed(false);
			}
			
			if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
				Mouse.setGrabbed(true);
			}
			
			p.update();
			
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glEnable(GL_TEXTURE_2D);


			glViewport(0, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			p.setupOpenGLMVP(Eye.Left);
			m.render();
			
			glViewport(Constants.ScreenWidth/2, 0, Constants.ScreenWidth/2, Constants.ScreenHeight);
			p.setupOpenGLMVP(Eye.Right);
			m.render();
			
			Display.sync(60);
			Display.update();
		}
	}
}
