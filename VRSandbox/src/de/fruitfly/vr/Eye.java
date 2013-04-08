package de.fruitfly.vr;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Eye {
	private Vector3f eye = new Vector3f();;
	private float yaw, pitch, roll;
	
	public static final int Left = 0;
	public static final int Right = 1;
	
	public Eye() {
		eye.set(6.0184317f, 5.181691f, 1.8f);
		yaw = -2.3960004f;
		pitch = 0.089999974f;
		roll = 0.0f;
	}
	
	public void setupOpenGLMVP() {
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(60.0f, Constants.ScreenWidth*0.5f/(float)Constants.ScreenHeight, 0.01f, 1000.0f);
				
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		// Make z point upward; x,y-plane is flat; camera points in positive y direction
		glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		glRotatef(90.0f, 0.0f, 0.0f, 1.0f);


		glRotatef(-MathUtil.r2d(roll), 1.0f, 0.0f, 0.0f);
		glRotatef(-MathUtil.r2d(pitch), 0.0f, 1.0f, 0.0f);
		glRotatef(-MathUtil.r2d(yaw), 0.0f, 0.0f, 1.0f);
		
		glTranslatef(-eye.x, -eye.y, -eye.z);
	}

	public Vector3f getPosition() {
		return eye;
	}

	public void setEye(Vector3f eye) {
		this.eye = eye;
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

	@Override
	public String toString() {
		return "Eye [eye=" + eye + ", yaw=" + yaw + ", pitch=" + pitch
				+ ", roll=" + roll + "]";
	}
	

}
