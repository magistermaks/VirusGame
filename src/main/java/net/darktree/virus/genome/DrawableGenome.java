package net.darktree.virus.genome;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.util.DrawContext;

import java.util.ArrayList;

public class DrawableGenome extends GenomeBase implements DrawContext {

    public DrawableGenome( String dna ) {
        super( dna );
    }

    public DrawableGenome( ArrayList<Codon> codons ) {
        super( codons );
    }

    public void drawCodons( float distance ) {
        final float codonAngle = 1.0f / Math.max(3, codons.size()) * TWO_PI;
        final float partAngle = codonAngle / 5.0f;
        int i = 0;

        for( Codon codon : codons ) {
            push();
            rotate( (i ++) * codonAngle - HALF_PI );

            if( codon.health < 0.97f ) {
                drawCodonElement(Const.TELOMERE_SHAPE, partAngle, +1, distance, Const.COLOR_TELOMERE);
            }

            float angle = partAngle * codon.health;
            drawCodonElement( Const.CODON_SHAPE, angle, -1, distance, codon.getBaseColor() );
            drawCodonElement( Const.CODON_SHAPE, angle, +1, distance, codon.getArgColor() );
            pop();
        }
    }

    private void drawCodonElement( float[][] geometry, float angleMultiplier, float distanceMultiplier, float distance, int color ) {
        fill(color);
        beginShape();
        for( float[] cv : geometry ) {
            final float angle = cv[0] * angleMultiplier;
            final float dist = cv[1] * distanceMultiplier * Const.CODON_WIDTH + distance;
            vertex(Main.cos(angle) * dist, Main.sin(angle) * dist);
        }
        endShape(CLOSE);
    }

}
