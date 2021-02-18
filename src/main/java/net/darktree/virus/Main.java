package net.darktree.virus;

import net.darktree.virus.gui.Input;
import net.darktree.virus.gui.editor.Editor;
import net.darktree.virus.gui.graph.Graph;
import net.darktree.virus.logger.Logger;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

public class Main extends PApplet {

    public static final Main applet = new Main();

    public static boolean showEditor = true;
    public static boolean showTampered = false;
    public static boolean showDebug = false;

    public World world;
    public Renderer renderer;
    public Editor editor;
    public Graph graph;

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
        surface.setTitle("The Game Of Life, Death And Viruses - " + Const.VERSION);
        surface.setResizable(true);

        // load settings
        Const.init();

        world = new World();
        renderer = new Renderer();
        editor = new Editor();
        graph = new Graph( Const.GRAPH_LENGTH, width - height - 20, height - 300, Const.GRAPH_DOWNSCALE );

        textFont( loadFont("font.vlw") );

        Logger.info("Ready!");
    }

    public void draw() {

        Input.update();
        world.updateParticleCount();
        world.tick();

        background(255);
        world.draw();
        renderer.drawUI();
    }

    public void keyPressed() {
        Input.keyPressed(key);
    }

    public void mouseWheel(MouseEvent event) {
        Input.mouseWheel(event);
    }













public class Renderer {

        public float camX = 0;
        public float camY = 0;
        public float camS = 0;
        public int maxRight;

        // textures
        public final PImage spriteGear;

        public Renderer() {
            camS = ((float) height) / Const.WORLD_SIZE;
            maxRight = Main.showEditor ? height : width;

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

            if( Main.showEditor ) {

                pushMatrix();
                translate(height, 0);
                fill(0);
                noStroke();
                rect(0,0,width-height,height);
                fill(255);
                textSize(40);
                textAlign(LEFT);
                text( "FPS: " + (int) Math.floor(frameRate), 25, 60);
                text( "Start: " + Helpers.framesToTime(frameCount), 25, 100);
                text( "Edit: " + Helpers.framesToTime(frameCount-world.lastEditFrame), 25, 140);
                textSize(28);
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

            if( Main.showDebug ) {

                int c = 20;

                fill(0);
                textSize(20);
                textAlign(LEFT);

                long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                long used  = total - free;

                text( "FPS: " + (int) Math.floor(frameRate) + ", frame: " + frameCount, 20, c += 20 );
                text( "Graph high: " + graph.getHighest(false) + ", offset: " + graph.offset + ", p: " + Const.GRAPH_UPDATE_PERIOD, 20, c += 20 );
                text( "Selected: " + editor.isOpened() + ", at: " + editor.selx + ", " + editor.sely, 20, c += 20 );
                text( "CamS: " + String.format("%.2f", camS) + ", CamX: " + String.format("%.2f", camX ) + ", CamY: " + String.format("%.2f", camY ), 20, c += 20 );
                text( "Mutability: " + Const.MUTABILITY, 20, c += 20 );
                text( "Memory: " + used + "/" + total + " MB, free: " + (int) (((double) free / total) * 100) + "%", 20, c += 20 );

            }

            drawCredits();

        }

        public void drawCredits() {
            pushMatrix();
            translate(4, height - 6);
            fill( Const.COLOR_COPYRIGHT_TEXT );
            noStroke();
            textSize(18);
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
            textSize(30);
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
                textSize(48);
                text("MAKE", width - 70, 70);
                text("UGO", width - 70, 120);
            }else{
                textSize(36);
                text("CANCEL", width - 70, 95);
            }
        }

    }

}