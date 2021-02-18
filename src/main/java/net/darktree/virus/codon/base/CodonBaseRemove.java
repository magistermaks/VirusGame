package net.darktree.virus.codon.base;

import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;

public class CodonBaseRemove extends CodonBase {

    public CodonBaseRemove( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.FOOD, CodonArgs.WASTE, CodonArgs.WALL }, info );
    }

    @Override
    public void tick(NormalCell cell, CodonArg arg ) {
        if( !cell.isHandInwards() ){
            if(arg == CodonArgs.WASTE){
                Particle p = cell.selectParticle( ParticleType.WASTE );
                if(p != null) cell.pushOut(p);
                cell.useEnergy();
            }else if(arg == CodonArgs.FOOD) {
                Particle p = cell.selectParticle( ParticleType.FOOD );
                if(p != null) cell.pushOut(p);
                cell.useEnergy();
            }else if(arg == CodonArgs.WALL){
                cell.die(false);
            }
        }
    }

}
