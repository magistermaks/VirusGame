package net.darktree.virus;

import net.darktree.virus.gui.editor.Editor;
import net.darktree.virus.gui.graph.Graph;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

public class Main extends PApplet {

    public static final Main applet = new Main();
    public static final String version = "1.1.2";

    public Settings settings;
    public World world;
    public Renderer renderer;
    public Editor editor;
    public Graph graph;
    public PFont font;

    public final int COLOR_WASTE = color(100, 65, 0);
    public final int COLOR_FOOD = color(255, 0, 0);
    public final int COLOR_UGO = color(0, 0, 0);
    public final int COLOR_HAND = color(0, 128, 0);
    public final int COLOR_TELOMERE = color(0, 0, 0);
    public final int COLOR_ENERGY = color(255, 255, 0);
    public final int COLOR_WALL = color(210, 50, 210);
    public final int COLOR_COPYRIGHT_TEXT = color(0, 0, 0, 200);
    public final int COLOR_DIVINE_CONTROL = color(204, 102, 0);
    public final int COLOR_DIVINE_DISABLED = color(128, 102, 77);
    public final int COLOR_GRAPH_WASTES = color(153, 99, 0);
    public final int COLOR_GRAPH_UGOS = color(30, 200, 30);
    public final int COLOR_GRAPH_CELLS = color(210, 50, 210);
    public final int COLOR_CODON_OPTION = color(100, 100, 100);
    public final int COLOR_CELL_WALL = color(170, 100, 170);
    public final int COLOR_CELL_BACK = color(225, 190, 225);
    public final int COLOR_CELL_TAMPERED = color(205, 225, 70);
    public final int COLOR_CELL_LOCKED = color(60, 60, 60);

    public static final float GENOM_LIST_ENTRY_HEIGHT = 40.9f;

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

    public float[][] CODON_SHAPE;
    public float[][] TELOMERE_SHAPE;

    public static void main(String[] args){
        String[] processingArgs = { "Main" };
        PApplet.runSketch(processingArgs, applet);
    }

    public void settings() {
        // IDK why but in this configuration, using OpenGL (P2D, P3D) crashes Java.
        // If you know why and how to fix this, let me know.
        size(1728, 972, JAVA2D);
        noSmooth();
    }

    public void setup() {
        hint( DISABLE_OPENGL_ERRORS );

        surface.setTitle("The Game Of Life, Death And Viruses - " + version);
        surface.setResizable(true);

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        font = loadFont("Jygquip1-96.vlw");
        settings = new Settings();
        world = new World( settings );
        renderer = new Renderer( settings );
        editor = new Editor( settings );
        graph = new Graph( settings.graph_length, width - height - 20, height - 300 );
        graph.setRescan( settings.graph_downscale );
        textFont(font);

        println("Ready!");
    }

    public void draw() {

        inputCheck();
        world.updateParticleCount();
        world.tick();

        background(255);
        world.draw();
        renderer.drawUI();
    }















    boolean isPressed = false;
    boolean doubleClick = false; // not really double click - find better name
    boolean wasMouseDown = false;
    float clickWorldX = -1;
    float clickWorldY = -1;
    int windowSizeX = 0; // used for resize detection
    int windowSizeY = 0;

    public void keyPressed() {

        // disable/enable GUI
        if( key == 'x' || key == 'X' ) {
            settings.show_ui = !settings.show_ui;
            renderer.maxRight = settings.show_ui ? height : width;
        }

        // disable/enable tampered cell highlighting
        if( key == 'z' || key == 'Z' ) {
            settings.show_tampered = !settings.show_tampered;
        }

        // disable/enable debug screen
        if( key == '\t' ) {
            settings.show_debug = !settings.show_debug;
        }

        // focus on the map
        if( key == ' ' ) {
            renderer.camX = 0;
            renderer.camY = 0;
            renderer.camS = ((float) height) / settings.world_size;
        }

        // make ESC key close the editor, and not the entire game
        if( key == ESC ) {
            editor.close();
            key = 0;
        }

    }

