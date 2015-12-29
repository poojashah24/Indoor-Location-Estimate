package edu.columbia.locationreceiver;

public class PressureCompensator {
	/*P_b = static pressure (pascals)
	T_b = standard temperature (K)
	L_b = standard temperature lapse rate (K/m) in ISA
	h = height above sea level (meters)
	h_b = height at bottom of layer b (meters; e.g., h1 = 11,000 meters)
	R^* = universal gas constant for air: 8.31432 N·m /(mol·K)
	g_0 = gravitational acceleration (9.80665 m/s2)
	M = molar mass of Earth's air (0.0289644 kg/mol)*/
		
	public static double getPressureAtElevation(double altitude) {
		double pressure = 101325.0 * Math.exp(-0.0001185*altitude);
		return pressure;
	}	
}
