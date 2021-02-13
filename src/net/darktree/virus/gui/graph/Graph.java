package net.darktree.virus.gui.graph;

import net.darktree.virus.Main;
import net.darktree.virus.util.DrawContext;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Graph implements DrawContext {

    private final GraphFrame[] frames;
    public int offset = 0;
    private int highest = 0;
    private boolean rescan = true;
    private boolean redraw = true;
    private PGraphics canvas;

    public Graph( int len, int w, int h ) {
        frames = new GraphFrame[len];
        canvas = createGraphics( w, h );
        for(int i = 0; i < len; i++) frames[i] = new GraphFrame();
    }

    public void append( GraphFrame frame ) {
        offset = (offset + 1) % frames.length;
        frames[offset] = frame;
        int h = frame.getHighest();

        if( h >= highest ) {
            highest = h;
        }else if( highest > 200 && rescan ) {
            highest = getHighest(true);
        }

        redraw = true;
    }

    public void setRescan( boolean rescan ) {
        this.rescan = rescan;
    }

    public void resize( int w, int h ) {
        canvas = createGraphics( w, h );
        redraw = true;
    }

    public void draw( float x, float y ) {

        if( redraw ) {

            final int hi = Math.max( 200, highest );
            final float uy = (float) (canvas.height) / hi;
            final float ux = (float) (canvas.width) / (frames.length - 1);
            final float ls = hi / 16.0f;
            final float ly = (canvas.height - 20) / hi;

            canvas.beginDraw();
            canvas.strokeWeight(4);

            canvas.fill(80);
            canvas.noStroke();
            canvas.rect( 0, 0, canvas.width, canvas.height );

            canvas.fill(255, 255, 255, 150);
            canvas.textAlign(LEFT);
            canvas.textFont(Main.applet.font, 20);

            for( int i = 16; i >= 0; i -- ) {
                canvas.text( "" + PApplet.floor( ls * i ), 4, (16 - i) * ls * ly + 20 );
            }

            GraphFrame last = frames[ (offset + 1) % frames.length ];

            for( int i = 2; i <= frames.length; i ++ ) {
                int pos = (offset + i) % frames.length;

                float x1 = ux * (i - 2);
                float x2 = ux * (i - 1);

                last = frames[ pos ].draw( canvas, x1, x2, uy, canvas.height, last );
            }

            canvas.endDraw();
            redraw = false;

        }

        image(canvas, x, y - canvas.height);

    }

    public int getHighest( boolean update ) {
        if( !update ) return highest;

        int hi = frames[0].getHighest();

        for( int i = 1; i <= frames.length; i ++ ) {
            int pos = (offset + i) % frames.length;
            int h = frames[pos].getHighest();
            if( h > hi ) hi = h;
        }

        return hi;
    }

}
