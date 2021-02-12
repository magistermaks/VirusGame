
Vec2f getRandomVelocity() {
    float sp = (float) Math.random() * (SPEED_HIGH - SPEED_LOW) + SPEED_LOW;
    float ang = random(0,2 * PI);
    return new Vec2f( sp * cos(ang), sp * sin(ang) );
}

Vec2f combineVelocity(Vec2f a, Vec2f b) {
    float ac = a.x + b.x + SPEED_LOW;
    float bc = a.y + b.y + SPEED_LOW;
    return new Vec2f( ac > SPEED_HIGH ? SPEED_HIGH : ac, bc > SPEED_HIGH ? SPEED_HIGH : bc );
}

color transperize(color col, float trans){
    return color(red(col), green(col), blue(col), trans * 255);
}

int clamp( int value, int min, int max ) {
    if( value > max ) return max;
    if( value < min ) return min;
    return value;
}

public class Vec2f {
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
    
    // TODO: temporary fix, remove later
    // deprected
    public float get( int dim ) {
        return dim == 0 ? x : y; 
    }
   
    // TODO: temporary fix, remove later
    // deprected
    public void set( int dim, float value ) {
        if( dim == 0 ) {
            x = value; 
        }else{
            y = value; 
        }
    }
    
}


// BEGIN JUNK //

float euclidLength(float[] coor){
    return sqrt( pow(coor[0]-coor[2], 2) + pow(coor[1]-coor[3], 2) );
}

String framesToTime(float f){
    float ticks = f/settings.gene_tick_time;
    if(ticks >= 1000) return round(ticks) + "";
    return nf((float)ticks, 0, 1);
}

float loopIt(float x, float len, boolean evenSplit){
    if(evenSplit){
        while(x >= len*0.5) x -= len;
        while(x < -len*0.5) x += len;
    }else{
        while(x > len-0.5) x -= len;
        while(x < -0.5) x += len;
    }
    
    return x;
}

int loopItInt(int x, int len){
    return (x + len * 10) % len;
}
