package net.darktree.virus.util;

import net.darktree.virus.Main;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public interface DrawContext {

    int CLOSE = PApplet.CLOSE;
    int LEFT = PApplet.LEFT;
    int RIGHT = PApplet.RIGHT;
    int CENTER = PApplet.CENTER;
    int TOP = PApplet.TOP;
    float PI = PApplet.PI;
    float HALF_PI = PApplet.HALF_PI;
    float TWO_PI = PApplet.TWO_PI;
    float EPSILON = PApplet.EPSILON;

    default int getFrameCount() {
        return Main.applet.frameCount;
    }
    default void fill( int color ) {
        Main.applet.fill( color );
    }

    default void fill( float r, float g, float b, float a ) {
        Main.applet.fill( r, g, b, a );
    }

    default void noFill() {
        Main.applet.noFill();
    }

    default void push() {
        Main.applet.pushMatrix();
    }

    default void pop() {
        Main.applet.popMatrix();
    }

    default void beginShape() {
        Main.applet.beginShape();
    }

    default void noStroke() {
        Main.applet.noStroke();
    }

    default void strokeWeight( float weight ) {
        Main.applet.strokeWeight(weight);
    }

    default void stroke( int color ) {
        Main.applet.stroke(color);
    }

    default void stroke( float r, float g, float b, float a ) {
        Main.applet.stroke( r, g, b, a );
    }

    default void endShape( int mode ) {
        Main.applet.endShape(mode);
    }

    default void rotate( float angle ) {
        Main.applet.rotate(angle);
    }

    default void vertex( float a, float b ) {
        Main.applet.vertex(a, b);
    }

    default void ellipse( float a, float b, float c, float d ) {
        Main.applet.ellipse(a, b, c, d);
    }

    default void translate( float a, float b ) {
        Main.applet.translate( a, b );
    }

    default void image( PImage image, float a, float b ) {
        Main.applet.image(image, a, b);
    }

    default void image( PImage image, float a, float b, float c, float d ) {
        Main.applet.image(image, a, b, c, d);
    }

    default PGraphics createGraphics( int w, int h ) {
        return Main.applet.createGraphics(w, h);
    }

    default void rect( float a, float b, float c, float d ) {
        Main.applet.rect(a, b, c, d);
    }

    default void scale( float scale ) {
        Main.applet.scale( scale );
    }

    default void ellipseMode( int mode ) {
        Main.applet.ellipseMode(mode);
    }

    default void textSize( float size ) {
        Main.applet.textSize(size);
    }

    default void textAlign( int mode ) {
        Main.applet.textAlign( mode );
    }

    default void textAlign( int mode1, int mode2 ) {
        Main.applet.textAlign( mode1, mode2 );
    }

    default void text( String str, float x, float y ) {
        Main.applet.text( str, x, y );
    }

    default void noTint() {
        Main.applet.noTint();
    }

    default void line( float a, float b, float c, float d ) {
        Main.applet.line(a, b, c, d);
    }

}
