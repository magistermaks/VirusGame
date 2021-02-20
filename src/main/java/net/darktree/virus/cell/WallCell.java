package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.gui.Screen;

public class WallCell extends Cell{

    public WallCell( int ex, int ey ) {
        super( ex, ey, CellType.Locked );
    }

    @Override
    protected void drawCell(Screen screen) {
        fill(Const.COLOR_CELL_LOCKED);
        rect(0, 0, Const.BIG_FACTOR, Const.BIG_FACTOR);
    }

    @Override
    public String getCellName(){
        return "Wall";
    }

}
