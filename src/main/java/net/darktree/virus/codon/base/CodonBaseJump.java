package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonValueArg;
import net.darktree.virus.util.Helpers;

public class CodonBaseJump extends CodonBase {

    public CodonBaseJump( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.VALUE }, info );
    }

    @Override
    public int execute(NormalCell cell, CodonArg arg, int acc) {
        if( arg.is(CodonArgs.VALUE) && acc == 0 ){
            CodonValueArg valueArg = (CodonValueArg) arg;

            // FIXME: Selected codon doesn't get executed, only the one after it.
            cell.genome.selected = Helpers.loopItInt( cell.genome.selected + valueArg.value, cell.genome.size() );
        }

        return acc;
    }

}
