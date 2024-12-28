package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.logger.Logger;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;
import net.darktree.virus.world.particle.ParticleCell;
import processing.core.PApplet;

public abstract class Particle implements DrawContext {

	private boolean removed = false;
	protected int age = 0;
	public Vec2f pos;
	public Vec2f velocity;
	public ParticleCell cell;

	public Particle(Vec2f pos, Vec2f velocity) {
		this.pos = pos;
		this.velocity = velocity;
	}

	public abstract ParticleType getType();

	public abstract int getColor();

	public boolean bouncesOff() {
		return true;
	}

	public void randomTick() {

	}

	protected void border() {

	}

	protected boolean interact(World world, Vec2f future, CellType cType, CellType fType) {
		return false;
	}

	public float getScale() {
		return Math.min(1.0f, age * Const.AGE_GROW_SPEED);
	}

	public void draw(Screen screen) {
		fill(getColor());
		ellipseMode(PApplet.CENTER);
		ellipse(0, 0, 0.1f * Const.BIG_FACTOR, 0.1f * Const.BIG_FACTOR);
	}

	void setSpeed(float v) {
		velocity.multiply(v / speed());
	}

	float speed() {
		return velocity.length() + 0.00001f;
	}

	boolean collision(Particle part) {
		return pos.distance(part.pos) < 0.05f;
	}

	public void tick(World world) {
		this.age++;

		for (Particle circ : world.pc.getAt(this).particles) {
			if (circ != this && collision(circ)) {
				float dx = (pos.x + circ.pos.x) / 2;
				float dy = (pos.y + circ.pos.y) / 2;

				float as = speed();
				float bs = circ.speed();

				velocity.set(pos.x - dx, pos.y - dy);
				setSpeed(bs);

				circ.velocity.set(circ.pos.x - dx, circ.pos.y - dy);
				circ.setSpeed(as);
			}
		}

		Vec2f future = new Vec2f();
		Cell cell = world.getCellAt(pos.x, pos.y);
		CellType ct = cell == null ? CellType.EMPTY : cell.getType();

		if (ct == CellType.LOCKED) remove();
		float viscosity = ct == CellType.EMPTY ? 1 : 0.5f;

		future.x = pos.x + velocity.x * viscosity * Const.PLAY_SPEED;
		future.y = pos.y + velocity.y * viscosity * Const.PLAY_SPEED;

		boolean cta = Math.floor(pos.x) != Math.floor(future.x);
		boolean ctb = Math.floor(pos.y) != Math.floor(future.y);

		if (cta || ctb) {

			CellType ft = world.getCellTypeAt(future.x, future.y);

			if (interact(world, future, ct, ft)) return;

			if (ft == CellType.LOCKED || (bouncesOff() && (ct.isSolid() || ft.isSolid()))) {

				Cell cell1 = world.getCellAt(future.x, future.y);
				if (cell1 instanceof NormalCell) {
					((NormalCell) cell1).hurtWall(cta && ctb ? 2 : 1);
				}

				if (cta) {
					if (velocity.x >= 0) {
						future.x = (float) Math.ceil(pos.x) - PApplet.EPSILON;
					} else {
						future.x = (float) Math.floor(pos.x) + PApplet.EPSILON;
					}

					velocity.x = -velocity.x;
				}

				if (ctb) {
					if (velocity.y >= 0) {
						future.y = (float) Math.ceil(pos.y) - PApplet.EPSILON;
					} else {
						future.y = (float) Math.floor(pos.y) + PApplet.EPSILON;
					}

					velocity.y = -velocity.y;
				}

				Cell cell2 = world.getCellAt(pos.x, pos.y);
				if (cell2 instanceof NormalCell) {
					((NormalCell) cell2).hurtWall(cta && ctb ? 2 : 1);
				}

			} else {

				if (future.x >= Const.WORLD_SIZE) {
					future.x -= Const.WORLD_SIZE;
					border();
				}
				if (future.x < 0) {
					future.x += Const.WORLD_SIZE;
					border();
				}
				if (future.y >= Const.WORLD_SIZE) {
					future.y -= Const.WORLD_SIZE;
					border();
				}
				if (future.y < 0) {
					future.y += Const.WORLD_SIZE;
					border();
				}

				hurtWall(world, pos);
				hurtWall(world, future);

				try {
					world.pc.updateCell(this, future);
				} catch (Exception e) {
					// IS THIS UNFIXABLE?!?!
					Logger.error(getType().name());
					e.printStackTrace();
				}

			}

		}

		pos = future;

	}

	private void hurtWall(World world, Vec2f pos) {
		Cell cell = world.getCellAt(pos.x, pos.y);
		if (cell instanceof NormalCell) {
			((NormalCell) cell).hurtWall(1);
		}
	}

	public final void remove() {
		removed = true;
	}

	public final boolean isRemoved() {
		return removed;
	}

	public final void alignWithWorld() {
		if (pos.x >= Const.WORLD_SIZE) pos.x = Const.WORLD_SIZE - Main.EPSILON;
		if (pos.x < 0) pos.x = 0;
		if (pos.y >= Const.WORLD_SIZE) pos.y = Const.WORLD_SIZE - Main.EPSILON;
		if (pos.y < 0) pos.y = 0;
	}

	public final float squaredDistanceTo(float x, float y) {
		float a = pos.x - x, b = pos.y - y;
		return a * a + b * b;
	}
}
