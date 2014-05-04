import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlotViewer extends JFrame {

	List<Plot3D> plots;

	private static final long serialVersionUID = 0xe1a_9353;
	final static double PI180 = Math.PI / 180.0;

	int width, height;

	double ax, ay, az;
	double bx, by, bz;
	double cx, cy, cz;
	double dx, dy, dz;
	double mx, my, mz;

	int horizontalAngle = 46;
	int verticalAngle = 40;

	double zoom = 32;
	double logZoom = 5;

	double angleOfView = 32;
	double logAngleOfView = 5;

	double speed = 16.0;
	double logSpeed = 4;

	int[] black;
	double[][] depth;
	BufferedImage canvas;
	JLabel jLabel = new JLabel();

	void onResize() {
		width = getWidth();
		height = getHeight();

		setTitle("[R] <screen resized> " + width + "x" + height);

		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		depth = new double[width][height];
		black = new int[width * height];
		Arrays.fill(black, 0xFF000000);
		jLabel.setIcon(new ImageIcon(canvas));
		drawPlot();
	}

	void debugVector(double sx, double sy, double sz, double fx, double fy, double fz) {
		System.out.println(String.format(Locale.ENGLISH, "(%.2f,%.2f,%.2f:%.2f,%.2f,%.2f)", sx, sy, sz, fx, fy, fz));
	}

	void clearCanvas() {
		canvas.setRGB(0, 0, width, height, black, 0, width);
		for (double[] dd : depth) {
			Arrays.fill(dd, Double.POSITIVE_INFINITY);
		}
	}

	void moveVectors() {
		ax += mx;
		bx += mx;
		cx += mx;
		dx += mx;

		ay += my;
		by += my;
		cy += my;
		dy += my;

		az += mz;
		bz += mz;
		cz += mz;
		dz += mz;
	}

	void calculateDirectionVectors() {
		ax = bx - Math.sin((90 - verticalAngle) * PI180) * Math.cos(horizontalAngle * PI180);
		ay = by - Math.sin((90 - verticalAngle) * PI180) * Math.sin(horizontalAngle * PI180);
		az = bz - Math.cos((90 - verticalAngle) * PI180);

		if (verticalAngle == 0) {
			cx = bx;
			cy = by;
			cz = bz + 1;
		} else if (verticalAngle == 90) {
			cx = bx - Math.cos(horizontalAngle * PI180);
			cy = by - Math.sin(horizontalAngle * PI180);
			cz = bz;
		} else if (verticalAngle == -90) {
			cx = bx + Math.cos(horizontalAngle * PI180);
			cy = by + Math.sin(horizontalAngle * PI180);
			cz = bz;
		} else {
			double ex = ax, ey = ay, ez = az + 1.0;

			double f = (by * ez - bz * ey) + (az * ey - ay * ez) + (ay * bz - az * by);
			double g = -((bx * ez - bz * ex) + (az * ex - ax * ez) + (ax * bz - az * bx));
			double h = (bx * ey - by * ex) + (ay * ex - ax * ey) + (ax * by - ay * bx);
			double q = ax * (by * ez - bz * ey) - ay * (bx * ez - bz * ex) + az * (bx * ey - by * ex);

			double w = ax - bx;
			double u = ay - by;
			double v = az - bz;
			double t = bx * (ax - bx) + by * (ay - by) + bz * (az - bz);

			double a1 = (v * g - h * u) / (f * u - g * w);
			double b1 = (q * u - g * t) / (f * u - g * w);

			double a2 = (h * w - v * f) / (f * u - g * w);
			double b2 = (f * t - q * w) / (f * u - g * w);

			double da = a1 * a1 + a2 * a2 + 1;
			double db = a1 * (b1 - bx) + a2 * (b2 - by) - bz;
			double dc = (b1 - bx) * (b1 - bx) + (b2 - by) * (b2 - by) + bz * bz - 1;

			cz = (Math.sqrt(db * db - da * dc) - db) / da;
			cx = a1 * cz + b1;
			cy = a2 * cz + b2;
		}

		dx = bx + (ay - by) * (cz - bz) - (az - bz) * (cy - by);
		dy = by + (az - bz) * (cx - bx) - (ax - bx) * (cz - bz);
		dz = bz + (ax - bx) * (cy - by) - (ay - by) * (cx - bx);
	}

	void fillCanvas() {

		double m11 = cx - bx, m12 = dx - bx, m13 = ax - bx;
		double m21 = cy - by, m22 = dy - by, m23 = ay - by;
		double m31 = cz - bz, m32 = dz - bz, m33 = az - bz;

		for (Plot3D plot : plots) {
			for (int p = 0; p < plot.size; p++) {
				double x = m12 * (plot.x[p] - bx) + m22 * (plot.y[p] - by) + m32 * (plot.z[p] - bz);
				double y = m11 * (plot.x[p] - bx) + m21 * (plot.y[p] - by) + m31 * (plot.z[p] - bz);
				double z = m13 * (plot.x[p] - bx) + m23 * (plot.y[p] - by) + m33 * (plot.z[p] - bz);

				if (z > 0) {
					double scale = (z + angleOfView) / (angleOfView * zoom);

					x /= scale;
					y /= scale;

					x += canvas.getWidth() / 2;
					y += canvas.getHeight() / 2;

					if (x >= 0 && x < canvas.getWidth() && y >= 0 && y < canvas.getHeight() && z < depth[(int) x][(int) y]) {

						depth[(int) x][(int) y] = z;

						scale = (0.5 + 0.5 / (z + 1.0));

						int red = (int) ((0xFF & (plot.color[p] >> 16)) * scale);
						int green = (int) ((0xFF & (plot.color[p] >> 8)) * scale);
						int blue = (int) ((0xFF & (plot.color[p] >> 0)) * scale);

						canvas.setRGB((int) x, canvas.getHeight() - (int) y - 1, 0xFF000000 | (red << 16) | (green << 8) | blue);

					}

				}

			}
		}

	}

	void drawPlot() {
		clearCanvas();
		fillCanvas();
		repaint();
	}

	public PlotViewer(List<Plot3D> plots) {
		this.plots = plots;
		setBounds(100, 0, 640, 480);

		calculateDirectionVectors();
		onResize();

		JPanel jPanel = new JPanel();
		jPanel.add(jLabel);
		add(jPanel);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {

					switch (e.getKeyCode()) {
					case 'R':
						onResize();
						break;
					case 32: // SPACE
						mx = speed * (ax - bx);
						my = speed * (ay - by);
						mz = speed * (az - bz);
						moveVectors();

						setTitle("[SPACE] <move forward>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;
					case 16: // SHIFT
						mx = speed * (bx - ax);
						my = speed * (by - ay);
						mz = speed * (bz - az);
						moveVectors();

						setTitle("[SHIFT] <move backward>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;

					case 37: // LEFT
						mx = speed * (bx - dx);
						my = speed * (by - dy);
						mz = speed * (bz - dz);
						moveVectors();

						setTitle("[LEFT] <move left>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;
					case 39: // RIGHT
						mx = speed * (dx - bx);
						my = speed * (dy - by);
						mz = speed * (dz - bz);
						moveVectors();

						setTitle("[RIGHT] <move right>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;

					case 38: // UP
						mx = speed * (cx - bx);
						my = speed * (cy - by);
						mz = speed * (cz - bz);
						moveVectors();

						setTitle("[UP] <move up>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;

					case 40: // DOWN
						mx = speed * (bx - cx);
						my = speed * (by - cy);
						mz = speed * (bz - cz);
						moveVectors();

						setTitle("[DOWN] <move down>  " + String.format("%.2f  %.2f  %.2f", bx, by, bz));
						drawPlot();
						break;

					case 'W':
						verticalAngle = Math.max(-90, verticalAngle - 2);
						calculateDirectionVectors();

						setTitle("[W] <turn up>  " + verticalAngle);
						drawPlot();
						break;
					case 'S':
						verticalAngle = Math.min(90, verticalAngle + 2);
						calculateDirectionVectors();

						setTitle("[S] <turn down>  " + verticalAngle);
						drawPlot();
						break;
					case 'A':
						horizontalAngle = (362 + horizontalAngle) % 360;
						calculateDirectionVectors();

						setTitle("[A] <turn left>  " + verticalAngle);
						drawPlot();
						break;
					case 'D':
						horizontalAngle = (358 + horizontalAngle) % 360;
						calculateDirectionVectors();

						setTitle("[D] <turn right>  " + verticalAngle);
						drawPlot();
						break;
					case 'Q':
						logSpeed = Math.max(logSpeed - 1.0, -4.0);
						speed = Math.pow(2, logSpeed);
						setTitle("[Q] <speed decreased>  " + speed);
						break;
					case 'E':
						logSpeed = Math.min(logSpeed + 1.0, 10.0);
						speed = Math.pow(2, logSpeed);
						setTitle("[E] <speed increased>  " + speed);
						break;
					case 'I':
						logZoom = Math.min(logZoom + 1.0, 10.0);
						zoom = Math.pow(2, logZoom);
						setTitle("[I] <zoom in>  " + zoom);
						drawPlot();
						break;
					case 'O':
						logZoom = Math.max(logZoom - 1.0, -4.0);
						zoom = Math.pow(2, logZoom);
						setTitle("[O] <zoom out>  " + zoom);
						drawPlot();
						break;
					case 'K':
						logAngleOfView = Math.min(logAngleOfView + 1.0, 10.0);
						angleOfView = Math.pow(2, logAngleOfView);
						setTitle("[K] <field of view narrowed>  " + angleOfView);
						drawPlot();
						break;
					case 'L':
						logAngleOfView = Math.max(logAngleOfView - 1.0, -4.0);
						angleOfView = Math.pow(2, logAngleOfView);
						setTitle("[L] <field of view expanded>  " + angleOfView);
						drawPlot();
						break;

					}
				}
				return false;
			}
		});

	}
}
