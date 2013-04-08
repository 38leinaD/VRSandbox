package de.fruitfly.vr;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.fruitfly.vr.InputHandler.GamepadState;

public class Player {

	private float yaw = 0.0f, pitch = 0.0f, roll = 0.0f;
	private Vector3f position = new Vector3f(0.0f, 0.0f, 1.7f);
	
	private InputHandler input;
	
	public Player(InputHandler input) {
		this.input = input;
	}
	
	public void update() {
		if (input.isKeyDown(Keyboard.KEY_LEFT)) {
			//v.setYaw(v.getYaw() + 0.05f);
			position.x = (position.x - 0.1f * (float) (Math.sin(yaw)));
			position.y = (position.y + 0.1f * (float) (Math.cos(yaw)));
		}
		if (input.isKeyDown(Keyboard.KEY_RIGHT)) {
			position.x = (position.x + 0.1f * (float) (Math.sin(yaw)));
			position.y = (position.y - 0.1f * (float) (Math.cos(yaw)));
		}
		if (input.isKeyDown(Keyboard.KEY_UP)) {
			position.x = (position.x + 0.1f * (float) (Math.cos(yaw)));
			position.y = (position.y + 0.1f * (float) (Math.sin(yaw)));
		}
		if (input.isKeyDown(Keyboard.KEY_DOWN)) {
			position.x = (position.x - 0.1f * (float) (Math.cos(yaw)));
			position.y = (position.y - 0.1f * (float) (Math.sin(yaw)));
		}
		
		if (Mouse.isGrabbed()) {
			float dx = Mouse.getDX() / 500.0f;
			float dy = Mouse.getDY() / 500.0f;
			
			yaw = yaw - dx;
			pitch = pitch - dy;
		}
		
		if (input.isGamepadActive()) {
			GamepadState gps = input.getGamepadState();
			
			// Left analog stick
			// Move forward
			position.x = (position.x + 0.1f * gps.ly * (float) (Math.cos(yaw)));
			position.y = (position.y + 0.1f * gps.ly * (float) (Math.sin(yaw)));
			
			// Strafe
			position.x = (position.x + 0.1f * gps.lx * (float) (Math.sin(yaw)));
			position.y = (position.y - 0.1f * gps.lx * (float) (Math.cos(yaw)));
			
			// Right analog stick
			yaw = yaw - 0.03f * gps.rx;
			pitch = pitch - 0.03f * gps.ry;
		}	

	}

	private Eye eye = new Eye();

	private Vector3f YawAxis = new Vector3f(0.0f, 0.0f, 1.0f);
	private Vector3f PitchAxis = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f RollAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	
	private Matrix4f m = new Matrix4f();
	
	public void setupOpenGLMVP() {
		
		// Cyclopse
		eye.getPosition().set(position);
			
		eye.setYaw(yaw);
		eye.setRoll(roll);
		eye.setPitch(pitch);
		
		eye.setupOpenGLMVP();
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Player [yaw=" + yaw + ", pitch=" + pitch + ", roll=" + roll
				+ ", position=" + position + "]";
	}
	
	
}