    public void mouseWheel(MouseEvent event) {
        if( !editor.isOpened() || !editor.handleScroll( event ) ) {
            float thisZoomF = event.getCount() == 1 ? 1/1.05f : 1.05f;
            float worldX = mouseX / renderer.camS + renderer.camX;
            float worldY = mouseY / renderer.camS + renderer.camY;
            renderer.camX = (renderer.camX - worldX) / thisZoomF+worldX;
            renderer.camY = (renderer.camY - worldY) / thisZoomF+worldY;
            renderer.camS *= thisZoomF;
        }
    }

    public void windowResized() {
        graph.resize( width - height - 20, height - 300 );
    }

    public void inputCheck(){
        if( width != windowSizeX || height != windowSizeY ) {
            windowSizeX = width;
            windowSizeY = height;
            windowResized();
        }

        if (mousePressed) {
            editor.arrow = null;
            if(!wasMouseDown) {
                if(mouseX < renderer.maxRight){
                    editor.selectedCodon = -1;
                    clickWorldX = renderer.appXtoTrueX(mouseX);
                    clickWorldY = renderer.appYtoTrueY(mouseY);
                    isPressed = true;
                }else{
                    editor.checkInput();
                    isPressed = false;
                }
                doubleClick = true;
            }else if(isPressed){

                float newCX = renderer.appXtoTrueX(mouseX);
                float newCY = renderer.appYtoTrueY(mouseY);

                if(newCX != clickWorldX || newCY != clickWorldY){
                    doubleClick = false;
                }
                if(editor.selected == editor.ugo){
                    stroke(0, 0, 0);
                    editor.arrow = new float[]{clickWorldX,clickWorldY,newCX,newCY};
                }else{
                    renderer.camX -= (newCX-clickWorldX);
                    renderer.camY -= (newCY-clickWorldY);
                }
            }

        }else{
            if(wasMouseDown) {
                if(editor.selected == editor.ugo && editor.arrow != null){
                    if(Helpers.euclidLength(editor.arrow) > settings.min_length_to_produce){
                        editor.produce();
                    }
                }
                if(doubleClick && isPressed){
                    if(editor.selected != editor.ugo){
                        editor.close();
                    }
                    if( world.isCellValid( clickWorldX, clickWorldY ) ) {
                        editor.select( (int) clickWorldX, (int) clickWorldY );
                    }
                }
            }
            clickWorldX = -1;
            clickWorldY = -1;
            editor.arrow = null;
        }
        wasMouseDown = mousePressed;
    }

public class Renderer {

        public float camX = 0;
        public float camY = 0;
        public float camS = 0;
        public int maxRight;

        // textures
        public final PImage spriteGear;

        public Renderer( Settings settings ) {
            camS = ((float) height) / settings.world_size;
            maxRight = settings.show_ui ? height : width;

            spriteGear = loadImage("gear.png");
        }

        public float trueXtoAppX(float x){
            return (x-camX)*camS;
        }

        public float trueYtoAppY(float y){
            return (y-camY)*camS;
        }

        public float appXtoTrueX(float x){
            return x/camS+camX;
        }

        public float appYtoTrueY(float y){
            return y/camS+camY;
        }

        public void scaledLine(Vec2f a, Vec2f b){
            float x1 = trueXtoAppX(a.x);
            float y1 = trueYtoAppY(a.y);
            float x2 = trueXtoAppX(b.x);
            float y2 = trueYtoAppY(b.y);
            strokeWeight(0.03f * camS);
            line(x1, y1, x2, y2);
        }

