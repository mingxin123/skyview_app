package Tests;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Core.MoonPhase;

/**
 * Test the calculation correctness of the MoonPhase function.
 * Some deprecated function in MoonPhase was adopted here for the purpose 
 * of the ease of verifiability.
 * @author Ivan Ko
 *
 */
class TestMoonPhase {
	int phaseIndex;
	String phasesStrings[] = {
    		"New moon",        	// 0
			"Waxing crescent",	// 1 
			"First quarter",	// 2 
			"Waxing gibbous",	// 3
			"Full moon",		// 4 
			"Waning gibbous",	// 5
			"Last quarter",		// 6
			"Waning crescent"	// 7
	};
	
	/**
	 * Get the current moon phase indexing.
	 * @return Moon Phase index from 0~7
	 */
	public int getPhaseIndex(double moonAgeInDays){
		// One orbit of the Moon with respect to the Sun (synodic month) has a mean duration of 29.53 days 
    	double offset = 29.53 / 8.0;
    	int cur = 0;
    	while(cur <= 7) {
    		double adjustedOffset = offset * (cur + 1);
    		// System.out.println("Current offset is " + adjustedOffset);
    		if(moonAgeInDays <= adjustedOffset) {
    			phaseIndex = cur;
        		return (cur);
        	}
    		cur++;
    	}
    	phaseIndex = -1;
    	return -1;
    }
	
	/**
	 * Get the string description of current moon phase.
	 * @return the string of moon phase
	 */
	public String getMoonPhaseString() {
		if(phaseIndex == -1) {
			return ("Either date is invalid or not set.\n");
		}
		StringBuilder strBld = new StringBuilder();
		strBld.append("Current moon phase is ");
		strBld.append(phasesStrings[phaseIndex]);
		strBld.append("\n");
		return strBld.toString();
	}

	@Test
	void testMoonPhase() {
		
		
		MoonPhase mp = new MoonPhase();
		
		// Should be New moon, a 0, on 2018 Dec 6
		mp.setDateAndUpdate(2458460.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(0.85351, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(0, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Waxing crescent, 1, on 2018 Dec 10
		mp.setDateAndUpdate(2458464.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(4.47998, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(1, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// First Quarter, 2, on 2018 Dec 14
		mp.setDateAndUpdate(2458468.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(8.06052, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(2, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Waxing gibbous, 3, on 2018 Dec 19
		mp.setDateAndUpdate(2458472.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(11.98452, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(3, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Full moon, 4, on 2018 Dec 23
		mp.setDateAndUpdate(2458475.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(15.28502, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(4, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Waning gibbous, 5, on 2018 Dec 27
		mp.setDateAndUpdate(2458479.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(19.80593, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(5, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Last quarter, 6, on 2018 Dec 31
		mp.setDateAndUpdate(2458483.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(23.99207, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(6, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
		
		// Waning crescent, 7, on 2018 Dec 4
		mp.setDateAndUpdate(2458458.7183);
		System.out.println(mp.getMoonAgeInDays());
		assertEquals(28.47946, mp.getMoonAgeInDays(), 0.0001);
		System.out.println(getPhaseIndex(mp.getMoonAgeInDays()));
		assertEquals(7, getPhaseIndex(mp.getMoonAgeInDays()));
		System.out.println(getMoonPhaseString());
	}

}
