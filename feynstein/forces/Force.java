package feynstein.forces;

import feynstein.Built;

import java.util.ArrayList;

public abstract class Force<E extends Force> extends Built<E> {
	protected int stencilSize;
	protected int meshSize;
	protected ArrayList<Integer> stencil;
	protected double[] localForce;
	
    public Force(int stencilSize) {
		objectType = "Force";
		this.stencilSize = stencilSize;
		stencil = new ArrayList<Integer>();
	}
	
	public boolean isGlobal() {
		return !(stencilSize > 0); 
	}
	
	public int getStencilIdx(int idx) {
		return stencil.get(idx);
	}
	
	public abstract double[] getLocalForce(double [] globalPositions,
										   double [] globalVelocities,
										   double [] globalMasses);
}