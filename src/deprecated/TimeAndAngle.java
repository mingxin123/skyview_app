package deprecated;
/**
 * @deprecated
 * Contains fields and methods for Hours, Minutes, and Seconds.
 * The constructor requires 4 things:
 * 1. UTC hour
 * 2. UTC minute
 * 3. UTC second
 * 4. Julian Date
 * 
 * The two methods outputs the GMST in seconds and GMST in hr:min:sec format respectively.
 * Note the GMST in sec is NOT the total secs, but modded secs, which is the seconds in an 24 hours
 * range. Similarly, the GMST string is base on 24 hours format, and the sec is to 0.1 decimal 
 * accuracy.
 * 
 * Also note that this calculation isn't completely exact in secs, because we are only using UTC 
 * time and didn't consider UT1 time.
 * The w (earth rotation in sec) is meant to be based on dUT1, where dUT1 = UT1 - UTC.
 * However, UT1 is +- 0.9 sec difference to UTC. For the sake of simplicity, we've forgone the UT1 
 * and dUT1.
 * 
 * For reference, see:
 * main:
 * https://www2.mps.mpg.de/homes/fraenz/systems/systems2art/node10.html
 * http://aa.usno.navy.mil/faq/docs/Alt_Az.php
 * minor:
 * https://www.nist.gov/pml/time-and-frequency-division/atomic-standards/leap-second-and-ut1-utc-information
 * https://physics.stackexchange.com/questions/46823/what-is-the-difference-between-ut0-ut1-and-gmt-time
 * https://www.iers.org/IERS/EN/Science/EarthRotation/UT1LOD.html
 * https://rhodesmill.org/skyfield/time.html#tai-tt-tdb
 * https://stackoverflow.com/questions/31152605/dealing-with-leap-seconds-correctly
 * 
 */
public class TimeAndAngle {
	private final double JULIAN_DATE_OFFSET = 2451545.0; 
	private final int TOTAL_SEC_PER_DAY = 86400;
	
	private double baseUTCHr;
	private double baseUTCMin;
	private double baseUTCSec;
	private double julianDate;
	
	private double totalGMSTSec;
	private double totalGMSTSecModded;
	private double GMSTModdedHr;
	private double GMSTModdedMin;
	private double GMSTModdedSec;
	private double GMSTModdedInHrs;
	private double LHA;
	
	/**
	 * Takes in 4 parameters and do the calculations at construction time to avoid error in
	 * calling, as most data are related to each other.
	 * @param UTC_hr Target UTC hour
	 * @param UTC_min Target UTC minute
	 * @param UTC_sec Target UTC 
	 * @param julianDateIn Target Julian Date
	 */
	public TimeAndAngle(double UTC_hr, double UTC_min, double UTC_sec, double julianDateIn) {
		baseUTCHr = UTC_hr;
		baseUTCMin = UTC_min;
		baseUTCSec = UTC_sec;
		julianDate = julianDateIn;
		
		double timeUnit = (julianDate - JULIAN_DATE_OFFSET) / 36525;
		
		// Greenwich sidereal time at midnight, in seconds of time
		double totalGMSTAtMidnightInSec = 24110.54841 + 8640184.812866 * timeUnit + 
				0.093104 * Math.pow(timeUnit, 2) - 6.2E-6 * (Math.pow(timeUnit, 3));

		//earth's Rotation Rate In Sidereal/UT Sec
		double w = 1.00273790935 + 5.9E-11 * timeUnit; 
		
		double totalUTCSec = baseUTCHr * 60 * 60 + baseUTCMin * 60 + baseUTCSec;
		
		totalGMSTSec = totalGMSTAtMidnightInSec + w * totalUTCSec;
		
		totalGMSTSecModded = totalGMSTSec % TOTAL_SEC_PER_DAY;
		
		GMSTModdedHr = Math.floor((totalGMSTSecModded / 60 / 60) % 24);
		GMSTModdedMin = Math.floor((totalGMSTSecModded / 60) % 60);
		GMSTModdedSec = Math.round((totalGMSTSecModded) % 60 * 10) / 10; //To 0.1 accuracy
		
		GMSTModdedInHrs = totalGMSTSecModded/60.0/60.0;
	}

	/**
	 * The GMST in seconds in a 24hr period. 
	 * @return The GMST in seconds in a 24hr period.
	 */
	public double getGMSTInSec() {
		return totalGMSTSecModded;
	}
	
	public double getGMSTInHour() {
		return GMSTModdedInHrs;
	}
	
	/**
	 * Use GMST to subtract RA in hrs, and add longitude in degress to get LHA in degrees. 
	 * 
	 * @param rightAscensionInHrs RA in hours
	 * @param longtitudeInDegrees 
	 * @return LHA in degrees
	 */
	public double getLHA(double rightAscensionInHrs, double longitudeInDegrees) {
		LHA = (GMSTModdedInHrs - rightAscensionInHrs) * 15 + longitudeInDegrees;
		return LHA;
	}
	
	/**
	 * The string in 24hr format as hr:min:sec.
	 * The sec is accurate to 0.1 decimal place.
	 */
	public String toString() {
		
		return String.format("%.0f:%.0f:%.1f", GMSTModdedHr, GMSTModdedMin, GMSTModdedSec);
		
	}
	
	/**
	 * Function to get altitude and azimuth of a star from a given longitude and latitude;
	 * @param longitud 
	 * @param lat
	 * @param ra
	 * @param dec
	 * @return
	 */
	public double[] getALTandAZ(double longitude, double lat, double ra, double dec) {
		//Formula to get altitude
		
		double sin_a = Math.cos(LHA) * Math.cos(dec) * Math.cos(lat) + Math.sin(dec) * Math.sin(lat);
		double altitude = Math.asin(sin_a);
		
		//Formula to get azimuth
		double tan_A = -Math.sin(LHA) / (Math.tan(dec) * Math.cos(lat) - Math.sin(lat) * Math.cos(LHA));
		double azimuth = Math.atan(tan_A);
		
		//store values in an array to be able to return both
		double[] values = new double[2];
		values[0] = altitude;
		values[1] = azimuth;
		return values;

	}
	
	public double convertDirectionalDegreeToDecimal(String direction, int degrees, int minutes, int seconds) {
		int totalSecs = minutes * 60 + seconds;
		double convertedSecs = totalSecs / (60 * 60);
		double convertedDecimal = degrees + convertedSecs;
		// Check for direction, if it's West then it's negative
		if(direction.toLowerCase().equals("w")) {
			convertedDecimal = convertedDecimal * -1; 
		}
		return convertedDecimal;
	}

	


	

	

	
			
	
}
