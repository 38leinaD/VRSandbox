package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.fruitfly.vr.InputHandler.GamepadState;

public class Player {
	private float bodyYaw = 0.0f;
	private float yaw = 0.0f, pitch = 0.0f, roll = 0.0f;
	
	private float floorToNeckZ = 1.70f;
	private float neckToEyeZ = 0.15f;
	private float spineToFaceX = 0.09f;
	
	private Vector3f position = new Vector3f(0.0f, 0.0f, floorToNeckZ); // This is the position on the neck used as the rotation center
	
	private InputHandler input;
	
	public Player(InputHandler input) {
		this.input = input;
		
		position.set(6.2200637f, -5.4675417f, floorToNeckZ);
		bodyYaw = -3.853997f;
		yaw = -3.853997f;
		pitch = 0.058000006f;
		roll = 0.0f;
	}
	
	public void update() {
		if (input.isKeyDown(Keyboard.KEY_LEFT)) {
			bodyYaw = bodyYaw + 0.02f;
		}
		if (input.isKeyDown(Keyboard.KEY_RIGHT)) {
			bodyYaw = bodyYaw - 0.02f;
		}
		if (input.isKeyDown(Keyboard.KEY_UP)) {
			position.x = (position.x + 0.04f * (float) (Math.cos(bodyYaw)));
			position.y = (position.y + 0.04f * (float) (Math.sin(bodyYaw)));
		}
		if (input.isKeyDown(Keyboard.KEY_DOWN)) {
			position.x = (position.x - 0.04f * (float) (Math.cos(bodyYaw)));
			position.y = (position.y - 0.04f * (float) (Math.sin(bodyYaw)));
		}
		
		if (input.isGamepadActive()) {
			GamepadState gps = input.getGamepadState();
			
			// Left analog stick
			// Move forward
			position.x = (position.x + 0.04f * gps.ly * (float) (Math.cos(bodyYaw)));
			position.y = (position.y + 0.04f * gps.ly * (float) (Math.sin(bodyYaw)));
			
			// Strafe
			position.x = (position.x + 0.04f * gps.lx * (float) (Math.sin(bodyYaw)));
			position.y = (position.y - 0.04f * gps.lx * (float) (Math.cos(bodyYaw)));
			
			// Right analog stick
			bodyYaw = bodyYaw - 0.01f * gps.rx;
			//pitch = pitch - 0.03f * gps.ry;
		}	
		
		HeadTracker ht = input.getHeadTracker();
		
		yaw = ht.getYaw();
		pitch = ht.getPitch();
		roll = ht.getRoll();
	}

	public static final int LeftEye = 0;
	public static final int RightEye = 1;

	private Vector3f YawAxis = new Vector3f(0.0f, 0.0f, 1.0f);
	private Vector3f PitchAxis = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f RollAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	
	private Vector4f neckToEye = new Vector4f();
	private Matrix4f m = new Matrix4f();
	private FloatBuffer m_fb = BufferUtils.createFloatBuffer(16);
	
	public void setupOpenGLMVP(int eye) {
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
        
		if (eye == Player.LeftEye) {
			glTranslatef(Constants.h, 0.0f, 0.0f);
		}
		else {
			glTranslatef(-Constants.h, 0.0f, 0.0f);   
		}

		float scale = BarrelDistortionRenderer.distfunc(1 + Constants.LensCenter);
		float xx = scale * Constants.VScreenSize/2.0f;
		float fov = (float) (2 * Math.atan2(xx, Constants.EyeToScreenDistance));
		
		GLU.gluPerspective(MathUtil.r2d(fov), Constants.AspectRatio, 0.01f, 1000.0f);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		// Make z point upward; x,y-plane is flat; camera points in positive x direction
		glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

		// Head-Neck Model
		glTranslatef(-spineToFaceX, (eye == LeftEye ? -1 : 1) *Constants.InterpupillaryDistance/2.0f, -neckToEyeZ);
		
		glRotatef(-MathUtil.r2d(-roll), 1.0f, 0.0f, 0.0f);
		glRotatef(-MathUtil.r2d(-pitch), 0.0f, 1.0f, 0.0f);
		glRotatef(-MathUtil.r2d(yaw + bodyYaw), 0.0f, 0.0f, 1.0f);
		
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
