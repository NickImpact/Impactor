/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.math;


import org.spongepowered.math.TrigMath;
import org.spongepowered.math.vector.Vector3d;

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
		y = v.y() * cos - v.z() * sin;
		z = v.y() * sin + v.z() * cos;
		return Vector3d.from(v.x(), y, z);
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
		x = v.x() * cos + v.z() * sin;
		z = v.x() * -sin + v.z() * cos;
		return Vector3d.from(x, v.y(), z);
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
		x = v.x() * cos - v.y() * sin;
		y = v.x() * sin + v.y() * cos;
		return Vector3d.from(x, y, v.z());

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

	public static Vector3d[] line(Vector3d start, Vector3d end, int points) {
		Vector3d[] result = new Vector3d[points];
		Vector3d link = end.sub(start);

		float len = (float) link.length();
		float ratio = len / (float) points;
		link = link.normalize();
		link = link.mul(ratio);

		result[0] = start.add(link);
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
