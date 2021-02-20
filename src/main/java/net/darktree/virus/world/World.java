package net.darktree.virus.world;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.gui.graph.GraphFrame;
import net.darktree.virus.particle.*;
import net.darktree.virus.util.Utils;
import net.darktree.virus.util.Vec2f;

import java.util.ArrayList;

public class World {

    private final ArrayList<Particle> queue = new ArrayList<>();
    private final Cell[][] cells;
    private final int size;

    public ParticleContainer pc = new ParticleContainer();

    public int initialCount = 0;
    public int aliveCount;
    public int deadCount = 0;
    public int shellCount = 0;
    public int infectedCount = 0;
    public int lastEditFrame = 0;
    public int totalFoodCount = 0;
    public int totalWasteCount = 0;
    public int totalVirusCount = 0;

    public World() {

        this.size = Const.WORLD_SIZE;
        cells = new Cell[ size ][ size ];
        CellType[] types = CellType.values();

        for( int y = 0; y < size; y++ ) {
            for( int x = 0; x < size; x++ ) {

                CellType type = types[Const.WORLD_DATA[x][y]];

                if( type == CellType.Empty ) {
                    cells[y][x] = null;
                    continue;
                }

                Cell cell = Cell.Factory.of( x, y, type ).build();
                cells[y][x] = cell;

                if( cell.getType() == CellType.Normal ) initialCount ++;
                if( cell.getType() == CellType.Shell ) shellCount ++;

            }
        }

        aliveCount = initialCount;

    }

    public void tick() {

        ParticleRenderer.clear();

        if( Main.applet.frameCount % Const.GRAPH_UPDATE_PERIOD == 0 ) {
            Main.applet.graph.append( new GraphFrame(
                    pc.get(ParticleType.WASTE).size(),
                    pc.get(ParticleType.VIRUS).size(),
                    aliveCount + shellCount) );
        }

        pc.tick( this, ParticleType.FOOD );
        pc.tick( this, ParticleType.WASTE );
        pc.tick( this, ParticleType.VIRUS );

        for( int y = 0; y < size; y++ ) {
            for( int x = 0; x < size; x++ ) {
                Cell c = cells[y][x];
                if( c != null ) {
                    c.tick();
                }
            }
        }

        pc.randomTick();
        pc.add( queue );

        ParticleRenderer.delegate();

    }

    public void updateParticleCount() {

        int count = 0;

        while(pc.foods.size() + count < Const.MAX_FOOD) {

            int x = -1, y = -1;

            for( int i = 0; i < 16 && (x == -1 || cells[x][y] != null); i ++ ) {
                x = Utils.random(0, Const.WORLD_SIZE);
                y = Utils.random(0, Const.WORLD_SIZE);
            }

            Vec2f pos = new Vec2f(
                    x + Utils.random(0.3f, 0.7f),
                    y + Utils.random(0.3f, 0.7f)
            );

            Particle food = new FoodParticle(pos, Main.applet.frameCount);
            addParticle( food );
            count ++;
        }
    }

    public void addParticle( Particle p ) {
        p.addToCellList();
        queue.add( p );
    }

    public void setCellAt( int x, int y, Cell c ) {
        if( cells[y][x] != null ) cells[y][x].die(true);
        cells[y][x] = c;
    }

    public boolean isCellValid( int x, int y ) {
        return !(x < 0 || x >= size || y < 0 || y >= size);
    }

    public <T> T getCellAt( int x, int y, Class<T> clazz ) {
        Cell cell = getCellAt( x, y );
        return clazz.isInstance(cell) ? clazz.cast(cell) : null;
    }

    public Cell getCellAt( int x, int y ) {
        int ix = (x + size) % size;
        int iy = (y + size) % size;

        if(ix < 0 || ix >= size || iy < 0 || iy >= size) {
            return null;
        }

        return cells[iy][ix];
    }

    public Cell getCellAt( float x, float y ) {
        return getCellAt( (int) x, (int) y );
    }

    public CellType getCellTypeAt( float x, float y ) {
        Cell c = getCellAt( (int) x, (int) y );
        return c != null ? c.getType() : CellType.Empty;
    }

    public void remove(Cell cell) {
        cells[cell.y][cell.x] = null;
    }

    public void draw(Screen screen) {
        // draw all cells
        for( int y = 0; y < size; y++ ) {
            for( int x = 0; x < size; x++ ) {
                Cell cell = cells[y][x];
                if( cell != null ) cell.draw(screen);
            }
        }

        // draw particles
        ParticleRenderer.draw(screen);
    }
}
