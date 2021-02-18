package net.darktree.virus.gui;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.Helpers;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Input {

    private static boolean isPressed = false;
    private static boolean doubleClick = false; // not really double click - find better name
    private static boolean wasMouseDown = false;
    private static float clickWorldX = -1;
    private static float clickWorldY = -1;
    private static int windowSizeX = 0;
    private static int windowSizeY = 0;

    public static void keyPressed( char key ) {

        // disable/enable GUI
        if( key == 'x' || key == 'X' ) {
            Main.showEditor = !Main.showEditor;
            Main.applet.renderer.maxRight = Main.showEditor ? windowSizeY : windowSizeX;
        }

        // disable/enable tampered cell highlighting
        if( key == 'z' || key == 'Z' ) {
            Main.showTampered = !Main.showTampered;
        }

        // disable/enable debug screen
        if( key == '\t' ) {
            Main.showDebug = !Main.showDebug;
        }

        // focus on the map
        if( key == ' ' ) {
            Main.applet.renderer.camX = 0;
            Main.applet.renderer.camY = 0;
            Main.applet.renderer.camS = ((float) Main.applet.height) / Const.WORLD_SIZE;
        }

        // make ESC key close the editor, and not the entire game
        if( key == PApplet.ESC ) {
            Main.applet.editor.close();

            // cancel close event
            Main.applet.key = 0;
        }

    }

    public static void mouseWheel(MouseEvent event) {
        if( !Main.applet.editor.isOpened() || !Main.applet.editor.handleScroll( event ) ) {
            float thisZoomF = event.getCount() == 1 ? 1/1.05f : 1.05f;
            float worldX = event.getX() / Main.applet.renderer.camS + Main.applet.renderer.camX;
            float worldY = event.getY() / Main.applet.renderer.camS + Main.applet.renderer.camY;
            Main.applet.renderer.camX = (Main.applet.renderer.camX - worldX) / thisZoomF+worldX;
            Main.applet.renderer.camY = (Main.applet.renderer.camY - worldY) / thisZoomF+worldY;
            Main.applet.renderer.camS *= thisZoomF;
        }
    }

    public static void windowResized( int width, int height ) {
        Main.applet.graph.resize( width - height - 20, height - 300 );
    }

    public static void update(){
        if( Main.applet.width != windowSizeX || Main.applet.height != windowSizeY ) {
            windowSizeX = Main.applet.width;
            windowSizeY = Main.applet.height;
            windowResized( windowSizeX, windowSizeY );
        }

        if( Main.applet.mousePressed ) {
            Main.applet.editor.arrow = null;
            if(!wasMouseDown) {
                if(Main.applet.mouseX < Main.applet.renderer.maxRight){
                    Main.applet.editor.selectedCodon = -1;
                    clickWorldX = Main.applet.renderer.appXtoTrueX(Main.applet.mouseX);
                    clickWorldY = Main.applet.renderer.appYtoTrueY(Main.applet.mouseY);
                    isPressed = true;
                }else{
                    Main.applet.editor.checkInput();
                    isPressed = false;
                }
                doubleClick = true;
            }else if(isPressed){

                float newCX = Main.applet.renderer.appXtoTrueX(Main.applet.mouseX);
                float newCY = Main.applet.renderer.appYtoTrueY(Main.applet.mouseY);

                if(newCX != clickWorldX || newCY != clickWorldY){
                    doubleClick = false;
                }
                if(Main.applet.editor.selected == Main.applet.editor.ugo){
                    Main.applet.editor.arrow = new float[]{clickWorldX,clickWorldY,newCX,newCY};
                }else{
                    Main.applet.renderer.camX -= (newCX-clickWorldX);
                    Main.applet.renderer.camY -= (newCY-clickWorldY);
                }
            }

        }else{
            if(wasMouseDown) {
                if(Main.applet.editor.selected == Main.applet.editor.ugo && Main.applet.editor.arrow != null){
                    if(Helpers.euclidLength(Main.applet.editor.arrow) > Const.MIN_LENGTH_TO_PRODUCE){
                        Main.applet.editor.produce();
                    }
                }
                if(doubleClick && isPressed){
                    if(Main.applet.editor.selected != Main.applet.editor.ugo){
                        Main.applet.editor.close();
                    }
                    if( Main.applet.world.isCellValid( clickWorldX, clickWorldY ) ) {
                        Main.applet.editor.select( (int) clickWorldX, (int) clickWorldY );
                    }
                }
            }
            clickWorldX = -1;
            clickWorldY = -1;
            Main.applet.editor.arrow = null;
        }

        wasMouseDown = Main.applet.mousePressed;
    }

}
