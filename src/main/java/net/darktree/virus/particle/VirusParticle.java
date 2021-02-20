package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.cell.ShellCell;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.DrawableGenome;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;

import java.util.ArrayList;

public class VirusParticle extends Particle {

    private final DrawableGenome genome;
    private boolean divine = false;

    @Deprecated
    public VirusParticle( float[] pos, String data ) {
        super( new Vec2f( pos[0], pos[1] ), Vec2f.ZERO, Main.applet.frameCount );
        genome = new DrawableGenome( data );
        Vec2f vel = new Vec2f( pos[2] - pos[0], pos[3] - pos[1] );

        setVelocity(vel.x, vel.y);
        Main.applet.world.totalUGOCount ++;
    }

    public VirusParticle( Vec2f vec, String data ) {
        super( vec, Vec2f.ZERO, Main.applet.frameCount );
        genome = new DrawableGenome( data );

        float theta = (float) Math.random() * 2 * PI;
        setVelocity( Main.cos(theta), Main.sin(theta) );
        Main.applet.world.totalUGOCount ++;
    }

    public void setVelocity( float vx, float vy ) {
        float dist = Main.sqrt(vx * vx + vy * vy);
        float sp = Helpers.mapSpeed(dist);
        velocity = new Vec2f( vx / dist * sp, vy / dist * sp );
    }

    public void markDivine() {
        divine = true;
    }

    public void mutate( double mutability ) {
        //genome.mutate( mutability );
    }

    @Override
    public void tick(World world) {
        super.tick(world);

        if( Main.applet.frameCount % Const.GENE_TICK_TIME == 0 ) {
            genome.hurtCodons(null);
            if( genome.codons.size() == 0 ) {
                removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );
                Particle p = new WasteParticle( pos, velocity, -99999 );
                Main.applet.world.addParticle( p );
            }
        }
    }

    @Override
    public ParticleType getType() {
        return ParticleType.VIRUS;
    }

    @Override
    public int getColor() {
        return Const.COLOR_UGO;
    }

    @Override
    public void draw(Screen screen) {

        float posx = screen.trueXtoAppX(pos.x);
        float posy = screen.trueYtoAppY(pos.y);

        if( posx > 0 && posy > 0 && posx < screen.maxRight && posy < Main.applet.height ) {

            super.draw(screen);
            if( screen.camS > Const.DETAIL_THRESHOLD && genome != null ) genome.drawCodons(Const.CODON_DIST_UGO);

        }

    }

    @Override
    protected boolean interact( World world, Vec2f future, CellType ct, CellType ft ) {
        Cell fc = world.getCellAt(future.x, future.y);

        if( fc instanceof NormalCell ) {
            NormalCell cell = (NormalCell) fc;

            if( divine || cell.wall * Const.CELL_WALL_PROTECTION < Main.applet.random(0,1) ) {
                if( genome.codons.size() + cell.getGenome().codons.size() <= Const.MAX_CODON_COUNT ) {
                    return injectGeneticMaterial(fc);
                }
            }
        }else if( fc instanceof ShellCell ) {
            return injectGeneticMaterial(fc);
        }

        return false;
    }

    public boolean injectGeneticMaterial( Cell c ){

        if( c instanceof NormalCell ) {

            NormalCell cell = (NormalCell) c;

            int injectionLocation = cell.genome.selected;
            ArrayList<Codon> toInject = genome.codons;
            int size = genome.codons.size();

            for(int i = 0; i < toInject.size(); i++){
                cell.genome.codons.add( injectionLocation+i, new Codon( toInject.get(i) ) );
            }

            if(cell.genome.pointed >= cell.genome.selected){
                cell.genome.pointed += size;
            }

            cell.genome.selected += size;
            if( !cell.tamper() ) Main.applet.world.infectedCount ++;

        }else if( c instanceof ShellCell ){

            Main.applet.world.setCellAt( c.x, c.y, new NormalCell( c.x, c.y, genome.codons ) );
            Main.applet.world.shellCount --;
            Main.applet.world.aliveCount ++;
            Main.applet.world.infectedCount ++;

        }

        removeParticle( Main.applet.world.getCellAt(pos.x, pos.y) );
        Particle p = new WasteParticle(pos, Helpers.combineVelocity( this.velocity, Helpers.getRandomVelocity() ), -99999);
        Main.applet.world.addParticle( p );

        return true;

    }

}
