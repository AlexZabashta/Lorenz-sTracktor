public class LorenzSystemSolver {

	final static double SIGMA = 10.0, BETA = 8.0 / 3.0;

	static double[][] solove(int m, double r, double sx, double sy, double sz, double dt, int n) {
		switch (m) {
		case 0:
			return simpleEulerMethod(r, sx, sy, sz, dt, n);
		case 1:
			return backwardEulerMethod(r, sx, sy, sz, dt, n);
		case 2:
			return simpleEulerMethod(r, sx, sy, sz, dt, n);
		default:
			return simpleEulerMethod(r, sx, sy, sz, dt, n);
		}
	}

	static double[][] simpleEulerMethod(double r, double sx, double sy, double sz, double dt, int n) {
		double[] x = new double[n], y = new double[n], z = new double[n];
		x[0] = sx;
		y[0] = sy;
		z[0] = sz;

		for (int i = 1; i < n; i++) {
			x[i] = x[i - 1] + (SIGMA * (y[i - 1] - x[i - 1])) * dt;
			y[i] = y[i - 1] + (x[i - 1] * (r - z[i - 1]) - y[i - 1]) * dt;
			z[i] = z[i - 1] + (x[i - 1] * y[i - 1] - BETA * z[i - 1]) * dt;
		}

		return new double[][] { x, y, z };
	}

	static double[][] backwardEulerMethod(double r, double sx, double sy, double sz, double dt, int n) {
		double[] x = new double[n], y = new double[n], z = new double[n];
		x[0] = sx;
		y[0] = sy;
		z[0] = sz;

		for (int i = 1; i < n; i++) {
			double a = 1 + dt * SIGMA, b = -dt * SIGMA, c = x[i - 1];
			double d = dt, e = -dt * r, f = 1 + dt, g = y[i - 1];
			double h = 1 + dt * BETA, k = -dt, m = -z[i - 1];

			double pa = g * a - c * e, pb = -c * d;
			double pg = f * a - b * e, pd = -b * d;
			double px = c * k / a, py = -k * b / a;

			double qa = h * pd * pd;
			double qb = h * pg * pd * 2 + px * pd * pb + pb * pb * py + pd * pd * m;
			double qc = h * pg * pg + px * pd * pa + px * pg * pb + 2 * py * pa * pb + 2 * m * pg * pd;
			double qd = px * pa * pg + py * pa * pa + m * pg * pg;

			double qp = (3 * qa * qc - qb * qb) / (3 * qa * qa);
			double qq = (2 * qb * qb * qb - 9 * qa * qb * qc + 27 * qa * qa * qd) / (27 * qa * qa * qa);

			double wq = Math.pow(qp / 3, 3.0) + Math.pow(qq / 2, 2.0);

			double wa = Math.pow(-qq / 2 + Math.sqrt(wq), 1.0 / 3.0);
			double wb = Math.pow(-qq / 2 - Math.sqrt(wq), 1.0 / 3.0);

			z[i] = wa + wb - qb / (3 * qa);
			y[i] = (pa + pb * z[i]) / (pg + pd * z[i]);
			x[i] = (c / a) - y[i] * (b / a);
		}

		return new double[][] { x, y, z };
	}

}
