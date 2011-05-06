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
public class rodbending0 extends Scene { 
  @Override public void createShapes() { 
    m = 1;
    addShape((new SpringChain()).set_name("spring1").set_vert(0,0,0).set_vert(0,1,0).set_vert(0,2,0).set_mass(m) shape SpringChain(name="spring2").set_vert(2,0,0).set_vert(2,1,0).set_vert(2.707106781186548,1.707106781186548,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring3").set_vert(-2,0,0).set_vert(-2,1,0).set_vert(-3,1,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring4").set_vert(-2,-3,0).set_vert(-2,-2,0).set_vert(-3,-2,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring5").set_vert(0,-3,0).set_vert(0,-2,0).set_vert(-1,-2,0).set_mass(2).compile());
    addShape((new SpringChain()).set_name("spring6").set_vert(2,-3,0).set_vert(2,-2,0).set_vert(1,-2,0).set_mass(4).compile());
    addShape((new SpringChain()).set_name("spring7").set_vert(-2,-5,0).set_vert(-2,-4,0).set_vert(-3,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring8").set_vert(0,-5,0).set_vert(0,-4,0).set_vert(-1,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring9").set_vert(2,-5,0).set_vert(2,-4,0).set_vert(1,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring10").set_vert(-3,-7,0).set_vert(-2,7,0).set_vert(-2,-8,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring11").set_vert(-1,-7,0).set_vert(0,-7,0).set_vert(-0,-8,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring12").set_vert(1,-7,0).set_vert(2,-7,0).set_vert(2,-8,0).set_mass(m).compile());
  }
  @Override public void createForces() { 
    addForce((new RodBendingForce()).set_actsOn(getShape("spring1")).set_strength(1).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring2")).set_strength(1).set_angle(0.785398163397448).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring3")).set_strength(1).set_angle(1.570796326794897).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring4")).set_strength(1).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring5")).set_strength(2).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring6")).set_strength(4).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring7")).set_strength(1).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring8")).set_strength(2).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring9")).set_strength(4).set_angle(0).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring1")).set_strength(1).set_angle(0.3926990816987242).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring1")).set_strength(1).set_angle(0.4487989505128276).compile());
    addForce((new RodBendingForce()).set_actsOn(getShape("spring1")).set_strength(1).set_angle(0.5235987755982989).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring1")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring2")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring3")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring4")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring5")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring6")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring7")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring8")).set_strength(5000).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring9")).set_strength(5000).set_length(1).compile());
  }
  @Override public void setProperties() { 
    addProperty((new SemiImplicitEuler(this)).set_stepSize(0.001).compile());
  }
  @Override public void onFrame() {};
  public static void main(String[] args) {
    rodbending0 scene = new rodbending0();
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
