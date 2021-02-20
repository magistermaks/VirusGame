package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonRangeArg;
import net.darktree.virus.codon.arg.CodonValueArg;
import net.darktree.virus.util.Helpers;

public class CodonBaseMoveHand extends CodonBase {

    public CodonBaseMoveHand( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.INWARD, CodonArgs.OUTWARD, CodonArgs.WEAK_LOC, CodonArgs.VALUE }, info );
    }

    @Override
    public void tick(NormalCell cell, CodonArg arg ) {
        if(arg == CodonArgs.WEAK_LOC){
            cell.genome.pointed = cell.genome.getWeakestCodon();
        }else if(arg == CodonArgs.INWARD){
            cell.genome.inwards = true;
        }else if(arg == CodonArgs.OUTWARD){
            cell.genome.inwards = false;
        }else if(arg instanceof CodonValueArg){
            cell.genome.pointed = Helpers.loopItInt(cell.genome.selected + ((CodonValueArg) arg).value, cell.genome.codons.size());
        }
    }

}
