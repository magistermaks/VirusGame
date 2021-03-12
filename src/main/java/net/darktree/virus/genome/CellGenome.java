package net.darktree.virus.genome;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.util.Helpers;

import java.util.ArrayList;

public class CellGenome extends DrawableGenome {

    private int acc = 0;

    public int selected = 0;
    public int pointed = 0;
    public boolean inwards = false;

    private float interS = 0; // interpolated selection
    private float interP = 0; // interpolated pointer
    private float interH = 0; // interpolated hand

    public CellGenome(String dna) {
        super(dna);
    }

    public CellGenome(ArrayList<Codon> codons) {
        super(codons);
    }

    // TODO: find the real cause of the problem, and not patch it around
    // TODO: this game surfers from some strange NULL illness, there are nulls everywhere!
    public void executeSelected(NormalCell cell) {
        int s = codons.size();
        Codon codon = s == 0 && selected >= s ? null : codons.get(selected);

        if( codon != null ) {
            hurtCodons(cell);
            acc = codon.execute(cell, acc);
        }
    }

    public void next() {
        int s = codons.size();
        selected = ((s == 0) ? 0 : ((selected + 1) % s));
    }

    public void update(){
        int s = codons.size();
        if( s != 0 ) {
            interS += Helpers.loopIt( selected - interS, s, true) * Const.VISUAL_TRANSITION * Const.PLAY_SPEED;
            interP += Helpers.loopIt( pointed - interP, s, true) * Const.VISUAL_TRANSITION * Const.PLAY_SPEED;
            interH += ((inwards?1:0) - interH) * Const.VISUAL_TRANSITION * Const.PLAY_SPEED;
            interS = Helpers.loopIt(interS, s, false);
            interP = Helpers.loopIt(interP, s, false);
        }else{
            interS = 0;
            interP = 0;
        }
    }

    public void drawHand(){

        // draw hand orbit
        strokeWeight(1);
        noFill();
        stroke( Helpers.addAlpha(Const.COLOR_HAND, 0.5f) );
        ellipse(0, 0, Const.HAND_DIST * 2, Const.HAND_DIST * 2);
        noStroke();

        push();

            // rotate and translate to hand position (pointer)
            rotate(interP * TWO_PI / codons.size());
            translate(0, -Const.HAND_DIST);

            // rotate hand itself
            rotate(interH * PI);

            // draw hand
            fill(Const.COLOR_HAND);
            beginShape();
            vertex(5, 0);
            vertex(-5, 0);
            vertex(0, -Const.HAND_LEN);
            endShape(CLOSE);

        pop();
    }

    public void drawInterpreter(float timer){
        float angle = 1.0f / codons.size() * PI;
        float delta = timer / Const.GENE_TICK_TIME;
        int col = delta < 0.5f ? Main.parseInt( Main.min(1, (0.5f - delta) * 4) * 255 ) : 255;

        push();

            // rotate to interpreter position (selected)
            rotate( -HALF_PI + angle * 2 * interS);

            fill( col );
            strokeWeight(1);
            stroke(80);
            beginShape();
            vertex(0, 0);
            vertex( Const.INTERPRETER_LENGTH * Main.cos(angle), Const.INTERPRETER_LENGTH * Main.sin(angle) );
            vertex( Const.INTERPRETER_LENGTH * Main.cos(-angle), Const.INTERPRETER_LENGTH * Main.sin(-angle) );
            endShape(CLOSE);
            noStroke();

        pop();
    }

    public int getWeakestCodon(){
        float record = Float.MAX_VALUE;
        int holder = -1;

        for(int i = 0; i < codons.size(); i++){
            float val = codons.get(i).health;

            if(val < record){
                record = val;
                holder = i;
            }
        }

        return holder;
    }

    public float getInterpolatedOffset() {
        return interS + 0.5f;
    }

    public int getAccumulator() {
        return acc;
    }
}
