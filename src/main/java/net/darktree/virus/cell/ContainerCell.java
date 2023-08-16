package net.darktree.virus.cell;

import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.util.Utils;
import net.darktree.virus.world.particle.ParticleCell;

public interface ContainerCell {

	ParticleCell getContainer();

	default Particle selectParticle(ParticleType type) {
		ParticleCell cell = getContainer();
		int i = cell.size();

		for (int j = Utils.random(i); i > 0; i--) {

			if (cell.get(j).getType() == type) {
				return cell.get(j);
			}

			j = (j + 1) % i;
		}

		return null;
	}

}
