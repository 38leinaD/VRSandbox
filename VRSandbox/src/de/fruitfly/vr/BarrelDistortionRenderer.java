package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.Util;

public class BarrelDistortionRenderer {
	protected int distortionShader=0;
    protected int noDistortionShader=0;
    protected int usedShader=0;
    
    protected int colorTextureID;
    protected int framebufferID;
    protected int depthRenderBufferID;
    
    private int width, height;
    private float scale;
    
    private int LensCenterLocation;
    private int ScreenCenterLocation;
    private int ScaleLocation;
    private int ScaleInLocation;
    private int HmdWarpParamLocation;

    private final static String VERTEX_SHADER_SOURCE = 
            "void main() {\n" +
            "   gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "   gl_Position = gl_Vertex;\n" +
            "}";

    private final static String FRAGMENT_SHADER_SOURCE_NO_DISTORTION = 
            "uniform sampler2D tex;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_FragColor = texture2D(tex, gl_TexCoord[0].st);\n" +
            "}";
    
    private final static String FRAGMENT_SHADER_SOURCE = 
            "uniform sampler2D tex;\n" +
            "uniform vec2 LensCenter;\n" +
            "uniform vec2 ScreenCenter;\n" +
            "uniform vec2 Scale;\n" +
            "uniform vec2 ScaleIn;\n" +
            "uniform vec4 HmdWarpParam;\n" +
            "\n" + 
            "vec2 HmdWarp(vec2 texIn)\n" + 
            "{\n" + 
            "   vec2 theta = (texIn - LensCenter) * ScaleIn;\n" +
            "   float  rSq= theta.x * theta.x + theta.y * theta.y;\n" +
            "   vec2 theta1 = theta * (HmdWarpParam.x + HmdWarpParam.y * rSq + " +
            "           HmdWarpParam.z * rSq * rSq + HmdWarpParam.w * rSq * rSq * rSq);\n" +
            "   return LensCenter + Scale * theta1;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "\n" + 
            "void main()\n" +
            "{\n" +
            "   vec2 tc = HmdWarp(gl_TexCoord[0]);\n" +
            "   if (any(notEqual(clamp(tc, ScreenCenter-vec2(0.25,0.5), ScreenCenter+vec2(0.25, 0.5)) - tc, vec2(0.0, 0.0))))\n" +
            "       gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "   else\n" +
            "       gl_FragColor = texture2D(tex, tc);\n" +
            "}";

    public BarrelDistortionRenderer(int screenWidth, int screenHeight, float scale) {
    	this.width = (int) (screenWidth * scale);
    	this.height = (int) (screenHeight * scale);
    	this.scale = scale;
    	
        distortionShader = initShaders(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE);
        initFBO(this.width, this.height);
        Util.checkGLError();
        
        LensCenterLocation = glGetUniformLocation(distortionShader, "LensCenter");
        ScreenCenterLocation = glGetUniformLocation(distortionShader, "ScreenCenter");
        ScaleLocation = glGetUniformLocation(distortionShader, "Scale");
        ScaleInLocation = glGetUniformLocation(distortionShader, "ScaleIn");
        HmdWarpParamLocation = glGetUniformLocation(distortionShader, "HmdWarpParam");

        Util.checkGLError();

        noDistortionShader = initShaders(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE_NO_DISTORTION);
        Util.checkGLError();
        
        setShader(distortionShader);
    }

    private void initFBO(int surfaceWidth, int surfaceHeight) {
        framebufferID = glGenFramebuffers();                                                                                
        colorTextureID = glGenTextures();                                                                                               
        depthRenderBufferID = glGenRenderbuffers();                                                                  

        System.out.println("Creating offscreen render target of dimensions " + surfaceWidth + "x" + surfaceHeight);
        
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);                                               

