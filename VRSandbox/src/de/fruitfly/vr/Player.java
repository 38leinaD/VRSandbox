package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
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
		
		position.set(6.2200637f, -5.4675417f, 1.7f);
		yaw = -3.853997f;
		pitch = 0.058000006f;
		roll = 0.0f;
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

	public static final int LeftEye = 0;
	public static final int RightEye = 1;

	private Vector3f YawAxis = new Vector3f(0.0f, 0.0f, 1.0f);
	private Vector3f PitchAxis = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f RollAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	
	private Matrix4f m = new Matrix4f();
	
	public void setupOpenGLMVP(int eye) {
		
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(MathUtil.r2d(Constants.FieldOfViewY), Constants.AspectRatio, 0.01f, 1000.0f);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		// Make z point upward; x,y-plane is flat; camera points in positive y direction
		glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

		glTranslatef(0.0f, (eye == LeftEye ? -1 : 1) * Constants.InterpupillaryDistance/2.0f, 0.0f);
		
		glRotatef(-MathUtil.r2d(roll), 1.0f, 0.0f, 0.0f);
		glRotatef(-MathUtil.r2d(pitch), 0.0f, 1.0f, 0.0f);
		glRotatef(-MathUtil.r2d(yaw), 0.0f, 0.0f, 1.0f);
		
		glTranslatef(-position.x, -position.y, -position.z);
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
