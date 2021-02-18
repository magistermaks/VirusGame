package net.darktree.virus.codon.base;

import net.darktree.virus.cell.Cell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonRangeArg;

public class CodonBaseWrite extends CodonBase {

    public CodonBaseWrite( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.RANGE }, info );
    }

    @Override
    public void tick(Cell cell, CodonArg arg ) {
        if( arg instanceof CodonRangeArg) {
            CodonRangeArg rangeArg = (CodonRangeArg) arg;
            cell.writeFromMemory( rangeArg.start, rangeArg.end );
            cell.useEnergy();
        }
    }

}
