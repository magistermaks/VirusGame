package net.darktree.virus;

import net.darktree.virus.logger.Logger;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Const {

    // Colors
    public static final int COLOR_WASTE = color(100, 65, 0);
    public static final int COLOR_FOOD = color(255, 0, 0);
    public static final int COLOR_UGO = color(0, 0, 0);
    public static final int COLOR_HAND = color(0, 128, 0);
    public static final int COLOR_TELOMERE = color(0, 0, 0);
    public static final int COLOR_ENERGY = color(255, 255, 0);
    public static final int COLOR_WALL = color(210, 50, 210);
    public static final int COLOR_COPYRIGHT_TEXT = color(0, 0, 0, 200);
    public static final int COLOR_DIVINE_CONTROL = color(204, 102, 0);
    public static final int COLOR_DIVINE_DISABLED = color(128, 102, 77);
    public static final int COLOR_GRAPH_WASTES = color(153, 99, 0);
    public static final int COLOR_GRAPH_UGOS = color(30, 200, 30);
    public static final int COLOR_GRAPH_CELLS = color(210, 50, 210);
    public static final int COLOR_CODON_OPTION = color(100, 100, 100);
    public static final int COLOR_CELL_WALL = color(170, 100, 170);
    public static final int COLOR_CELL_BACK = color(225, 190, 225);
    public static final int COLOR_CELL_TAMPERED = color(205, 225, 70);
    public static final int COLOR_CELL_LOCKED = color(60, 60, 60);

    // Settings
    public static String DEFAULT_CELL_GENOME;
    public static String DEFAULT_VIRUS_GENOME;
    public static float GENE_TICK_TIME;
    public static int MAX_FOOD;
    public static int MAX_WASTE;
    public static double CODON_DEGRADE_SPEED;
    public static double WALL_DAMAGE;
    public static float GENE_TICK_ENERGY;
    public static double WASTE_DISPOSAL_CHANCE_HIGH;
    public static double WASTE_DISPOSAL_CHANCE_LOW;
    public static double WASTE_DISPOSAL_CHANCE_RANDOM;
    public static double CELL_WALL_PROTECTION;
    public static int PARTICLES_PER_RAND_UPDATE;
    public static int MAX_CODON_COUNT;
    public static double MUTABILITY;
    public static int GRAPH_LENGTH;
    public static boolean GRAPH_DOWNSCALE;
    public static int GRAPH_UPDATE_PERIOD;
    public static int WORLD_SIZE;
    public static int[][] WORLD_DATA;

    // Others
    public static final float GENOME_LIST_ENTRY_HEIGHT = 40.9f;
    public static final float E_RECIPROCAL = 0.3678794411f;
    public static final float HAND_DIST = 32;
    public static final float HAND_LEN = 7;
    public static final float SPEED_LOW = 0.01f;
    public static final float SPEED_HIGH = 0.02f;
    public static final float BIG_FACTOR = 100;
    public static final float PLAY_SPEED = 0.6f;
    public static final double VISUAL_TRANSITION = 0.38f;
    public static final float MARGIN = 4;
    public static final double DETAIL_THRESHOLD = 10;
    public static final float[] GENOME_LIST_DIMS = {70, 430, 360, 450};
    public static final float[] EDIT_LIST_DIMS = {550, 434, 180, 450};
    public static final float CODON_DIST = 17;
    public static final float CODON_DIST_UGO = 10.6f;
    public static final float CODON_WIDTH = 1.4f;
    public static final float INTERPRETER_LENGTH = 23;
    public static final String[] DIVINE_CONTROLS = {"Remove", "Revive", "Heal", "Energize", "Make Wall", "Make Shell"};
    public static final String COPYRIGHT = "Copyright (C) 2020 Cary Huang & magistermaks";
    public static final float MIN_LENGTH_TO_PRODUCE = 0.4f;
    public static final int LASER_LINGER_TIME = 30;
    public static final float AGE_GROW_SPEED = 0.08f;
    public static float[][] CODON_SHAPE;
    public static float[][] TELOMERE_SHAPE;

    // Game version
    public static String VERSION = getVersion();

    // Load settings from file
    public static void init() {

        JSONObject settings = readJSON("settings.json");
        JSONObject world = readJSON("world.json");

        DEFAULT_CELL_GENOME = settings.getString("genome");
        DEFAULT_VIRUS_GENOME = settings.getString("editor_default");
        GENE_TICK_TIME = settings.getFloat("gene_tick_time");
        MAX_FOOD = settings.getInt("max_food");
        MAX_WASTE = settings.getInt("max_waste");
        CODON_DEGRADE_SPEED = settings.getDouble("codon_degrade_speed");
        GRAPH_LENGTH = settings.getInt("graph_length");
        GRAPH_UPDATE_PERIOD = settings.getInt("graph_update_period");
        GRAPH_DOWNSCALE = settings.getBoolean("graph_downscale");
        WALL_DAMAGE = settings.getDouble("wall_damage");
        GENE_TICK_ENERGY = settings.getFloat("gene_tick_energy");
        MUTABILITY = settings.getDouble("mutability");
        WASTE_DISPOSAL_CHANCE_HIGH = settings.getDouble("waste_disposal_chance_high");
        WASTE_DISPOSAL_CHANCE_LOW = settings.getDouble("waste_disposal_chance_low");
        WASTE_DISPOSAL_CHANCE_RANDOM = settings.getDouble("waste_disposal_chance_random");
        CELL_WALL_PROTECTION = settings.getDouble("cell_wall_protection");
        PARTICLES_PER_RAND_UPDATE = settings.getInt("particles_per_rand_update");
        MAX_CODON_COUNT = settings.getInt("max_codon_count");
        WORLD_SIZE = world.getInt("world_size");
        loadWorld( world.getJSONArray("map"), WORLD_SIZE );
        setDetails( settings.getInt("details") );

    }

    private static void loadWorld( JSONArray json, int size ) {
        WORLD_DATA = new int[ size ][ size ];

        for( int y = 0; y < size; y ++ ) {
            JSONArray row = json.getJSONArray(y);
            for( int x = 0; x < size; x ++ ) {
                WORLD_DATA[x][y] = row.getInt(x);
            }
        }
    }

    private static void setDetails( int detailes ) {
        switch( detailes ) {

            case 0: // fast
                Const.CODON_SHAPE = new float[][] {{-2, 0}, {-2, 2}, {2, 2}, {2, 0}};
                Const.TELOMERE_SHAPE = new float[][] {{-2, 2}, {2, 2}, {2, -2}, {-2, -2}};
                break;

            case 1: // fancy
                Const.CODON_SHAPE = new float[][] {{-2, 0}, {-2, 2}, {0, 3}, {2, 2}, {2, 0}, {0, 0}};
                Const.TELOMERE_SHAPE = new float[][] {{-2, 2}, {0, 3}, {2, 2}, {2, -2}, {0, -3}, {-2, -2}};
                break;

            case 2: // ultra
                Const.CODON_SHAPE = new float[][] {{-2, 0}, {-2, 2}, {-1, 3}, {0, 3}, {1, 3}, {2, 2}, {2, 0}, {0,0}};
                Const.TELOMERE_SHAPE = new float[][] {{-2, 2}, {-1, 3}, {0, 3}, {1, 3}, {2, 2}, {2, -2}, {1, -3}, {0, -3}, {-1, -3}, {-2, -2}};
                break;

        }
    }

    private static String getVersion() {
        InputStream stream = Main.class.getClassLoader().getResourceAsStream(".version");
        if( stream == null ) {
            Logger.error( "Failed to load version file!" );
            return "undefined";
        }

        return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining(""));
    }

    private static JSONObject readJSON(String file) {
        return Main.applet.loadJSONObject(file);
    }

    private static int color( int r, int g, int b ) {
        return color( r, g, b, 255 );
    }

    private static int color( int r, int g, int b, int a ) {
        return Main.applet.color(r, g, b, a);
    }

}
