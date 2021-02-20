package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.cell.ContainerCell;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;
import processing.core.PApplet;

public abstract class Particle implements DrawContext {

    public boolean removed = false;
    public final int birth;
    public Vec2f pos;
    public Vec2f velocity;

    public Particle(Vec2f pos, Vec2f velocity, int b){
        this.pos = pos;
        this.velocity = velocity;
        this.birth = b;
    }

    public abstract ParticleType getType();
    public abstract int getColor();

    public boolean bouncesOff() {
        return true;
    }

    public void draw(Screen screen) {
        fill( getColor() );
        ellipseMode(PApplet.CENTER);
        ellipse(0, 0, 0.1f * Const.BIG_FACTOR, 0.1f * Const.BIG_FACTOR);
    }

    public void tick(World world) {
        Vec2f future = new Vec2f();
        CellType ct = world.getCellTypeAt(pos.x, pos.y);

        if( ct == CellType.Locked ) removeParticle( world.getCellAt(pos.x, pos.y) );

        float viscosity = ct == CellType.Empty ? 1 : 0.5f;

        future.x = pos.x + velocity.x * viscosity * Const.PLAY_SPEED;
        future.y = pos.y + velocity.y * viscosity * Const.PLAY_SPEED;

        boolean cta = Math.floor(pos.x) != Math.floor(future.x);
        boolean ctb = Math.floor(pos.y) != Math.floor(future.y);

        if( cta || ctb ) {

            CellType ft = world.getCellTypeAt(future.x, future.y);

            if( interact( world, future, ct, ft ) ) return;

            if(ft == CellType.Locked || (bouncesOff() && (ct != CellType.Empty || ft != CellType.Empty))) {

                Cell cell1 = world.getCellAt(future.x, future.y);
                if( cell1 instanceof NormalCell){
                    ((NormalCell) cell1).hurtWall( cta && ctb ? 2 : 1 );
                }

                if( cta ) {
                    if(velocity.x >= 0){
                        future.x = (float) Math.ceil(pos.x) - PApplet.EPSILON;
                    }else{
                        future.x = (float) Math.floor(pos.x) + PApplet.EPSILON;
                    }

                    velocity.x = -velocity.x;
                }

                if( ctb ) {
                    if(velocity.y >= 0){
                        future.y = (float) Math.ceil(pos.y) - PApplet.EPSILON;
                    }else{
                        future.y = (float) Math.floor(pos.y) + PApplet.EPSILON;
                    }

                    velocity.y = -velocity.y;
                }

                Cell cell2 = world.getCellAt(pos.x, pos.y);
                if( cell2 instanceof NormalCell){
                    ((NormalCell) cell2).hurtWall( cta && ctb ? 2 : 1 );
                }

            }else{

                if(future.x >= Const.WORLD_SIZE) { future.x -= Const.WORLD_SIZE; border(); } else
                if(future.x < 0) { future.x += Const.WORLD_SIZE; border(); } else
                if(future.y >= Const.WORLD_SIZE) { future.y -= Const.WORLD_SIZE; border(); } else
                if(future.y < 0) { future.y += Const.WORLD_SIZE; border(); }

                hurtWall( pos, false );
                hurtWall( future, true );
            }

        }

        pos = future;

    }

    protected void randomTick() {

    }

    protected void border() {

    }

    protected void hurtWall(Vec2f pos, boolean add) {
        Cell cell = Main.applet.world.getCellAt(pos.x, pos.y);
        if( cell instanceof ContainerCell ) {
            if(cell instanceof NormalCell){
                ((NormalCell) cell).hurtWall(1);
            }

            ContainerCell container = (ContainerCell) cell;

            if( add ) {
                container.addParticle(this);
            }else{
                container.removeParticle(this);
            }
        }
    }

    public void removeParticle( Cell cell ) {
        removed = true;
        if( cell instanceof ContainerCell ) ((ContainerCell) cell).removeParticle(this);
    }

    public void addToCellList(){
        Cell cell = Main.applet.world.getCellAt(pos.x, pos.y);
        if( cell instanceof ContainerCell ) ((ContainerCell) cell).addParticle(this);
    }

    protected boolean interact(World world, Vec2f future, CellType cType, CellType fType ) {
        return false;
    }

    public void alignWithWorld(){
        while(pos.x >= Const.WORLD_SIZE) {
            pos.x -= Const.WORLD_SIZE;
        }

        while(pos.x < 0) {
            pos.x += Const.WORLD_SIZE;
        }

        while(pos.y >= Const.WORLD_SIZE) {
            pos.y -= Const.WORLD_SIZE;
        }

        while(pos.y < 0) {
            pos.y += Const.WORLD_SIZE;
        }
    }

}