        public void drawUI(){

            editor.drawSelection();

            if( settings.show_ui ) {

                pushMatrix();
                translate(height, 0);
                fill(0);
                noStroke();
                rect(0,0,width-height,height);
                fill(255);
                textFont(font,40);
                textAlign(LEFT);
                text( "FPS: " + (int) Math.floor(frameRate), 25, 60);
                text( "Start: " + Helpers.framesToTime(frameCount), 25, 100);
                text( "Edit: " + Helpers.framesToTime(frameCount-world.lastEditFrame), 25, 140);
                textFont(font, 28);
                text("Initial: " + world.initialCount, 340, 50);
                text("Alive: " + world.aliveCount, 340, 75);
                text("Dead: " + world.deadCount, 340, 100);
                text("Shells: " + world.shellCount, 340, 125);
                text("Infected: " + world.infectedCount, 340, 150);
                if( editor.isOpened() ){
                    editor.draw();
                }else{
                    drawWorldStats();
                }
                popMatrix();
                drawUGObutton( !editor.isOpened() );
            }

            if( settings.show_debug ) {

                int c = 20;

                fill(0);
                textFont(font, 20);
                textAlign(LEFT);

                long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                long used  = total - free;

                text( "FPS: " + (int) Math.floor(frameRate) + ", frame: " + frameCount, 20, c += 20 );
                text( "Graph high: " + graph.getHighest(false) + ", offset: " + graph.offset + " p: " + settings.graph_update_period, 20, c += 20 );
                text( "Selected: " + editor.isOpened() + ", at: " + editor.selx + ", " + editor.sely, 20, c += 20 );
                text( "CamS: " + String.format("%.2f", camS) + ", CamX: " + String.format("%.2f", camX ) + ", CamY: " + String.format("%.2f", camY ), 20, c += 20 );
                text( "Mutability: " + settings.mutability, 20, c += 20 );
                text( "Memory: " + used + "/" + total + " MB, free: " + (int) (((double) free / total) * 100) + "%", 20, c += 20 );

            }

            drawCredits();

        }

        public void drawCredits() {
            pushMatrix();
            translate(4, height - 6);
            fill( COLOR_COPYRIGHT_TEXT );
            noStroke();
            textFont(font, 18);
            textAlign(LEFT);
            text("Copyright (C) 2020 Cary Huang & magistermaks", 0, 0);
            popMatrix();
        }

        public void drawGenomeArrows(double dw, double dh){
            float w = (float)dw;
            float h = (float)dh;

            fill(255);
            beginShape();
            vertex(-5, 0);
            vertex(-45, -40);
            vertex(-45, 40);
            endShape(CLOSE);
            beginShape();
            vertex(w + 5, 0);
            vertex(w + 45, -40);
            vertex(w + 45, 40);
            endShape(CLOSE);
            noStroke();
            rect(0, -h / 2, w, h);
        }

        public void drawWorldStats() {
            fill(255);
            textAlign(LEFT);
            textFont(font, 30);
            text("Foods: " + world.pc.foods.size(), 25, 200);
            text("Wastes: " + world.pc.wastes.size(), 25, 230);
            text("UGOs: " + world.pc.ugos.size(), 25, 260);

            text("total: " + world.totalFoodCount, 200, 200);
            text("total: " + world.totalWasteCount, 200, 230);
            text("total: " + world.totalUGOCount, 200, 260);

            graph.draw( 10, height - 10 );
        }

        public void drawArrow(float dx1, float dx2, float dy1, float dy2){
            float x1 = trueXtoAppX(dx1);
            float y1 = trueYtoAppY(dx2);
            float x2 = trueXtoAppX(dy1);
            float y2 = trueYtoAppY(dy2);

            float angle = atan2(y2 - y1, x2 - x1);
            float head_size = 0.3f * camS;

            strokeWeight(0.03f*camS);
            line(x1, y1, x2, y2);
            float x3 = x2 + head_size * cos(angle + PI * 0.8f);
            float y3 = y2 + head_size * sin(angle + PI * 0.8f);
            line(x2, y2, x3, y3);
            float x4 = x2 + head_size * cos(angle - PI * 0.8f);
            float y4 = y2 + head_size * sin(angle - PI * 0.8f);
            line(x2, y2, x4, y4);
        }

        public void drawUGObutton(boolean drawUGO){
            fill(80);
            noStroke();
            rect(width - 130, 10, 120, 140);
            fill(255);
            textAlign(CENTER);

            if(drawUGO){
                textFont(font, 48);
                text("MAKE", width - 70, 70);
                text("UGO", width - 70, 120);
            }else{
                textFont(font, 36);
                text("CANCEL", width - 70, 95);
            }
        }

    }
    public class Settings {

