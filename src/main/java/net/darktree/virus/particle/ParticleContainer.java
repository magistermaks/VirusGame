package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.Utils;
import net.darktree.virus.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ParticleContainer {

    public final ArrayList<Particle> foods = new ArrayList<>();
    public final ArrayList<Particle> wastes = new ArrayList<>();
    public final ArrayList<Particle> viruses = new ArrayList<>();

    public ArrayList<Particle> get(ParticleType type ) {

        switch( type ){
            case FOOD: return foods;
            case WASTE: return wastes;
            case VIRUS: return viruses;
        }

        return null;
    }

    public void add( ArrayList<Particle> queue ) {
        for( Particle p : queue ) {
            get( p.getType() ).add( p );
        }

        queue.clear();
    }

    public void tick( World world, ParticleType type ) {
        for (Iterator<Particle> it = get(type).iterator(); it.hasNext();) {
            Particle p = it.next();
            p.tick( world );
            if( p.removed ) it.remove(); else ParticleRenderer.add(p);
        }
    }

    public int count() {
        return foods.size() + wastes.size() + viruses.size();
    }

    public Particle getAround( float x, float y, float r, ParticleType type ) {
        float range = r * r;

        for( Particle particle : get(type) ) {
            if( particle.squaredDistanceTo( x, y ) <= range ) return particle;
        }

        return null;
    }

    public void randomTick() {
        if( Main.applet.frameCount % 10 == 0 ) {
            int c = count() / Const.PARTICLES_PER_RAND_UPDATE;

            for( ; c > 0; c -- ) {
                ArrayList<Particle> array = get( Objects.requireNonNull( ParticleType.fromId( Utils.random(0, 2) ) ) );
                if( array.size() > 0 ) {
                    int index = Utils.random(0, array.size());
                    if( index != -1 ) array.get( index ).randomTick();
                }
            }
        }
    }

    public void draw(Screen screen) {
        for( Particle p : foods ) drawParticle(p, screen);
        for( Particle p : wastes ) drawParticle(p, screen);
        for( Particle p : viruses) drawParticle(p, screen);
    }

    private void drawParticle(Particle p, Screen screen) {
        Main.applet.pushMatrix();
        p.draw(screen);
        Main.applet.popMatrix();
    }

}
