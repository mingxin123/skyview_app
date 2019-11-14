package Core;
/*
 * @author Lou Shin, Ivan Ko, Ming Xin, Kevin Velazquez
 * The Planets class stores all the information we need for each planet, and has the math function we need to calculate their position.
 */
import javafx.scene.paint.Color;

public class Planets {

	// Heliocentric Osculating Orbital Elements Referred to the Mean Equinox and
	// Ecliptic of Date for 2013:
	// http://asa.usno.navy.mil/static/files/2013/Osculating_Elements_2013.txt
	// Values of the Osculating Orbital Elements for 8th August 1997:
	// http://www.stargazing.net/kepler/ellipse.html
	// Uncertainties in RA (pre 2050) should be: <400" (Jupiter); <600" (Saturn);
	// <50" everything else
	// See also: https://ssd.jpl.nasa.gov/txt/p_elem_t1.txt
	// https://ssd.jpl.nasa.gov/?planet_pos
	private Planet[] planets = new Planet[] { new Planet(), new Planet(),
			new Planet("Mercury", 2439.7, 0.5, Color.rgb(170,150,170), (d) -> {
				return -0.36 + d[0] + 0.027 * d[1] + 2.2E-13 * Math.pow(d[1], 6);
			}, new double[][] { { 2456280.5, 7.0053, 48.485, 77.658, 0.387100, 4.09232, 0.205636, 191.7001 },
					{ 2456360.5, 7.0052, 48.487, 77.663, 0.387098, 4.09235, 0.205646, 159.0899 },
					{ 2456440.5, 7.0052, 48.490, 77.665, 0.387097, 4.09236, 0.205650, 126.4812 },
					{ 2456520.5, 7.0052, 48.493, 77.669, 0.387098, 4.09235, 0.205645, 93.8725 },
					{ 2456600.5, 7.0052, 48.495, 77.672, 0.387099, 4.09234, 0.205635, 61.2628 },
					{ 2456680.5, 7.0052, 48.498, 77.677, 0.387098, 4.09234, 0.205633, 28.6524 } }),
			new Planet("Venus", 6051.9, 1, Color.rgb(245,222,179), (d) -> {
				return -4.34 + d[0] + 0.013 * d[1] + 4.2E-7 * Math.pow(d[1], 3);
			}, new double[][] { { 2456280.5, 3.3949, 76.797, 132.00, 0.723328, 1.60214, 0.006777, 209.0515 },
					{ 2456360.5, 3.3949, 76.799, 132.07, 0.723327, 1.60215, 0.006787, 337.2248 },
					{ 2456440.5, 3.3949, 76.802, 131.97, 0.723333, 1.60213, 0.006780, 105.3980 },
					{ 2456520.5, 3.3949, 76.804, 131.99, 0.723327, 1.60215, 0.006769, 233.5729 },
					{ 2456600.5, 3.3949, 76.807, 132.03, 0.723326, 1.60215, 0.006775, 1.7475 },
					{ 2456680.5, 3.3948, 76.808, 131.63, 0.723345, 1.60209, 0.006770, 129.9169 } }),
			new Planet("Mars", 3386, 1, Color.rgb(255,50,50), (d) -> {
				return -1.51 + d[0] + 0.016 * d[1];
			}, new double[][] { { 2450680.5, 1.84992, 49.5664, 336.0882, 1.5236365, 0.5240613, 0.0934231, 262.42784 },
					{ 2456320.5, 1.8497, 49.664, 336.249, 1.523605, 0.524079, 0.093274, 338.1493 },
					{ 2456400.5, 1.8497, 49.666, 336.268, 1.523627, 0.524068, 0.093276, 20.0806 },
					{ 2456480.5, 1.8496, 49.668, 336.306, 1.523731, 0.524014, 0.093316, 62.0048 },
					{ 2456560.5, 1.8495, 49.666, 336.329, 1.523748, 0.524005, 0.093385, 103.9196 },
					{ 2456680.5, 1.8495, 49.665, 336.330, 1.523631, 0.524066, 0.093482, 166.8051 } }),
			new Planet("Jupiter", 69173, 10, Color.rgb(255,150,150), (d) -> {
				return -9.25 + d[0] + 0.014 * d[1];
			}, new double[][] { { 2456280.5, 1.3033, 100.624, 14.604, 5.20269, 0.083094, 0.048895, 68.0222 },
					{ 2456360.5, 1.3033, 100.625, 14.588, 5.20262, 0.083095, 0.048895, 74.6719 },
					{ 2456440.5, 1.3033, 100.627, 14.586, 5.20259, 0.083096, 0.048892, 81.3228 },
					{ 2456520.5, 1.3033, 100.629, 14.556, 5.20245, 0.083099, 0.048892, 87.9728 },
					{ 2456600.5, 1.3033, 100.631, 14.576, 5.20254, 0.083097, 0.048907, 94.6223 },
					{ 2456680.5, 1.3033, 100.633, 14.592, 5.20259, 0.083096, 0.048891, 101.2751 } }),
			new Planet("Saturn", 57316, 10, Color.rgb(200,150,150), (d) -> {
				double slon = Math.atan2(d[3], d[2]);
				double slat = Math.atan2(d[4], Math.sqrt(d[2] * d[2] + d[3] * d[3]));
				while (slon < 0) {
					slon += 2 * Math.PI;
				}
				while (slon >= 360) {
					slon -= 2 * Math.PI;
				}
				double ir = Math.toRadians(28.06);
				double Nr = Math.toRadians(169.51 + 3.82E-5 * (d[5] - 2451543.5)); // Compared to J2000 epoch
				double B = Math
						.asin(Math.sin(slat) * Math.cos(ir) - Math.cos(slat) * Math.sin(ir) * Math.sin(slon - Nr));
				return -9.0 + d[0] + 0.044 * d[1] + (-2.6 * Math.sin(Math.abs(B)) + 1.2 * Math.pow(Math.sin(B), 2));
			}, new double[][] { { 2456280.5, 2.4869, 113.732, 90.734, 9.51836, 0.033583, 0.055789, 208.6057 },
					{ 2456360.5, 2.4869, 113.732, 90.979, 9.52024, 0.033574, 0.055794, 211.2797 },
					{ 2456440.5, 2.4869, 113.732, 91.245, 9.52234, 0.033562, 0.055779, 213.9525 },
					{ 2456520.5, 2.4869, 113.732, 91.500, 9.52450, 0.033551, 0.055724, 216.6279 },
					{ 2456600.5, 2.4870, 113.732, 91.727, 9.52630, 0.033541, 0.055691, 219.3014 },
					{ 2456680.5, 2.4870, 113.733, 92.021, 9.52885, 0.033528, 0.055600, 221.9730 } }),
			new Planet("Uranus", 25266, 20, Color.rgb(130,150,255), (d) -> {
				return -7.15 + d[0] + 0.001 * d[1];
			}, new double[][] { { 2456280.5, 0.7726, 74.004, 169.227, 19.2099, 0.011713, 0.046728, 9.1400 },
					{ 2456360.5, 0.7727, 73.997, 169.314, 19.2030, 0.011720, 0.047102, 10.0873 },
					{ 2456440.5, 0.7728, 73.989, 169.434, 19.1953, 0.011727, 0.047509, 11.0340 },
					{ 2456520.5, 0.7728, 73.989, 169.602, 19.1882, 0.011733, 0.047874, 11.9756 },
					{ 2456600.5, 0.7728, 73.985, 169.740, 19.1816, 0.011739, 0.048215, 12.9200 },
					{ 2456680.5, 0.7728, 73.983, 169.962, 19.1729, 0.011747, 0.048650, 13.8617 } }),
			new Planet("Neptune", 24553, 20, Color.rgb(100,100,255), (d) -> {
				return -6.90 + d[0] + 0.001 * d[1];
			}, new double[][] { { 2456280.5, 1.7686, 131.930, 53.89, 30.0401, 0.005990, 0.010281, 333.6121 },
					{ 2456360.5, 1.7688, 131.935, 56.47, 30.0259, 0.005994, 0.010138, 334.0856 },
					{ 2456440.5, 1.7690, 131.940, 59.24, 30.0108, 0.005999, 0.009985, 334.5566 },
					{ 2456520.5, 1.7692, 131.946, 61.52, 29.9987, 0.006002, 0.009816, 335.0233 },
					{ 2456600.5, 1.7694, 131.951, 63.84, 29.9867, 0.006006, 0.009690, 335.4937 },
					{ 2456680.5, 1.7697, 131.957, 66.66, 29.9725, 0.006010, 0.009508, 335.9564 } }) };
	private final Planet earth = new Planet();
//	private final double AUinkm = 149597870.700;
	/**
	 * Build an array containing all the planets.
	 * @param jd = the Julian Date to calculate from
	 * @return
	 */
	public Planet[] build(double jd) {
		for (int i = 2; i < planets.length; i++)
			buildPlanet(planets[i], jd, 365.25);
		return planets;
	}

