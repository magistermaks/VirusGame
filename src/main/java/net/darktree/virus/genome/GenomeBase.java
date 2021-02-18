package net.darktree.virus.genome;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.util.Vec2f;

import java.util.ArrayList;

public class GenomeBase {

    public ArrayList<Codon> codons = new ArrayList<>();

    public GenomeBase( String dna ) {
        for( String part : dna.split("-") ) {
            codons.add( new Codon( part ) );
        }
    }

    public GenomeBase( ArrayList<Codon> codons ) {
        this.codons = codons;
    }

    public void hurtCodons( Cell cell ){
        for(int i = 0; i < codons.size(); i++){
            Codon codon = codons.get(i);

            if( codon.hasSubstance() ){
                if( codon.hurt() ) {
                    if( cell != null ) {
                        Particle waste = new Particle( getCodonPos(i, Const.CODON_DIST, cell.x, cell.y), ParticleType.WASTE, -99999 );
                        Main.applet.world.addParticle( waste );
                    }

                    codons.remove(i);
                    return;
                }
            }
        }
    }

    public Vec2f getCodonPos(int i, float r, int x, int y){
        final float theta = i * Main.TWO_PI / codons.size() - Main.HALF_PI;
        final float sr = r / Const.BIG_FACTOR;
        final float cx = x + 0.5f + sr * Main.cos(theta);
        final float cy = y + 0.5f + sr * Main.sin(theta);
        return new Vec2f( cx, cy );
    }

    public String asDNA() {
        StringBuilder dna = new StringBuilder();

        for (Codon codon : codons) {
            dna.append( codon.asDNA() ).append("-");
        }

        return dna.substring(0, dna.length() - 1);
    }

    public void shorten() {
        codons.remove( codons.size() - 1 );
    }

    public void lengthen() {
        codons.add( new Codon() );
    }

    public int size() {
        return codons.size();
    }

}
