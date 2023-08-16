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

}