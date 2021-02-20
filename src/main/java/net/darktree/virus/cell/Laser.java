package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;

import java.util.ArrayList;

public class Laser implements DrawContext {

    private final ArrayList<Vec2f> targets = new ArrayList<>();
    private Particle target = null;
    private int time = Integer.MIN_VALUE;

    public void targetParticle( Particle particle ) {
        targets.clear();
        time = getFrameCount();
        target = particle;
    }

    public void targetWall( int x, int y ) {
        reset();
        int a = x + 1, b = y + 1;
        addTargetPos( new Vec2f( x, b ) );
        addTargetPos( new Vec2f( x, y ) );
        addTargetPos( new Vec2f( a, b ) );
        addTargetPos( new Vec2f( a, y ) );
    }

    public void addTargetPos( Vec2f pos ) {
        targets.add( pos );
    }

    public void reset() {
        targets.clear();
        target = null;
        time = getFrameCount();
    }

    public void draw( Screen screen, Vec2f hand ) {
        float delta = time + Const.LASER_LINGER_TIME - getFrameCount();

        if( delta > 0 ){
            float alpha = delta / Const.LASER_LINGER_TIME;
            stroke( Helpers.addAlpha(Const.COLOR_HAND, alpha) );
            strokeWeight( 0.03f * Const.BIG_FACTOR );

            // no particle target set
            if(target == null){

                // copy array to avoid CME
                Vec2f[] points = targets.toArray(new Vec2f[] {});

                try {
                    for (Vec2f pos : points) {
                        screen.scaledLine(hand, pos);
                    }
                }catch(NullPointerException exception){
                    // How can this possibly happen?
                    // Good question! I would also like to know...
                    exception.printStackTrace();
                }

                return;
            }

            // check to eliminate blinking lines when targeted particle crosses world border
            if( Main.dist(hand.x, hand.y, target.pos.x, target.pos.y) < 2 ) {
                screen.scaledLine(hand, target.pos);
            }
        }
    }

}
