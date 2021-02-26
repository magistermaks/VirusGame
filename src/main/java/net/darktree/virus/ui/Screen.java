package net.darktree.virus.ui;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Vec2f;

public class Screen implements DrawContext {

    public float camX;
    public float camY;
    public float camS;
    public int maxRight;

    public Screen() {
        camS = (float) Main.applet.height / Const.WORLD_SIZE;
        maxRight = Main.showEditor ? Main.applet.height : Main.applet.width;
        camX = camY = 0;
    }

    public void zoom( float s, float x, float y ) {
        float wx = x / camS + camX;
        float wy = y / camS + camY;
        camX = (camX - wx) / s + wx;
        camY = (camY - wy) / s + wy;
        camS *= s;
    }

    public float trueXtoAppX(float x){
        return (x - camX) * camS;
    }

    public float trueYtoAppY(float y){
        return (y - camY) * camS;
    }

    public float appXtoTrueX(float x){
        return x / camS + camX;
    }

    public float appYtoTrueY(float y){
        return y / camS + camY;
    }

    public void scaledLine(Vec2f a, Vec2f b){
        float x1 = trueXtoAppX(a.x);
        float y1 = trueYtoAppY(a.y);
        float x2 = trueXtoAppX(b.x);
        float y2 = trueYtoAppY(b.y);
        strokeWeight(0.03f * camS);
        line(x1, y1, x2, y2);
    }

    public void draw() {
        Main.applet.world.draw(this);
        Main.applet.editor.drawSelection(this);

        if( Main.showEditor ) {
            Main.applet.editor.draw(this);
        }

        if( Main.showDebug ) {
            drawDebugOverlay();
        }

        // draw credits
        push();
        translate(4, Main.applet.height - 6);
        fill(Const.COLOR_COPYRIGHT_TEXT);
        noStroke();
        textSize(18);
        textAlign(LEFT);
        text(Const.COPYRIGHT, 0, 0);
        pop();
    }

    private void drawDebugOverlay() {
        fill(0);
        textSize(20);
        textAlign(LEFT, TOP);

        long free  = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used  = total - free;

        String debug = "FPS: " + (int) Math.floor(Main.applet.frameRate) + ", TPS: " + Main.applet.tickThread.getTPS() + "\n" +
                Main.applet.graph.getDebugString() + "\n" +
                Main.applet.editor.getDebugString() + "\n" +
                getDebugString() + "\n" +
                "Memory: " + used + "/" + total + " MB, free: " + (int) (((double) free / total) * 100) + "%";

        text(debug, 20, 20 );
    }

    private String getDebugString() {
        return "Screen: CamS: " + String.format("%.2f", camS)
                + ", CamX: " + String.format("%.2f", camX)
                + ", CamY: " + String.format("%.2f", camY)
                + ", Mxr: " + maxRight;
    }

}
