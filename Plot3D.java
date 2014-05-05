import java.util.Arrays;

public class Plot3D {
	public int rgb;
	public double[] x, y, z;
	public int[] color;
	int size;

	public Plot3D(double[][] p, int rgb) {
		this.x = p[0];
		size = x.length;
		this.y = p[1];
		size = Math.min(size, y.length);
		this.z = p[2];
		size = Math.min(size, z.length);

		this.color = new int[size];
		Arrays.fill(color, rgb);
	}

	public Plot3D(double[][] p, int[] color) {
		this.color = color;
		size = color.length;
		this.x = p[0];
		size = Math.min(size, x.length);
		this.y = p[1];
		size = Math.min(size, y.length);
		this.z = p[2];
		size = Math.min(size, z.length);
	}

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
