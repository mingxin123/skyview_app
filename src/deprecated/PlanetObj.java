package deprecated;
/**
 * Planets as abstract class.
 * @author Ivan Ko
 * @deprecated
 */
public class PlanetObj {
	protected String label;
	protected double longAscNodeInDecDeg; // N 
	private double N1InDecDeg;
	private double N2InDecDeg;
	protected double inclinationInDecDeg; // i
	private double i1InDecDeg;
	private double i2InDecDeg;
	protected double argOfPerihelionInDecDeg; // w
	private double w1InDecDeg;
	private double w2InDecDeg;
	protected double meanDistanceInAU; // a
	protected double eccentricityInDecDeg; // e
	private double e1InDecDeg;
	private double e2InDecDeg;
	protected double meanAnomalyInDecDeg; // M
	private double M1InDecDeg;
	private double M2InDecDeg;
	protected double julianDate; // d
	protected double eccentricAnomaly; // E
	protected double xPos; // x
	protected double yPos; // y
	protected double rDistance; // r
	protected double vTrueAnomalyInDecDeg; // v

	public PlanetObj(String name, double N1In, double N2In, double i1In, double i2In, double w1In, 
			double w2In, double aIn, double e1In, double e2In, double M1In, double M2In) {
		label = name;
		longAscNodeInDecDeg = 0;
		inclinationInDecDeg = 0;
		argOfPerihelionInDecDeg = 0;
		meanDistanceInAU = aIn;
		eccentricityInDecDeg = 0;
		meanAnomalyInDecDeg = 0;
		julianDate = 0;
		eccentricAnomaly = 0;
		xPos = 0;
		yPos = 0;
		rDistance = 0;
		vTrueAnomalyInDecDeg = 0;
		
		N1InDecDeg = N1In;
		N2InDecDeg = N2In;
		i1InDecDeg = i1In;
		i2InDecDeg = i2In;
		w1InDecDeg = w1In;
		w2InDecDeg = w2In;
		e1InDecDeg = e1In;
		e2InDecDeg = e2In;
		M1InDecDeg = M1In;
		M2InDecDeg = M2In;
		
	}
	
	public void setJulianDate(double dateIn) {
		julianDate = dateIn;
	}
	
	public double getJulianDate() {
		return julianDate;
	}
	
	/**
	 * longAscNodeInDecDeg = N1InDecDeg + N2InDecDeg * d;
	 */
	public void setLongAscNode() {
		this.longAscNodeInDecDeg = N1InDecDeg + N2InDecDeg * this.julianDate;
		this.longAscNodeInDecDeg = convertValueInto360DecDegree(this.longAscNodeInDecDeg);
	}
	
	public double getLongAscNode() {
		return longAscNodeInDecDeg;
	}
	
	/**
	 * inclinationInDecDeg = i1InDecDeg + i2InDecDeg * d
	 */
	public void setInclination() {
		this.inclinationInDecDeg = i1InDecDeg + i2InDecDeg * this.julianDate;
		this.inclinationInDecDeg = convertValueInto360DecDegree(this.inclinationInDecDeg);
	}
	
	public double getInclination() {
		return inclinationInDecDeg;
	}
	
	/**
	 * argOfPerihelionInDecDeg = w1InDecDeg + w2InDecDeg * d
	 */
	public void setArgOfPerihelion() {
		this.argOfPerihelionInDecDeg = w1InDecDeg +  w2InDecDeg * this.julianDate;
		this.argOfPerihelionInDecDeg = convertValueInto360DecDegree(this.argOfPerihelionInDecDeg);
	}
	
	public double getArgOfPerihelion() {
		return argOfPerihelionInDecDeg;
	}
	
	public void setMeanDistance(double distIn) {
		this.meanAnomalyInDecDeg = distIn;
	}
	
	public double getMeanDistance() {
		return meanDistanceInAU;
	}
	
