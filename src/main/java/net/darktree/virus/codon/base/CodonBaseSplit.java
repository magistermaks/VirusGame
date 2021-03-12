package net.darktree.virus.codon.base;

import net.darktree.virus.Main;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonArgValue;
import net.darktree.virus.util.Direction;
import net.darktree.virus.world.World;

public class CodonBaseSplit extends CodonBase {

    public CodonBaseSplit( int code, CodonMetaInfo info ) {
        super(code, new CodonArg[] { CodonArgs.NONE, CodonArgs.VALUE }, info);
    }

    @Override
    public int execute(NormalCell cell, CodonArg arg, int acc) {
        World world = Main.applet.world;
        float split;

        if( arg.is( CodonArgs.VALUE ) ) {
            split = (((CodonArgValue) arg).value + 20f) / 40f;
        }else{
            return SUCCESS;
        }

        Direction direction = Direction.getRandom();
        int ox, oy;

        for( int i = 0; i < 4; i ++ ) {
            ox = cell.x + direction.x;
            oy = cell.y + direction.y;

            if( world.isCellValid(ox, oy) && world.getCellTypeAt(ox, oy) == CellType.Empty ) {
                NormalCell child = new NormalCell( ox, oy, cell.genome.asDNA() );
                child.genome.mutate();

                child.energy = cell.energy * split;
                child.wall = cell.wall * split;

                cell.energy -= child.energy;
                cell.wall -= child.wall;

                world.setCellAt(ox, oy, child);
                return SUCCESS;
            }

            direction = direction.cycle();
        }

        return FAILURE;
    }

}
