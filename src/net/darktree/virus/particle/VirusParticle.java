package net.darktree.virus.particle;

import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.DrawableGenome;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;

import java.util.ArrayList;

public class VirusParticle extends Particle {

    DrawableGenome genome;
    boolean divine = false;

    @Deprecated
    public VirusParticle( float[] pos, String data ) {
        super( new Vec2f( pos[0], pos[1] ), ParticleType.UGO, Main.applet.frameCount );
        genome = new DrawableGenome( data );
        Vec2f coor = new Vec2f( pos[2] - pos[0], pos[3] - pos[1] );

        float dist = Main.sqrt(coor.x * coor.x + coor.y * coor.y);
        float sp = dist * ( Main.SPEED_HIGH - Main.SPEED_LOW ) + Main.SPEED_LOW;
        velocity = new Vec2f( coor.x / dist * sp, coor.y / dist * sp );
        Main.applet.world.totalUGOCount ++;
    }

    public VirusParticle( Vec2f vec, String data ) {
        super( vec, ParticleType.UGO, Main.applet.frameCount );
        genome = new DrawableGenome( data );

        float dist = Main.sqrt(vec.x * vec.x + vec.y * vec.y);
        float sp = dist * ( Main.SPEED_HIGH - Main.SPEED_LOW ) + Main.SPEED_LOW;
        velocity = new Vec2f( vec.x / dist * sp, vec.y / dist * sp );
        Main.applet.world.totalUGOCount ++;
    }

    public void markDivine() {
        divine = true;
    }

    public void mutate( double mutability ) {
        //genome.mutate( mutability );
    }

    public void tick() {
        super.tick();

        if( Main.applet.frameCount % Main.applet.settings.gene_tick_time == 0 ) {
            genome.hurtCodons(null);
            if( genome.codons.size() == 0 ) {
                removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );
                Particle p = new Particle( pos, velocity, ParticleType.WASTE, -99999 );
                Main.applet.world.addParticle( p );
            }
        }
    }

    public int getColor() {
        return Main.applet.COLOR_UGO;
    }

    public void draw() {

        float posx = Main.applet.renderer.trueXtoAppX(pos.x);
        float posy = Main.applet.renderer.trueYtoAppY(pos.y);

        if( posx > 0 && posy > 0 && posx < Main.applet.width && posy < Main.applet.width ) {

            super.draw();
            if( Main.applet.renderer.camS > Main.DETAIL_THRESHOLD && genome != null ) genome.drawCodons(Main.CODON_DIST_UGO);

        }

    }

    protected boolean interact(Vec2f future, CellType ct, CellType ft ) {

        Cell fc = Main.applet.world.getCellAt(future.x, future.y);
        if( fc != null ) {

            if( divine || fc.wall * Main.applet.settings.cell_wall_protection < Main.applet.random(0,1) || fc.type == CellType.Shell ) {

                if(type == ParticleType.UGO && ct == CellType.Empty && ft == CellType.Normal && genome.codons.size()+fc.genome.codons.size() <= Main.applet.settings.max_codon_count){
                    return injectGeneticMaterial(fc);
                }else if(type == ParticleType.UGO && ft == CellType.Shell && ct == CellType.Empty ){
                    return injectGeneticMaterial(fc);
                }

            }

        }

        return false;
    }

    public boolean injectGeneticMaterial( Cell c ){

        if( c.type == CellType.Shell ) {

            c.type = CellType.Normal;
            c.genome.codons = genome.codons;
            c.genome.selected = 0;
            c.genome.pointed = 0;
            Main.applet.world.shellCount --;
            Main.applet.world.aliveCount ++;

        }else{

            int injectionLocation = c.genome.selected;
            ArrayList<Codon> toInject = genome.codons;
            int size = genome.codons.size();

            for(int i = 0; i < toInject.size(); i++){
                c.genome.codons.add( injectionLocation+i, new Codon( toInject.get(i) ) );
            }

            if(c.genome.pointed >= c.genome.selected){
                c.genome.pointed += size;
            }

            c.genome.selected += size;
        }

        if( !c.tamper() ) Main.applet.world.infectedCount ++;
        removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );
        Particle p = new Particle(pos, Helpers.combineVelocity( this.velocity, Helpers.getRandomVelocity() ), ParticleType.WASTE, -99999);
        Main.applet.world.addParticle( p );

        return true;

    }

}
