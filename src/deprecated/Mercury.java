package deprecated;

/**
 * 
 * @author Ivan Ko
 * @deprecated
 */

public class Mercury extends PlanetObj{
	private final static double N1 = 48.3313;
	private final static double N2 = 3.24587E-5;
	private final static double i1 = 7.0047;
	private final static double i2 = 5.00E-8;
	private final static double w1 = 29.1241;
	private final static double w2 = 1.01444E-5;
	private final static double a = 0.38709893;
	private final static double e1 = 0.205635;
	private final static double e2 = 5.59E-10;
	private final static double M1 = 168.6562;
	private final static double M2 = 4.0923344368;
	

	public Mercury() {
		super("Mercury", N1, N2, i1, i2, w1, w2, a, e1, e2, M1, M2);
	}
	
	

}