        // initialize color texture
        glBindTexture(GL_TEXTURE_2D, colorTextureID);                                                                  
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);                               
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, surfaceWidth, surfaceHeight, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null); 
        //glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        //glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D, colorTextureID, 0);

        // initialize depth renderbuffer
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBufferID);                               
        glRenderbufferStorage(GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, surfaceWidth, surfaceHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_RENDERBUFFER, depthRenderBufferID); 

        glBindFramebuffer(GL_FRAMEBUFFER, 0);     
        glBindTexture(GL_TEXTURE_2D, 0); 
    }

    public void toggleShader() {
    	if (usedShader == distortionShader) {
    		setShader(noDistortionShader);
    	}
    	else if (usedShader == noDistortionShader) {
    		setShader(distortionShader);
    	}
    }
    
    public void beginOffScreenRenderPass() {
        
        Util.checkGLError();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
        Util.checkGLError();
    }
    
    public void setViewportForEye(int eye) {
    	if (eye == Player.LeftEye) {
			glViewport(0, 0, width/2, height);
    	}
    	else if (eye == Player.RightEye) {
			glViewport(width/2, 0, width/2, height);
    	}
    	else {
    		throw new RuntimeException("Unknown eye=" + eye);
    	}
    }
    
    public void endOffScreenRenderPass() {
        
    }

    public void renderToScreen() {

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        
        Util.checkGLError();
        glUseProgram(usedShader);
        Util.checkGLError();

        glDisable(GL_DEPTH_TEST);

        glBindTexture(GL_TEXTURE_2D, colorTextureID);   

        glViewport(0, 0, 1280, 800);
        
        renderEye(Player.LeftEye, 0.0f, 0.0f, 0.5f, 1.0f);
        renderEye(Player.RightEye, 0.5f, 0.0f, 0.5f, 1.0f);

        glUseProgram(0);
        
        glPopAttrib();
    }
    
    protected void setShader(int shader) {
        usedShader = shader;
    }
    
    public static float distfunc(float r) {
    	float r2 = r * r; // r^2
    	float r4 = r2 * r2; // r^4
    	float r6 = r4 * r2; // r^6
    	return Constants.K0 + Constants.K1 * r2 + Constants.K2 * r4 + Constants.K3 * r6;
    }
    
    protected void renderEye(int eye, float x, float y, float w, float h) {

    	if (usedShader == distortionShader) {
        	float as = (Constants.HResolution/2)/(float)Constants.VResolution;

        	this.validate();
            Util.checkGLError();
           
            if (eye == Player.LeftEye) {
            	 glUniform2f(LensCenterLocation, x + (w + Constants.LensCenter * 0.5f) * 0.5f, y + h*0.5f);
            }
            else {
            	glUniform2f(LensCenterLocation, x + (w - Constants.LensCenter * 0.5f) * 0.5f, y + h*0.5f);
            }
            float r = 1 + Constants.LensCenter;
            float scale = distfunc(r);
            
            float scaleFactor = 1f/ scale;
            
            glUniform2f(ScreenCenterLocation, x + w*0.5f, y + h*0.5f);
            glUniform2f(ScaleLocation, (w/2.0f) * scaleFactor, (h/2.0f) * scaleFactor * as);
            glUniform2f(ScaleInLocation, (2.0f/w), (2.0f/h) / as);
    
            glUniform4f(HmdWarpParamLocation, Constants.K0, Constants.K1, Constants.K2, Constants.K3);
        }
        
        if (eye == Player.LeftEye) {
            glBegin(GL_TRIANGLE_STRIP);
                glTexCoord2f(0.0f, 0.0f);   glVertex2f(-1.0f, -1.0f);
                glTexCoord2f(0.5f, 0.0f);   glVertex2f(0.0f, -1.0f);
                glTexCoord2f(0.0f, 1.0f);   glVertex2f(-1.0f, 1.0f);
                glTexCoord2f(0.5f, 1.0f);   glVertex2f(0.0f, 1.0f);
            glEnd();
        }
        else {
            glBegin(GL_TRIANGLE_STRIP);
                glTexCoord2f(0.5f, 0.0f);   glVertex2f(0.0f, -1.0f);
                glTexCoord2f(1.0f, 0.0f);   glVertex2f(1.0f, -1.0f);
                glTexCoord2f(0.5f, 1.0f);   glVertex2f(0.0f, 1.0f);
                glTexCoord2f(1.0f, 1.0f);   glVertex2f(1.0f, 1.0f);
            glEnd();            
        }
    }
    
    protected int initShaders(String vertexShader, String fragmentShader) {
        int shaderId = glCreateProgram();

        int vertShader=createVertShader(vertexShader);
        int fragShader=createFragShader(fragmentShader);
        Util.checkGLError();

        if (vertShader != 0 && fragShader != 0) {
            glAttachShader(shaderId, vertShader);
            glAttachShader(shaderId, fragShader);

            glLinkProgram(shaderId);
            if (glGetProgram(shaderId, GL_LINK_STATUS) == GL_FALSE) {
                System.out.println("Linkage error");
                printLogInfo(shaderId);
                System.exit(0);
            }

            glValidateProgram(shaderId);
            if (glGetProgram(shaderId, GL_VALIDATE_STATUS) == GL_FALSE) {
                printLogInfo(shaderId);
                System.exit(0);
            }
        } else {
            System.out.println("No shaders");
            System.exit(0);
        }
        Util.checkGLError();
        return shaderId;
    }

    public void validate() {
        glValidateProgram(usedShader);
        if (glGetProgram(usedShader, GL_VALIDATE_STATUS) == GL_FALSE) {
            printLogInfo(usedShader);
        }
    }

    private int createVertShader(String vertexCode){
        int vertShader=glCreateShader(GL_VERTEX_SHADER);

        if (vertShader==0) {
            return 0;
        }

        glShaderSource(vertShader, vertexCode);
        glCompileShader(vertShader);

        if (glGetShader(vertShader, GL_COMPILE_STATUS) == GL_FALSE) {
            printLogInfo(vertShader);
            vertShader=0;
        }
        return vertShader;
    }

    private int createFragShader(String fragCode){
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        if (fragShader==0) {
            return 0;
        }
        glShaderSource(fragShader, fragCode);
        glCompileShader(fragShader);
        if (glGetShader(fragShader, GL_COMPILE_STATUS) == GL_FALSE) {
            printLogInfo(fragShader);
            fragShader=0;
        }
        return fragShader;
    }

    protected static boolean printLogInfo(int obj){
        IntBuffer iVal = BufferUtils.createIntBuffer(1);
        glGetShader(obj,GL_INFO_LOG_LENGTH, iVal);

        int length = iVal.get();
        if (length > 1) {
            ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            glGetShaderInfoLog(obj, iVal, infoLog);
            byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            String out = new String(infoBytes);
            System.out.println("Info log:\n"+out);
            return false;
        }
        else {
            return true;
        }
    }
}
