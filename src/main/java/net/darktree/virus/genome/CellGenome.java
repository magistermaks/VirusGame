package net.darktree.virus.genome;

import net.darktree.virus.Main;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.codon.CodonBases;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.base.CodonBase;
import net.darktree.virus.util.Helpers;

import java.util.ArrayList;

public class CellGenome extends DrawableGenome {

    public int selected = 0;
    public int pointed = 0;
    public boolean inwards = false;

    public float appRO = 0;
    public float appPO = 0;
    public float appDO = 0;

    public CellGenome(String dna) {
        super(dna);
    }

    public CellGenome(ArrayList<Codon> codons) {
        super(codons);
    }

    public Codon getSelected() {
        return codons.size() == 0 ? null : codons.get(selected);
    }

    public void next() {
        int s = codons.size();
        selected = ((s == 0) ? 0 : ((selected + 1) % s));
    }

    public void mutate( double m ) {
        if( m > Main.applet.random(0, 1) ) {

            if( Main.applet.random(0, 1) < 0.3f && codons.size() > 1 ) { // delete
                codons.remove( (int) Main.applet.random( 0, codons.size() ) );
                return;
            }

            if( Main.applet.random(0, 1) < 0.4f ) { // replace
                CodonBase base = CodonBases.rand();
                CodonArg arg = base.getRandomArg();
                codons.set( (int) Main.applet.random( 0, codons.size() ), new Codon( base, arg ) );
                return;
            }

            if( Main.applet.random(0, 1) < 0.5f ) { // add
                CodonBase base = CodonBases.rand();
                CodonArg arg = base.getRandomArg();
                codons.add( new Codon( base, arg ) );
                return;
            }

            if( Main.applet.random(0, 1) < 0.6f ) { // swap

                int a = (int) Main.applet.random( 0, codons.size() );
                int b = (int) Main.applet.random( 0, codons.size() );

                if( a != b ) {
                    Codon ca = codons.get(a);
                    Codon cb = codons.get(b);

                    codons.set(a, cb);
                    codons.set(b, ca);
                }
            }
        }
    }

    public void update(){
        int s = codons.size();
        if( s != 0 ) {
            appRO += Helpers.loopIt( selected % Main.applet.settings.codons_per_page - appRO, s, true) * Main.VISUAL_TRANSITION * Main.PLAY_SPEED;
            appPO += Helpers.loopIt( pointed -appPO, s, true) * Main.VISUAL_TRANSITION * Main.PLAY_SPEED;
            appDO += ((inwards?1:0) - appDO) * Main.VISUAL_TRANSITION * Main.PLAY_SPEED;
            appRO = Helpers.loopIt( appRO, s, false);
            appPO = Helpers.loopIt( appPO, s, false);
        }else{
            appRO = 0;
            appPO = 0;
        }
    }

    public void drawHand(){
        float appPOAngle = appPO * TWO_PI / codons.size();
        float appDOAngle = appDO * PI;

        strokeWeight(1);
        noFill();
        stroke( Helpers.addAlpha(Main.applet.COLOR_HAND, 0.5f) );
        ellipse(0, 0, Main.HAND_DIST  *2, Main.HAND_DIST * 2);
        push();
        rotate( appPOAngle);
        translate(0, -Main.HAND_DIST);
        rotate(appDOAngle);
        noStroke();
        fill(Main.applet.COLOR_HAND);
        beginShape();
        vertex(5, 0);
        vertex(-5, 0);
        vertex(0, -Main.HAND_LEN);
        endShape(CLOSE);
        pop();
    }

    public void drawInterpreter(float timer){
        float angle = 1.0f / codons.size() * PI;
        float delta = timer / Main.applet.settings.gene_tick_time;
        int col = delta < 0.5f ? Main.parseInt( Main.min(1, (0.5f - delta) * 4) * 255 ) : 255;

        push();
        rotate( -HALF_PI + angle * 2 * appRO );
        fill( col );
        beginShape();
        strokeWeight(1);
        stroke(80);
        vertex(0, 0);
        vertex( Main.INTERPRETER_LENGTH * Main.cos(angle), Main.INTERPRETER_LENGTH * Main.sin(angle) );
        vertex( Main.INTERPRETER_LENGTH * Main.cos(-angle), Main.INTERPRETER_LENGTH * Main.sin(-angle) );
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

}
