package net.darktree.virus.codon.arg;

import net.darktree.virus.codon.CodonMetaInfo;

public abstract class CodonArgComplex extends CodonArg {

    public CodonArgComplex(int id, CodonMetaInfo info ) {
        super( id, info );
    }

    public static String serialize( int value, int len ) {
        String str = String.valueOf( value );
        int length = str.length();

        if( length == len ) return str;
        if( length >= len ) return str.substring(0, len);

        return String.format("%0" + len + "d", Integer.parseInt(str));
    }

    @Override
    public String asDNA() {
        return super.asDNA() + getParam();
    }

    public void setParam( String param ) {
    }

    public String getParam() {
        return "";
    }

    public String[] getOptions() {
        return new String [] {};
    }

    public void increment( int option ) {

    }

    public void decrement( int option ) {

    }

    public int get( int option ) {
        return 0;
    }

}
