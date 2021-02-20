package net.darktree.virus.util;

import net.darktree.virus.Main;

public class Utils {

    public static int color(int r, int g, int b) {
        return Main.applet.color(r, g, b);
    }

    public static float ceilOrFloor(float value, float mode) {
        return (mode < 0) ? Main.floor(value) : Main.ceil(value);
    }
}
