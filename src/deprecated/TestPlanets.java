package deprecated;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Ivan Ko
 * @deprecated
 *
 */
class TestPlanets {
	
	/**
	 * Test to see if the conversion of a value into 0 to 360 degree limit is correct.
	 */
	@Test
	void testPlanetAsMoon() {
		PlanetObj moon = new PlanetObj("Moon", 125.1228, -0.0529538083, 5.1454, 0, 318.0634, 0.1643573223, 
				60.2666, 0.054900, 0, 115.3654, 13.0649929509);
		moon.setAllOnGivenDate(-3543);
		assertEquals(312.7381, moon.getLongAscNode(), 0.0001);
		assertEquals(5.1454, moon.getInclination(), 0.0001);
		assertEquals(95.7454, moon.getArgOfPerihelion(), 0.0001);
		assertEquals(60.2666, moon.getMeanDistance(), 0.0001);
		assertEquals(0.054900, moon.getEccentricity(), 0.0001);
		assertEquals(266.0954, moon.getMeanAnomaly(), 0.0001);
		assertEquals(262.9735, moon.eccentricAnomaly, 0.0001);
		assertEquals(-10.68095, moon.xPos, 0.0001);
		assertEquals(-59.72377, moon.yPos, 0.0001);
		assertEquals(60.67134, moon.rDistance, 0.0001);
		
		
	}

	@Test
	void testMercury() {
		PlanetObj merc = new Mercury();
		// Set date as 19 april 1990 0h UT
		merc.setAllOnGivenDate(-3543);
		// 48.2163  7.0045   29.0882   0.387098  0.205633   69.5153
		assertEquals(48.2163, merc.getLongAscNode(), 0.0001);
		assertEquals(7.0045, merc.getInclination(), 0.0001);
		assertEquals(29.0882, merc.getArgOfPerihelion(), 0.0001);
		assertEquals(0.387098, merc.getMeanDistance(), 0.0001);
		assertEquals(0.205633, merc.getEccentricity(), 0.0001);
		assertEquals(69.5153, merc.getMeanAnomaly(), 0.0001);
		assertEquals(81.1572, merc.eccentricAnomaly, 0.0001);
	}
	
	

}
