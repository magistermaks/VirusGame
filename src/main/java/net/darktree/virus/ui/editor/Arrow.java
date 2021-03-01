package net.darktree.virus.ui.editor;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.DrawContext;

public class Arrow implements DrawContext {

    private final float a, b, c, d;

    public Arrow( float a, float b, float c, float d ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public void draw(Screen screen) {
        float x1 = screen.trueXtoAppX(a);
        float y1 = screen.trueYtoAppY(b);
        float x2 = screen.trueXtoAppX(c);
        float y2 = screen.trueYtoAppY(d);

        // set arrow color based on length
        if( shouldProduce() ) stroke(0); else stroke(150);

        float angle = Main.atan2(y2 - y1, x2 - x1);
        float size = 0.3f * screen.camS;

        strokeWeight(0.03f * screen.camS);
        line(x1, y1, x2, y2);

        x1 = x2 + size * Main.cos(angle + PI * 0.8f);
        y1 = y2 + size * Main.sin(angle + PI * 0.8f);
        line(x2, y2, x1, y1);

        x1 = x2 + size * Main.cos(angle - PI * 0.8f);
        y1 = y2 + size * Main.sin(angle - PI * 0.8f);
        line(x2, y2, x1, y1);
    }

    public boolean shouldProduce() {
        return length() > Const.MIN_LENGTH_TO_PRODUCE * Const.MIN_LENGTH_TO_PRODUCE;
    }

    private float length() {
        float x = a - c;
        float y = b - d;
        return x * x + y * y;
    }

    public float getX() {
        return a;
    }

    public float getY() {
        return b;
    }

    public float getVX() {
        return c - a;
    }

    public float getVY() {
        return d - b;
    }

}
