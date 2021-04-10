package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.genome.GenomeBase;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.VirusParticle;

public class EditorCell extends Cell implements GenomeCell {

    private GenomeBase genome = new GenomeBase( Const.DEFAULT_VIRUS_GENOME );
    private Particle particle;

    public EditorCell(Particle particle) {
        super(-1, -1);
        this.particle = particle;
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

    @Override
    public GenomeBase getGenome() {
        return particle == null ? genome : particle instanceof VirusParticle ? ((VirusParticle) particle).getGenome() : null;
    }

    public void setGenome(GenomeBase genome) {
        this.genome = genome;
    }

    public Particle getParticle() {
        if( particle != null && particle.isRemoved() ) {
            Main.applet.editor.close();
            particle = null;
        }

        return particle;
    }

}