	/**
	 * Build the data array for a particular planet
	 * @param planet = planet object
	 * @param jd = the Julian Date to calculate from
	 * @param days = the number of days to calculate ephemerides for
	 */
	private void buildPlanet(Planet planet, double jd, double days) {
		double interval = planet.interval;

		// Build an array of the form:
		// { { jd_1, ra_1, dec_1, mag_1 }, { jd_2, ra_2, dec_2, mag_2 }....}
		int n = (int) Math.floor(days / interval);
		planet.data = new double[n][4];
		double jdcurr = jd;
		for (int i = 0; i < n; i++) {
			jdcurr += interval;
			double[] coord = this.getEphem(planet, jdcurr);
			planet.data[i][0] = jdcurr;
			planet.data[i][1] = coord[0];
			planet.data[i][2] = coord[1];
			planet.data[i][3] = coord[2];
		}
	}

	/**
	 * Get the ephemeris for the specified planet number
	 * Method from http://www.stargazing.net/kepler/ellipse.html#twig06
	 * @param planet = planet object
	 * @param day	= Jualian Date to calculate the ephemeris for
	 * @return
	 */
	private double[] getEphem(Planet planet, double day) {

		// Heliocentric coordinates of planet
		double[] v = getHeliocentric(planet, day);

		// Heliocentric coordinates of Earth
		double[] e = getHeliocentric(earth, day);

		// Geocentric ecliptic coordinates of the planet
		double x = v[0] - e[0];
		double y = v[1] - e[1];
		double z = v[2] - e[2];

		// Geocentric equatorial coordinates of the planet
		double ec = Math.toRadians(23.439292); // obliquity of the ecliptic for the epoch the elements are referred to
		double[] q = new double[] { x, y * Math.cos(ec) - z * Math.sin(ec), y * Math.sin(ec) + z * Math.cos(ec) };

		double ra = Math.toDegrees(Math.atan(q[1] / q[0]));
		if (q[0] < 0)
			ra += 180;
		if (q[0] >= 0 && q[1] < 0)
			ra += 360;

		double dc = Math.toDegrees(Math.atan(q[2] / Math.sqrt(q[0] * q[0] + q[1] * q[1])));

		double R = Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);

