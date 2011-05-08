package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

import java.util.ArrayList;

public class TriangleForce extends Force<TriangleForce> {
    private Shape actsOn;
    private double nu, Y;
	private double [] undefVerts;
	
	/*
	 * The TriangleForce is based on a triangle stencil
	 * involving three particles. This force resists both stretching and
	 * compressing the triangular formation and depends upon the undeformed
	 * lengths of the triangle edges, as well as the forces tensile modulus,
	 * a measure of material stiffness, and its Poisson ratio, which relates
	 * material compression to extraction.
	 */
    public TriangleForce() {
		super(3);
		objectType = "TriangleForce";
    }

    public TriangleForce set_actsOn(Shape s) {
		// add a force for each triangle in the mesh
		for(Triangle t : s.getLocalMesh().getTriangles()) {
			for (int i = 0; i < 3; i++) {
				stencil.add(t.getIdx(i));
			}
		}
		actsOn = s;
		
		return this;
    }

    public TriangleForce set_poisson(double poisson) {
		nu = poisson;
		return this;
    }

    public TriangleForce set_youngs(double youngs) {
		Y = youngs;
		return this;
    }
	
	public double[] getLocalForce(double [] globalPositions,
								  double [] globalVelocities,
								  double [] globalMasses) {
		if(undefVerts == null) {
			undefVerts = new double[3*stencil.size()];
			for(int i = 0; i < stencil.size(); i ++) {
				undefVerts[3*i] = globalPositions[3*stencil.get(i)];
				undefVerts[3*i+1] = globalPositions[3*stencil.get(i)+1];
				undefVerts[3*i+2] = globalPositions[3*stencil.get(i)+2];
			}
		}
		
		int n = stencil.size();
		if(localForce == null)
			localForce = new double[3*n];
		
		int base0, base1, base2;
		double x1u, y1u, z1u, x2u, y2u, z2u, x3u, y3u, z3u;
		double x1d, y1d, z1d, x2d, y2d, z2d, x3d, y3d, z3d;
		for(int i = 0; i < stencil.size(); i += stencilSize) {
			// Load undeformed vert positions
			x1u = undefVerts[3*i];
			y1u = undefVerts[3*i+1];
			z1u = undefVerts[3*i+2];
			
			x2u = undefVerts[3*i+3];
			y2u = undefVerts[3*i+4];
			z2u = undefVerts[3*i+5];
			
			x3u = undefVerts[3*i+6];
			y3u = undefVerts[3*i+7];
			z3u = undefVerts[3*i+8];
			
			base0 = 3*stencil.get(i);
			base1 = 3*stencil.get(i+1);
			base2 = 3*stencil.get(i+2);
			// Load deformed vert positions
			x1d = globalPositions[base0];
			y1d = globalPositions[base0+1];
			z1d = globalPositions[base0+2];
			
			x2d = globalPositions[base1];
			y2d = globalPositions[base1+1];
			z2d = globalPositions[base1+2];
			
			x3d = globalPositions[base2];
			y3d = globalPositions[base2+1];
			z3d = globalPositions[base2+2];
			
			// Particle 1, Force x
			//--------------------
			
			double Fx1 = -(Y*((((2*(-1 + nu)*(x1d - x3d)*(square(x1u - x2u) + square(y1u - y2u) + 
														  square(z1u - z2u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								nu*(-(((-2*x1d + x2d + x3d)*
									   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									(2*(-x1d + x2d)*(square(x1u - x3u) + square(y1u - y3u) + 
													 square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
								((-1 + nu)*(2*x1d - x2d - x3d)*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-(x3d*((2*(square(x1u - x2u) + square(y1u - y2u) + 
														 square(z1u - z2u)))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) + 
											  (2*x1d*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											  (2*x1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  (x2d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											  nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													  (z1d - z2d)*(z1d - z3d))*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
														  y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(x3d*((-2*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										   (z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
									  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) - 
							   (2*x1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   (2*x1d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
							   (x2d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((nu*((2*(-x1d + x3d)*(square(x1u - x2u) + square(y1u - y2u) + 
													 square(z1u - z2u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
									((-2*x1d + x2d + x3d)*
									 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
								((-1 + nu)*(2*x1d - x2d - x3d)*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								(2*(-1 + nu)*(x1d - x2d)*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
									   (z1d - z2d)*(z1d - z3d))*
									  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-(((2*x1d - x2d - x3d)*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(x1d - x2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((2*x1d - x2d - x3d)*
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   (2*(x1d - x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			// Particle 1, Force y
			//--------------------
			
			double Fy1 = -(Y*((((2*(-1 + nu)*(y1d - y3d)*(square(x1u - x2u) + square(y1u - y2u) + 
														  square(z1u - z2u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								nu*(-(((-2*y1d + y2d + y3d)*
									   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									(2*(-y1d + y2d)*(square(x1u - x3u) + square(y1u - y3u) + 
													 square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
								((-1 + nu)*(2*y1d - y2d - y3d)*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-(y3d*((2*(square(x1u - x2u) + square(y1u - y2u) + 
														 square(z1u - z2u)))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) + 
											  (2*y1d*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											  (2*y1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  (y2d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											  nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													  (z1d - z2d)*(z1d - z3d))*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
														  y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(y3d*((-2*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										   (z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
									  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) - 
							   (2*y1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   (2*y1d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
							   (y2d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((nu*((2*(-y1d + y3d)*(square(x1u - x2u) + square(y1u - y2u) + 
													 square(z1u - z2u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
									((-2*y1d + y2d + y3d)*
									 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
								((-1 + nu)*(2*y1d - y2d - y3d)*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								(2*(-1 + nu)*(y1d - y2d)*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
									   (z1d - z2d)*(z1d - z3d))*
									  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-(((2*y1d - y2d - y3d)*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(y1d - y2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((2*y1d - y2d - y3d)*
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   (2*(y1d - y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			// Particle 1, Force z
			//--------------------
			
			double Fz1 = -(Y*(((-(z3d*((2*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
									   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											 (z1u - z2u)*(z1u - z3u)) + 
										(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
										(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
									   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u))/
									   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											 (z1u - z2u)*(z1u - z3u)) + 
										(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
										(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) + 
								(2*z1d*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								(2*z1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								(z2d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
										(z1d - z2d)*(z1d - z3d))*
									   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
								((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
											y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-1 + ((square(x1u - x2u) + square(y1u - y2u) + 
													 square(z1u - z2u))*
													(square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 ((2*(-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (z1d - z3d))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  nu*(-(((-2*z1d + z2d + z3d)*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												  (2*(-z1d + z2d)*(square(x1u - x3u) + square(y1u - y3u) + 
																   square(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*(2*z1d - z2d - z3d)*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + 
														square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (-(((2*z1d - z2d - z3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																	  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(z1d - z2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
													 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
													((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (nu*((2*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (-z1d + z3d))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												  ((-2*z1d + z2d + z3d)*
												   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*(2*z1d - z2d - z3d)*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  (2*(-1 + nu)*(z1d - z2d)*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(z3d*((-2*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										   (z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
									  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))) - 
							   (2*z1d*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   (2*z1d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
							   (z2d*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (2*z1d - z2d - z3d))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								 (2*(z1d - z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			// Particle 2, Force x
			//--------------------
			
			double Fx2 = -(Y*(((nu*(-(((x1d - x3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									(2*(x1d - x2d)*(square(x1u - x3u) + square(y1u - y3u) + 
													square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((-1 + nu)*(x1d - x3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - (((x1d - x3d)*
											  (-(((-1 + nu)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															 (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											   (nu*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(-1 + nu)*(x1d - x2d)*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													 (z1d - z2d)*(z1d - z3d))*
													((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - ((-(((x1d - x3d)*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(x1d - x2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((-x1d + x3d)*
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   (2*(-x1d + x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																(z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((x1d - x3d)*(1 + ((-1 + nu)*
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													(z1d - z2d)*(z1d - z3d))*
												   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
														y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  (2*(-x1d + x3d)*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			// Particle 2, Force y
			//--------------------
			
			double Fy2 = -(Y*(((nu*(-(((y1d - y3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									(2*(y1d - y2d)*(square(x1u - x3u) + square(y1u - y3u) + 
													square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((-1 + nu)*(y1d - y3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - (((y1d - y3d)*
											  (-(((-1 + nu)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															 (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											   (nu*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(-1 + nu)*(y1d - y2d)*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													 (z1d - z2d)*(z1d - z3d))*
													((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - ((-(((y1d - y3d)*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(y1d - y2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((-y1d + y3d)*
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   (2*(-y1d + y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																(z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((y1d - y3d)*(1 + ((-1 + nu)*
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													(z1d - z2d)*(z1d - z3d))*
												   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
														y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  (2*(-y1d + y3d)*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			// Particle 2, Force z
			//--------------------
			
			double Fz2 = -(Y*(((-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								  (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (nu*(-(((z1d - z3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
									  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											(z1u - z2u)*(z1u - z3u)) + 
									   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
									(2*(z1d - z2d)*(square(x1u - x3u) + square(y1u - y3u) + 
													square(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((-1 + nu)*(z1d - z3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - ((1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + 
														square(z1u - z2u))*
													   (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													  (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														(z1d - z2d)*(z1d - z3d))*
													   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
													  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															(z1u - z2u)*(z1u - z3u)) + 
													   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											  ((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												(z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (-(((z1d - z3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(z1d - z2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) - ((-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
													 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
													((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 ((z1d - z3d)*(-(((-1 + nu)*
															  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
															 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																   (z1u - z2u)*(z1u - z3u)) + 
															  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
															  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
														   (nu*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																(z1u - z2u)*(z1u - z3u)))/
														   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																 (z1u - z2u)*(z1u - z3u)) + 
															(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
															(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  (2*(-1 + nu)*(z1d - z2d)*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(-z1d + z3d))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   (2*(-z1d + z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																(z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
								   (z1u - z2u)*(z1u - z3u)))/
								 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)) + 
								  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							   ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((z1d - z3d)*(1 + ((-1 + nu)*
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													(z1d - z2d)*(z1d - z3d))*
												   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
												((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
														y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  (2*(-z1d + z3d)*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			
			// Particle 3, Force x
			//--------------------
			
			double Fx3 = -(Y*(-((((2*(-1 + nu)*(x1d - x3d)*(square(x1u - x2u) + square(y1u - y2u) + 
															square(z1u - z2u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((-1 + nu)*(x1d - x2d)*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  (nu*(-x1d + x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
								 (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
										(square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
									y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
								(-1 + 2*nu)) - (((2*(x1d - x3d)*
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 ((x1d - x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
												(1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														 (z1d - z2d)*(z1d - z3d))*
														((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														 (z1u - z2u)*(z1u - z3u)))/
													   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															 (z1u - z2u)*(z1u - z3u)) + 
														(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
														(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
													 ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
												 ((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
															 y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
												  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  ((-2*(-x1d + x3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   ((-x1d + x2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((nu*((2*(x1d - x3d)*(square(x1u - x2u) + square(y1u - y2u) + 
													square(z1u - z2u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
									((x1d - x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((-1 + nu)*(x1d - x2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
									   (z1d - z2d)*(z1d - z3d))*
									  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + (2*(-x1d + x2d)*
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							  ((x1d - x2d)*(1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + 
													  square(z1u - z2u))*
													 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													(((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													  (z1d - z2d)*(z1d - z3d))*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			// Particle 3, Force y
			//--------------------
			
			double Fy3 = -(Y*(-((((2*(-1 + nu)*(y1d - y3d)*(square(x1u - x2u) + square(y1u - y2u) + 
															square(z1u - z2u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((-1 + nu)*(y1d - y2d)*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  (nu*(-y1d + y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
								 (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
										(square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
									y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
								(-1 + 2*nu)) - (((2*(y1d - y3d)*
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 ((y1d - y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
												(1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														 (z1d - z2d)*(z1d - z3d))*
														((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														 (z1u - z2u)*(z1u - z3u)))/
													   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															 (z1u - z2u)*(z1u - z3u)) + 
														(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
														(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
													 ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
												 ((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
															 y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
												  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  ((-2*(-y1d + y3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   ((-y1d + y2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  ((nu*((2*(y1d - y3d)*(square(x1u - x2u) + square(y1u - y2u) + 
													square(z1u - z2u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
									((y1d - y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
									(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										  (z1u - z2u)*(z1u - z3u)) + 
									 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
									 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((-1 + nu)*(y1d - y2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
									   (z1d - z2d)*(z1d - z3d))*
									  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									   (z1u - z2u)*(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
								((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + (2*(-y1d + y2d)*
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
												 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)))/
												(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							  ((y1d - y2d)*(1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + 
													  square(z1u - z2u))*
													 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													(((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													  (z1d - z2d)*(z1d - z3d))*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			
			// Particle 3, Force z
			//--------------------
			
			double Fz3 = -(Y*(-((((2*(-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (z1d - z3d))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((-1 + nu)*(z1d - z2d)*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  (nu*(-z1d + z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
								 (-1 + ((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
										(square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
								  ((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
									y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
								(-1 + 2*nu)) - (((2*(square(x1u - x2u) + square(y1u - y2u) + 
													 square(z1u - z2u))*(z1d - z3d))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 ((z1d - z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
												(1 + ((-1 + nu)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												 nu*(-((((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
														 (z1d - z2d)*(z1d - z3d))*
														((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														 (z1u - z2u)*(z1u - z3u)))/
													   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
															 (z1u - z2u)*(z1u - z3u)) + 
														(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
														(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
													 ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
													 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														   (z1u - z2u)*(z1u - z3u)) + 
													  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
												 ((-1 + nu)*(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + 
															 y2d*y3d - y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
												  ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												   (z1u - z2u)*(z1u - z3u)))/
												 (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													   (z1u - z2u)*(z1u - z3u)) + 
												  (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												  (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + ((nu*((2*(square(x1u - x2u) + square(y1u - y2u) + 
													  square(z1u - z2u))*(z1d - z3d))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
												  ((z1d - z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																(z1u - z2u)*(z1u - z3u)))/
												  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														(z1u - z2u)*(z1u - z3u)) + 
												   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
											  ((-1 + nu)*(z1d - z2d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
																	  (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
											 (-1 - (((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													 (z1d - z2d)*(z1d - z3d))*
													((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											  ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													(z1u - z2u)*(z1u - z3u)) + 
											   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-1 + 2*nu) + 2*(((square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
												 y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d)))/
											   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													 (z1u - z2u)*(z1u - z3u)) + 
												(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
											   ((square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
												((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))
											   /(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)) + 
												 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
												 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							  ((-2*(-z1d + z3d)*((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							   ((-z1d + z2d)*(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
							   (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									 (z1u - z2u)*(z1u - z3u)) + 
								(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
							  (2*(-z1d + z2d)*(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (-(((square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d))*
								   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)))/
								  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
										(z1u - z2u)*(z1u - z3u)) + 
								   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) + 
								((square(x1d) + x2d*x3d - x1d*(x2d + x3d) + square(y1d) + y2d*y3d - 
								  y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
								(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									  (z1u - z2u)*(z1u - z3u)) + 
								 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
								 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))/
							  (-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
									(z1u - z2u)*(z1u - z3u)) + 
							   (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
							   (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
							  ((z1d - z2d)*(1 - nu*(((square(x1u - x2u) + square(y1u - y2u) + 
													  square(z1u - z2u))*
													 (square(x1d - x3d) + square(y1d - y3d) + square(z1d - z3d)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) - 
													(((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - y1d*(y2d + y3d) + 
													  (z1d - z2d)*(z1d - z3d))*
													 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
													  (z1u - z2u)*(z1u - z3u)))/
													(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
														  (z1u - z2u)*(z1u - z3u)) + 
													 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
													 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))) - 
											((-1 + nu)*((x1d - x2d)*(x1d - x3d) + square(y1d) + y2d*y3d - 
														y1d*(y2d + y3d) + (z1d - z2d)*(z1d - z3d))*
											 ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
											  (z1u - z2u)*(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))) + 
											((-1 + nu)*(square(x1d - x2d) + square(y1d - y2d) + square(z1d - z2d))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))/
											(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												  (z1u - z2u)*(z1u - z3u)) + 
											 (square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											 (square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u))))*
							   ((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + (z1u - z2u)*(z1u - z3u)))/
							  ((-1 + 2*nu)*(-square((x1u - x2u)*(x1u - x3u) + (y1u - y2u)*(y1u - y3u) + 
												 (z1u - z2u)*(z1u - z3u)) + 
											(square(x1u - x2u) + square(y1u - y2u) + square(z1u - z2u))*
											(square(x1u - x3u) + square(y1u - y3u) + square(z1u - z3u)))))*
						   Math.sqrt(square(Math.abs(x2u*y1u - x3u*y1u - x1u*y2u + x3u*y2u + x1u*y3u - x2u*y3u)) + 
								square(Math.abs(x2u*z1u - x3u*z1u - x1u*z2u + x3u*z2u + x1u*z3u - x2u*z3u)) + 
								square(Math.abs(y2u*z1u - y3u*z1u - y1u*z2u + y3u*z2u + y1u*z3u - y2u*z3u))))/
			(16.*(1 + nu));
			
			// Sanity check -- sum of internal forces should be zero
			//assert( approxEqual( Fx1+Fx2+Fx3, 0.0 ) );
			//assert( approxEqual( Fy1+Fy2+Fy3, 0.0 ) );
			//assert( approxEqual( Fz1+Fz2+Fz3, 0.0 ) );
			
			localForce[3*i]   = Fx1;
			localForce[3*i+1] = Fy1;
			localForce[3*i+2] = Fz1;
			
			localForce[3*i+3]   = Fx2;
			localForce[3*i+4] = Fy2;
			localForce[3*i+5] = Fz2;
			
			localForce[3*i+6]   = Fx3;
			localForce[3*i+7] = Fy3;
			localForce[3*i+8] = Fz3;
			
		}
		
		return localForce;
	}
	
	private double square(double x) {
		return x*x;
	}
}
