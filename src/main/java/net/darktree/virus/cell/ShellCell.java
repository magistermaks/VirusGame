package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.particle.ParticleContainer;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.world.particle.ParticleCell;

public class ShellCell extends Cell implements ContainerCell {

    public float wall = 1.0f;
    public float energy = 0.5f;

    public ShellCell(int x, int y) {
        super(x, y);
    }

    @Override
    public CellType getType() {
        return CellType.Shell;
    }

    @Override
    protected void drawCell(Screen screen) {
        drawCellBackground( Const.COLOR_CELL_BACK );

        push();
        translate(Const.BIG_FACTOR * 0.5f, Const.BIG_FACTOR * 0.5f);
        drawEnergy();
        pop();
    }

    @Override
    public String getCellName(){
        return "Shell Cell";
    }

    public void healWall(){
        wall += (1-wall) * Const.E_RECIPROCAL;
    }

    public void giveEnergy() {
        energy += (1-energy) * Const.E_RECIPROCAL;
    }

    protected void drawCellBackground(int color) {
        fill(Const.COLOR_CELL_WALL);
        rect(0, 0, Const.BIG_FACTOR, Const.BIG_FACTOR);
        fill( color );

        float w = Const.BIG_FACTOR * 0.08f * wall;
        rect(w, w, Const.BIG_FACTOR - 2 * w, Const.BIG_FACTOR - 2 * w);
    }

    public void drawEnergy(){
        noStroke();
        fill(Const.COLOR_TELOMERE);
        ellipse(0, 0, 17, 17);

        if( energy > 0 ) {
            fill(Const.COLOR_ENERGY);
            ellipseMode(CENTER);
            ellipse(0, 0, 12 * energy + 2, 12 * energy + 2);
        }
    }

    @Override
    public ParticleCell getContainer() {
        return Main.applet.world.pc.getAt(x, y);
    }

}