	/**
	 * eccentricityInDec = e1InDecDeg + e2InDecDeg * d
	 */
	public void setEccentricity() {
		this.eccentricityInDecDeg = e1InDecDeg + e2InDecDeg * this.julianDate;
		this.eccentricityInDecDeg = convertValueInto360DecDegree(this.eccentricityInDecDeg);
	}
	
	public double getEccentricity() {
		return eccentricityInDecDeg;
	}
	
	/**
	 * meanAnomalyInDecDeg = M1InDecDeg + M2InDecDeg * d;
	 */
	public void setMeanAnomaly() {
		this.meanAnomalyInDecDeg = M1InDecDeg + M2InDecDeg * this.julianDate;
		this.meanAnomalyInDecDeg = convertValueInto360DecDegree(this.meanAnomalyInDecDeg);
	}
	
	public double getMeanAnomaly() {
		return meanAnomalyInDecDeg;
	}
	
	public void setAllOnGivenDate(double dateIn) {
		setJulianDate(dateIn);
		setLongAscNode();
		setInclination();
		setArgOfPerihelion();
		setEccentricity();
		setMeanAnomaly();
		setEccentricAnomaly();
		setX();
		setY();
		setRDistance();
	}
	
	public void setAllAlreadyHasDate() {
		setLongAscNode();
		setInclination();
		setArgOfPerihelion();
		setEccentricity();
		setMeanAnomaly();
		setEccentricAnomaly();
		setX();
		setY();
		setRDistance();
	}
	
	/**
	 * Note: Math.sin() and cos() takes in radian! Always convert the dec deg to radians first!
	 */
	public void setEccentricAnomaly() {
		// E0 = M + (180_deg/pi) * e * sin(M) * (1 + e * cos(M))
		double baseEccentricity = meanAnomalyInDecDeg + (180/Math.PI) * eccentricityInDecDeg 
				* Math.sin(Math.toRadians(meanAnomalyInDecDeg)) * (1 + eccentricityInDecDeg 
						* Math.cos(Math.toRadians(meanAnomalyInDecDeg)));
		// E1 = E0 - (E0 - (180_deg/pi) * e * sin(E0) - M) / (1 - e * cos(E0))
		while(Math.abs(eccentricAnomaly - baseEccentricity) > 0.005) {
			baseEccentricity = eccentricAnomaly; 
			eccentricAnomaly = baseEccentricity - (baseEccentricity - (180/Math.PI) 
					* eccentricityInDecDeg * Math.sin(Math.toRadians(baseEccentricity)) 
					- meanAnomalyInDecDeg) / (1 - eccentricityInDecDeg 
							* Math.cos(Math.toRadians(baseEccentricity)));
		}
	}
	
	
	public String getName() {
		return label;
	}
	
	public double convertValueInto360DecDegree(double numIn) {
		double result = numIn % 360;
		if(result < 0) {
			result = 360 + result;
		}
		return result;
	}
	
	/**
	 * x = r * cos(v) = a * (cos(E) - e)
	 */
	public void setX() {
		xPos = meanDistanceInAU * (Math.cos(Math.toRadians(eccentricAnomaly)) - eccentricityInDecDeg);
	}
	
	/**
	 * y = r * sin(v) = a * sqrt(1 - e*e) * sin(E)
	 */
	public void setY() {
		yPos = meanDistanceInAU * Math.sqrt(1 - eccentricityInDecDeg * eccentricityInDecDeg) 
				* (Math.sin(Math.toRadians(eccentricAnomaly)));
	}
	
	/**
	 * r = sqrt( x*x + y*y )
	 */
	public void setRDistance() {
		rDistance = Math.sqrt(xPos * xPos + yPos * yPos);
	}
	
	/**
	 * 
	 * In decimal degree!
	 */
	public void setVTrueAnomalyInDecDeg() {
		vTrueAnomalyInDecDeg = 0;
	}

}
