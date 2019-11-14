package Core;
/**
 * @author Lou Shin, Ivan Ko
 * This class has methods to get the moon phase for a particular day.
 * It has all the math functions that we are going to need to get the moon phase.
 * The moon phase shows the current moon age out of ~30 days (29.53).
 *
 */
public class MoonPhase {
	public final double EPOCH = 2444238.5; /* 1980 January 0.0. */
	public final double SUN_ELONG_EPOCH = 278.833540; /* Ecliptic longitude of the Sun at epoch 1980.0. */
	public final double SUN_ELONG_PERIGEE = 282.596403; /* Ecliptic longitude of the Sun at perigee. */
	public final static double ECCENT_EARTH_ORBIT = 0.016718; /* Eccentricity of Earth's orbit. */
	public final double MOON_MEAN_LONGITUDE_EPOCH = 64.975464; /* Moon's mean lonigitude at the epoch. */
	public final double MOON_MEAN_LONGITUDE_PERIGREE = 349.383063; /* Mean longitude of the perigee at the epoch. */
	public final static double KEPLER_EPSILON = 1E-6; /* Accurancy of the Kepler equation. */
	public final long RC_MIN_BCE_TO_1_CE = 1721424L; /* Days between 1.5-Jan-4713 BCE and 1.5-Jan-0001 CE */
	public final double SYNMONTH = 29.53058868; /* Synodic month (new Moon to new Moon) */
	private static double moonAgeInDays;
	private double jDate;

	/**
	 * Basic constructor. No date is set, so nothing is calculated until a date is
	 * set.
	 */
	public MoonPhase() {
		jDate = 0;
	}

	/**
	 * For setting date, and do the calculations.
	 * 
	 * @param julianDate
	 */
	public void setDateAndUpdate(double julianDate) {
		jDate = julianDate;
		calPhase();
	}

	private double FIXANGLE(double a) {
		return (a) - 360.0 * (Math.floor((a) / 360.0));
	}

	public double getMoonAgeInDays() {
		return moonAgeInDays;
	}

	/**
	 * Solves the kepler equation.
	 * 
	 * @param m
	 * @return
	 */
	private static double kepler(double m) {
		double e;
		double delta;
		e = m = Math.toRadians(m);
		do {
			delta = e - ECCENT_EARTH_ORBIT * Math.sin(e) - m;
			e -= delta / (1.0 - ECCENT_EARTH_ORBIT * Math.cos(e));
		} while (Math.abs(delta) - KEPLER_EPSILON > 0.0);

		return (e);
	}

	/**
	 * Calculates the phase of the Moon and returns the illuminated fraction of the
	 * Moon's disc as a value within the range of -99.9~...0.0...+99.9~, which has a
	 * negative sign in case the Moon wanes, otherwise the sign is positive. The New
	 * Moon phase is around the 0.0 value and the Full Moon phase is around the
	 * +/-99.9~ value. The argument is the time for which the phase is requested,
	 * expressed as a Julian date and fraction.
	 * </p>
	 * 
	 * This function is taken from the program "moontool" by John Walker, February
	 * 1988, which is in the public domain.
	 * 
	 * @return
	 */
	private double calPhase() {
		double date_within_epoch;
		double sun_eccent;
		double sun_mean_anomaly;
		double sun_perigree_co_ordinates_to_epoch;
		double sun_geocentric_elong;
		double moon_evection;
		double moon_variation;
		double moon_mean_anomaly;
		double moon_mean_longitude;
		double moon_annual_equation;
		double moon_correction_term1;
		double moon_correction_term2;
		double moon_correction_equation_of_center;
		double moon_corrected_anomaly;
		double moon_corrected_longitude;
		double moon_present_age;
		double moon_present_phase;
		double moon_present_longitude;

		/*
		 * Calculation of the Sun's position.
		 */
		date_within_epoch = jDate - EPOCH;
		sun_mean_anomaly = FIXANGLE((360.0 / 365.2422) * date_within_epoch);
		sun_perigree_co_ordinates_to_epoch = FIXANGLE(sun_mean_anomaly + SUN_ELONG_EPOCH - SUN_ELONG_PERIGEE);
		sun_eccent = kepler(sun_perigree_co_ordinates_to_epoch);
		sun_eccent = Math.sqrt((1.0 + ECCENT_EARTH_ORBIT) / (1.0 - ECCENT_EARTH_ORBIT)) * Math.tan(sun_eccent / 2.0);
		sun_eccent = 2.0 * Math.toDegrees(Math.atan(sun_eccent));
		sun_geocentric_elong = FIXANGLE(sun_eccent + SUN_ELONG_PERIGEE);
		/*
		 * Calculation of the Moon's position.
		 */
		moon_mean_longitude = FIXANGLE(13.1763966 * date_within_epoch + MOON_MEAN_LONGITUDE_EPOCH);
		moon_mean_anomaly = FIXANGLE(
				moon_mean_longitude - 0.1114041 * date_within_epoch - MOON_MEAN_LONGITUDE_PERIGREE);
		moon_evection = 1.2739
				* Math.sin(Math.toRadians(2.0 * (moon_mean_longitude - sun_geocentric_elong) - moon_mean_anomaly));
		moon_annual_equation = 0.1858 * Math.sin(Math.toRadians(sun_perigree_co_ordinates_to_epoch));
		moon_correction_term1 = 0.37 * Math.sin(Math.toRadians(sun_perigree_co_ordinates_to_epoch));
		moon_corrected_anomaly = moon_mean_anomaly + moon_evection - moon_annual_equation - moon_correction_term1;
		moon_correction_equation_of_center = 6.2886 * Math.sin(Math.toRadians(moon_corrected_anomaly));
		moon_correction_term2 = 0.214 * Math.sin(Math.toRadians(2.0 * moon_corrected_anomaly));
		moon_corrected_longitude = moon_mean_longitude + moon_evection + moon_correction_equation_of_center
				- moon_annual_equation + moon_correction_term2;
		moon_variation = 0.6583 * Math.sin(Math.toRadians(2.0 * (moon_corrected_longitude - sun_geocentric_elong)));

		// true longitude
		moon_present_longitude = moon_corrected_longitude + moon_variation;
		moon_present_age = moon_present_longitude - sun_geocentric_elong;
		moon_present_phase = 100.0 * ((1.0 - Math.cos(Math.toRadians(moon_present_age))) / 2.0);

		if (0.0 < FIXANGLE(moon_present_age) - 180.0) {
			moon_present_phase = -moon_present_phase;
		}

		moonAgeInDays = SYNMONTH * (FIXANGLE(moon_present_age) / 360.0);

		return moon_present_phase;
	}
}

