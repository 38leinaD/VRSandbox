package de.fruitfly.vr;

import org.lwjgl.input.Mouse;

import de.fruitfly.ovr.HMDInfo;
import de.fruitfly.ovr.OculusRift;

public class HeadTracker {
	private float yaw, pitch, roll;
	private OculusRift hmd;
	
	public HeadTracker() {
		yaw = pitch = roll = 0.0f;
	}
	
	public HeadTracker(OculusRift hmd) {
		this.hmd = hmd;
	}
	
	public void poll() {

		if (hmd != null && hmd.isInitialized()) {
			hmd.poll();
			yaw = hmd.getYaw();
			pitch = hmd.getPitch();
			roll = hmd.getRoll();
		}
		else {
			if (Mouse.isGrabbed()) {
				float dx = Mouse.getDX() / 500.0f;
				float dy = Mouse.getDY() / 500.0f;

				yaw = yaw - dx;
				pitch = pitch + dy;
			}
		}
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getRoll() {
		return roll;
	}
	
	public static void main(String[] args) {
		OculusRift or = new OculusRift();
		or.init();
		
		HMDInfo hmdInfo = or.getHMDInfo();
		System.out.println("HMD initialized? " + or.isInitialized());
		System.out.println(hmdInfo);
		
		while (or.isInitialized()) {
			or.poll();
			
			System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		or.destroy();
	}
}
