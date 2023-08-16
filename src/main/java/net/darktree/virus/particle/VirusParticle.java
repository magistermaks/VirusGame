package net.darktree.virus.particle;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.cell.Cell;
import net.darktree.virus.cell.CellType;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.cell.ShellCell;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.DrawableGenome;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.ui.editor.Arrow;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Utils;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;

import java.util.ArrayList;

public class VirusParticle extends Particle {

    private final DrawableGenome genome;
    private boolean divine = false;
    
    public VirusParticle( Arrow arrow, String data ) {
        super( new Vec2f( arrow.getX(), arrow.getY() ), Vec2f.zero());
        genome = new DrawableGenome( data );

        setVelocity( arrow.getVX(), arrow.getVY() );
        Main.applet.world.getStats().VIRUSES.increment();
    }

    public VirusParticle( Vec2f vec, String data ) {
        super( vec, Vec2f.zero());
        genome = new DrawableGenome( data );

        float theta = (float) Math.random() * TWO_PI;
        setVelocity( Main.cos(theta), Main.sin(theta) );
        Main.applet.world.getStats().VIRUSES.increment();
    }

    public void setVelocity( float vx, float vy ) {
        float dist = Main.sqrt(vx * vx + vy * vy);
        float sp = Helpers.mapSpeed(dist);
        velocity = new Vec2f( vx / dist * sp, vy / dist * sp );
    }

    public void markDivine() {
        divine = true;
    }

    public void mutate() {
        genome.mutate();
    }

    public DrawableGenome getGenome() {
        return genome;
    }

    public void applyDamage() {
        genome.hurtCodons(null);
        if( genome.codons.size() == 0 ) {
            remove();
            Particle p = new WasteParticle( pos, velocity);
            Main.applet.world.addParticle( p );
        }
    }

    @Override
    public void tick(World world) {
        super.tick(world);

        if( Main.applet.frameCount % Const.GENE_TICK_TIME == 0 ) {
            applyDamage();
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
        super.draw(screen);
        if( screen.camS > Const.DETAIL_THRESHOLD ) genome.drawCodons(Const.CODON_DIST_UGO);
    }

    @Override
    protected boolean interact( World world, Vec2f future, CellType ct, CellType ft ) {
        Cell fc = world.getCellAt(future.x, future.y);

        if( ct == CellType.Empty || ct == CellType.Kill ) {

            if (fc instanceof NormalCell) {
                NormalCell cell = (NormalCell) fc;

                if (divine || cell.wall * Const.CELL_WALL_PROTECTION < Utils.random(0.0f, 1.0f)) {
                    if (genome.codons.size() + cell.getGenome().codons.size() <= Const.MAX_CODON_COUNT) {
                        return injectGeneticMaterial(fc);
                    }
                }
            } else if (fc instanceof ShellCell) {
                return injectGeneticMaterial(fc);
            }

        }

        return false;
    }

    public boolean injectGeneticMaterial( Cell c ){

        World world = Main.applet.world;
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
            if( !cell.tamper() ) world.getStats().INFECTIONS.increment();

        }else if( c instanceof ShellCell ){

            world.setCellAt( c.x, c.y, new NormalCell( c.x, c.y, genome.codons ) );
            world.getStats().INFECTIONS.increment();

        }

        remove();
        Particle p = new WasteParticle(pos, Helpers.combineVelocity( this.velocity, Helpers.getRandomVelocity() ));
        world.addParticle( p );

        return true;

    }

}
