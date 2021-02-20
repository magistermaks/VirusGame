package net.darktree.virus.gui;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.util.Helpers;
import processing.event.MouseEvent;

public class Input {

    private static boolean isPressed = false;
    private static boolean doubleClick = false; // not really double click - find better name
    private static boolean wasMouseDown = false;
    private static float clickWorldX = -1;
    private static float clickWorldY = -1;
    private static int windowSizeX = 0;
    private static int windowSizeY = 0;

    private static boolean moveUp = false;
    private static boolean moveDown = false;
    private static boolean moveLeft = false;
    private static boolean moveRight = false;
    private static boolean moveIn = false;
    private static boolean moveOut = false;

    public static void keyPressed( char key ) {

        Screen screen = Main.applet.screen;

        // disable/enable GUI
        if( key == 'x' || key == 'X' ) {
            Main.showEditor = !Main.showEditor;
            screen.maxRight = Main.showEditor ? windowSizeY : windowSizeX;
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
            screen.camX = 0;
            screen.camY = 0;
            screen.camS = ((float) Main.applet.height) / Const.WORLD_SIZE;
        }

        // make ESC key close the editor, and not the entire game
        if( key == Main.ESC ) {
            Main.applet.editor.close();

            // cancel close event
            Main.applet.key = 0;
        }

        // zoom in
        if( key == 's' || key == 'S' ) {
            moveIn = true;
        }

        // zoom out
        if( key == 'w' || key == 'W' ) {
            moveOut = true;
        }

        // move map using arrow keys
        if( key == Main.CODED ) {
            int code = Main.applet.keyCode;

            if( code == Main.UP ) moveUp = true;
            if( code == Main.DOWN ) moveDown = true;
            if( code == Main.LEFT ) moveLeft = true;
            if( code == Main.RIGHT ) moveRight = true;
        }

    }

    public static void keyReleased( char key ) {

        // stop moving map
        if( key == Main.CODED ) {
            int code = Main.applet.keyCode;

            if( code == Main.UP ) moveUp = false;
            if( code == Main.DOWN ) moveDown = false;
            if( code == Main.LEFT ) moveLeft = false;
            if( code == Main.RIGHT ) moveRight = false;
        }

        // zoom in
        if( key == 's' || key == 'S' ) {
            moveIn = false;
        }

        // zoom out
        if( key == 'w' || key == 'W' ) {
            moveOut = false;
        }

    }

    public static void mouseWheel(MouseEvent event) {
        if( !Main.applet.editor.isOpened() || !Main.applet.editor.handleScroll( event ) ) {
            Screen screen = Main.applet.screen;

            float s = event.getCount() == 1 ? 1/1.05f : 1.05f;
            screen.zoom(s, event.getX(), event.getY());
        }
    }

    public static void windowResized( int width, int height ) {
        Main.applet.graph.resize( width - height - 20, height - 300 );
    }

    public static void update(){

        float moveX = 0, moveY = 0;

        Screen screen = Main.applet.screen;
        float speed = (1 / screen.camS) * Const.MAP_MOVE_SPEED;
        float offset = windowSizeY * 0.5f;

        if( moveUp ) moveY -= speed;
        if( moveDown ) moveY += speed;
        if( moveLeft ) moveX -= speed;
        if( moveRight ) moveX += speed;
        if( moveIn ) screen.zoom(0.97f, offset, offset);
        if( moveOut ) screen.zoom(1.03f, offset, offset);

        screen.camX += moveX;
        screen.camY += moveY;

        if( Main.applet.width != windowSizeX || Main.applet.height != windowSizeY ) {
            windowSizeX = Main.applet.width;
            windowSizeY = Main.applet.height;
            windowResized( windowSizeX, windowSizeY );
        }

        if( Main.applet.mousePressed ) {
            Main.applet.editor.arrow = null;
            if(!wasMouseDown) {
                if(Main.applet.mouseX < Main.applet.screen.maxRight){
                    Main.applet.editor.selectedCodon = -1;
                    clickWorldX = Main.applet.screen.appXtoTrueX(Main.applet.mouseX);
                    clickWorldY = Main.applet.screen.appYtoTrueY(Main.applet.mouseY);
                    isPressed = true;
                }else{
                    Main.applet.editor.checkInput();
                    isPressed = false;
                }
                doubleClick = true;
            }else if(isPressed){

                float newCX = Main.applet.screen.appXtoTrueX(Main.applet.mouseX);
                float newCY = Main.applet.screen.appYtoTrueY(Main.applet.mouseY);

                if(newCX != clickWorldX || newCY != clickWorldY){
                    doubleClick = false;
                }
                if(Main.applet.editor.selected == Main.applet.editor.virus){
                    Main.applet.editor.arrow = new float[]{clickWorldX,clickWorldY,newCX,newCY};
                }else{
                    Main.applet.screen.camX -= (newCX-clickWorldX);
                    Main.applet.screen.camY -= (newCY-clickWorldY);
                }
            }

        }else{
            if(wasMouseDown) {
                if(Main.applet.editor.selected == Main.applet.editor.virus && Main.applet.editor.arrow != null){
                    if(Helpers.euclidLength(Main.applet.editor.arrow) > Const.MIN_LENGTH_TO_PRODUCE){
                        Main.applet.editor.produce();
                    }
                }
                if(doubleClick && isPressed){
                    if(Main.applet.editor.selected != Main.applet.editor.virus){
                        Main.applet.editor.close();
                    }
                    if( Main.applet.world.isCellValid( (int) clickWorldX, (int) clickWorldY ) ) {
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
