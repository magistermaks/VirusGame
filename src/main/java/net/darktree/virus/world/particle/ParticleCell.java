package net.darktree.virus.world.particle;

import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.world.World;

import java.util.ArrayList;
import java.util.Iterator;

public class ParticleCell {

    private final ArrayList<Particle> particles = new ArrayList<>();

    public void add( Particle particle ) {
        particles.add(particle);
    }

    public Particle get( int i ) {
        return particles.get( i );
    }

    public int size() {
        return particles.size();
    }

    public void tick( World world ) {
        for( Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();

            if( p.cell != this || p.removed ) {
                it.remove();
            }else{
                p.tick(world);
            }
        }
    }

    public int getCount(ParticleType type) {
        int count = 0;

        for( Particle particle : particles ) {
            if( particle.getType() == type ) {
                count ++;
            }
        }

        return count;
    }

}
