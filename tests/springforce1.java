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
public class springforce1 extends Scene { 
  @Override public void createShapes() { 
    m = 1;
    addShape((new SpringChain()).set_name("spring1").set_vert(0,0,0).set_vert(0,1,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring2").set_vert(1,0,0).set_vert(1,2,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring3").set_vert(2,0,0).set_vert(2,3,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring4").set_vert(3,0,0).set_vert(3,4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring5").set_vert(4,0,0).set_vert(4,5,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring6").set_vert(0,-1,0).set_vert(0,-2,0).set_mass(1).compile());
    addShape((new SpringChain()).set_name("spring7").set_vert(1,-1,0).set_vert(1,-2,0).set_mass(2).compile());
    addShape((new SpringChain()).set_name("spring8").set_vert(2,-1,0).set_vert(2,-2,0).set_mass(3).compile());
    addShape((new SpringChain()).set_name("spring9").set_vert(3,-1,0).set_vert(3,-2,0).set_mass(4).compile());
    addShape((new SpringChain()).set_name("spring10").set_vert(4,-1,0).set_vert(4,-2,0).set_mass(5).compile());
    addShape((new SpringChain()).set_name("spring11").set_vert(0,-3,0).set_vert(0,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring12").set_vert(1,-3,0).set_vert(1,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring13").set_vert(2,-3,0).set_vert(2,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring14").set_vert(3,-3,0).set_vert(3,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring15").set_vert(4,-3,0).set_vert(4,-4,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring16").set_vert(0,-5,0).set_vert(0,-6,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring17").set_vert(1,-5,0).set_vert(1,-6,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring18").set_vert(2,-5,0).set_vert(2,-6,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring19").set_vert(3,-5,0).set_vert(3,-6,0).set_mass(m).compile());
    addShape((new SpringChain()).set_name("spring20").set_vert(4,-5,0).set_vert(4,-6,0).set_mass(m).compile());
  }
  @Override public void createForces() { 
    addForce((new SpringForce()).set_actsOn(getShape("spring1")).set_strength(10).set_length(1).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring2")).set_strength(10).set_length(2).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring3")).set_strength(10).set_length(3).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring4")).set_strength(10).set_length(4).compile());
    addForce((new SpringForce()).set_actsOn(getShape("spring5")).set_strength(10).set_length(5).compile());
    addForce(SpringForce(actsOngetShape("spring6"), strength=1, length=0.8));
    addForce(SpringForce(actsOngetShape("spring6"), strength=2, length=0.8));
    addForce(SpringForce(actsOngetShape("spring6"), strength=3, length=0.8));
    addForce(SpringForce(actsOngetShape("spring6"), strength=4, length=0.8));
    addForce(SpringForce(actsOngetShape("spring6"), strength=5, length=0.8));
    addForce(SpringForce(actsOngetShape("spring6"), strength=1, length=0.8));
    addForce(SpringForce(actsOngetShape("spring7"), strength=2, length=0.8));
    addForce(SpringForce(actsOngetShape("spring8"), strength=4, length=0.8));
    addForce(SpringForce(actsOngetShape("spring9"), strength=8, length=0.8));
    addForce(SpringForce(actsOngetShape("spring10"), strength=16, length=0.8));
    addForce(SpringForce(actsOngetShape("spring11"), strength=1, length=0.9));
    addForce(SpringForce(actsOngetShape("spring12"), strength=1, length=0.8));
    addForce(SpringForce(actsOngetShape("spring13"), strength=1, length=0.7));
    addForce(SpringForce(actsOngetShape("spring14"), strength=1, length=0.6));
    addForce(SpringForce(actsOngetShape("spring15"), strength=1, length=0.5));
  }
  @Override public void setProperties() { 
    addProperty((new SemiImplicitEuler(this)).set_stepSize(0.01).compile());
  }
  @Override public void onFrame() {};
  public static void main(String[] args) {
    springforce1 scene = new springforce1();
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
