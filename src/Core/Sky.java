package Core;
/**
 * @author Lou Shin, Ivan Ko, Ming Xin, Kevin Velazquez
 * The Sky class has all the math methods to calculate the position of the celestial objects (except the planets) and the constellation lines.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

import javafx.scene.paint.Color;

public class Sky {

	private double longitude, latitude, azOffset;
	private double[][] fStars;
	private int[][] lines;
	private Date clock;
	private Planets p = new Planets();
	private HashMap<String, double[]> constellations;

	/**
	 * Constructor for Sky, it sets the date, time, latitude and longitude to a default number.
	 **/
	public Sky() {
		setAzOffset(180);
		setClock(new Date());
		setLongitude(0);
		setLatitude(0);
		Scanner scanner;
		try {
			scanner = new Scanner(new File("stars.json"));
			String starsS = scanner.useDelimiter("\\A").next();
			scanner.close();

			scanner = new Scanner(new File("lines_latin.json"));
			String line = scanner.useDelimiter("\\A").next();
			scanner.close();

			starsS = starsS.substring(starsS.indexOf("1"), starsS.indexOf("]]"));
			String[] starsA = starsS.split("\\],\\[");
			fStars = new double[starsA.length][4];
			for (int i = 0; i < starsA.length; i++)
				for (int j = 0; j < 4; j++)
					fStars[i][j] = Double.valueOf(starsA[i].split(",")[j]);
			fStars = convertStarsToRadians(fStars);

			line = line.substring(line.indexOf("\"A"), line.indexOf("]]"));
			String[] lineA = line.split("\\],\\[");
			lines = new int[673][2];
			constellations = new HashMap<String, double[]>();
			int t = 0;
			for (String s : lineA) {
				String[] temp = s.split(",");
				constellations.put(temp[0].replace("\"", "").replace("\\n", "\n"), new double[] { 0, 0,
						Math.toRadians(Double.parseDouble(temp[1])), Math.toRadians(Double.parseDouble(temp[2])) });
				for (int i = 3; i < temp.length; i += 2) {
					lines[t][0] = Integer.parseInt(temp[i]);
					lines[t][1] = Integer.parseInt(temp[i + 1]);
					t++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the date to the given parameter. Example of date format: Mon Dec 03 13:04:04 MST 2018
	 * @param date
	 */
	public void setClock(Date date) {
		clock = date;
	}
	
	/**
	 * Getter method for clock object.
	 * @return date object that contains the date and time.
	 */
	public Date getClock() {
		return (Date) clock.clone();
	}
	
	/**
	 * Set the offset for azimuth
	 * @param deg
	 */
	public void setAzOffset(double deg) {
		azOffset = inrangeAz(deg, true);
	}

	/**
	 * Getter method for the azimuth offset.
	 * @return azOffset
	 */
	public double getAzOffset() {
		return azOffset;
	}

	/**
	 * Set the latitude (defaults to 0.0 if user doesn't input a different one.) 
	 * @param l
	 */
	public void setLatitude(double l) {
		latitude = inrangeAlt(Math.toRadians(l), false);
	}

	/**
	 * Getter method for latitude.
	 * @return latitude (converted to degrees)
	 */
	public double getLatitude() {
		return Math.toDegrees(latitude);
	}

	/**
	 * Set the latitude (defaults to 0.0 if user doesn't input a different one.)
	 * @return
	 */
	public void setLongitude(double l) {
		longitude = Math.toRadians(l);
		while (longitude <= -Math.PI)
			longitude += 2 * Math.PI;
		while (longitude > Math.PI)
			longitude -= 2 * Math.PI;
	}
	
	/**
	 * Getter method for longitude.
	 * @return longitude (converted to degrees)
	 */
	public double getLongitude() {
		return Math.toDegrees(longitude);
	}
	
	/**
	 * This method returns a HashMap of the stars ID's mapped to an array of doubles which contains its information.
	 * @param w
	 * @param h
	 * @param magnitude
	 * @param stars
	 * @return
	 */
	public HashMap<Integer, double[]> getStars(double w, double h, double magnitude, double[][] stars) {
		HashMap<Integer, double[]> xy = new HashMap<>();
		for (double[] i : stars)
			xy.put((int) i[0], (altaz2xy(coord2horizon(i), i[1], w, h)));
		return xy;
	}
	
	/**
	 * This method returns a HashMap of the stars ID's mapped to an array of doubles which contains its information.
	 * This method calls the other get stars method and returns the HashMap created by it.
	 * @param w
	 * @param h
	 * @param magnitude
	 * @return
	 */
	public HashMap<Integer, double[]> getStars(double w, double h, double magnitude) {
		return getStars(w, h, magnitude, stars);
	}

	/**
	 * Getter method for the Fine Stars, it returns an array of stars ID's mapped to an array of doubles.
	 * @param w
	 * @param h
	 * @param magnitude
	 * @return
	 */
	public HashMap<Integer, double[]> getFineStars(double w, double h, double magnitude) {
		return getStars(w, h, magnitude, fStars);
	}

	/**
	 * Return a HashMap of the stars ID's mapped to an array of doubles. This is for the Messier catalogue stars.
	 * @param w
	 * @param h
	 * @param magnitude
	 * @return
	 */
	public HashMap<Integer, double[]> getMessier(double w, double h, double magnitude) {
		return getStars(w, h, magnitude, messier);
	}

	/**
	 * Return a HashMap of the stars ID'S mapped to an array of doubles. This is for the Cldwell catalogue stars.
	 * @param w
	 * @param h
	 * @param magnitude
	 * @return
	 */
	public HashMap<Integer, double[]> getCaldwell(double w, double h, double magnitude) {
		return getStars(w, h, magnitude, caldwell);
	}

	/**
	 * This method returns an array of Planet objects. Each Planet object contains the data that will be needed to plot the planets.
	 * Like name, color we are using for it, magnitude, x and y positions and altitude.
	 * @param w
	 * @param h
	 * @return
	 */
	public Planet[] getPlanets(double w, double h) {
		double jd = getJD();
		Planet[] planets = p.build(jd - 150);

		// Sun & Moon
		double[] pos = altaz2xy(coord2horizon(ecliptic2radec(sunPos())), 0, w, h);
		planets[0].name = "Sun";
		planets[0].color = Color.rgb(255, 215, 0);
		planets[0].mag = -26;
		planets[0].x = pos[0];
		planets[0].y = pos[1];
		planets[0].alt = pos[3];

		pos = altaz2xy(coord2horizon(ecliptic2radec(moonPos())), 0, w, h);
		planets[1].name = "Moon";
		planets[1].color = Color.rgb(150, 150, 150);
		planets[1].mag = -12;
		planets[1].x = pos[0];
		planets[1].y = pos[1];
		planets[1].alt = pos[3];

		double ra, dec, mag;
		for (int i = 2; i < planets.length; i++) {
			if (jd > planets[i].data[0][0] && jd < planets[i].data[(planets[i].data.length - 1)][0]) {
				double[] interp = this.interpolate(jd, planets[i].data);
				ra = interp[0];
				dec = interp[1];
				mag = interp[2];
			} else {
				planets[i].mag = 7;
				continue; // We don't have data for this planet so skip to the next
			}
			pos = altaz2xy(coord2horizon(new double[] { 0, 0, Math.toRadians(ra), Math.toRadians(dec) }), mag, w, h);
			planets[i].mag = mag;
			planets[i].x = pos[0];
			planets[i].y = pos[1];
			planets[i].alt = pos[3];
		}
		return planets;
	}

	/**
	 * Getter method for the constellations lines.
	 * @return lines
	 */
	public int[][] getLines() {
		return lines;
	}

	/**
	 * Getter method for the constellations labels.
	 * It returns a HashMap of the constellation name to a an array of doubles that contains the points used to draw the lines.
	 * @param w
	 * @param h
	 * @return constellationLabels
	 */
	public HashMap<String, double[]> getConstellationLabels(double w, double h) {
		HashMap<String, double[]> constellationLabels = new HashMap<String, double[]>();
		for (String l : constellations.keySet())
			constellationLabels.put(l, (altaz2xy(coord2horizon(constellations.get(l)), 0, w, h)));
		return constellationLabels;
	}

	/** 
	 * Stars are stored in decimal degrees, this method converts them to radians.
	 * It returns a 2D array of the stars values converted to radians.
	 * @param stars
	 * @return
	 */
	private double[][] convertStarsToRadians(double[][] stars) {
		for (int i = 0; i < stars.length; i++) {
			stars[i][2] = Math.toRadians(stars[i][2]);
			stars[i][3] = Math.toRadians(stars[i][3]);
		}
		return stars;
	}

	/**
	 * When provided with an array of Julian dates, ra, dec, and magnitude this will
	 * interpolate to the nearest
	 * data = { { jd_1, ra_1, dec_1, mag_1 }, { jd_2, ra_2, dec_2, mag_2 }....}
	 * @param jd
	 * @param data
	 * @return
	 */
	private double[] interpolate(double jd, double[][] data) {
		double mindt = jd; // Arbitrary starting value in days
		int mini = 0; // index where we find the minimum
		for (int i = 0; i < data.length; i++) {
			// Find the nearest point to now
			double dt = (jd - data[i][0]);
			if (Math.abs(dt) < Math.abs(mindt)) {
				mindt = dt;
				mini = i;
			}
		}
		double dra, ddec, dmag, fract;
		int pos_2, pos_1;
		if (mindt >= 0) {
			pos_2 = mini + 1;
			pos_1 = mini;
			fract = mindt / Math.abs(data[pos_2][0] - data[pos_1][0]);
		} else {
			pos_2 = mini;
			pos_1 = mini - 1;
			fract = (1 + (mindt) / Math.abs(data[pos_2][0] - data[pos_1][0]));
		}
		// We don't want to attempt to find positions beyond the edges of the array
		if (pos_2 > data.length || pos_1 < 0) {
			dra = data[mini][1];
			ddec = data[mini][2];
			dmag = data[mini][3];
		} else {
			dra = (Math.abs(data[pos_2][1] - data[pos_1][1]) > 180)
					? (data[pos_1][1] + (data[pos_2][1] + 360 - data[pos_1][1]) * fract) % 360
					: (data[pos_1][1] + (data[pos_2][1] - data[pos_1][1]) * fract) % 360;
			ddec = data[pos_1][2] + (data[pos_2][2] - data[pos_1][2]) * fract;
			dmag = data[pos_1][3] + (data[pos_2][3] - data[pos_1][3]) * fract;
		}
		return new double[] { dra, ddec, dmag };
	}

	/**
	 * Convert altitude and azimuth to X and Y.
	 * @param altaz
	 * @param m
	 * @param w
	 * @param h
	 * @return
	 */
	private double[] altaz2xy(double[] altaz, double m, double w, double h) {
		double az = inrangeAz(altaz[1] + Math.toRadians(azOffset), false);
		double r = Math.min(w, h) / 2 * Math.tan((Math.PI - 2 * altaz[0]) / 4);
		double x = w / 2 - r * Math.sin(az);
		double y = h / 2 - r * Math.cos(az);
		return new double[] { x, y, m, altaz[0] };
	}
	
	private double[] ecliptic2radec(double[] lb) {
		double e = meanObliquity();
		double sl = Math.sin(lb[0]);
		double cl = Math.cos(lb[0]);
		double sb = Math.sin(lb[1]);
		double cb = Math.cos(lb[1]);
		double tb = Math.tan(lb[1]);
		double se = Math.sin(e);
		double ce = Math.cos(e);
		double ra = Math.atan2(sl * ce - tb * se, cl);
		double dec = Math.asin(sb * ce + cb * se * sl);
		// Make sure RA is positive
		if (ra < 0)
			ra += 2 * Math.PI;
		return new double[] { 0, 0, ra, dec };
	}

	/**
	 * Method to convert right ascension and declination to altitude and azimuth.
	 * Uses the latitude and longitude inputed by the user (0.0 for both if they are not provided)
	 * @param radec
	 * @return
	 */
	private double[] coord2horizon(double[] radec) {
		double ha, alt, az, sd, sl, cl;
		// compute hour angle in degrees
		ha = Math.PI * getLST() / 12 - radec[2];
		sd = Math.sin(radec[3]);
		sl = Math.sin(this.latitude);
		cl = Math.cos(this.latitude);
		// compute altitude in radians
		alt = Math.asin(sd * sl + Math.cos(radec[3]) * cl * Math.cos(ha));
		// compute azimuth in radians
		// divide by zero error at poles or if alt = 90 deg (so we should've already
		// limited to 89.9999)
		az = Math.acos((sd - Math.sin(alt) * sl) / (Math.cos(alt) * cl));
		// choose hemisphere
		if (Math.sin(ha) > 0)
			az = 2 * Math.PI - az;
		return new double[] { alt, az };
	}
	
	/**
	 * Check to see if the azimuth value is in range.
	 * @param a
	 * @param deg
	 * @return
	 */
	private double inrangeAz(double a, boolean deg) {
		if (deg) {
			while (a < 0)
				a += 360;
			while (a > 360)
				a -= 360;
		} else {
			double twopi = (2 * Math.PI);
			while (a < 0)
				a += twopi;
			while (a > twopi)
				a -= twopi;
		}
		return a;
	}
	
	/**
	 * Check to see if the altitude value is in range.
	 * @param a
	 * @param deg
	 * @return
	 */
	private double inrangeAlt(double a, boolean deg) {
		if (deg) {
			if (a >= 90)
				a = 89.99999;
			if (a <= -90)
				a = -89.99999;
		} else {
			if (a >= Math.PI / 2)
				a = (Math.PI / 2) * 0.999999;
			if (a <= -Math.PI / 2)
				a = (-Math.PI / 2) * 0.999999;
		}
		return a;
	}

	/**
	 * Getter method for the Julian date for the date provided.
	 * @return
	 */
	public double getJD() {
		// The Julian Date of the Unix Time epoch is 2440587.5
		return (clock.getTime() / 86400000.0) + 2440587.5;
	}
	
	/**
	 * Method to calculate the Local Sidereal Time for a given date and time.
	 * @return
	 */
	private double getLST() {
		double lon = Math.toDegrees(longitude), JD, JD0, S, T, T0, UT, A, GST, d, LST;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(clock);
		//get Julian date
		JD = getJD();
		JD0 = Math.floor(JD - .5) + .5;
		S = JD0 - 2451545;
		T = S / 36525;
		T0 = (6.697374558 + (2400.051336 * T) + (0.000025862 * T * T)) % 24;
		if (T0 < 0)
			T0 += 24;
		//Calculate Universal Time
		UT = (((cal.getTimeInMillis() % 1000 / 1000.0 + cal.get(Calendar.SECOND)) / 60.0) + cal.get(Calendar.MINUTE))
				/ 60.0 + cal.get(Calendar.HOUR_OF_DAY);
		A = UT * 1.002737909;
		T0 += A;
		GST = T0 % 24;
		if (GST < 0)
			GST += 24;
		d = (GST + lon / 15) / 24;
		d = d - Math.floor(d);
		if (d < 0)
			d += 1;
		LST = 24 * d;
		return LST;
	}
	
	/**
	 * Method to calculate the Sun's position.
	 * It returns an array of doubles with the Sun's position information.
	 * @return 
	 */
	private double[] sunPos() {
		double D, eg, wg, e, N, Mo, v, lon, lat;
		D = (getJD() - 2455196.5); // Number of days since the epoch of 2010 January 0.0
		// Calculated for epoch 2010.0. If T is the number of Julian centuries since
		// 1900 January 0.5 = (JD-2415020.0)/36525
		eg = 279.557208; // mean ecliptic longitude in degrees = (279.6966778 + 36000.76892*T +
							// 0.0003025*T*T)%360;
		wg = 283.112438; // longitude of the Sun at perigee in degrees = 281.2208444 + 1.719175*T +
							// 0.000452778*T*T;
		e = 0.016705; // eccentricity of the Sun-Earth orbit in degrees = 0.01675104 - 0.0000418*T -
						// 0.000000126*T*T;
		N = ((360 / 365.242191) * D) % 360;
		if (N < 0)
			N += 360;
		Mo = (N + eg - wg) % 360; // mean anomaly in degrees
		if (Mo < 0)
			Mo += 360;
		v = Mo + 360 / Math.PI * e * Math.sin(Math.toRadians(Mo));
		lon = v + wg;
		if (lon > 360)
			lon -= 360;
		lat = 0;

		return new double[] { Math.toRadians(lon), lat, Mo, D, N };
	}
	
	/**
	 * Method to calculate the Moon's position.
	 * Returns an array of doubles that contains the Moon's position information.
	 * @return
	 */
	private double[] moonPos() {
		double lo, Po, No, i, l, Mm, N, C, Ev, sinMo, Ae, A3, Mprimem, Ec, A4, lprime, V, lprimeprime, Nprime, lppNp,
				sinlppNp, y, x, lm, Bm;
		double[] sun = sunPos();
		lo = 91.929336; // Moon's mean longitude at epoch 2010.0
		Po = 130.143076; // mean longitude of the perigee at epoch
		No = 291.682547; // mean longitude of the node at the epoch
		i = 5.145396; // inclination of Moon's orbit
//		e = 0.0549; // eccentricity of the Moon's orbit
		l = (13.1763966 * sun[3] + lo) % 360;
		if (l < 0)
			l += 360;
		Mm = (l - 0.1114041 * sun[3] - Po) % 360;
		if (Mm < 0)
			Mm += 360;
		N = (No - 0.0529539 * sun[3]) % 360;
		if (N < 0)
			N += 360;
		C = l - sun[0];
		Ev = 1.2739 * Math.sin(Math.toRadians(2 * C - Mm));
		sinMo = Math.sin(Math.toRadians(sun[2]));
		Ae = 0.1858 * sinMo;
		A3 = 0.37 * sinMo;
		Mprimem = Mm + Ev - Ae - A3;
		Ec = 6.2886 * Math.sin(Math.toRadians(Mprimem));
		A4 = 0.214 * Math.sin(2 * Math.toRadians(Mprimem));
		lprime = l + Ev + Ec - Ae + A4;
		V = 0.6583 * Math.sin(2 * Math.toRadians(lprime - sun[0]));
		lprimeprime = lprime + V;
		Nprime = N - 0.16 * sinMo;
		lppNp = Math.toRadians(lprimeprime - Nprime);
		sinlppNp = Math.sin(lppNp);
		y = sinlppNp * Math.cos(Math.toRadians(i));
		x = Math.cos(lppNp);
		lm = Math.toDegrees(Math.atan2(y, x)) + Nprime;
		Bm = Math.asin(sinlppNp * Math.sin(Math.toRadians(i)));
		if (lm > 360)
			lm -= 360;
		return new double[] { Math.toRadians(lm), Bm };
	}

	/**
	 * Method to the the mean obliquity (earth's inclination) of the earth for a given Julian date.
	 * @return
	 */
	private double meanObliquity() {
		double JD = getJD();
		double T, T2, T3;
		T = (JD - 2451545.0) / 36525; // centuries since 2451545.0 (2000 January 1.5)
		T2 = T * T;
		T3 = T2 * T;
		return Math.toRadians(23.4392917 - 0.0130041667 * T - 0.00000016667 * T2 + 0.0000005027778 * T3);
	}

	// Data for stars < mag 4.5 or that are a vertex for a constellation line
	// [id, mag, right ascension, declination]
	// index with Hipparcos number
	private double[][] stars = convertStarsToRadians(
			new double[][] { { 677, 2.1, 2.097, 29.09 }, { 746, 2.3, 2.295, 59.15 }, { 765, 3.9, 2.353, -45.75 },
					{ 1067, 2.8, 3.309, 15.18 }, { 1562, 3.6, 4.857, -8.82 }, { 1599, 4.2, 5.018, -64.87 },
					{ 1645, 5.4, 5.149, 8.19 }, { 2021, 2.8, 6.438, -77.25 }, { 2072, 3.9, 6.551, -43.68 },
					{ 2081, 2.4, 6.571, -42.31 }, { 2484, 4.4, 7.886, -62.96 }, { 2920, 3.7, 9.243, 53.9 },
					{ 3092, 3.3, 9.832, 30.86 }, { 3179, 2.2, 10.127, 56.54 }, { 3419, 2, 10.897, -17.99 },
					{ 3760, 5.9, 12.073, 7.3 }, { 3821, 3.5, 12.276, 57.82 }, { 3881, 4.5, 12.454, 41.08 },
					{ 4427, 2.1, 14.177, 60.72 }, { 4436, 3.9, 14.188, 38.5 }, { 4577, 4.3, 14.652, -29.36 },
					{ 4889, 5.5, 15.705, 31.8 }, { 4906, 4.3, 15.736, 7.89 }, { 5165, 3.3, 16.521, -46.72 },
					{ 5348, 3.9, 17.096, -55.25 }, { 5364, 3.5, 17.147, -10.18 }, { 5447, 2.1, 17.433, 35.62 },
					{ 5742, 4.7, 18.437, 24.58 }, { 6193, 4.7, 19.867, 27.26 }, { 6537, 3.6, 21.006, -8.18 },
					{ 6686, 2.7, 21.454, 60.24 }, { 6867, 3.4, 22.091, -43.32 }, { 7007, 4.8, 22.546, 6.14 },
					{ 7083, 3.9, 22.813, -49.07 }, { 7097, 3.6, 22.871, 15.35 }, { 7588, 0.5, 24.429, -57.24 },
					{ 7607, 3.6, 24.498, 48.63 }, { 7884, 4.5, 25.358, 5.49 }, { 8102, 3.5, 26.017, -15.94 },
					{ 8198, 4.3, 26.348, 9.16 }, { 8645, 3.7, 27.865, -10.34 }, { 8796, 3.4, 28.27, 29.58 },
					{ 8832, 3.9, 28.383, 19.29 }, { 8833, 4.6, 28.389, 3.19 }, { 8837, 4.4, 28.411, -46.3 },
					{ 8886, 3.4, 28.599, 63.67 }, { 8903, 2.6, 28.66, 20.81 }, { 9007, 3.7, 28.989, -51.61 },
					{ 9236, 2.9, 29.692, -61.57 }, { 9347, 4, 30.001, -21.08 }, { 9487, 3.8, 30.512, 2.76 },
					{ 9598, 4, 30.859, 72.42 }, { 9640, 2.1, 30.975, 42.33 }, { 9884, 2, 31.793, 23.46 },
					{ 10064, 3, 32.386, 34.99 }, { 10324, 4.4, 33.25, 8.85 }, { 10559, 5.3, 33.985, 33.36 },
					{ 10602, 3.6, 34.127, -51.51 }, { 10826, 6.5, 34.837, -2.98 }, { 11001, 4.1, 35.437, -68.66 },
					{ 11345, 4.9, 36.488, -12.29 }, { 11407, 4.2, 36.746, -47.7 }, { 11484, 4.3, 37.04, 8.46 },
					{ 11767, 2, 37.955, 89.26 }, { 11783, 4.7, 38.022, -15.24 }, { 12093, 4.9, 38.969, 5.59 },
					{ 12387, 4.1, 39.871, 0.33 }, { 12390, 4.8, 39.891, -11.87 }, { 12394, 4.1, 39.897, -68.27 },
					{ 12413, 4.7, 39.95, -42.89 }, { 12484, 5.2, 40.165, -54.55 }, { 12486, 4.1, 40.167, -39.86 },
					{ 12706, 3.5, 40.825, 3.24 }, { 12770, 4.2, 41.031, -13.86 }, { 12828, 4.3, 41.236, 10.11 },
					{ 12843, 4.5, 41.276, -18.57 }, { 13147, 4.5, 42.273, -32.41 }, { 13209, 3.6, 42.496, 27.26 },
					{ 13254, 4.2, 42.646, 38.32 }, { 13268, 3.8, 42.674, 55.9 }, { 13531, 3.9, 43.564, 52.76 },
					{ 13701, 3.9, 44.107, -8.9 }, { 13847, 2.9, 44.565, -40.3 }, { 13954, 4.7, 44.929, 8.91 },
					{ 14135, 2.5, 45.57, 4.09 }, { 14146, 4.1, 45.598, -23.62 }, { 14240, 5.1, 45.903, -59.74 },
					{ 14328, 2.9, 46.199, 53.51 }, { 14354, 3.3, 46.294, 38.84 }, { 14576, 2.1, 47.042, 40.96 },
					{ 14668, 3.8, 47.374, 44.86 }, { 14879, 3.8, 48.019, -28.99 }, { 15197, 4.8, 48.958, -8.82 },
					{ 15474, 3.7, 49.879, -21.76 }, { 15510, 4.3, 49.982, -43.07 }, { 15863, 1.8, 51.081, 49.86 },
					{ 15900, 3.6, 51.203, 9.03 }, { 16083, 3.7, 51.792, 9.73 }, { 16228, 4.2, 52.267, 59.94 },
					{ 16537, 3.7, 53.233, -9.46 }, { 16611, 4.3, 53.447, -21.63 }, { 17358, 3, 55.731, 47.79 },
					{ 17378, 3.5, 55.812, -9.76 }, { 17440, 3.8, 56.05, -64.81 }, { 17448, 3.8, 56.08, 32.29 },
					{ 17499, 3.7, 56.219, 24.11 }, { 17529, 3.8, 56.298, 42.58 }, { 17573, 3.9, 56.457, 24.37 },
					{ 17651, 4.2, 56.712, -23.25 }, { 17678, 3.3, 56.81, -74.24 }, { 17702, 2.9, 56.871, 24.11 },
					{ 17797, 4.3, 57.149, -37.62 }, { 17847, 3.6, 57.291, 24.05 }, { 17874, 4.2, 57.364, -36.2 },
					{ 17959, 4.6, 57.59, 71.33 }, { 18246, 2.8, 58.533, 31.88 }, { 18505, 5, 59.356, 63.07 },
					{ 18532, 2.9, 59.463, 40.01 }, { 18543, 3, 59.507, -13.51 }, { 18597, 4.6, 59.686, -61.4 },
					{ 18614, 4, 59.741, 35.79 }, { 18724, 3.4, 60.17, 12.49 }, { 18907, 3.9, 60.789, 5.99 },
					{ 19343, 4, 62.165, 47.71 }, { 19747, 3.9, 63.5, -42.29 }, { 19780, 3.3, 63.606, -62.47 },
					{ 19893, 4.3, 64.007, -51.49 }, { 19921, 4.4, 64.121, -59.3 }, { 20042, 3.5, 64.474, -33.8 },
					{ 20205, 3.6, 64.948, 15.63 }, { 20455, 3.8, 65.734, 17.54 }, { 20535, 4, 66.009, -34.02 },
					{ 20648, 4.3, 66.372, 17.93 }, { 20885, 3.8, 67.144, 15.96 }, { 20889, 3.5, 67.154, 19.18 },
					{ 20894, 3.4, 67.166, 15.87 }, { 21060, 5.1, 67.709, -44.95 }, { 21281, 3.3, 68.499, -55.04 },
					{ 21393, 3.8, 68.888, -30.56 }, { 21421, 0.9, 68.98, 16.51 }, { 21444, 3.9, 69.08, -3.35 },
					{ 21594, 3.9, 69.545, -14.3 }, { 21770, 4.4, 70.14, -41.86 }, { 21861, 5, 70.515, -37.14 },
					{ 21881, 4.3, 70.561, 22.96 }, { 21949, 5.5, 70.767, -70.93 }, { 22109, 4, 71.376, -3.25 },
					{ 22449, 3.2, 72.46, 6.96 }, { 22509, 4.3, 72.653, 8.9 }, { 22549, 3.7, 72.802, 5.61 },
					{ 22701, 4.4, 73.224, -5.45 }, { 22730, 5.3, 73.345, 2.51 }, { 22783, 4.3, 73.513, 66.34 },
					{ 22797, 3.7, 73.563, 2.44 }, { 22845, 4.6, 73.724, 10.15 }, { 23015, 2.7, 74.248, 33.17 },
					{ 23123, 4.5, 74.637, 1.71 }, { 23416, 3, 75.492, 43.82 }, { 23453, 3.7, 75.62, 41.08 },
					{ 23685, 3.2, 76.365, -22.37 }, { 23767, 3.2, 76.629, 41.23 }, { 23875, 2.8, 76.962, -5.09 },
					{ 23972, 4.3, 77.287, -8.75 }, { 24244, 4.5, 78.075, -11.87 }, { 24305, 3.3, 78.233, -16.21 },
					{ 24327, 4.4, 78.308, -12.94 }, { 24436, 0.2, 78.634, -8.2 }, { 24608, 0.1, 79.172, 46 },
					{ 24674, 3.6, 79.402, -6.84 }, { 24845, 4.3, 79.894, -13.18 }, { 24873, 5.3, 79.996, -12.32 },
					{ 25110, 5.1, 80.64, 79.23 }, { 25281, 3.4, 81.119, -2.4 }, { 25336, 1.6, 81.283, 6.35 },
					{ 25428, 1.6, 81.573, 28.61 }, { 25606, 2.8, 82.061, -20.76 }, { 25859, 3.9, 82.803, -35.47 },
					{ 25918, 5.2, 82.971, -76.34 }, { 25930, 2.3, 83.002, -0.3 }, { 25985, 2.6, 83.183, -17.82 },
					{ 26069, 3.8, 83.406, -62.49 }, { 26207, 3.4, 83.784, 9.93 }, { 26241, 2.8, 83.858, -5.91 },
					{ 26311, 1.7, 84.053, -1.2 }, { 26451, 3, 84.411, 21.14 }, { 26549, 3.8, 84.687, -2.6 },
					{ 26634, 2.6, 84.912, -34.07 }, { 26727, 1.7, 85.19, -1.94 }, { 27072, 3.6, 86.116, -22.45 },
					{ 27100, 4.3, 86.193, -65.74 }, { 27288, 3.5, 86.739, -14.82 }, { 27321, 3.9, 86.821, -51.07 },
					{ 27366, 2.1, 86.939, -9.67 }, { 27530, 4.5, 87.457, -56.17 }, { 27628, 3.1, 87.74, -35.77 },
					{ 27654, 3.8, 87.83, -20.88 }, { 27673, 4, 87.872, 39.15 }, { 27890, 4.7, 88.525, -63.09 },
					{ 27913, 4.4, 88.596, 20.28 }, { 27989, 0.5, 88.793, 7.41 }, { 28103, 3.7, 89.101, -14.17 },
					{ 28199, 4.4, 89.384, -35.28 }, { 28328, 4, 89.787, -42.82 }, { 28358, 3.7, 89.882, 54.28 },
					{ 28360, 1.9, 89.882, 44.95 }, { 28380, 2.6, 89.93, 37.21 }, { 28614, 4.1, 90.596, 9.65 },
					{ 28691, 5.1, 90.864, 19.69 }, { 28734, 4.2, 91.03, 23.26 }, { 28910, 4.7, 91.539, -14.94 },
					{ 29038, 4.4, 91.893, 14.77 }, { 29151, 5.7, 92.241, 2.5 }, { 29426, 4.5, 92.985, 14.21 },
					{ 29651, 4, 93.714, -6.27 }, { 29655, 3.3, 93.719, 22.51 }, { 29807, 4.4, 94.138, -35.14 },
					{ 30060, 4.4, 94.906, 59.01 }, { 30122, 3, 95.078, -30.06 }, { 30277, 3.9, 95.528, -33.44 },
					{ 30324, 2, 95.675, -17.96 }, { 30343, 2.9, 95.74, 22.51 }, { 30419, 4.4, 95.942, 4.59 },
					{ 30438, -0.6, 95.988, -52.7 }, { 30867, 3.8, 97.204, -7.03 }, { 30883, 4.1, 97.241, 20.21 },
					{ 31416, 4.5, 98.764, -22.96 }, { 31592, 4, 99.171, -19.26 }, { 31681, 1.9, 99.428, 16.4 },
					{ 31685, 3.2, 99.44, -43.2 }, { 32246, 3.1, 100.983, 25.13 }, { 32349, -1.4, 101.287, -16.72 },
					{ 32362, 3.4, 101.322, 12.9 }, { 32607, 3.2, 102.048, -61.94 }, { 32759, 3.5, 102.46, -32.51 },
					{ 32768, 2.9, 102.484, -50.61 }, { 33018, 3.6, 103.197, 33.96 }, { 33152, 3.9, 103.533, -24.18 },
					{ 33160, 4.1, 103.547, -12.04 }, { 33165, 6.7, 103.554, -23.93 }, { 33347, 4.4, 104.034, -17.05 },
					{ 33449, 4.3, 104.319, 58.42 }, { 33579, 1.5, 104.656, -28.97 }, { 33856, 3.5, 105.43, -27.93 },
					{ 33977, 3, 105.756, -23.83 }, { 34045, 4.1, 105.94, -15.63 }, { 34088, 4, 106.027, 20.57 },
					{ 34444, 1.8, 107.098, -26.39 }, { 34481, 3.8, 107.187, -70.5 }, { 34693, 4.4, 107.785, 30.25 },
					{ 34769, 4.2, 107.966, -0.49 }, { 35037, 4, 108.703, -26.77 }, { 35228, 4, 109.208, -67.96 },
					{ 35264, 2.7, 109.286, -37.1 }, { 35350, 3.6, 109.523, 16.54 }, { 35550, 3.5, 110.031, 21.98 },
					{ 35904, 2.5, 111.024, -29.3 }, { 36046, 3.8, 111.432, 27.8 }, { 36145, 4.6, 111.679, 49.21 },
					{ 36188, 2.9, 111.788, 8.29 }, { 36377, 3.3, 112.308, -43.3 }, { 36850, 1.6, 113.649, 31.89 },
					{ 36962, 4.1, 113.981, 26.9 }, { 37229, 3.8, 114.708, -26.8 }, { 37279, 0.4, 114.825, 5.22 },
					{ 37447, 3.9, 115.312, -9.55 }, { 37504, 3.9, 115.455, -72.61 }, { 37677, 3.9, 115.952, -28.95 },
					{ 37740, 3.6, 116.112, 24.4 }, { 37819, 3.6, 116.314, -37.97 }, { 37826, 1.2, 116.329, 28.03 },
					{ 38146, 5.3, 117.257, -24.91 }, { 38170, 3.3, 117.324, -24.86 }, { 38414, 3.7, 118.054, -40.58 },
					{ 38827, 3.5, 119.195, -52.98 }, { 39429, 2.2, 120.896, -40 }, { 39757, 2.8, 121.886, -24.3 },
					{ 39794, 4.3, 121.982, -68.62 }, { 39863, 4.4, 122.149, -2.98 }, { 39953, 1.8, 122.383, -47.34 },
					{ 40526, 3.5, 124.129, 9.19 }, { 40702, 4, 124.631, -76.92 }, { 40843, 5.1, 125.016, 27.22 },
					{ 41037, 1.9, 125.628, -59.51 }, { 41075, 4.3, 125.709, 43.19 }, { 41307, 3.9, 126.415, -3.91 },
					{ 41312, 3.8, 126.434, -66.14 }, { 41704, 3.4, 127.566, 60.72 }, { 42313, 4.1, 129.414, 5.7 },
					{ 42402, 4.5, 129.689, 3.34 }, { 42515, 4, 130.026, -35.31 }, { 42536, 3.6, 130.073, -52.92 },
					{ 42568, 4.3, 130.154, -59.76 }, { 42570, 3.8, 130.157, -46.65 }, { 42799, 4.3, 130.806, 3.4 },
					{ 42806, 4.7, 130.821, 21.47 }, { 42828, 3.7, 130.898, -33.19 }, { 42911, 3.9, 131.171, 18.15 },
					{ 42913, 1.9, 131.176, -54.71 }, { 43023, 3.9, 131.507, -46.04 }, { 43103, 4, 131.674, 28.76 },
					{ 43109, 3.4, 131.694, 6.42 }, { 43234, 4.3, 132.108, 5.84 }, { 43409, 4, 132.633, -27.71 },
					{ 43783, 3.8, 133.762, -60.64 }, { 43813, 3.1, 133.848, 5.95 }, { 44066, 4.3, 134.622, 11.86 },
					{ 44127, 3.1, 134.802, 48.04 }, { 44248, 4, 135.16, 41.78 }, { 44382, 4, 135.612, -66.4 },
					{ 44471, 3.6, 135.906, 47.16 }, { 44511, 3.8, 136.039, -47.1 }, { 44700, 4.6, 136.632, 38.45 },
					{ 44816, 2.2, 136.999, -43.43 }, { 45080, 3.4, 137.742, -58.97 }, { 45101, 4, 137.82, -62.32 },
					{ 45238, 1.7, 138.3, -69.72 }, { 45336, 3.9, 138.591, 2.31 }, { 45556, 2.2, 139.273, -59.28 },
					{ 45688, 3.8, 139.711, 36.8 }, { 45860, 3.1, 140.264, 34.39 }, { 45941, 2.5, 140.528, -55.01 },
					{ 46390, 2, 141.897, -8.66 }, { 46509, 4.6, 142.287, -2.77 }, { 46651, 3.6, 142.675, -40.47 },
					{ 46701, 3.2, 142.805, -57.03 }, { 46733, 3.6, 142.882, 63.06 }, { 46776, 4.5, 142.996, -1.18 },
					{ 46853, 3.2, 143.214, 51.68 }, { 46952, 4.5, 143.556, 36.4 }, { 47431, 3.9, 144.964, -1.14 },
					{ 47508, 3.5, 145.288, 9.89 }, { 47854, 3.7, 146.312, -62.51 }, { 47908, 3, 146.463, 23.77 },
					{ 48002, 2.9, 146.776, -65.07 }, { 48319, 3.8, 147.747, 59.04 }, { 48356, 4.1, 147.87, -14.85 },
					{ 48402, 4.5, 148.026, 54.06 }, { 48455, 3.9, 148.191, 26.01 }, { 48774, 3.5, 149.216, -54.57 },
					{ 48926, 5.2, 149.718, -35.89 }, { 49583, 3.5, 151.833, 16.76 }, { 49593, 4.5, 151.857, 35.24 },
					{ 49641, 4.5, 151.985, -0.37 }, { 49669, 1.4, 152.093, 11.97 }, { 49841, 3.6, 152.647, -12.35 },
					{ 50099, 3.3, 153.434, -70.04 }, { 50191, 3.9, 153.684, -42.12 }, { 50335, 3.4, 154.173, 23.42 },
					{ 50371, 3.4, 154.271, -61.33 }, { 50372, 3.5, 154.274, 42.91 }, { 50583, 2, 154.993, 19.84 },
					{ 50801, 3.1, 155.582, 41.5 }, { 50954, 4, 156.099, -74.03 }, { 51069, 3.8, 156.523, -16.84 },
					{ 51172, 4.3, 156.788, -31.07 }, { 51232, 3.8, 156.97, -58.74 }, { 51233, 4.2, 156.971, 36.71 },
					{ 51437, 5.1, 157.573, -0.64 }, { 51576, 3.3, 158.006, -61.69 }, { 51624, 3.8, 158.203, 9.31 },
					{ 51839, 4.1, 158.867, -78.61 }, { 51986, 3.8, 159.326, -48.23 }, { 52419, 2.7, 160.739, -64.39 },
					{ 52468, 4.6, 160.885, -60.57 }, { 52727, 2.7, 161.692, -49.42 }, { 52943, 3.1, 162.406, -16.19 },
					{ 53229, 3.8, 163.328, 34.21 }, { 53253, 3.8, 163.374, -58.85 }, { 53740, 4.1, 164.944, -18.3 },
					{ 53910, 2.3, 165.46, 56.38 }, { 54061, 1.8, 165.932, 61.75 }, { 54463, 3.9, 167.147, -58.98 },
					{ 54539, 3, 167.416, 44.5 }, { 54682, 4.5, 167.915, -22.83 }, { 54872, 2.6, 168.527, 20.52 },
					{ 54879, 3.3, 168.56, 15.43 }, { 55219, 3.5, 169.62, 33.09 }, { 55282, 3.6, 169.835, -14.78 },
					{ 55425, 3.9, 170.252, -54.49 }, { 55687, 4.8, 171.152, -10.86 }, { 55705, 4.1, 171.221, -17.68 },
					{ 56211, 3.8, 172.851, 69.33 }, { 56343, 3.5, 173.25, -31.86 }, { 56480, 4.6, 173.69, -54.26 },
					{ 56561, 3.1, 173.945, -63.02 }, { 56633, 4.7, 174.17, -9.8 }, { 57283, 4.7, 176.191, -18.35 },
					{ 57363, 3.6, 176.402, -66.73 }, { 57380, 4, 176.465, 6.53 }, { 57399, 3.7, 176.513, 47.78 },
					{ 57632, 2.1, 177.265, 14.57 }, { 57757, 3.6, 177.674, 1.76 }, { 57936, 4.3, 178.227, -33.91 },
					{ 58001, 2.4, 178.458, 53.69 }, { 58188, 5.2, 179.004, -17.15 }, { 59196, 2.6, 182.09, -50.72 },
					{ 59199, 4, 182.103, -24.73 }, { 59316, 3, 182.531, -22.62 }, { 59449, 4, 182.913, -52.37 },
					{ 59747, 2.8, 183.786, -58.75 }, { 59774, 3.3, 183.857, 57.03 }, { 59803, 2.6, 183.952, -17.54 },
					{ 60000, 4.2, 184.587, -79.31 }, { 60030, 5.9, 184.668, -0.79 }, { 60129, 3.9, 184.976, -0.67 },
					{ 60260, 3.6, 185.34, -60.4 }, { 60718, 0.8, 186.65, -63.1 }, { 60742, 4.3, 186.734, 28.27 },
					{ 60823, 3.9, 187.01, -50.23 }, { 60965, 2.9, 187.466, -16.52 }, { 61084, 1.6, 187.791, -57.11 },
					{ 61174, 4.3, 188.018, -16.2 }, { 61199, 3.8, 188.117, -72.13 }, { 61281, 3.9, 188.371, 69.79 },
					{ 61317, 4.2, 188.436, 41.36 }, { 61359, 2.6, 188.597, -23.4 }, { 61585, 2.7, 189.296, -69.14 },
					{ 61622, 3.9, 189.426, -48.54 }, { 61932, 2.2, 190.379, -48.96 }, { 61941, 2.7, 190.415, -1.45 },
					{ 62322, 3, 191.57, -68.11 }, { 62434, 1.3, 191.93, -59.69 }, { 62956, 1.8, 193.507, 55.96 },
					{ 63090, 3.4, 193.901, 3.4 }, { 63125, 2.9, 194.007, 38.32 }, { 63608, 2.9, 195.544, 10.96 },
					{ 63613, 3.6, 195.568, -71.55 }, { 64166, 4.9, 197.264, -23.12 }, { 64241, 4.3, 197.497, 17.53 },
					{ 64394, 4.2, 197.968, 27.88 }, { 64962, 3, 199.73, -23.17 }, { 65109, 2.8, 200.149, -36.71 },
					{ 65378, 2.2, 200.981, 54.93 }, { 65474, 1, 201.298, -11.16 }, { 65477, 4, 201.306, 54.99 },
					{ 65936, 3.9, 202.761, -39.41 }, { 66249, 3.4, 203.673, -0.6 }, { 66657, 2.3, 204.972, -53.47 },
					{ 67301, 1.9, 206.885, 49.31 }, { 67459, 4, 207.369, 15.8 }, { 67464, 3.4, 207.376, -41.69 },
					{ 67472, 3.5, 207.404, -42.47 }, { 67927, 2.7, 208.671, 18.4 }, { 68002, 2.5, 208.885, -47.29 },
					{ 68245, 3.8, 209.568, -42.1 }, { 68282, 3.9, 209.67, -44.8 }, { 68520, 4.2, 210.412, 1.54 },
					{ 68702, 0.6, 210.956, -60.37 }, { 68756, 3.7, 211.097, 64.38 }, { 68895, 3.3, 211.593, -26.68 },
					{ 68933, 2.1, 211.671, -36.37 }, { 69427, 4.2, 213.224, -10.27 }, { 69673, -0.1, 213.915, 19.18 },
					{ 69701, 4.1, 214.004, -6 }, { 69996, 3.5, 214.851, -46.06 }, { 70576, 4.3, 216.545, -45.38 },
					{ 70638, 4.3, 216.73, -83.67 }, { 71053, 3.6, 217.957, 30.37 }, { 71075, 3, 218.019, 38.31 },
					{ 71352, 2.3, 218.877, -42.16 }, { 71536, 4, 219.472, -49.43 }, { 71681, 1.4, 219.896, -60.84 },
					{ 71683, -0, 219.902, -60.83 }, { 71795, 3.8, 220.287, 13.73 }, { 71860, 2.3, 220.482, -47.39 },
					{ 71908, 3.2, 220.627, -64.98 }, { 71957, 3.9, 220.765, -5.66 }, { 72105, 2.4, 221.247, 27.07 },
					{ 72220, 3.7, 221.562, 1.89 }, { 72370, 3.8, 221.965, -79.04 }, { 72607, 2.1, 222.676, 74.16 },
					{ 72622, 2.8, 222.72, -16.04 }, { 73273, 2.7, 224.633, -43.13 }, { 73334, 3.1, 224.79, -42.1 },
					{ 73555, 3.5, 225.487, 40.39 }, { 73714, 3.3, 226.018, -25.28 }, { 73807, 3.9, 226.28, -47.05 },
					{ 74376, 3.9, 227.984, -48.74 }, { 74395, 3.4, 228.071, -52.1 }, { 74666, 3.5, 228.876, 33.31 },
					{ 74785, 2.6, 229.252, -9.38 }, { 74824, 4.1, 229.379, -58.8 }, { 74946, 2.9, 229.727, -68.68 },
					{ 75097, 3, 230.182, 71.83 }, { 75141, 3.2, 230.343, -40.65 }, { 75177, 3.6, 230.452, -36.26 },
					{ 75264, 3.4, 230.67, -44.69 }, { 75323, 4.5, 230.844, -59.32 }, { 75458, 3.3, 231.232, 58.97 },
					{ 75695, 3.7, 231.957, 29.11 }, { 76127, 4.1, 233.232, 31.36 }, { 76267, 2.2, 233.672, 26.71 },
					{ 76276, 3.8, 233.701, 10.54 }, { 76297, 2.8, 233.785, -41.17 }, { 76333, 3.9, 233.882, -14.79 },
					{ 76470, 3.6, 234.256, -28.14 }, { 76552, 4.3, 234.513, -42.57 }, { 76600, 3.7, 234.664, -29.78 },
					{ 76952, 3.8, 235.686, 26.3 }, { 77055, 4.3, 236.015, 77.79 }, { 77070, 2.6, 236.067, 6.43 },
					{ 77233, 3.6, 236.547, 15.42 }, { 77450, 4.1, 237.185, 18.14 }, { 77512, 4.6, 237.399, 26.07 },
					{ 77516, 3.5, 237.405, -3.43 }, { 77622, 3.7, 237.704, 4.48 }, { 77634, 4, 237.74, -33.63 },
					{ 77760, 4.6, 238.169, 42.45 }, { 77853, 4.1, 238.456, -16.73 }, { 77952, 2.8, 238.786, -63.43 },
					{ 78072, 3.9, 239.113, 15.66 }, { 78104, 3.9, 239.221, -29.21 }, { 78159, 4.1, 239.397, 26.88 },
					{ 78265, 2.9, 239.713, -26.11 }, { 78384, 3.4, 240.031, -38.4 }, { 78401, 2.3, 240.083, -22.62 },
					{ 78493, 5, 240.361, 29.85 }, { 78527, 4, 240.472, 58.57 }, { 78639, 4.7, 240.804, -49.23 },
					{ 78820, 2.6, 241.359, -19.81 }, { 78933, 3.9, 241.702, -20.67 }, { 78970, 5.7, 241.818, -36.76 },
					{ 79509, 5, 243.37, -54.63 }, { 79593, 2.7, 243.586, -3.69 }, { 79664, 3.9, 243.859, -63.69 },
					{ 79822, 5, 244.376, 75.76 }, { 79882, 3.2, 244.58, -4.69 }, { 79992, 3.9, 244.935, 46.31 },
					{ 80000, 4, 244.96, -50.16 }, { 80112, 2.9, 245.297, -25.59 }, { 80170, 3.7, 245.48, 19.15 },
					{ 80331, 2.7, 245.998, 61.51 }, { 80582, 4.5, 246.796, -47.55 }, { 80763, 1.1, 247.352, -26.43 },
					{ 80816, 2.8, 247.555, 21.49 }, { 80883, 3.8, 247.728, 1.98 }, { 81065, 3.9, 248.363, -78.9 },
					{ 81126, 4.2, 248.526, 42.44 }, { 81266, 2.8, 248.971, -28.22 }, { 81377, 2.5, 249.29, -10.57 },
					{ 81693, 2.8, 250.322, 31.6 }, { 81833, 3.5, 250.724, 38.92 }, { 81852, 4.2, 250.769, -77.52 },
					{ 82080, 4.2, 251.493, 82.04 }, { 82273, 1.9, 252.166, -69.03 }, { 82363, 3.8, 252.446, -59.04 },
					{ 82396, 2.3, 252.541, -34.29 }, { 82514, 3, 252.968, -38.05 }, { 82545, 3.6, 253.084, -38.02 },
					{ 82671, 4.7, 253.499, -42.36 }, { 82729, 3.6, 253.646, -42.36 }, { 83000, 3.2, 254.417, 9.38 },
					{ 83081, 3.1, 254.655, -55.99 }, { 83207, 3.9, 255.072, 30.93 }, { 83895, 3.2, 257.197, 65.71 },
					{ 84012, 2.4, 257.595, -15.72 }, { 84143, 3.3, 258.038, -43.24 }, { 84345, 2.8, 258.662, 14.39 },
					{ 84379, 3.1, 258.758, 24.84 }, { 84380, 3.2, 258.762, 36.81 }, { 84606, 4.6, 259.418, 37.29 },
					{ 84880, 4.3, 260.207, -12.85 }, { 84970, 3.3, 260.502, -25 }, { 85112, 4.2, 260.921, 37.15 },
					{ 85258, 2.8, 261.325, -55.53 }, { 85267, 3.3, 261.349, -56.38 }, { 85670, 2.8, 262.608, 52.3 },
					{ 85693, 4.4, 262.685, 26.11 }, { 85696, 2.7, 262.691, -37.3 }, { 85727, 3.6, 262.775, -60.68 },
					{ 85755, 4.8, 262.854, -23.96 }, { 85792, 2.8, 262.96, -49.88 }, { 85822, 4.3, 263.054, 86.59 },
					{ 85829, 4.9, 263.067, 55.17 }, { 85927, 1.6, 263.402, -37.1 }, { 86032, 2.1, 263.734, 12.56 },
					{ 86228, 1.9, 264.33, -43 }, { 86263, 3.5, 264.397, -15.4 }, { 86414, 3.8, 264.866, 46.01 },
					{ 86565, 4.2, 265.354, -12.88 }, { 86670, 2.4, 265.622, -39.03 }, { 86742, 2.8, 265.868, 4.57 },
					{ 86929, 3.6, 266.433, -64.72 }, { 86974, 3.4, 266.615, 27.72 }, { 87072, 4.5, 266.89, -27.83 },
					{ 87073, 3, 266.896, -40.13 }, { 87108, 3.8, 266.973, 2.71 }, { 87261, 3.2, 267.465, -37.04 },
					{ 87585, 3.7, 268.382, 56.87 }, { 87808, 3.9, 269.063, 37.25 }, { 87833, 2.2, 269.152, 51.49 },
					{ 87933, 3.7, 269.441, 29.25 }, { 88048, 3.3, 269.757, -9.77 }, { 88192, 3.9, 270.161, 2.93 },
					{ 88635, 3, 271.452, -30.42 }, { 88714, 3.6, 271.658, -50.09 }, { 88771, 3.7, 271.837, 9.56 },
					{ 88794, 3.8, 271.886, 28.76 }, { 88866, 4.3, 272.145, -63.67 }, { 89341, 3.8, 273.441, -21.06 },
					{ 89642, 3.1, 274.407, -36.76 }, { 89931, 2.7, 275.249, -29.83 }, { 89937, 3.5, 275.264, 72.73 },
					{ 89962, 3.2, 275.328, -2.9 }, { 90098, 4.3, 275.807, -61.49 }, { 90139, 3.9, 275.925, 21.77 },
					{ 90185, 1.8, 276.043, -34.38 }, { 90422, 3.5, 276.743, -45.97 }, { 90496, 2.8, 276.993, -25.42 },
					{ 90568, 4.1, 277.208, -49.07 }, { 90595, 4.7, 277.299, -14.57 }, { 90887, 5.2, 278.089, -39.7 },
					{ 91117, 3.9, 278.802, -8.24 }, { 91262, 0, 279.235, 38.78 }, { 91792, 4, 280.759, -71.43 },
					{ 91875, 5.1, 280.946, -38.32 }, { 91971, 4.3, 281.193, 37.61 }, { 92041, 3.2, 281.414, -26.99 },
					{ 92175, 4.2, 281.794, -4.75 }, { 92202, 5.4, 281.871, -5.71 }, { 92420, 3.5, 282.52, 33.36 },
					{ 92609, 4.2, 283.054, -62.19 }, { 92791, 4.2, 283.626, 36.9 }, { 92814, 5.1, 283.68, -15.6 },
					{ 92855, 2, 283.816, -26.3 }, { 92946, 4.6, 284.055, 4.2 }, { 92953, 5.3, 284.071, -42.71 },
					{ 92989, 5.4, 284.169, -37.34 }, { 93015, 4.4, 284.238, -67.23 }, { 93085, 3.5, 284.433, -21.11 },
					{ 93174, 4.8, 284.681, -37.11 }, { 93194, 3.3, 284.736, 32.69 }, { 93244, 4, 284.906, 15.07 },
					{ 93506, 2.6, 285.653, -29.88 }, { 93542, 4.7, 285.779, -42.1 }, { 93683, 3.8, 286.171, -21.74 },
					{ 93747, 3, 286.353, 13.86 }, { 93805, 3.4, 286.562, -4.88 }, { 93825, 4.2, 286.605, -37.06 },
					{ 93864, 3.3, 286.735, -27.67 }, { 94005, 4.6, 287.087, -40.5 }, { 94114, 4.1, 287.368, -37.9 },
					{ 94141, 2.9, 287.441, -21.02 }, { 94160, 4.1, 287.507, -39.34 }, { 94376, 3.1, 288.139, 67.66 },
					{ 94779, 3.8, 289.276, 53.37 }, { 94820, 4.9, 289.409, -18.95 }, { 95168, 3.9, 290.418, -17.85 },
					{ 95241, 4, 290.66, -44.46 }, { 95294, 4.3, 290.805, -44.8 }, { 95347, 4, 290.972, -40.62 },
					{ 95501, 3.4, 291.375, 3.11 }, { 95771, 4.4, 292.176, 24.66 }, { 95853, 3.8, 292.426, 51.73 },
					{ 95947, 3, 292.68, 27.96 }, { 96406, 5.6, 294.007, -24.72 }, { 96757, 4.4, 295.024, 18.01 },
					{ 96837, 4.4, 295.262, 17.48 }, { 97165, 2.9, 296.244, 45.13 }, { 97278, 2.7, 296.565, 10.61 },
					{ 97365, 3.7, 296.847, 18.53 }, { 97433, 3.8, 297.043, 70.27 }, { 97649, 0.8, 297.696, 8.87 },
					{ 97804, 3.9, 298.118, 1.01 }, { 98032, 4.1, 298.815, -41.87 }, { 98036, 3.7, 298.828, 6.41 },
					{ 98110, 3.9, 299.077, 35.08 }, { 98337, 3.5, 299.689, 19.49 }, { 98412, 4.4, 299.934, -35.28 },
					{ 98495, 4, 300.148, -72.91 }, { 98543, 4.7, 300.275, 27.75 }, { 98688, 4.4, 300.665, -27.71 },
					{ 98920, 5.1, 301.29, 19.99 }, { 99240, 3.5, 302.182, -66.18 }, { 99473, 3.2, 302.826, -0.82 },
					{ 99675, 3.8, 303.408, 46.74 }, { 99848, 4, 303.868, 47.71 }, { 100064, 3.6, 304.514, -12.54 },
					{ 100345, 3, 305.253, -14.78 }, { 100453, 2.2, 305.557, 40.26 }, { 100751, 1.9, 306.412, -56.74 },
					{ 101421, 4, 308.303, 11.3 }, { 101769, 3.6, 309.387, 14.6 }, { 101772, 3.1, 309.392, -47.29 },
					{ 101958, 3.8, 309.91, 15.91 }, { 102098, 1.3, 310.358, 45.28 }, { 102281, 4.4, 310.865, 15.07 },
					{ 102395, 3.4, 311.24, -66.2 }, { 102422, 3.4, 311.322, 61.84 }, { 102485, 4.1, 311.524, -25.27 },
					{ 102488, 2.5, 311.553, 33.97 }, { 102532, 4.3, 311.665, 16.12 }, { 102618, 3.8, 311.919, -9.5 },
					{ 102831, 4.9, 312.492, -33.78 }, { 102978, 4.1, 312.955, -26.92 },
					{ 103227, 3.7, 313.703, -58.45 }, { 103413, 3.9, 314.293, 41.17 }, { 103738, 4.7, 315.323, -32.26 },
					{ 104060, 3.7, 316.233, 43.93 }, { 104139, 4.1, 316.487, -17.23 }, { 104521, 4.7, 317.585, 10.13 },
					{ 104732, 3.2, 318.234, 30.23 }, { 104858, 4.5, 318.62, 10.01 }, { 104887, 3.7, 318.698, 38.05 },
					{ 104987, 3.9, 318.956, 5.25 }, { 105140, 4.7, 319.485, -32.17 }, { 105199, 2.5, 319.645, 62.59 },
					{ 105319, 4.4, 319.967, -53.45 }, { 105515, 4.3, 320.562, -16.83 }, { 105570, 5.2, 320.723, 6.81 },
					{ 105858, 4.2, 321.611, -65.37 }, { 105881, 3.8, 321.667, -22.41 }, { 106032, 3.2, 322.165, 70.56 },
					{ 106278, 2.9, 322.89, -5.57 }, { 106481, 4, 323.495, 45.59 }, { 106985, 3.7, 325.023, -16.66 },
					{ 107089, 3.7, 325.369, -77.39 }, { 107310, 4.5, 326.036, 28.74 }, { 107315, 2.4, 326.046, 9.88 },
					{ 107354, 4.1, 326.161, 25.65 }, { 107556, 2.9, 326.76, -16.13 }, { 107608, 5, 326.934, -30.9 },
					{ 108085, 3, 328.482, -37.36 }, { 108661, 5.4, 330.209, -28.45 }, { 109074, 3, 331.446, -0.32 },
					{ 109111, 4.5, 331.529, -39.54 }, { 109139, 4.3, 331.609, -13.87 }, { 109176, 3.8, 331.753, 25.35 },
					{ 109268, 1.7, 332.058, -46.96 }, { 109352, 5.6, 332.307, 33.17 }, { 109422, 4.9, 332.537, -32.55 },
					{ 109427, 3.5, 332.55, 6.2 }, { 109492, 3.4, 332.714, 58.2 }, { 109937, 4.1, 333.992, 37.75 },
					{ 110003, 4.2, 334.208, -7.78 }, { 110130, 2.9, 334.625, -60.26 }, { 110395, 3.9, 335.414, -1.39 },
					{ 110538, 4.4, 335.89, 52.23 }, { 110609, 4.5, 336.129, 49.48 }, { 110960, 3.6, 337.208, -0.02 },
					{ 110997, 4, 337.317, -43.5 }, { 111022, 4.3, 337.383, 47.71 }, { 111104, 4.5, 337.622, 43.12 },
					{ 111123, 4.8, 337.662, -10.68 }, { 111169, 3.8, 337.823, 50.28 }, { 111188, 4.3, 337.876, -32.35 },
					{ 111497, 4, 338.839, -0.12 }, { 111954, 4.2, 340.164, -27.04 }, { 112029, 3.4, 340.366, 10.83 },
					{ 112122, 2.1, 340.667, -46.88 }, { 112158, 2.9, 340.751, 30.22 }, { 112405, 4.1, 341.515, -81.38 },
					{ 112440, 4, 341.633, 23.57 }, { 112447, 4.2, 341.673, 12.17 }, { 112623, 3.5, 342.139, -51.32 },
					{ 112716, 4, 342.398, -13.59 }, { 112724, 3.5, 342.42, 66.2 }, { 112748, 3.5, 342.501, 24.6 },
					{ 112961, 3.7, 343.154, -7.58 }, { 113136, 3.3, 343.663, -15.82 }, { 113246, 4.2, 343.987, -32.54 },
					{ 113368, 1.2, 344.413, -29.62 }, { 113638, 4.1, 345.22, -52.75 }, { 113726, 3.6, 345.48, 42.33 },
					{ 113881, 2.4, 345.944, 28.08 }, { 113963, 2.5, 346.19, 15.21 }, { 114131, 4.3, 346.72, -43.52 },
					{ 114341, 3.7, 347.362, -21.17 }, { 114421, 3.9, 347.59, -45.25 }, { 114855, 4.2, 348.973, -9.09 },
					{ 114971, 3.7, 349.291, 3.28 }, { 114996, 4, 349.357, -58.24 }, { 115102, 4.4, 349.706, -32.53 },
					{ 115438, 4, 350.743, -20.1 }, { 115738, 5, 351.733, 1.26 }, { 115830, 4.3, 351.992, 6.38 },
					{ 116231, 4.4, 353.243, -37.82 }, { 116584, 3.8, 354.391, 46.46 }, { 116727, 3.2, 354.837, 77.63 },
					{ 116771, 4.1, 354.988, 5.63 }, { 116928, 4.5, 355.512, 1.78 }, { 118268, 4, 359.828, 6.86 } });

	//Stars contained in the messier catalogue.
	private double[][] messier = convertStarsToRadians(new double[][] { { 1, 8.4, 83.625, 22.01667 },
			{ 2, 6.5, 323.37495, -0.81667 }, { 3, 6.4, 205.54995, 28.38333 }, { 4, 5.9, 245.89995, -26.53333 },
			{ 5, 5.8, 229.65, 2.08333 }, { 6, 4.2, 265.02495, -32.21667 }, { 7, 3.3, 268.47495, -34.81667 },
			{ 8, 5.8, 270.94995, -24.38333 }, { 9, 7.9, 259.8, -18.51667 }, { 10, 6.6, 254.27505, -4.1 },
			{ 11, 5.8, 282.77505, -6.26667 }, { 12, 6.6, 251.80005, -1.95 }, { 13, 5.9, 250.425, 36.46667 },
			{ 14, 7.6, 264.40005, -3.25 }, { 15, 6.4, 322.5, 12.16667 }, { 16, 6, 274.69995, -13.78333 },
			{ 17, 6, 275.20005, -16.18333 }, { 18, 6.9, 274.97505, -17.13333 }, { 19, 7.2, 255.64995, -26.26667 },
			{ 20, 6.3, 270.57495, -23.03333 }, { 21, 5.9, 271.15005, -22.5 }, { 22, 5.1, 279.10005, -23.9 },
			{ 23, 5.5, 269.20005, -19.01667 }, { 24, 11, 274.60005, -18.41667 }, { 25, 0, 277.90005, -19.25 },
			{ 26, 8, 281.29995, -9.4 }, { 27, 8.1, 299.89995, 22.71667 }, { 28, 6.9, 276.12495, -24.86667 },
			{ 29, 6.6, 305.97495, 38.53333 }, { 30, 7.5, 325.09995, -23.18333 }, { 31, 3.5, 10.67505, 41.26667 },
			{ 32, 8.2, 10.67505, 40.86667 }, { 33, 5.7, 23.475, 30.65 }, { 34, 5.2, 40.5, 42.78333 },
			{ 35, 5.1, 92.22495, 24.33333 }, { 36, 6, 84.02505, 34.13333 }, { 37, 5.6, 88.09995, 32.55 },
			{ 38, 6.4, 82.17495, 35.83333 }, { 39, 4.6, 323.05005, 48.43333 }, { 40, 0, 185.59995, 58.08333 },
			{ 41, 4.5, 101.50005, -20.73333 }, { 42, 4, 83.85, -5.45 }, { 43, 9, 83.89995, -5.26667 },
			{ 44, 3.1, 130.02495, 19.98333 }, { 45, 0, 56.749995, 24.11667 }, { 46, 6.1, 115.45005, -14.81667 },
			{ 47, 4.4, 114.15, -14.5 }, { 48, 5.8, 123.45, -5.8 }, { 49, 8.4, 187.45005, 8 },
			{ 50, 5.9, 105.79995, -8.33333 }, { 51, 8.4, 202.47495, 47.2 }, { 52, 6.9, 351.04995, 61.58333 },
			{ 53, 7.7, 198.225, 18.16667 }, { 54, 7.7, 283.77495, -30.48333 }, { 55, 7, 295.00005, -30.96667 },
			{ 56, 8.3, 289.15005, 30.18333 }, { 57, 9, 283.39995, 33.03333 }, { 58, 9.8, 189.42495, 11.81667 },
			{ 59, 9.8, 190.5, 11.65 }, { 60, 8.8, 190.92495, 11.55 }, { 61, 9.7, 185.475, 4.46667 },
			{ 62, 6.6, 255.3, -30.11667 }, { 63, 8.6, 198.94995, 42.03333 }, { 64, 8.5, 194.175, 21.68333 },
			{ 65, 9.3, 169.725, 13.08333 }, { 66, 9, 170.05005, 12.98333 }, { 67, 6.9, 132.6, 11.81667 },
			{ 68, 8.2, 189.87495, -26.75 }, { 69, 7.7, 277.84995, -32.35 }, { 70, 8.1, 280.8, -32.3 },
			{ 71, 8.3, 298.45005, 18.78333 }, { 72, 9.4, 313.37505, -12.53333 }, { 73, 9, 314.74995, -12.63333 },
			{ 74, 9.2, 24.17505, 15.78333 }, { 75, 8.6, 301.52505, -21.91667 }, { 76, 12, 25.575, 51.56667 },
			{ 77, 8.8, 40.67505, -0.01667 }, { 78, 8, 86.67495, 0.05 }, { 79, 8, 81.12495, -24.55 },
			{ 80, 7.2, 244.24995, -22.98333 }, { 81, 6.9, 148.90005, 69.06667 }, { 82, 8.4, 148.95, 69.68333 },
			{ 83, 7.6, 204.25005, -29.86667 }, { 84, 9.3, 186.27495, 12.88333 }, { 85, 9.2, 186.34995, 18.18333 },
			{ 86, 9.2, 186.55005, 12.95 }, { 87, 8.6, 187.69995, 12.4 }, { 88, 9.5, 187.99995, 14.41667 },
			{ 89, 9.8, 188.925, 12.55 }, { 90, 9.5, 189.19995, 13.16667 }, { 91, 10.2, 188.85, 14.5 },
			{ 92, 6.5, 259.275, 43.13333 }, { 93, 6.2, 116.14995, -23.86667 }, { 94, 8.2, 192.72495, 41.11667 },
			{ 95, 9.7, 160.99995, 11.7 }, { 96, 9.2, 161.7, 11.81667 }, { 97, 11.2, 168.70005, 55.01667 },
			{ 98, 10.1, 183.45, 14.9 }, { 99, 9.8, 184.69995, 14.41667 }, { 100, 9.4, 185.72505, 15.81667 },
			{ 101, 7.7, 210.79995, 54.35 }, { 102, 7.7, 210.79995, 54.35 }, { 103, 7.4, 23.29995, 60.7 },
			{ 104, 8.3, 190.00005, -11.61667 }, { 105, 9.3, 161.95005, 12.58333 }, { 106, 8.3, 184.75005, 47.3 },
			{ 107, 8.1, 248.12505, -13.05 }, { 108, 10.1, 167.87505, 55.66667 }, { 109, 9.8, 179.4, 53.38333 },
			{ 110, 8, 10.09995, 41.68333 } });
	
	//Stars contained in the caldwell catalog.
	private double[][] caldwell = convertStarsToRadians(new double[][] { { 1, 0, 11.1, 85.33333333 },
			{ 2, 0, 3.25, 72.53333333 }, { 3, 12, 184.175, 69.46666667 }, { 4, 21, 315.45, 68.2 }, { 5, 3, 56.7, 68.1 },
			{ 6, 17, 269.65, 66.63333333 }, { 7, 7, 114.225, 65.6 }, { 8, 1, 22.375, 63.3 },
			{ 9, 22, 344.2, 62.61666667 }, { 10, 1, 26.5, 61.25 }, { 11, 23, 350.175, 61.2 }, { 12, 20, 308.7, 60.15 },
			{ 13, 1, 19.775, 58.33333333 }, { 14, 2, 35, 57.13333333 }, { 15, 19, 296.2, 50.51666667 },
			{ 16, 22, 333.825, 49.88333333 }, { 17, 0, 8.3, 48.5 }, { 18, 0, 9.75, 48.33333333 },
			{ 19, 21, 328.375, 47.26666667 }, { 20, 20, 314.7, 44.33333333 }, { 21, 12, 187.05, 44.1 },
			{ 22, 23, 351.475, 42.55 }, { 23, 2, 35.65, 42.35 }, { 24, 3, 49.95, 41.51666667 },
			{ 25, 7, 114.525, 38.88333333 }, { 26, 12, 184.375, 37.81666667 }, { 27, 20, 303, 38.35 },
			{ 28, 1, 29.45, 37.68333333 }, { 29, 13, 197.725, 37.05 }, { 30, 22, 339.275, 34.41666667 },
			{ 31, 5, 79.05, 34.26666667 }, { 32, 12, 190.525, 32.53333333 }, { 33, 20, 314.1, 31.71666667 },
			{ 34, 20, 311.425, 30.71666667 }, { 35, 13, 195.025, 27.98333333 }, { 36, 12, 189, 27.96666667 },
			{ 37, 20, 303, 26.48333333 }, { 38, 12, 189.075, 25.98333333 }, { 39, 7, 112.3, 20.91666667 },
			{ 40, 11, 170.025, 18.35 }, { 41, 4, 66.75, 16 }, { 42, 21, 315.375, 16.18333333 }, { 43, 0, 0.825, 16.15 },
			{ 44, 23, 346.225, 12.31666667 }, { 45, 13, 204.375, 8.883333333 }, { 46, 6, 99.8, 8.733333333 },
			{ 47, 20, 308.55, 7.4 }, { 48, 9, 137.575, 7.033333333 }, { 49, 6, 98.075, 5.05 },
			{ 50, 6, 98.1, 4.866666667 }, { 51, 1, 16.2, 2.116666667 }, { 52, 12, 192.15, -5.8 },
			{ 53, 10, 151.3, -7.716666667 }, { 54, 8, 120.05, -10.78333333 }, { 55, 21, 316.05, -11.36666667 },
			{ 56, 0, 11.75, -11.88333333 }, { 57, 19, 296.225, -14.8 }, { 58, 7, 109.45, -15.61666667 },
			{ 59, 10, 156.2, -18.63333333 }, { 60, 12, 180.475, -18.86666667 }, { 61, 12, 180.475, -18.88333333 },
			{ 62, 0, 11.775, -20.76666667 }, { 63, 22, 337.4, -20.8 }, { 64, 7, 109.7, -24.95 },
			{ 65, 0, 11.9, -25.28333333 }, { 66, 14, 219.9, -26.53333333 }, { 67, 2, 41.575, -30.28333333 },
			{ 68, 19, 285.475, -36.95 }, { 69, 17, 258.425, -37.1 }, { 70, 0, 13.725, -37.68333333 },
			{ 71, 7, 118.075, -38.55 }, { 72, 0, 3.725, -39.18333333 }, { 73, 5, 78.525, -40.05 },
			{ 74, 10, 151.925, -40.43333333 }, { 75, 16, 246.4, -40.66666667 }, { 76, 16, 253.5, -41.8 },
			{ 77, 13, 201.375, -43.01666667 }, { 78, 18, 272, -43.7 }, { 79, 10, 154.4, -46.41666667 },
			{ 80, 13, 201.7, -47.48333333 }, { 81, 17, 261.375, -48.41666667 }, { 82, 16, 250.325, -48.76666667 },
			{ 83, 13, 196.35, -49.46666667 }, { 84, 13, 206.6, -51.36666667 }, { 85, 8, 130.05, -53.06666667 },
			{ 86, 17, 265.175, -53.66666667 }, { 87, 3, 48.075, -55.21666667 }, { 88, 15, 226.425, -55.6 },
			{ 89, 16, 244.725, -57.9 }, { 90, 9, 140.35, -58.31666667 }, { 91, 11, 166.6, -58.66666667 },
			{ 92, 10, 160.95, -59.86666667 }, { 93, 19, 287.725, -59.98333333 }, { 94, 12, 193.4, -60.33333333 },
			{ 95, 16, 240.925, -60.5 }, { 96, 7, 119.575, -60.86666667 }, { 97, 11, 174.025, -61.61666667 },
			{ 98, 12, 190.575, -62.96666667 }, { 99, 12, 193.25, -63 }, { 100, 11, 174.15, -63.03333333 },
			{ 101, 19, 287.45, -63.85 }, { 102, 10, 160.8, -64.4 }, { 103, 5, 84.675, -69.1 }, { 104, 1, 15.8, -70.85 },
			{ 105, 12, 194.9, -70.88333333 }, { 106, 0, 6.025, -72.08333333 }, { 107, 16, 246.45, -72.2 },
			{ 108, 12, 186.45, -72.66666667 }, { 109, 10, 152.375, -80.86666667 } });
}
