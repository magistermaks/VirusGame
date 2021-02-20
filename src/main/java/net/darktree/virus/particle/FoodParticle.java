package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;

public class FoodParticle extends Particle {

    public FoodParticle(Vec2f pos, int b) {
        super(pos, Helpers.getRandomVelocity(), b);
        Main.applet.world.totalFoodCount ++;
    }

    @Override
    public ParticleType getType() {
        return ParticleType.FOOD;
    }

    @Override
    public int getColor() {
        return Const.COLOR_FOOD;
    }

    @Override
    public boolean bouncesOff() {
        return false;
    }

}