		// Calculate the magnitude (http://stjarnhimlen.se/comp/tutorial.html)
		//double angdiam = (planet.radius * 2 / (R * this.AUinkm));
		double mag = 1;

		// planet's heliocentric distance, v.r, its geocentric distance, R, and the
		// distance to the Sun, e.r.
		double FV = Math.acos((v[3] * v[3] + R * R - e[3] * e[3]) / (2 * v[3] * R));
		//double phase = (1 + Math.cos(FV)) / 2;
		mag = planet.magnitude.m(new double[] { 5 * Math.log10(v[3] * R), Math.toDegrees(FV), x, y, z, day });

		return new double[] { ra, dc, mag };
	}
	/**
	 * Method to create an array of doubles with the heliocentric coordinates of the planets.
	 * @param planet = planet object
	 * @param jd = julian date
	 * @return
	 */
	private double[] getHeliocentric(Planet planet, double jd) {
		double min = 1e10;
		int i = 0;
		double mn, d, M, v, r;

		// Choose a set of orbital elements
		// Loop over elements and pick the one closest in time
		for (int j = 0; j < planet.elements.length; j++) {
			mn = Math.abs(planet.elements[j][0] - jd);
			if (mn < min) {
				i = j;
				min = mn;
			}
		}
		double[] p = planet.elements[i];

		// The day number is the number of days (decimal) since epoch of elements.
		d = (jd - p[0]);

		// Heliocentric coordinates of planet
		M = meanAnomaly(p[5], d, p[7], p[3]);
		v = trueAnomaly(Math.toRadians(M), p[6]);
		r = p[4] * (1 - Math.pow(p[6], 2)) / (1 + p[6] * Math.cos(Math.toRadians(v)));
		double[] xyz = heliocentric(Math.toRadians(v), r, Math.toRadians(p[3]), Math.toRadians(p[2]),
				Math.toRadians(p[1]));
		return new double[] { xyz[0], xyz[1], xyz[2], r };
	}

	/**
	 * Find the Mean Anomaly (M, degrees) of the planet where
	 * n is daily motion
	 * d is the number of days since the date of the elements
	 * L is the mean longitude (deg)
	 * p is the longitude of perihelion (deg)
	 * M should be in range 0 to 360 degrees
	 * @param d
	 * @param n
	 * @param L
	 * @param p
	 * @return
	 */
	private double meanAnomaly(double d, double n, double L, double p) {
		double M = n * d + L - p;
		while (M < 0) {
			M += 360;
		}
		while (M >= 360) {
			M -= 360;
		}
		return M;
	}

	/**
	 * Heliocentric coordinates of the planet where:
	 * o is longitude of ascending node (radians)
	 * p is longitude of perihelion (radians)
	 * i is inclination of plane of orbit (radians)
	 * the quantity v + o - p is the angle of the planet measured in the plane of
	 * the orbit from the ascending node
	 * @param v
	 * @param r
	 * @param p
	 * @param o
	 * @param i
	 * @return
	 */
	private double[] heliocentric(double v, double r, double p, double o, double i) {
		double vpo = v + p - o;
		double svpo = Math.sin(vpo);
		double cvpo = Math.cos(vpo);
		double co = Math.cos(o);
		double so = Math.sin(o);
		double ci = Math.cos(i);
		double si = Math.sin(i);
		return new double[] { r * (co * cvpo - so * svpo * ci), r * (so * cvpo + co * svpo * ci), r * (svpo * si) };
	}

	/**
	 * Find the True Anomaly given m - the 'mean anomaly' in orbit theory (in
	 * radians) ecc - the eccentricity of the orbit
	 * @param m
	 * @param ecc
	 * @return
	 */
	private double trueAnomaly(double m, double ecc) {
		double e = m; // first guess
		double delta = 0.05; // set delta equal to a dummy value
		double eps = 10; // eps - the precision parameter - solution will be within 10^-eps of the true
							// value. Don't set eps above 14, as convergence can't be guaranteed

		while (Math.abs(delta) >= Math.pow(10, -eps)) { // converged?
			delta = e - ecc * Math.sin(e) - m; // new error
			e -= delta / (1 - ecc * Math.cos(e)); // corrected guess
		}
		double v = 2 * Math.atan(Math.pow(((1 + ecc) / (1 - ecc)), 0.5) * Math.tan(0.5 * e));
		if (v < 0)
			v += Math.PI * 2;
		return Math.toDegrees(v); // return estimate
	}
}
