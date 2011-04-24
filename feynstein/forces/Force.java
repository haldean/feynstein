package feynstein.forces;

import feynstein.Built;

import java.util.ArrayList;

public abstract class Force<E extends Force> extends Built<E> {
	protected int stencilSize;
	protected ArrayList<Integer> stencil;

    public Force(int stencilSize) {
		objectType = "Force";
		this.stencilSize = stencilSize;
		stencil = new ArrayList<Integer>(stencilSize);
    }
}