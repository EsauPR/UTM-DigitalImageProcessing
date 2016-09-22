
public class rgb {
	int R;
	int G;
	int B;
	
	public rgb(int r, int g, int b) {
		super();
		R = r;
		G = g;
		B = b;
	}
    public rgb() {
		super();
		R = 0;
		G = 0;
		B = 0;
	}

	public void setR(int r) {
		R = r;
	}
	public void setG(int g) {
		G = g;
	}
	public void setB(int b) {
		B = b;
	}

	public int getR() {
		return R;
	}
	public int getG() {
		return G;
	}
	public int getB() {
		return B;
	}
	
	
}
