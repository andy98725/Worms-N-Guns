package util;

public abstract class QuickMath {
	// Lerp double vals
	public static double lerp(double a, double b, double l) {
		if(l > 1) l = 1;
		if(l < 0) l = 0;
		return a + l * (b - a);
	}

	// Lerp float vals
	public static float lerp(float a, float b, double l) {
		if(l > 1) l = 1;
		if(l < 0) l = 0;
		return a + (float) (l * (b - a));
	}

	// Lerp int vals
	public static int lerp(int a, int b, double l) {
		if(l > 1) l = 1;
		if(l < 0) l = 0;
		return a + (int) Math.round(l * (b - a));
	}
}
