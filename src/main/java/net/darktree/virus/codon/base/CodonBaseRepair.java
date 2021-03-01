package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;

public class CodonBaseRepair extends CodonBase {

    public CodonBaseRepair( int code, CodonMetaInfo info ) {
        super( code, new CodonArg[] { CodonArgs.NONE, CodonArgs.WALL }, info );
    }

    @Override
    public int execute(NormalCell cell, CodonArg arg, int acc) {
        if( !cell.isHandInwards() ){
            if( arg.is(CodonArgs.WALL) ){
                cell.healWall();
                cell.laserWall();
                cell.useEnergy();
                return SUCCESS;
            }
        }

        return getDefault(arg);
    }

}
