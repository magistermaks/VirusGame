package net.darktree.virus.particle;

import net.darktree.virus.Main;

public enum ParticleType {
    FOOD,
    WASTE,
    UGO;

    public static ParticleType fromId(int id ) {
        switch(id){
            case 0: return ParticleType.FOOD;
            case 1: return ParticleType.WASTE;
            case 2: return ParticleType.UGO;
        }
        return null;
    }
}