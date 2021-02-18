package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.gui.Screen;

import java.util.ArrayList;
import java.util.Iterator;

public class ParticleContainer {

    public final ArrayList<Particle> foods = new ArrayList<>();
    public final ArrayList<Particle> wastes = new ArrayList<>();
    public final ArrayList<Particle> ugos = new ArrayList<>();

    public ArrayList<Particle> get(ParticleType type ) {

        switch( type ){
            case FOOD: return foods;
            case WASTE: return wastes;
            case UGO: return ugos;
        }

        return null;
    }

    public void add( ArrayList<Particle> queue ) {
        for( Particle p : queue ) {
            get( p.type ).add( p );
        }

        queue.clear();
    }

    public void tick( ParticleType type ) {
        for (Iterator<Particle> it = get(type).iterator(); it.hasNext();) {
            Particle p = it.next();
            p.tick();
            if( p.removed ) it.remove();
        }
    }

    public int count() {
        return foods.size() + wastes.size() + ugos.size();
    }

    public void randomTick() {
        if( Main.applet.frameCount % 10 == 0 ) {
            int c = count() / Const.PARTICLES_PER_RAND_UPDATE;

            for( ; c > 0; c -- ) {
                ArrayList<Particle> array = get( ParticleType.fromId( (int) Main.applet.random(0, 2) ) );
                if( array.size() > 0 ) {
                    int index = (int) Main.applet.random(0, array.size());
                    if( index != -1 ) array.get( index ).randomTick();
                }
            }
        }
    }

    public void draw(Screen screen) {
        for( Particle p : foods ) drawParticle(p, screen);
        for( Particle p : wastes ) drawParticle(p, screen);
        for( Particle p : ugos ) drawParticle(p, screen);
    }

    private void drawParticle(Particle p, Screen screen) {
        Main.applet.pushMatrix();
        p.draw(screen);
        Main.applet.popMatrix();
    }

}
