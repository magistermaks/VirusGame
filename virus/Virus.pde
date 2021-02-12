
Settings settings;
World world;
Renderer renderer;
Editor editor;
Graph graph;
PFont font;

final String VERSION = "1.1.0";

final color COLOR_WASTE = color(100, 65, 0);
final color COLOR_FOOD = color(255, 0, 0);
final color COLOR_HAND = color(0, 128, 0);
final color COLOR_TELOMERE = color(0, 0, 0);
final color COLOR_ENERGY = color(255, 255, 0);
final color COLOR_WALL = color(210, 50, 210);
final color COLOR_COPYRIGHT_TEXT = color(0, 0, 0, 200);
final color COLOR_DIVINE_CONTROL = color(204, 102, 0);
final color COLOR_DIVINE_DISABLED = color(128, 102, 77);
final color COLOR_GRAPH_WASTES = color(153, 99, 0);
final color COLOR_GRAPH_UGOS = color(30, 200, 30);
final color COLOR_GRAPH_CELLS = color(210, 50, 210);
final color COLOR_CODON_OPTION = color(100, 100, 100);
final color COLOR_CELL_WALL = color(170, 100, 170);
final color COLOR_CELL_BACK = color(225, 190, 225);
final color COLOR_CELL_TAMPERED = color(205, 225, 70);
final color COLOR_CELL_LOCKED = color(60, 60, 60);

final float GENOM_LIST_ENTRY_HEIGHT = 40.9;

final float E_RECIPROCAL = 0.3678794411;
final float HAND_DIST = 32;
final float HAND_LEN = 7;
final float SPEED_LOW = 0.01;
final float SPEED_HIGH = 0.02;
final float BIG_FACTOR = 100;
final float PLAY_SPEED = 0.6;
final double VISUAL_TRANSITION = 0.38;
final float MARGIN = 4;
final double DETAIL_THRESHOLD = 10;
final float[] GENOME_LIST_DIMS = {70, 430, 360, 450};
final float[] EDIT_LIST_DIMS = {550, 434, 180, 450};
final float CODON_DIST = 17;
final float CODON_DIST_UGO = 10.6;
final float CODON_WIDTH = 1.4;
final String[] DIVINE_CONTROLS = {"Remove", "Revive", "Heal", "Energize", "Make Wall", "Make Shell"};

float[][] CODON_SHAPE;
float[][] TELOMERE_SHAPE;

// Ugly work-arounds for Processing's design problems
final CodonArgsClass CodonArgs = new CodonArgsClass();
final CodonsClass Codons = new CodonsClass();

void setup() {
  
    // Sometimes Processing decides to use X11 directly to draw
    // which is TERRIBLE, and nearly halts my entire visual environment
    // together with this game. Using P3D seems to force Processing into
    // using OpenGL and thus fixes the issue but causes small graphical errors.
    size(1728, 972, P2D);
    noSmooth(); 
    
    hint( DISABLE_OPENGL_ERRORS );
  
    surface.setTitle("The Game Of Life, Death And Viruses - " + VERSION);
    surface.setResizable(true);
  
    font = loadFont("Jygquip1-96.vlw");
    settings = new Settings();
    world = new World( settings );
    renderer = new Renderer( settings );
    editor = new Editor( settings );
    graph = new Graph( settings.graph_length, width - height - 20, height - 300 );
    graph.setRescan( settings.graph_downscale );
    
    println("Ready!");
    
}

void draw() {
  
    inputCheck();
    world.updateParticleCount();
    world.tick();
    
    background(255);
    world.draw();
    renderer.drawUI();
}
