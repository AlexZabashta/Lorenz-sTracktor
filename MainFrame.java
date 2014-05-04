import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.WindowConstants;

public class MainFrame {

	public static void main(String[] args) {

		Random rnd = new Random();

		List<Plot3D> testPlot = new ArrayList<Plot3D>();

		double[][][] points = new double[3][3][30000];
		for (int d = 0; d < 3; d++) {
			for (int i = 0; i < 30000; i++) {
				points[d][d][i] = i / 100.0;
			}
		}

		for (int d = 0; d < 3; d++) {
			testPlot.add(new Plot3D(points[d][0], points[d][1], points[d][2], 0xFFFFFFFF));
		}

		int n = 10000;
		double r = rnd.nextDouble() * 100;

		double sx = rnd.nextDouble() * 20;
		double sy = rnd.nextDouble() * 20;
		double sz = rnd.nextDouble() * 20;

		for (int i = 0; i < 3; i++) {
			double[][] g = LorenzSystemSolver.solove(i, r, sx + i, sy + i, sz + i, 0.001, n);
			testPlot.add(new Plot3D(g[0], g[1], g[2], 0xFF << (8 * i)));

		}

		PlotViewer pw = new PlotViewer(testPlot);
		pw.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pw.setVisible(true);
	}
}