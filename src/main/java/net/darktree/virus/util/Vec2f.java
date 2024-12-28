package net.darktree.virus.util;

public class Vec2f {

	public float x, y;

	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2f() {
		this(0, 0);
	}

	public Vec2f copy() {
		return new Vec2f(x, y);
	}

	public static Vec2f zero() {
		return new Vec2f(0, 0);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public void multiply(float v) {
		x *= v;
		y *= v;
	}

	public float distance(Vec2f pos) {
		float dx = (x - pos.x);
		float dy = (y - pos.y);
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}