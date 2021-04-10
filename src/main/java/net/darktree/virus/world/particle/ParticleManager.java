package net.darktree.virus.world.particle;

import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleRenderer;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.util.MutableInteger;
import net.darktree.virus.util.Utils;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ParticleManager {

    private final HashMap<ParticleType, MutableInteger> counters = new HashMap<>();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final ParticleCell[][] cells;
    private final int size;

    public ParticleManager( int size ) {
        this.cells = new ParticleCell[size][size];
        this.size = size;

        for( int y = 0; y < size; y++ ) {
            for (int x = 0; x < size; x++) {
                this.cells[x][y] = new ParticleCell();
            }
        }

        for( ParticleType type : ParticleType.values() ) {
            counters.put(type, new MutableInteger());
        }
    }

    public void add( ArrayList<Particle> queue ) {
        for( Particle particle : queue ) {
            getAt(particle).add(particle);
            particles.add(particle);
            counters.get( particle.getType() ).value ++;
        }
        queue.clear();
    }

    public void tick( World world ) {
        for( int y = 0; y < size; y++ ) {
            for (int x = 0; x < size; x++) {
                this.cells[x][y].tick( world );
            }
        }

        randomTick();

        for( Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();
            if( p.isRemoved() ) {
                it.remove();
                counters.get( p.getType() ).value --;
            } else {
                ParticleRenderer.add(p);
            }
        }
    }

    private void randomTick() {
        int size = particles.size();
        int count = size / 100;

        for( int i = 0; i < count; i ++ ) {
            particles.get(Utils.random(size)).randomTick();
        }
    }

    public Particle getAround( float x, float y, float r, ParticleType type ) {
        float range = r * r;

        for( Particle particle : particles ) {
            if( particle.getType() == type && particle.squaredDistanceTo( x, y ) <= range ) return particle;
        }

        return null;
    }

    public ParticleCell getAt( int x, int y ) {
        if( x >= 0 && x < size && y >= 0 && y < size ) {
            return cells[x][y];
        }

        throw new IndexOutOfBoundsException("x: " + x + ", y: " + y + ", max: " + size + ", min: 0");
    }

    public ParticleCell getAt( Particle particle ) {
        return getAt( (int) particle.pos.x, (int) particle.pos.y );
    }

    public int getCount( ParticleType type ) {
        return counters.get(type).value;
    }

    public void updateCell( Particle particle, Vec2f pos ) {
        particle.cell = getAt( (int) pos.x, (int) pos.y );
        particle.cell.add( particle );
    }

}
