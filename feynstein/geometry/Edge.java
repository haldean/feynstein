package feynstein.geometry;

public class Edge {
	private int [] idx;
	
	public Edge (int idx1, int idx2) {
		idx = new int[2];
		idx[0] = idx1;
		idx[1] = idx2;
	}
	
	public int getIdx(int index) {
		if(index < 2)
			return idx[index];
		return -1;
	}
}