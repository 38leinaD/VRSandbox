package de.fruitfly.vr;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.fruitfly.vr.InputHandler.GamepadState;

public class InputHandler {

    class KeyState {
        boolean  pressed = false;
    }
    
    class GamepadState {
    	float lx = 0.0f;
    	float ly = 0.0f;
    	float rx = 0.0f;
    	float ry = 0.0f;
    }
    
	private KeyState[] keyStates = new KeyState[256];
	private Controller controller = null;
	private GamepadState initialGamepadState = new GamepadState();
	private GamepadState gamepadState = new GamepadState();
	
	public InputHandler() {
		for (int i=0; i<keyStates.length; i++) {
			keyStates[i] = new KeyState();
		}

		try {
			Controllers.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        int numControllers = Controllers.getControllerCount();
        for (int i=0; i<numControllers; i++) {
        	controller = Controllers.getController(i);
        	System.out.println("Found controller: " + controller.getName());
        	System.out.println("Number of axes: " + controller.getAxisCount());
        	if (controller.getAxisCount() >= 4) {
        		for (int j=0; j<controller.getAxisCount(); j++) {
        			System.out.println("Found axis: " + controller.getAxisName(j));
        		}
        		System.out.println("Bound");
        		
        		controller.poll();
        		// Initial state might be messed up at some controller. store and only say initialized when state changes.
        		initialGamepadState.lx = controller.getXAxisValue();
        		initialGamepadState.ly = controller.getYAxisValue();
        		initialGamepadState.rx = controller.getRXAxisValue();
        		initialGamepadState.ry = controller.getRYAxisValue();
        		break;
        	}
        }
        
        //Mouse.setGrabbed(true);
	}
	
	public void fetchEvents() {
		while (Keyboard.next()) {
			KeyState key = keyStates[Keyboard.getEventKey()];
			key.pressed = Keyboard.getEventKeyState();
		}
		
		if (controller != null) {
			controller.poll();

			if (initialGamepadState == null) {
				gamepadState.lx = Math.abs(controller.getXAxisValue()) > 0.15 ? controller.getXAxisValue() : 0.0f;
				gamepadState.ly = Math.abs(controller.getYAxisValue()) > 0.15 ? -controller.getYAxisValue() : 0.0f;
				gamepadState.rx = Math.abs(controller.getRXAxisValue()) > 0.15 ? controller.getRXAxisValue() : 0.0f;
				gamepadState.ry = Math.abs(controller.getRYAxisValue()) > 0.15 ? -controller.getRYAxisValue() : 0.0f;
			}
			else {
				if (	controller.getXAxisValue() != initialGamepadState.lx ||
						controller.getYAxisValue() != initialGamepadState.ly ||
						controller.getRXAxisValue() != initialGamepadState.rx ||
						controller.getRYAxisValue() != initialGamepadState.ry) {
					initialGamepadState = null; // Values different then initial; assuming they are good now
				}
			}
		}
	}
	
	public boolean isKeyDown(int key) {
		return keyStates[key].pressed;
	}
	
	public boolean isKeyPressed(int key) {
		if (keyStates[key].pressed) {
			keyStates[key].pressed = false;
			return true;
		}
		return false;
	}

	public boolean isGamepadActive() {
		return controller != null;
	}
	
	public GamepadState getGamepadState() {
		return gamepadState;
	}
}
