package net.impactdev.impactor.api.math;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3d;

public class VectorMath {

	public static final float TAU = 2F * (float) Math.PI;

	/**
	 * Rotates a vector around the X axis at an angle
	 *
	 * @param v Starting vector
	 * @param angle How much to rotate
	 * @return The starting vector rotated
	 */
	public static Vector3d rotateAroundAxisX(Vector3d v, double angle) {
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
		return Vector3d.from(v.getX(), y, z);
	}

	/**
	 * Rotates a vector around the Y axis at an angle
	 *
	 * @param v Starting vector
	 * @param angle How much to rotate
	 * @return The starting vector rotated
	 */
	public static Vector3d rotateAroundAxisY(Vector3d v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return Vector3d.from(x, v.getY(), z);
	}

	/**
	 * Rotates a vector around the Z axis at an angle
	 *
	 * @param v Starting vector
	 * @param angle How much to rotate
	 * @return The starting vector rotated
	 */
	public static Vector3d rotateAroundAxisZ(Vector3d v, double angle) {
		double x, y, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos - v.getY() * sin;
		y = v.getX() * sin + v.getY() * cos;
		return Vector3d.from(x, y, v.getZ());

	}

	/**
	 * Rotates a vector around the X, Y, and Z axes
	 *
	 * @param v The starting vector
	 * @param angleX The change angle on X
	 * @param angleY The change angle on Y
	 * @param angleZ The change angle on Z
	 * @return The starting vector rotated
	 */
	public static Vector3d rotateVector(Vector3d v, double angleX, double angleY, double angleZ) {
		rotateAroundAxisX(v, angleX);
		rotateAroundAxisY(v, angleY);
		rotateAroundAxisZ(v, angleZ);
		return v;
	}

	/**
	 * Return a point along a circle in the xz plane for the given radians.
	 */
	public static Vector3d getPointForCircle(float radians, double radius) {
		return Vector3d.from(cos(radians) * radius, 0, -sin(radians) * radius);
	}

	public static Vector3d[] getLine(Vector3d start, Vector3d end, int points) {
		Vector3d[] result = new Vector3d[points];
		Vector3d link = end.sub(start);

		float len = (float) link.length();
		float ratio = len / (float) points;
		link = link.normalize();
		link = link.mul(ratio);

		result[0] = new Vector3d(start.add(link));
		for(int i = 1; i < points; i++) {
			result[i] = result[i - 1].add(link);
		}

		return result;
	}

	/**
	 * Returns the sine of the given radians, obtained from a cached table.
	 *
	 * @see TrigMath#sin(double)
	 */
	public static float sin(float radians) {
		return TrigMath.sin(radians % TAU);
	}

	/**
	 * Returns the cosine of the given radians, obtained from a cached table.
	 *
	 * @see TrigMath#cos(double)
	 */
	public static float cos(float radians) {
		return TrigMath.cos(radians % TAU);
	}

}
