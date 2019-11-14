package Tests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

import Core.Sky;

/**
 * Basic test case for checking main functions in Sky.java
 * that achieve >90% branch coverage for all non-GUI, non-networked code.
 * @author Ming Xin
 *
 */


class TestSkyFunction {
	
	Sky sky = new Sky();
	@Test
	void testSky() {
		// sky object to check the sky.java
		sky.setClock(new Date());
		
		// print it to see if the date is correct or not
		System.out.println(sky.getClock());
	
		// checking the basic set is correct or not
		assertEquals(sky.getAzOffset(), 180.0);
		assertEquals(sky.getLatitude(), 0.0);
		assertEquals(sky.getLongitude(), 0.0);
	}
	
	@Test
	void testPlanets() {
		// checking for basic planets 
		assertEquals(sky.getPlanets(100, 100)[0].name, "Sun");
		assertEquals(sky.getPlanets(100, 100)[1].name, "Moon");
	}
	
	@Test
	void testConstellation() {
		// checking for constellation from database
		assertTrue(sky.getConstellationLabels(100, 100).containsKey("Aquila"));
		
		// main methods checking for getStats to get the right database
		assertEquals(sky.getStars(100, 100, 123).get(32768)[2], 2.9);
		
	}

}
