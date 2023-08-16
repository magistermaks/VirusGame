package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Utils;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;

public class WasteParticle extends Particle {

    public boolean decay = false;

    public WasteParticle(Vec2f pos) {
        this(pos, Helpers.getRandomVelocity());
    }

    public WasteParticle(Vec2f pos, Vec2f vel) {
        super(pos, vel);
        Main.applet.world.getStats().WASTES.increment();
    }

    @Override
    public void tick(World world) {
        super.tick(world);

        float maxAge = 1 / Const.AGE_GROW_SPEED;
        if( decay && age > maxAge ) {
            age = (int) maxAge;
        }

        if( decay ) age -= 2;
        if( age < 0 ) remove();
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
    public void randomTick() {
        if( (age > 3600) && Main.applet.world.getCellAt(pos.x, pos.y) == null ) decay = true;
    }

    @Override
    protected void border() {
        if( Main.applet.world.pc.getCount(ParticleType.WASTE) > Const.MAX_WASTE && Utils.random(0.0f, 1.0f) < Const.WASTE_DISPOSAL_CHANCE_HIGH ) remove();
        if( Utils.random(0.0f, 1.0f) < Const.WASTE_DISPOSAL_CHANCE_LOW ) remove();
    }

}
