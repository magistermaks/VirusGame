package net.darktree.virus.particle;

import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
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
        return type == ParticleType.FOOD ? Main.applet.COLOR_FOOD : Main.applet.COLOR_WASTE;
    }

    public void draw() {
        float posx = Main.applet.renderer.trueXtoAppX(pos.x);
        float posy = Main.applet.renderer.trueYtoAppY(pos.y);

        if( posx > 0 && posy > 0 && posx < Main.applet.renderer.maxRight && posy < Main.applet.height ) {

            translate( posx, posy );
            float ageScale = Math.min(1.0f, (Main.applet.frameCount - birthFrame) * Main.applet.settings.age_grow_speed);
            scale( Main.applet.renderer.camS / Main.BIG_FACTOR * ageScale );
            noStroke();
            fill( getColor() );
            ellipseMode(PApplet.CENTER);
            ellipse(0, 0, 0.1f * Main.BIG_FACTOR, 0.1f * Main.BIG_FACTOR);

        }
    }

    public void tick() {
        Vec2f future = new Vec2f();
        CellType ct = Main.applet.world.getCellTypeAt(pos.x, pos.y);

        if( ct == CellType.Locked ) removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );

        float visc = ct == CellType.Empty ? 1 : 0.5f;

        future.x = pos.x + velocity.x * visc * Main.PLAY_SPEED;
        future.y = pos.y + velocity.y * visc * Main.PLAY_SPEED;

        boolean cta = Math.floor(pos.x) != Math.floor(future.x);
        boolean ctb = Math.floor(pos.y) != Math.floor(future.y);

        if( cta || ctb ) {

            CellType ft = Main.applet.world.getCellTypeAt(future.x, future.y);

            if( interact( future, ct, ft ) ) return;

            if(ft == CellType.Locked || (type != ParticleType.FOOD && (ct != CellType.Empty || ft != CellType.Empty))) {

                Cell cell1 = Main.applet.world.getCellAt(future.x, future.y);
                if(cell1 != null && cell1.type.isHurtable()){
                    cell1.hurtWall( cta && ctb ? 2 : 1 );
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
                if(cell2 != null && cell2.type.isHurtable()){
                    cell2.hurtWall( cta && ctb ? 2 : 1 );
                }

            }else{

                if(future.x >= Main.applet.settings.world_size) { future.x -= Main.applet.settings.world_size; border(); } else
                if(future.x < 0) { future.x += Main.applet.settings.world_size; border(); } else
                if(future.y >= Main.applet.settings.world_size) { future.y -= Main.applet.settings.world_size; border(); } else
                if(future.y < 0) { future.y += Main.applet.settings.world_size; border(); }

                hurtWall( pos, false );
                hurtWall( future, true );
            }

        }

        pos = future;

    }

    public void randomTick() {
        if( type == ParticleType.WASTE ) {
            if( Main.applet.random(0, 1) < Main.applet.settings.waste_disposal_chance_random && Main.applet.world.getCellAt(pos.x, pos.y) == null ) removeParticle(null);
        }
    }

    private void border() {
        if( type == ParticleType.WASTE ) {
            if( Main.applet.world.pc.wastes.size() > Main.applet.settings.max_waste && Main.applet.random(0, 1) < Main.applet.settings.waste_disposal_chance_high ) removeParticle(null);
            if( Main.applet.random(0, 1) < Main.applet.settings.waste_disposal_chance_low ) removeParticle(null);
        }
    }

    public Vec2f copyCoor(){
        return pos.copy();
    }

    protected void hurtWall(Vec2f pos, boolean add) {
        Cell cell = Main.applet.world.getCellAt(pos.x, pos.y);
        if( cell != null ) {
            if(cell.type.isHurtable()){
                cell.hurtWall(1);
            }

            if( add ) {
                cell.addParticle(this);
            }else{
                cell.removeParticle(this);
            }
        }
    }

    public void removeParticle( Cell c ) {
        removed = true;
        if(c != null) c.removeParticle(this);
    }

    public void addToCellList(){
        Cell c = Main.applet.world.getCellAt(pos.x, pos.y);
        if( c != null ) c.addParticle(this);
    }

    protected boolean interact(Vec2f future, CellType cType, CellType fType ) {
        return false;
    }

    @Deprecated
    public void loopCoor(int d){
        if( d == 0 ) {
            while(pos.x >= Main.applet.settings.world_size){
                pos.x -= Main.applet.settings.world_size;
            }

            while(pos.x < 0){
                pos.x += Main.applet.settings.world_size;
            }
        }else{
            while(pos.y >= Main.applet.settings.world_size){
                pos.y -= Main.applet.settings.world_size;
            }

            while(pos.y < 0){
                pos.y += Main.applet.settings.world_size;
            }
        }
    }

}
