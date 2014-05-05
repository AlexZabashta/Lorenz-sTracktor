import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import javax.swing.WindowConstants;

public class MainFrame {

	public static void main(String[] args) throws IOException {

		Random rnd = new Random();

		final List<Plot3D> testPlot = new ArrayList<Plot3D>();

		Scanner sc = new Scanner(new File("input.txt"));

		sc.useLocale(Locale.ENGLISH);
		final int m = sc.nextInt(), n = sc.nextInt();
		final double dt = sc.nextDouble(), r = sc.nextDouble();

		final int[] met = new int[m];

		for (int i = 0; i < m; i++) {
			met[i] = sc.nextInt();
			double sx = sc.nextDouble(), sy = sc.nextDouble(), sz = sc.nextDouble();
			int red = sc.nextInt(), green = sc.nextInt(), blue = sc.nextInt();
			int color = (red << 16) | (green << 8) | blue;
			testPlot.add(new Plot3D(LorenzSystemSolver.solove(met[i], r, sx, sy, sz, dt, n), color));
		}

		sc.close();

		int nc = 30000;
		double lc = 100.0;

		double[][] cord = new double[3][nc * 3];
		for (int d = 0; d < 3; d++) {
			for (int i = 0; i < nc; i++) {
				cord[d][nc * d + i] = i * lc / nc;
			}
		}

		testPlot.add(new Plot3D(cord, 0xFFFFFF));

		final PlotViewer pw = new PlotViewer(testPlot);
		pw.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pw.setVisible(true);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			double tr = r;
			int m = testPlot.size() - 1;

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == 45 || e.getKeyCode() == 61) {
						if (e.getKeyCode() == 45) {
							tr = Math.max(1, tr - 1);
						} else {
							tr = Math.min(100, tr + 1);
						}

						for (int i = 0; i < m; i++) {
							double sx = testPlot.get(i).x[0], sy = testPlot.get(i).y[0], sz = testPlot.get(i).z[0];
							double[][] g = LorenzSystemSolver.solove(met[i], tr, sx, sy, sz, dt, n);
							testPlot.get(i).x = g[0];
							testPlot.get(i).y = g[1];
							testPlot.get(i).z = g[2];
						}

						pw.drawPlot();
						pw.setTitle("[+/-] r = " + tr);
					}
				}
				return false;
			}
		});

	}
}