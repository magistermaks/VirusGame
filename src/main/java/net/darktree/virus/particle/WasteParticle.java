package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;

public class WasteParticle extends Particle {

    public WasteParticle(Vec2f pos, int b) {
        this(pos, Helpers.getRandomVelocity(), b);
    }

    public WasteParticle(Vec2f pos, Vec2f vel, int b) {
        super(pos, vel, b);
        Main.applet.world.totalWasteCount ++;
    }

    @Override
    public ParticleType getType() {
        return ParticleType.WASTE;
    }

    @Override
    public int getColor() {
        return Const.COLOR_WASTE;
    }

    @Override
    protected void randomTick() {
        if( Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_RANDOM && Main.applet.world.getCellAt(pos.x, pos.y) == null ) removeParticle(null);
    }

    @Override
    protected void border() {
        if( Main.applet.world.pc.wastes.size() > Const.MAX_WASTE && Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_HIGH ) removeParticle(null);
        if( Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_LOW ) removeParticle(null);
    }

}
