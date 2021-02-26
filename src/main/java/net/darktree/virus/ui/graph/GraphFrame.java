package net.darktree.virus.ui.graph;

import net.darktree.virus.Const;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.data.Table;
import processing.data.TableRow;

public class GraphFrame {

    protected int wastes = 0;
    protected int viruses = 0;
    protected int cells = 0;

    public GraphFrame( int wastes, int viruses, int cells ) {
        this.wastes = wastes;
        this.viruses = viruses;
        this.cells = cells;
    }

    public GraphFrame() {
        super();
    }

    public int getHighest() {
        return PApplet.max( wastes, viruses, cells );
    }

    public GraphFrame draw( PGraphics canvas, float x1, float x2, float u, float h, GraphFrame last ) {

        canvas.stroke(Const.COLOR_GRAPH_WASTES);
        canvas.line( x1, h - last.wastes * u, x2, h - wastes * u );

        canvas.stroke(Const.COLOR_GRAPH_UGOS);
        canvas.line( x1, h - last.viruses * u, x2, h - viruses * u );

        canvas.stroke(Const.COLOR_GRAPH_CELLS);
        canvas.line( x1, h - last.cells * u, x2, h - cells * u );

        return this;
    }

    public void appendToTable( Table table, int id ) {
        TableRow row = table.addRow();
        row.setInt( "id", id );
        row.setInt( "wastes", wastes );
        row.setInt( "viruses", viruses );
        row.setInt( "cells", cells );
    }

}
