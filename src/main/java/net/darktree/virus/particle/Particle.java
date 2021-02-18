package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.*;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import processing.core.PApplet;

public class Particle implements DrawContext {

    public Vec2f pos;
    public Vec2f velocity;
    public boolean removed = false;
    public int birthFrame;
    public ParticleType type;

    public Particle(Vec2f pos, ParticleType type, int b){
        this(pos, Helpers.getRandomVelocity(), type, b);
    }

    public Particle(Vec2f pos, Vec2f velocity, ParticleType type, int b){
        this.pos = pos;
        this.velocity = velocity;
        this.type = type;
        this.birthFrame = b;

        if( type == ParticleType.FOOD ) Main.applet.world.totalFoodCount ++; else
        if( type == ParticleType.WASTE ) Main.applet.world.totalWasteCount ++;
    }

    public int getColor() {
        return type == ParticleType.FOOD ? Const.COLOR_FOOD : Const.COLOR_WASTE;
    }

    public void draw(Screen screen) {
        float posx = screen.trueXtoAppX(pos.x);
        float posy = screen.trueYtoAppY(pos.y);

        if( posx > 0 && posy > 0 && posx < screen.maxRight && posy < Main.applet.height ) {

            translate( posx, posy );
            float ageScale = Math.min(1.0f, (Main.applet.frameCount - birthFrame) * Const.AGE_GROW_SPEED);
            scale( screen.camS / Const.BIG_FACTOR * ageScale );
            noStroke();
            fill( getColor() );
            ellipseMode(PApplet.CENTER);
            ellipse(0, 0, 0.1f * Const.BIG_FACTOR, 0.1f * Const.BIG_FACTOR);

        }
    }

    public void tick() {
        Vec2f future = new Vec2f();
        CellType ct = Main.applet.world.getCellTypeAt(pos.x, pos.y);

        if( ct == CellType.Locked ) removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );

        float visc = ct == CellType.Empty ? 1 : 0.5f;

        future.x = pos.x + velocity.x * visc * Const.PLAY_SPEED;
        future.y = pos.y + velocity.y * visc * Const.PLAY_SPEED;

        boolean cta = Math.floor(pos.x) != Math.floor(future.x);
        boolean ctb = Math.floor(pos.y) != Math.floor(future.y);

        if( cta || ctb ) {

            CellType ft = Main.applet.world.getCellTypeAt(future.x, future.y);

            if( interact( future, ct, ft ) ) return;

            if(ft == CellType.Locked || (type != ParticleType.FOOD && (ct != CellType.Empty || ft != CellType.Empty))) {

                Cell cell1 = Main.applet.world.getCellAt(future.x, future.y);
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

                Cell cell2 = Main.applet.world.getCellAt(pos.x, pos.y);
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

    public void randomTick() {
        if( type == ParticleType.WASTE ) {
            if( Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_RANDOM && Main.applet.world.getCellAt(pos.x, pos.y) == null ) removeParticle(null);
        }
    }

    private void border() {
        if( type == ParticleType.WASTE ) {
            if( Main.applet.world.pc.wastes.size() > Const.MAX_WASTE && Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_HIGH ) removeParticle(null);
            if( Main.applet.random(0, 1) < Const.WASTE_DISPOSAL_CHANCE_LOW ) removeParticle(null);
        }
    }

    public Vec2f copyCoor(){
        return pos.copy();
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

    protected boolean interact(Vec2f future, CellType cType, CellType fType ) {
        return false;
    }

    @Deprecated
    public void loopCoor(int d){
        if( d == 0 ) {
            while(pos.x >= Const.WORLD_SIZE){
                pos.x -= Const.WORLD_SIZE;
            }

            while(pos.x < 0){
                pos.x += Const.WORLD_SIZE;
            }
        }else{
            while(pos.y >= Const.WORLD_SIZE){
                pos.y -= Const.WORLD_SIZE;
            }

            while(pos.y < 0){
                pos.y += Const.WORLD_SIZE;
            }
        }
    }

}
