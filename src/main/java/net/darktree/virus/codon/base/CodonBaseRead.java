package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonRangeArg;

public class CodonBaseRead extends CodonBase {

    public CodonBaseRead( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.RANGE }, info );
    }

    @Override
    public int execute(NormalCell cell, CodonArg arg, int acc) {
        if( cell.isHandInwards() && arg.is(CodonArgs.RANGE) ){
            CodonRangeArg rangeArg = (CodonRangeArg) arg;
            cell.readToMemory( rangeArg.start, rangeArg.end );
            cell.useEnergy();
            return SUCCESS;
        }

        return getDefault(arg);
    }

}
