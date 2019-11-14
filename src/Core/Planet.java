package Core;
/**
 * @author Lou Shin
 * Planet class stores the information for a particular planet. (name, radius, interval, color, magnitude and elements)
 */
import javafx.scene.paint.Color;

interface Magnitude {
	double m(double[] d);
}

// Create an object to deal with planet ephemerides
public class Planet {
	public String name;
	public Color color;
	public double radius, interval, x, y, mag, alt;
	public double[][] elements, data;
	public Magnitude magnitude;

	/**
	 * Constructor for the Planet class
	 * @param name
	 * @param radius
	 * @param interval
	 * @param color
	 * @param magnitude
	 * @param elements
	 */
	public Planet(String name, double radius, double interval, Color color, Magnitude magnitude, double[][] elements) {
		this.name = name;
		this.radius = radius;
		this.interval = interval;
		this.color = color;
		this.magnitude = magnitude;
		this.elements = elements;
	}

	Planet() {
		name = "Earth";
		elements = new double[][] { { 2450680.5, 0.00041, 349.2, 102.8517, 1.0000200, 0.9855796, 0.0166967, 328.40353 },
				{ 2456320.5, 0.0, 349.2, 103.005, 0.999986, 0.985631, 0.016682, 127.4201 },
				{ 2456400.5, 0.0, 349.2, 103.022, 0.999987, 0.985630, 0.016677, 206.2740 },
				{ 2456480.5, 0.0, 349.2, 103.119, 1.000005, 0.985603, 0.016675, 285.1238 },
				{ 2456560.5, 0.0, 349.2, 103.161, 0.999995, 0.985618, 0.016682, 3.9752 },
				{ 2456680.5, 0.0, 349.2, 103.166, 1.000005, 0.985603, 0.016693, 122.2544 } };
	}
}