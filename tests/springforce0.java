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
public class springforce0 extends Scene { 
  @Override public void createShapes() { 
    m = 1;
    addShape((new SpringChain()).set_name("spring1").set_vert(0,0,0).set_vert(0,1,0).set_mass(m).set_fixed(1).compile());
    addShape((new SpringChain()).set_name("spring2").set_vert(1,0,0).set_vert(1,1,0).set_mass(m).set_fixed(1).compile());
    addShape((new SpringChain()).set_name("spring3").set_vert(2,0,0).set_vert(2,1,0).set_mass(m).set_fixed(1).compile());
    addShape((new SpringChain()).set_name("spring4").set_vert(3,0,0).set_vert(3,1,0).set_mass(m).set_fixed(1).compile());
    addShape((new SpringChain()).set_name("spring5").set_vert(4,0,0).set_vert(4,1,0).set_mass(m).set_fixed(1).compile());
    addShape((new SpringChain()).set_name("spring6").set_vert(0,-7,0).set_vert(0,-8,0).set_mass(1).set_fixed(0).compile());
    addShape((new SpringChain()).set_name("spring7").set_vert(1,-7,0).set_vert(1,-8,0).set_mass(2).set_fixed(0).compile());
    addShape((new SpringChain()).set_name("spring8").set_vert(2,-7,0).set_vert(2,-8,0).set_mass(3).set_fixed(0).compile());
    addShape((new SpringChain()).set_name("spring9").set_vert(3,-7,0).set_vert(3,-8,0).set_mass(4).set_fixed(0).compile());
    addShape((new SpringChain()).set_name("spring10").set_vert(4,-7,0).set_vert(4,-8,0).set_mass(5).set_fixed(0).compile());
  }
  @Override public void createForces() { 
    addForce((new SpringForce()).set_actsOn(getShape("spring1")).set_strength(1).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring2")).set_strength(2).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring3")).set_strength(4).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring4")).set_strength(8).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring5")).set_strength(16).set_length(1).compile());
    addForce(SpringForce(actsOngetShape("spring6"), strength=1, length=1));
    addForce(SpringForce(actsOngetShape("spring7"), strength=1, length=1));
    addForce(SpringForce(actsOngetShape("spring8"), strength=1, length=1));
    addForce(SpringForce(actsOngetShape("spring9"), strength=1, length=1));
    addForce(SpringForce(actsOngetShape("spring10"), strength=1, length=1));
  }
  @Override public void setProperties() { 
    addProperty((new SemiImplicitEuler(this)).set_stepSize(0.01).compile());
  }
  @Override public void onFrame() {};
  public static void main(String[] args) {
    springforce0 scene = new springforce0();
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
