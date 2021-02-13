package net.darktree.virus.codon.arg;

import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.util.Helpers;

public class CodonRangeArg extends ComplexCodonArg {

    public CodonRangeArg(int id, CodonMetaInfo info) {
        super( id, info );
    }

    public int start = 0;
    public int end = 0;

    @Override
    public void setParam( String param ) {
        start = Helpers.clamp( Integer.parseInt( param.substring(0, 2) ), 0, 40 ) - 20;
        end = Helpers.clamp( Integer.parseInt( param.substring(2) ), 0, 40 ) - 20;
    }

    @Override
    public String getParam() {
        String str = "";
        str += serialize( start + 20, 2 );
        str += serialize( end + 20, 2 );
        return str;
    }

    @Override
    public String getText() {
        return super.getText() + " (" + start + " to " + end + ")";
    }

    @Override
    public CodonArg clone() {
        CodonRangeArg arg = new CodonRangeArg( code, info );
        arg.start = start;
        arg.end = end;
        return arg;
    }

    @Override
    public boolean is( CodonArg arg ) {
        return arg instanceof CodonRangeArg;
    }

    @Override
    public String[] getOptions() {
        return new String[] { "from", "to" };
    }

    @Override
    public void increment( int option ) {
        if( option == 0 ) start = Helpers.clamp( start + 1, -20, 20 ); else end = Helpers.clamp( end + 1, -20, 20 );
    }

    @Override
    public void decrement( int option ) {
        if( option == 0 ) start = Helpers.clamp( start - 1, -20, 20 ); else end = Helpers.clamp( end - 1, -20, 20 );
    }

    @Override
    public int get( int option ) {
        return option == 0 ? start : end;
    }

}
