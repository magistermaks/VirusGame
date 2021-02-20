package net.darktree.virus.codon.arg;

import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.util.Helpers;

public class CodonValueArg extends ComplexCodonArg {

    public int value = 0;

    public CodonValueArg(int id, CodonMetaInfo info) {
        super(id, info);
    }

    @Override
    public void setParam( String param ) {
        value = Helpers.clamp( Integer.parseInt( param.substring(0, 2) ), 0, 40 ) - 20;
    }

    @Override
    public String getParam() {
        return serialize( value + 20, 2 );
    }

    @Override
    public String getText() {
        return super.getText() + " (" + value + ")";
    }

    @Override
    public CodonArg clone() {
        CodonValueArg arg = new CodonValueArg( code, info );
        arg.value = value;
        return arg;
    }

    @Override
    public boolean is( CodonArg arg ) {
        return arg instanceof CodonValueArg;
    }

    @Override
    public String[] getOptions() {
        return new String[] { "value" };
    }

    @Override
    public void increment( int option ) {
        value = Helpers.clamp( value + 1, -20, 20 );
    }

    @Override
    public void decrement( int option ) {
        value = Helpers.clamp( value - 1, -20, 20 );
    }

    @Override
    public int get( int option ) {
        return value;
    }

}
