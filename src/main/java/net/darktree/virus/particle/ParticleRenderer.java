package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.DrawContext;

import java.util.ArrayList;

public class ParticleRenderer implements DrawContext {

    private static final ParticleRenderer INSTANCE = new ParticleRenderer();
    private static final ArrayList<Particle> particles = new ArrayList<>();
    private static Particle[] delegates;

    public static void clear() {
        particles.clear();
    }

    public static void add( Particle particle ) {
        particles.add(particle);
    }

    public static void delegate() {
        delegates = particles.toArray(new Particle[] {});
    }

    public static void draw(Screen screen) {
        INSTANCE.drawDelegates(screen);
    }

    private void drawDelegates(Screen screen) {
        noStroke();
        float size = screen.camS / Const.BIG_FACTOR;

        for( Particle particle : delegates ) {

            float x = screen.trueXtoAppX(particle.pos.x);
            float y = screen.trueYtoAppY(particle.pos.y);

            if( x > 0 && y > 0 && x < screen.maxRight && y < Main.applet.height ) {

                push();
                translate( x, y );
                scale( size * particle.getScale() );
                particle.draw(screen);
                pop();

            }
        }
    }

}
