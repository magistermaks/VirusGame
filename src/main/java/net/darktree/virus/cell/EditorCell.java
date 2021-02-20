package net.darktree.virus.cell;

import net.darktree.virus.Const;

public class EditorCell extends NormalCell {

    public EditorCell() {
        super(-1, -1, Const.DEFAULT_VIRUS_GENOME);
    }

    @Override
    public String getCellName() {
        return "Custom Virus";
    }

    @Override
    public CellType getType() {
        // I probably should define a special cell type for this
        return CellType.Normal;
    }

}
