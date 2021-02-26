package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.ui.Screen;

public class WallCell extends Cell{

    public WallCell( int x, int y ) {
        super( x, y );
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

    @Override
    public CellType getType() {
        return CellType.Locked;
    }

}
