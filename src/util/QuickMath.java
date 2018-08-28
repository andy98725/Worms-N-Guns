package util;

public abstract class QuickMath {
	// Lerp double vals
	public static double lerp(double a, double b, double l) {
		return a + l * (b - a);
	}

	// Lerp float vals
	public static float lerp(float a, float b, double l) {
		return a + (float) (l * (b - a));
	}

	// Lerp int vals
	public static int lerp(int a, int b, double l) {
		return a + (int) Math.round(l * (b - a));
	}
}
