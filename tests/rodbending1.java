package tests;
import feynstein.collision.*;
import com.jogamp.opengl.util.*;
import javax.media.opengl.awt.*;
import java.awt.event.*;
import java.awt.*;
import feynstein.properties.integrators.*;
import feynstein.properties.*;
import feynstein.renderer.*;
import feynstein.*;
import feynstein.forces.*;
import feynstein.shapes.*;
public class rodbending1 extends Scene { 
  @Override public void createShapes() { 
    m = 1;
    addShape((new SpringChain()).set_name("spring1").set_vert(0,0,0).set_vert(1,0,0).set_vert(2,0,0).set_vert(3,0,0).set_vert(4,0,0).set_vert(5,0,0).set_vert(6,0,0).set_vert(7,0,0).set_mass(m).set_fixed(0).set_fixed(1).compile());
  }
  @Override public void createForces() { 
    addForce((new RodBendingForce()).set_actsOn(getShape("spring1")).set_strength(1).set_angle(0).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring1")).set_strength(1000).set_length(1).compile());
  }
  @Override public void setProperties() { 
    addProperty((new SemiImplicitEuler(this)).set_stepSize(0.001).compile());
  }
  @Override public void onFrame() {};
  public static void main(String[] args) {
    rodbending1 scene = new rodbending1();
    canvas.addGLEventListener(new Renderer(scene));

    frame.add(canvas);
    frame.setSize(640, 480);
    frame.setUndecorated(true);
    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
    frame.setVisible(true);
    animator.start();
    canvas.requestFocus();
};
}
