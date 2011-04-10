package feystein.renderer;

import feynstein.geometry.*;
import feynstein.utilities.*;

import java.awt.*;
import javax.media.opengl.*;
import java.swing.*;
import com.sun.opengl.util.*;

public class FeynsteinRenderer extends GLCanvas implements GLEventListener {
	
    private static GLU glu;
    private FPSAnimator animator;
    private GLCanvas canvas;
    private float x1,y1,z1,x2,y2,z2,x3,y3,z3;
	private Mesh mesh;
	
    public FeynsteinRenderer(GLCapabilities capabilities, int width, int height) {
        addGLEventListener(this);
        
    }
	
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL(drawable.getGL()));
        final GL gl = drawable.getGL();
		canvas = new GLCanvas();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glShadeModel(GL.GL_SMOOTH);
		
        //clear
        gl.glClearColor(0f, 0f, 0f, 0f);
        
		//perspective
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
        // make glu
        glu = new GLU();
        
        animator = new FPSAnimator(canvas);
        animator.setRunAsFastAsPossible( false );
        animator.start();
    }
	
    public void display(GLAutoDrawable drawable) {
        final GL gl = drawable.getGL();
		
        // this line clears the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
        // this line sets the camera
        setCamera(gl, glu, 100);
		
        // this part creates the actual triangle
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_TRIANGLE_STRIP);
        gl.glVertex3f(x1, y1, z1);
        gl.glVertex3f(x2, y2, z2);
        gl.glVertex3f(x3, y3, z3);
        gl.glEnd();
    }
	
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }
	
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
	
    private void setCamera(GL gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public static void exit() {
        animator.stop();
        System.exit(0);
    }
    
    public void setTriangleLocation(float a1,float b1,float c1,float a2,float b2,float c2,float a3,float b3,float c3){
    	x1 = a1;
    	y1 = b1;
    	z1 = c1;
    	x2 = a2;
    	y2 = b2;
    	z2 = c2;
    	x3 = a3;
    	y3 = b3;
    	z3 = c3;
    }
    
	public void setTriangleMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
    public void renderToScreen(GLAutoDrawable drawable){
 		JPanel panel = new JPanel();
		panel.add(canvas);
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
    }
	
}
