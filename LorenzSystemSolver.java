public class LorenzSystemSolver {

	final static double SIGMA = 10.0, BETA = 8.0 / 3.0;

	static double[][] solove(int m, double r, double sx, double sy, double sz, double dt, int n) {
		switch (m) {
		case 0:
			return simpleEulerMethod(r, sx, sy, sz, dt, n);
		case 1:
			return backwardEulerMethod(r, sx, sy, sz, dt, n);
		case 2:
			return rungeKuttaMethod(r, sx, sy, sz, dt, n);
		default:
			return predictorCorrectorAdamsBashforthMethod(r, sx, sy, sz, dt, n);
		}
	}

	static double dX(double x, double y) {
		return SIGMA * (y - x);
	}

	static double dY(double x, double y, double z, double r) {
		return x * (r - z) - y;
	}

	static double dZ(double x, double y, double z) {
		return x * y - BETA * z;
	}

	static double[][] simpleEulerMethod(double r, double sx, double sy, double sz, double dt, int n) {
		double[] x = new double[n], y = new double[n], z = new double[n];
		x[0] = sx;
		y[0] = sy;
		z[0] = sz;

		for (int i = 1; i < n; i++) {
			x[i] = x[i - 1] + dX(x[i - 1], y[i - 1]) * dt;
			y[i] = y[i - 1] + dY(x[i - 1], y[i - 1], z[i - 1], r) * dt;
			z[i] = z[i - 1] + dZ(x[i - 1], y[i - 1], z[i - 1]) * dt;
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

	static double[][] rungeKuttaMethod(double r, double sx, double sy, double sz, double dt, int n) {
		double[] x = new double[n], y = new double[n], z = new double[n];
		x[0] = sx;
		y[0] = sy;
		z[0] = sz;

		for (int i = 1; i < n; i++) {

			double kx1 = dX(x[i - 1], y[i - 1]) * dt / 2;
			double ky1 = dY(x[i - 1], y[i - 1], z[i - 1], r) * dt / 2;
			double kz1 = dZ(x[i - 1], y[i - 1], z[i - 1]) * dt / 2;

			double kx2 = dX(x[i - 1] + kx1, y[i - 1] + ky1) * dt / 2;
			double ky2 = dY(x[i - 1] + kx1, y[i - 1] + ky1, z[i - 1] + kz1, r) * dt / 2;
			double kz2 = dZ(x[i - 1] + kx1, y[i - 1] + ky1, z[i - 1] + kz1) * dt / 2;

			double kx3 = dX(x[i - 1] + kx2, y[i - 1] + ky2) * dt;
			double ky3 = dY(x[i - 1] + kx2, y[i - 1] + ky2, z[i - 1] + kz2, r) * dt;
			double kz3 = dZ(x[i - 1] + kx2, y[i - 1] + ky2, z[i - 1] + kz2) * dt;

			double kx4 = dX(x[i - 1] + kx3, y[i - 1] + ky3) * dt;
			double ky4 = dY(x[i - 1] + kx3, y[i - 1] + ky3, z[i - 1] + kz3, r) * dt;
			double kz4 = dZ(x[i - 1] + kx3, y[i - 1] + ky3, z[i - 1] + kz3) * dt;

			x[i] = x[i - 1] + kx1 / 3 + 2 * kx2 / 3 + kx3 / 3 + kx4 / 6;
			y[i] = y[i - 1] + ky1 / 3 + 2 * ky2 / 3 + ky3 / 3 + ky4 / 6;
			z[i] = z[i - 1] + kz1 / 3 + 2 * kz2 / 3 + kz3 / 3 + kz4 / 6;

		}

		return new double[][] { x, y, z };
	}

	static double[][] predictorCorrectorAdamsBashforthMethod(double r, double sx, double sy, double sz, double dt, int n) {

		if (n < 5) {
			return backwardEulerMethod(r, sx, sy, sz, dt, n);
		}

		double[] pk = { 0, 55 * dt / 24, -59 * dt / 24, 37 * dt / 24, -9 * dt / 24 };
		double[] ck = { 9 * dt / 24, 19 * dt / 24, -5 * dt / 24, dt / 24 };

		double[][] bem = backwardEulerMethod(r, sx, sy, sz, dt, 4);
		double[] x = new double[n], y = new double[n], z = new double[n];

		System.arraycopy(bem[0], 0, x, 0, 4);
		System.arraycopy(bem[1], 0, y, 0, 4);
		System.arraycopy(bem[2], 0, z, 0, 4);

		for (int i = 4; i < n; i++) {
			double px = x[i - 1];
			for (int j = 1; j <= 4; j++) {
				px += dX(x[i - j], y[i - j]) * pk[j];
			}

			double py = y[i - 1];
			for (int j = 1; j <= 4; j++) {
				py += dY(x[i - j], y[i - j], z[i - j], r) * pk[j];
			}

			double pz = z[i - 1];
			for (int j = 1; j <= 4; j++) {
				pz += dZ(x[i - j], y[i - j], z[i - j]) * pk[j];
			}

			x[i] = x[i - 1] + dX(px, py) * ck[0];
			for (int j = 1; j <= 3; j++) {
				x[i] += dX(x[i - j], y[i - j]) * ck[j];
			}

			y[i] = y[i - 1] + dY(px, py, pz, r) * ck[0];
			for (int j = 1; j <= 3; j++) {
				y[i] += dY(x[i - j], y[i - j], z[i - j], r) * ck[j];
			}

			z[i] = z[i - 1] + dZ(px, py, pz) * ck[0];
			for (int j = 1; j <= 3; j++) {
				z[i] += dZ(x[i - j], y[i - j], z[i - j]) * ck[j];
			}

		}

		return new double[][] { x, y, z };
	}
}
