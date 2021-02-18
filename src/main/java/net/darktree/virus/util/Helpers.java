package net.darktree.virus.util;

import net.darktree.virus.Main;
import processing.core.PApplet;

public class Helpers {

    public static Vec2f getRandomVelocity() {
        float sp = (float) Math.random() * (Main.SPEED_HIGH - Main.SPEED_LOW) + Main.SPEED_LOW;
        float ang = Main.applet.random(0,2 * Main.applet.PI);
        return new Vec2f( sp * PApplet.cos(ang), sp * PApplet.sin(ang) );
    }

    public static Vec2f combineVelocity(Vec2f a, Vec2f b) {
        float ac = a.x + b.x + Main.SPEED_LOW;
        float bc = a.y + b.y + Main.SPEED_LOW;
        return new Vec2f(Math.min(ac, Main.SPEED_HIGH), Math.min(bc, Main.SPEED_HIGH));
    }

    public static int addAlpha(int col, float alpha){
        return Main.applet.color(Main.applet.red(col), Main.applet.green(col), Main.applet.blue(col), alpha * 255);
    }

    public static int clamp( int value, int min, int max ) {
        if (value > max) return max;
        return Math.max(value, min);
    }

    @Deprecated
    public static float euclidLength(float[] coor){
        return PApplet.sqrt( PApplet.pow(coor[0]-coor[2], 2) + PApplet.pow(coor[1]-coor[3], 2) );
    }

    @Deprecated
    public static String framesToTime(float f){
        float ticks = f / Main.applet.settings.gene_tick_time;
        if(ticks >= 1000) return PApplet.round(ticks) + "";
        return PApplet.nf(ticks, 0, 1);
    }

    @Deprecated
    public static float loopIt(float x, float len, boolean evenSplit){
        if(evenSplit){
            while(x >= len*0.5f) x -= len;
            while(x < -len*0.5f) x += len;
        }else{
            while(x > len-0.5f) x -= len;
            while(x < -0.5f) x += len;
        }

        return x;
    }

    @Deprecated
    public static int loopItInt(int x, int len){
        return (x + len * 10) % len;
    }

}
