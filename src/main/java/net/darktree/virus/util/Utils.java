package net.darktree.virus.util;

import net.darktree.virus.Main;

import java.util.Random;

public class Utils {

    private static final Random random = new Random();

    public static int color(int r, int g, int b) {
        return Main.applet.color(r, g, b);
    }

    public static float ceilOrFloor(float value, float mode) {
        return (mode < 0) ? Main.floor(value) : Main.ceil(value);
    }

    public static float random(float max) {
        float value;

        if( max == 0 ) {
            return 0;
        }

        do{
            value = random.nextFloat() * max;
        }while(value == max);

        return value;
    }

    public static float random(float min, float max) {
        if(min >= max) return min;
        float value;

        do {
            value = random(max) + min;
        } while (value == max);

        return value;
    }

    public static int random(int max) {
        return (max < 1) ? 0 : random.nextInt(max);
    }

    public static int random(int min, int max) {
        int diff = max - min;

        if(diff < 1) {
            return 0;
        }

        return random.nextInt(diff) + min;
    }

}
