
Settings settings;
World world;
Renderer renderer;
Editor editor;
Graph graph;
PFont font;

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
final float[][] CODON_SHAPE = {{-2,0}, {-2,2}, {0,3}, {2,2}, {2,0}, {0,0}};
final float[][] TELOMERE_SHAPE = {{-2,2}, {0,3}, {2,2}, {2,-2}, {0,-3}, {-2,-2}};
final String[] DIVINE_CONTROLS = {"Remove", "Revive", "Heal", "Energize", "Make Wall", "Make Shell"};

// simpliefed geometry for better performance
// original: CODON_SHAPE = {{-2,0}, {-2,2}, {-1,3}, {0,3}, {1,3}, {2,2}, {2,0}, {0,0}};
// original: TELOMERE_SHAPE = {{-2,2}, {-1,3}, {0,3}, {1,3}, {2,2}, {2,-2}, {1,-3}, {0,-3}, {-1,-3}, {-2,-2}};

// Ugly work-arounds for Processing's design problems
final CodonArgsClass CodonArgs = new CodonArgsClass();
final CodonsClass Codons = new CodonsClass();

void setup() {
  
    // Use P3D to force Processign to use OpenGL,
    // as it's sometimes defaults to X11.
    size(1728, 972, P3D);
    noSmooth(); 
  
    surface.setTitle("The Game Of Life, Death And Viruses");
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
    
    renderer.drawBackground();
    renderer.drawCells();
    renderer.drawParticles();
    renderer.drawUI();
    renderer.drawCredits(); 
    
}
