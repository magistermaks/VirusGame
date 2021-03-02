package net.darktree.virus.ui;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.ui.editor.Arrow;
import net.darktree.virus.ui.editor.Editor;
import net.darktree.virus.ui.sound.Sounds;
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
        if( key == 'x' ) {
            Main.showEditor = !Main.showEditor;
            screen.maxRight = Main.showEditor ? windowSizeY : windowSizeX;
        }

        // disable/enable tampered cell highlighting
        if( key == 'z' ) {
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
        if( key == 's' ) {
            moveIn = true;
        }

        // zoom out
        if( key == 'w' ) {
            moveOut = true;
        }

        // start/stop recording graph
        if( key == 'r' ) {
            Main.applet.graph.toggleRecorder();
        }

        if( key == 'p' ) {
            Main.applet.tickThread.togglePause();
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
        if( key == 's' ) {
            moveIn = false;
        }

        // zoom out
        if( key == 'w' ) {
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
        Editor editor = Main.applet.editor;

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
            editor.arrow = null;
            if(!wasMouseDown) {

                if(Main.applet.mouseX < screen.maxRight){
                    editor.selectedCodon = -1;
                    clickWorldX = screen.appXtoTrueX(Main.applet.mouseX);
                    clickWorldY = screen.appYtoTrueY(Main.applet.mouseY);
                    isPressed = true;
                }else{
                    editor.checkInput();
                    isPressed = false;
                }

                doubleClick = true;
            }else if(isPressed){

                float newCX = screen.appXtoTrueX(Main.applet.mouseX);
                float newCY = screen.appYtoTrueY(Main.applet.mouseY);

                if(newCX != clickWorldX || newCY != clickWorldY){
                    doubleClick = false;
                }

                if(editor.selected == editor.virus){
                    editor.arrow = new Arrow( clickWorldX, clickWorldY, newCX, newCY );
                }else{
                    screen.camX -= (newCX - clickWorldX);
                    screen.camY -= (newCY - clickWorldY);
                }
            }

        }else{
            if(wasMouseDown) {
                if(editor.selected == editor.virus && editor.arrow != null){
                    if( editor.arrow.shouldProduce() ){
                        editor.produce();
                    }
                }

                if(doubleClick && isPressed){
                    if(editor.selected != editor.virus){
                        editor.close();
                    }
                    if( Main.applet.world.isCellValid( (int) clickWorldX, (int) clickWorldY ) ) {
                        editor.select( clickWorldX, clickWorldY );
                        Sounds.CLICK.play();
                    }
                }
            }

            clickWorldX = -1;
            clickWorldY = -1;
            editor.arrow = null;
        }

        wasMouseDown = Main.applet.mousePressed;
    }

}
