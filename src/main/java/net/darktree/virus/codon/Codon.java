package net.darktree.virus.codon;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.ComplexCodonArg;
import net.darktree.virus.codon.base.CodonBase;
import net.darktree.virus.logger.Logger;

public class Codon {

    public float health = 1.0f;
    public CodonArg arg;
    public CodonBase base;

    public Codon( Codon codon ) {
        this( codon.base, codon.arg );
        this.health = codon.health;
    }

    public Codon( CodonBase base, CodonArg arg ) {
        this.base = base;
        this.arg = arg;
    }

    public Codon() {
        this( CodonBases.NONE, CodonArgs.NONE );
    }

    public Codon( String dna ) {

        try{
            int codonCode = (int) dna.charAt(0) - (int) 'A';
            int argCode = (int) dna.charAt(1) - (int) 'a';

            base = CodonBases.get( codonCode );
            CodonArg arg = CodonArgs.get( argCode ).clone();

            if( arg instanceof ComplexCodonArg) {
                ((ComplexCodonArg) arg).setParam( dna.substring(2) );
            }

            setArg( arg );
        }catch(Exception ex) {
            Logger.error( "Failed to create codon from genome: '" + dna + "'!" );
            this.arg = CodonArgs.NONE;
            this.base = CodonBases.NONE;
        }

    }

    public String getArgText() {
        return arg.getText();
    }

    public String getBaseText() {
        return base.getText();
    }

    public int getArgColor() {
        return arg.getColor();
    }

    public int getBaseColor() {
        return base.getColor();
    }

    public CodonArg[] getArgs() {
        return base.getArgs();
    }

    public String asDNA() {
        return base.asDNA() + arg.asDNA();
    }

    public boolean hasSubstance() {
        return (base != CodonBases.NONE) || (arg != CodonArgs.NONE);
    }

    public boolean isComplex() {
        return arg instanceof ComplexCodonArg;
    }

    public void setArg( CodonArg arg ) {
        boolean flag = false;

        for( CodonArg ca : base.args ) {
            if( ca.is(arg) ) {
                flag = true;
                break;
            }
        }

        this.arg = flag ? arg : CodonArgs.NONE;
    }

    public void setBase( CodonBase base ) {
        this.base = base;

        for( CodonArg ca : base.getArgs() ) {
            if( ca.is(this.arg) ) return;
        }

        this.arg = CodonArgs.NONE;
    }

    public boolean hurt() {
        if( hasSubstance() ) {
            health -= Math.random() * Const.CODON_DEGRADE_SPEED;
            if(health <= 0) {
                health = 1;
                arg = CodonArgs.NONE;
                base = CodonBases.NONE;
                return true;
            }
        }
        return false;
    }

    public CodonArg getArg() {
        return arg;
    }

    public void tick( NormalCell cell ) {
        base.tick( cell, arg );
    }

}