        private JSONObject settings;
        private JSONObject world;

        // Runtime settings:
        public boolean show_ui = true;
        public boolean show_tampered = false;
        public boolean show_debug = false;

        // Settings:
        public String genome;
        public String editor_default;
        public float gene_tick_time;
        public int max_food;
        public int max_waste;
        public double codon_degrade_speed;
        public double wall_damage;
        public float gene_tick_energy;
        public int world_size;
        public int[][] map_data;
        public double waste_disposal_chance_high;
        public double waste_disposal_chance_low;
        public double waste_disposal_chance_random;
        public double cell_wall_protection;
        public int particles_per_rand_update;
        public int max_codon_count;
        public int laser_linger_time;
        public float age_grow_speed;
        public double min_length_to_produce;
        public double mutability;
        public int graph_length;
        public boolean graph_downscale;
        public int graph_update_period;
        public int codons_per_page = 100;

        public Settings() {

            settings = loadJSONObject("settings.json");
            world = loadJSONObject("world.json");

            genome = settings.getString("genome");
            editor_default = settings.getString("editor_default");
            gene_tick_time = settings.getFloat("gene_tick_time");
            max_food = settings.getInt("max_food");
            max_waste = settings.getInt("max_waste");
            codon_degrade_speed = settings.getDouble("codon_degrade_speed");
            graph_length = settings.getInt("graph_length");
            graph_update_period = settings.getInt("graph_update_period");
            graph_downscale = settings.getBoolean("graph_downscale");
            wall_damage = settings.getDouble("wall_damage");
            gene_tick_energy = settings.getFloat("gene_tick_energy");
            mutability = settings.getDouble("mutability");
            waste_disposal_chance_high = settings.getDouble("waste_disposal_chance_high");
            waste_disposal_chance_low = settings.getDouble("waste_disposal_chance_low");
            waste_disposal_chance_random = settings.getDouble("waste_disposal_chance_random");
            cell_wall_protection = settings.getDouble("cell_wall_protection");
            particles_per_rand_update = settings.getInt("particles_per_rand_update");
            max_codon_count = settings.getInt("max_codon_count");
            laser_linger_time = settings.getInt("laser_linger_time");
            age_grow_speed = settings.getFloat("age_grow_speed");
            min_length_to_produce = settings.getDouble("min_length_to_produce");
            world_size = world.getInt("world_size");
            loadWorld( world.getJSONArray("map"), world_size );
            setDetailes( settings.getInt("detailes") );

        }

        private void loadWorld( JSONArray json, int size ) {
            map_data = new int[ size ][ size ];

            for( int y = 0; y < size; y ++ ) {
                JSONArray row = json.getJSONArray(y);
                for( int x = 0; x < size; x ++ ) {
                    map_data[x][y] = row.getInt(x);
                }
            }
        }

        private void setDetailes( int detailes ) {
            switch( detailes ) {

                case 0: // fast
                    CODON_SHAPE = new float[][] {{-2,0}, {-2,2}, {2,2}, {2,0}};
                    TELOMERE_SHAPE = new float[][] {{-2,2}, {2,2}, {2,-2}, {-2,-2}};
                    break;

                case 1: // fancy
                    CODON_SHAPE = new float[][] {{-2,0}, {-2,2}, {0,3}, {2,2}, {2,0}, {0,0}};
                    TELOMERE_SHAPE = new float[][] {{-2,2}, {0,3}, {2,2}, {2,-2}, {0,-3}, {-2,-2}};
                    break;

                case 2: // ultra
                    CODON_SHAPE = new float[][] {{-2, 0}, {-2, 2}, {-1, 3}, {0, 3}, {1, 3}, {2, 2}, {2, 0}, {0,0}};
                    TELOMERE_SHAPE = new float[][] {{-2, 2}, {-1, 3}, {0, 3}, {1, 3}, {2, 2}, {2, -2}, {1, -3}, {0, -3}, {-1, -3}, {-2, -2}};
                    break;

            }
        }

    }

}