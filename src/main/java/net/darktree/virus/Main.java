package net.darktree.virus;

import net.darktree.virus.gui.Input;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.gui.editor.Editor;
import net.darktree.virus.gui.graph.Graph;
import net.darktree.virus.logger.Logger;
import net.darktree.virus.world.TickThread;
import net.darktree.virus.world.World;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ConcurrentModificationException;

public class Main extends PApplet {

    public static final Main applet = new Main();

    public static boolean showEditor = true;
    public static boolean showTampered = false;
    public static boolean showDebug = false;

    public World world;
    public Editor editor;
    public Graph graph;
    public Screen screen;
    public TickThread tickThread;

    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        PApplet.runSketch(processingArgs, applet);
    }

    @Override
    public void settings() {
        // IDK why but in this configuration, using OpenGL (P2D, P3D) crashes Java.
        // If you know why and how to fix this, let me know.
        size(1728, 972, JAVA2D);
        noSmooth();
    }

    @Override
    public void setup() {
        hint(DISABLE_OPENGL_ERRORS);
        surface.setTitle("The Game Of Life, Death And Viruses - " + Const.VERSION);
        surface.setResizable(true);

        // load settings
        Const.init();

        world = new World();
        screen = new Screen();
        editor = new Editor();
        graph = new Graph(Const.GRAPH_LENGTH, width - height - 20, height - 300, Const.GRAPH_DOWNSCALE);
        tickThread = new TickThread(world).start();

        textFont(loadFont("font.vlw"));
        Logger.info("Ready!");
    }

    @Override
    public void draw() {
        Input.update();
        background(255);
        screen.draw();
    }

    @Override
    public void keyPressed() {
        Input.keyPressed(key);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        Input.mouseWheel(event);
    }

    @Override
    public void exit() {
        tickThread.stop();
        super.exit();
    }
}