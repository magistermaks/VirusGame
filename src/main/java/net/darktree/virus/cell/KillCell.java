package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.particle.WasteParticle;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.world.particle.ParticleCell;

public class KillCell extends Cell{

	int waste = 0;

	public KillCell( int x, int y ) {
		super( x, y );
	}

	@Override
	protected void drawCell(Screen screen) {
		fill(Const.COLOR_CELL_KILLER);
		rect(0, 0, Const.BIG_FACTOR, Const.BIG_FACTOR);
	}

	@Override
	public String getCellName(){
		return "Kill";
	}

	@Override
	public CellType getType() {
		return CellType.Kill;
	}

	@Override
	public void tick() {
		ParticleCell particles = Main.applet.world.pc.getAt(x, y);
		boolean has = false;

		for (int i = 0; i < particles.size(); i ++) {
			Particle particle = particles.get(i);

			if (particle instanceof VirusParticle) {
				((VirusParticle) particle).applyDamage();
			}

			if (particle instanceof WasteParticle) {
				has = true;

				if (waste > 100) {
					((WasteParticle) particle).decay = true;
					waste = 0;
				}
			}
		}

		if (has) {
			waste ++;
		}
	}

}
