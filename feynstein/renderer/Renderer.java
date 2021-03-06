package feynstein.renderer;

import feynstein.*;
import feynstein.properties.collision.*;
import feynstein.geometry.*;
import feynstein.utilities.*;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT; 

import com.jogamp.opengl.util.Animator;


/*
 * The Feynstein Render using Java OpenGL (JOGL)
 */
public class Renderer implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
  float rotateT = 0.0f;

  GLU glu = new GLU();
  GLUT glut = new GLUT();

  static GLCanvas canvas = new GLCanvas();

  static Frame frame = new Frame("Feynstein");

  static Animator animator = new Animator(canvas);

  //camera position on the z axis
  float zpos = 100.0f;
  //camera angle with X axis
  double theta_X = 0.0;
  //camera angle with Y axis
  double theta_Y = 0.0;
  //camera angle with X axis
  double delta_X = 0.0;
  //camera angle with Y axis
  double delta_Y = 0.0;
  //mouse coordinates
  int mouse_X = 0;
  int mouse_Y = 0;
  boolean rotateCamera = false;
  static boolean paused = true;

  private final Scene scene;

  public Renderer (Scene scene) {
    this.scene = scene;
  }

  private void setColor(GL2 gl, float r, float g, float b) {
    gl.glColorMaterial(gl.GL_FRONT_AND_BACK, gl.GL_AMBIENT_AND_DIFFUSE);
    gl.glColor3f(r, g, b);
    /*
    float color[] = {r, g, b};
    gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_AMBIENT_AND_DIFFUSE, color, 0);
    */
  }

  public void display(GLAutoDrawable gLDrawable) {
    final GL2 gl = gLDrawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();
    gl.glPushMatrix();
    // moving the camera to zpos is the same as moving all the drawn
    // primitives to -zpos:
    gl.glTranslated(0, 0, -zpos);
    // rotate primitives relative to the camera
    gl.glRotated(theta_Y, 0.0, 1.0, 0.0);
    gl.glRotated(theta_X, 1.0, 0.0, 0.0);

    Vector3d pos, pos0, pos1, pos2, norm;
    Particle p0, p1, p2;

    // render tris
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
    setColor(gl, 0.4f, 1.0f, 0.0f);
    gl.glBegin(GL.GL_TRIANGLES);

    for (Triangle tri: scene.getMesh().getTriangles()) {
      p0 = scene.getMesh().getParticles().get(tri.getIdx(0));
      pos0 = p0.getPos();

      p1 = scene.getMesh().getParticles().get(tri.getIdx(1));
      pos1 = p1.getPos();

      p2 = scene.getMesh().getParticles().get(tri.getIdx(2));
      pos2 = p2.getPos();

      norm = pos1.minus(pos0).cross(pos2.minus(pos0)).normalize();
      gl.glNormal3d(norm.x(), norm.y(), norm.z());

      if (p0.isFixed()) {
        setColor(gl, 1.0f, 0.4f, 0.0f);
      } else {
        setColor(gl, 0.0f, 1.0f, 0.4f);
      }
      gl.glVertex3d(pos0.x(), pos0.y(), pos0.z());

      if (p0.isFixed()) {
        setColor(gl, 1.0f, 0.4f, 0.0f);
      } else {
        setColor(gl, 0.0f, 1.0f, 0.4f);
      }
      gl.glVertex3d(pos1.x(), pos1.y(), pos1.z());

      if (p0.isFixed()) {
        setColor(gl, 1.0f, 0.4f, 0.0f);
      } else {
        setColor(gl, 0.0f, 1.0f, 0.4f);
      }
      gl.glVertex3d(pos2.x(), pos2.y(), pos2.z());
    }

    gl.glEnd();

    // render edges
    setColor(gl, 0f, 0f, 0f);
    gl.glBegin(GL.GL_LINES);
    for (Edge e: scene.getMesh().getEdges()) {
      pos = scene.getMesh().getParticles().get(e.getIdx(0)).getPos();
      gl.glVertex3d(pos.x(), pos.y(), pos.z());

      pos = scene.getMesh().getParticles().get(e.getIdx(1)).getPos();
      gl.glVertex3d(pos.x(), pos.y(), pos.z());
    }
    gl.glEnd();

    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
    // render particles
    for (Particle p: scene.getMesh().getParticles()) {
      if (p.getSize() > 0) {
        if (p.isFixed())
          setColor(gl, 1.0f, 0.4f, 0.0f);
        else
          setColor(gl, 0.0f, 1.0f, 0.4f);

        gl.glPushMatrix();
        gl.glTranslated(p.getPos().x(), p.getPos().y(), p.getPos().z());
        glut.glutSolidSphere(p.getSize(), 20, 20);
        gl.glPopMatrix();
      }
    }

    gl.glEnd();
    gl.glPopMatrix();

    if(!paused)
      scene.update();
  }

  public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
  }

  public void init(GLAutoDrawable gLDrawable) {
    GL2 gl = gLDrawable.getGL().getGL2();
    gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

    ((Component) gLDrawable).addKeyListener(this);
    ((Component) gLDrawable).addMouseListener(this);
    ((Component) gLDrawable).addMouseMotionListener(this);
    // turn on lighting
    gl.glEnable (gl.GL_LIGHTING);

    // Setup and enable light 0
    gl.glEnable (gl.GL_LIGHT0);

    // specify the values for the ambient, specular, and diffuse terms:
    float ambLight[] = { 0.1f, 0.1f, 0.1f, 1.0f };
    float specLight[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    float diffLight[] = { 0.25f, 0.25f, 0.25f, 1.0f };
    float position[] = { 0, 10f, -10f };

    // set light 0 to use those values:
    gl.glLightfv (gl.GL_LIGHT0, gl.GL_AMBIENT, ambLight, 0);
    gl.glLightfv (gl.GL_LIGHT0, gl.GL_SPECULAR, specLight, 0);
    gl.glLightfv (gl.GL_LIGHT0, gl.GL_DIFFUSE, diffLight, 0);
    gl.glLightfv (gl.GL_LIGHT0, gl.GL_POSITION, position, 0);

    // set material color
    gl.glEnable (gl.GL_COLOR_MATERIAL);

    // add to materials specular on the front side of geometry
    gl.glMaterialfv (gl.GL_FRONT, gl.GL_SPECULAR, specLight, 0);
    gl.glMateriali (gl.GL_FRONT, gl.GL_SHININESS, 100);

    // put the light in a fixed position RELATIVE TO THE CAMERA
    float lightXYZ[] = { 50.f, 50.0f, 100.0f, 0.0f };
    gl.glLightfv (gl.GL_LIGHT0, gl.GL_POSITION, lightXYZ, 0);

  }

  /* Resize open gl window */
  public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
    GL2 gl = gLDrawable.getGL().getGL2();
    if (height <= 0) {
      height = 1;
    }
    float h = (float) width / (float) height;
    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(50.0f, h, 1.0, 1000.0);
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  /* Key controls for zoom in/out */
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      exit();
    }
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      paused = !paused;
    }
    //zoom out
    if (e.getKeyCode() == 'X') {
      zpos++;
    }
    //zoom in
    if (e.getKeyCode() == 'Z') {
      zpos--;
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  // mouse event
  public  void mouseClicked(MouseEvent e) {

  }

  public void	mouseEntered(MouseEvent e) {

  }

  public void	mouseExited(MouseEvent e) {

  }

  public void	mousePressed(MouseEvent e) {
    mouse_X = e.getX();
    mouse_Y = e.getY();
  }

  /*
   * Sets new camera rotation 
   */
  public void	mouseReleased(MouseEvent e) {
    // update the elevation and roll of the camera
    theta_X += delta_X;
    theta_Y += delta_Y;

    // reset the change in elevation and roll of the camera
    delta_X = delta_Y = 0.0;
  }

  /*
   * Rotates the camera on mouse move 
   */
  public void	mouseDragged(MouseEvent e) {
    double mouse_dx = e.getX() - mouse_X;
    double mouse_dy = e.getY() - mouse_Y;
    delta_X =  mouse_dy/5.0;
    delta_Y = mouse_dx/5.0;
    theta_X += delta_X;
    theta_Y += delta_Y;
    // reset the change in elevation and roll of the camera
    delta_X = delta_Y = 0.0;
    mouse_X = e.getX();
    mouse_Y = e.getY();
  }

  public void	mouseMoved(MouseEvent e) {

  }

  public static void exit() {
    animator.stop();
    frame.dispose();
    System.exit(0);
  }

  public void dispose(GLAutoDrawable gLDrawable) {
    // do nothing
  }
}
