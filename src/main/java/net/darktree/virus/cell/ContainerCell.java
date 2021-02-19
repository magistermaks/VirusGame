package net.darktree.virus.cell;

import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleContainer;
import net.darktree.virus.particle.ParticleType;

import java.util.ArrayList;

public interface ContainerCell {

    ParticleContainer getContainer();

    default void addParticle(Particle particle){
        getContainer().get(particle.getType()).add(particle);
    }

    default void removeParticle(Particle particle){
        getContainer().get(particle.getType()).remove(particle);
    }

    default Particle selectParticle(ParticleType type){
        ArrayList<Particle> myList = getContainer().get(type);
        if(myList.size() == 0){

            // TODO: make it more obvious
            if( type == ParticleType.WASTE ) {
                return selectParticle( ParticleType.UGO );
            }

            return null;
        }else{
            int choiceIndex = (int)(Math.random() * myList.size());
            return myList.get(choiceIndex);
        }
    }

    default int getParticleCount(ParticleType t){
        if(t == null){
            return getContainer().count();
        }else{
            return getContainer().get(t).size();
        }
    }

}
