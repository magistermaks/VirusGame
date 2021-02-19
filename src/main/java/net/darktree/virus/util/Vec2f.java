package net.darktree.virus.util;

public class Vec2f {

    public static final Vec2f ZERO = new Vec2f(0, 0);
    public float x, y;

    public Vec2f( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public Vec2f() {
        this( 0, 0 );
    }

    public Vec2f copy() {
        return new Vec2f( x, y );
    }

    @Deprecated
    public float get( int dim ) {
        return dim == 0 ? x : y;
    }

    @Deprecated
    public void set( int dim, float value ) {
        if( dim == 0 ) {
            x = value;
        }else{
            y = value;
        }
    }

}