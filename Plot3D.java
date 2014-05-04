import java.util.Arrays;

public class Plot3D {
	public int rgb;
	public double[] x, y, z;
	public int[] color;
	int size;

	public Plot3D(double[] x, double[] y, double[] z, int rgb) {
		this.x = x;
		size = x.length;
		this.y = y;
		size = Math.min(size, y.length);
		this.z = z;
		size = Math.min(size, z.length);

		this.color = new int[size];
		Arrays.fill(color, rgb);
	}

	public Plot3D(double[] x, double[] y, double[] z, int[] color) {
		this.color = color;
		size = color.length;
		this.x = x;
		size = Math.min(size, x.length);
		this.y = y;
		size = Math.min(size, y.length);
		this.z = z;
		size = Math.min(size, z.length);
	}

}
