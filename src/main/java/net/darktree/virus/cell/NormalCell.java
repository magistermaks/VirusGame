package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.CellGenome;
import net.darktree.virus.genome.GenomeBase;
import net.darktree.virus.particle.FoodParticle;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.particle.WasteParticle;
import net.darktree.virus.ui.Screen;
import net.darktree.virus.util.Direction;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Utils;
import net.darktree.virus.util.Vec2f;
import net.darktree.virus.world.World;

import java.util.ArrayList;

public class NormalCell extends ShellCell implements GenomeCell {

    public CellGenome genome;
    public float geneTimer;
    public boolean tampered = false;
    private final Laser laser = new Laser();
    public String memory = "";

    public NormalCell(int x, int y, String dna) {
        super(x, y);
        genome = new CellGenome(dna);
        genome.selected = (int) (Math.random() * genome.codons.size());
        geneTimer = (float) (Math.random() * Const.GENE_TICK_TIME);
    }

    public NormalCell(int ex, int ey, ArrayList<Codon> codons) {
        super(ex, ey);
        genome = new CellGenome(codons);
        genome.selected = (int) (Math.random() * genome.codons.size());
        geneTimer = (float) (Math.random() * Const.GENE_TICK_TIME);
    }

    @Override
    public CellGenome getGenome() {
        return genome;
    }

    @Override
    public String getCellName(){
        return "Cell at (" + x + ", " + y + ")";
    }

    @Override
    protected void drawCell(Screen screen) {
        drawCellBackground( (tampered && Main.showTampered) ? Const.COLOR_CELL_TAMPERED : Const.COLOR_CELL_BACK );

        push();
        translate(Const.BIG_FACTOR * 0.5f, Const.BIG_FACTOR * 0.5f);

        if(screen.camS > Const.DETAIL_THRESHOLD) {
            genome.drawInterpreter(geneTimer);
            genome.drawCodons(Const.CODON_DIST);
        }

        drawEnergy();
        genome.drawHand();
        pop();
    }

    @Override
    protected void unscaledDraw(Screen screen) {
        laser.draw(screen, getHandPos());
    }

    public String getMemory() {
        return memory.length() == 0 ? "empty" : "\"" + memory + "\"";
    }

    public void hurtWall(double multi){
        wall -= Const.WALL_DAMAGE * multi;
        if(wall <= 0) die(false);
    }

    public boolean isHandInwards() {
        return genome.inwards;
    }

    public boolean tamper() {
        boolean old = tampered;
        tampered = true;
        return old;
    }

    public void useEnergy() {
        useEnergy( Const.GENE_TICK_ENERGY );
    }

    public void useEnergy( float amount ){
        energy = Math.max(0, energy - amount);
    }

    public void laserWall(){
        laser.targetWall(x, y);
    }

    public Vec2f getHandPos(){
        float r = Const.HAND_DIST + ( genome.inwards ? -Const.HAND_LEN : Const.HAND_LEN ) ;
        return genome.getCodonPos( genome.pointed, r, x, y );
    }

    public void eat(Particle p){
        if(p instanceof FoodParticle){
            Particle waste = new WasteParticle(p.pos, Helpers.combineVelocity( p.velocity, Helpers.getRandomVelocity() ));
            laser.targetParticle(waste);
            Main.applet.world.addParticle(waste);
            p.remove();
            giveEnergy();
        }else{
            laser.targetParticle(p);
        }
    }

    public void readToMemory(int start, int end) {
        memory = "";
        laser.reset();

        StringBuilder dna = new StringBuilder(memory);

        for(int pos = start; pos <= end; pos++){
            int index = Helpers.loopItInt( genome.pointed + pos, genome.codons.size() );
            dna.append( genome.codons.get(index).asDNA() ).append('-');
            laser.addTargetPos( genome.getCodonPos(index, Const.CODON_DIST, x, y) );
        }

        memory =  dna.length() != 0 ? dna.substring(0, dna.length() - 1) : "";
    }

    public void writeFromMemory(int start, int end){
        if(memory.length() != 0) {
            if( genome.inwards ){
                writeInwards(start, end);
            }else{
                writeOutwards();
            }
        }
    }

    private void writeOutwards() {
        VirusParticle virus = new VirusParticle(getHandPos(), memory);
        virus.mutate();
        Main.applet.world.addParticle(virus);
        laser.targetParticle(virus);

        String[] memoryParts = memory.split("-");
        for(int i = 0; i < memoryParts.length; i++){
            useEnergy();
        }
    }

    private void writeInwards(int start, int end){
        laser.reset();
        String[] memoryParts = memory.split("-");
        for(int pos = start; pos <= end; pos++){
            int index = Helpers.loopItInt(genome.pointed +pos,genome.codons.size());
            if(pos-start < memoryParts.length){
                String memoryPart = memoryParts[pos-start];
                genome.codons.set(index, new Codon( memoryPart ));
                laser.addTargetPos( genome.getCodonPos(index, Const.CODON_DIST, x, y) );
            }
            useEnergy();
        }
    }

    public void pushOut(Particle particle){

        Direction chosen = null;
        Direction dir = Direction.getRandom();
        World world = Main.applet.world;

        // check every direction - starting with a random one
        for( int i = 0; i < 4; i ++ ) {
            if( world.isCellValid( x + dir.x, y + dir.y ) && world.getCellAt( x + dir.x, y + dir.y ) == null ) {
                chosen = dir;
                break;
            }

            dir = dir.cycle();
        }

        // failed to find suitable push direction
        if( chosen == null ) return;

        Vec2f old = particle.pos.copy();
        float x = chosen.x, y = chosen.y;

        if(x != 0) {
            particle.pos.x = Utils.ceilOrFloor(particle.pos.x, x) + EPSILON * x;
            particle.velocity.x = Main.abs(particle.velocity.x) * x;
        }

        if(y != 0) {
            particle.pos.y = Utils.ceilOrFloor(particle.pos.y, y) + EPSILON * y;
            particle.velocity.y = Main.abs(particle.velocity.y) * y;
        }

        particle.alignWithWorld();
        Main.applet.world.pc.updateCell(particle, particle.pos);

        laser.targetParticle(particle);
    }

    public void jump( int offset ) {
        genome.selected = Helpers.loopItInt( genome.selected + offset, genome.size() );
        geneTimer = Const.GENE_TICK_TIME;
    }

    @Override
    public void tick(){
        if(energy > 0){
            float oldGT = geneTimer;
            float halfGeneTime = Const.GENE_TICK_TIME * 0.5f;

            geneTimer -= Const.PLAY_SPEED;

            if(geneTimer <= halfGeneTime && oldGT > halfGeneTime) {
                genome.executeSelected(this);
            }

            if(geneTimer <= 0){
                geneTimer += Const.GENE_TICK_TIME;
                genome.next();
            }
        }

        // Updated visual interpolation
        genome.update();
    }

    @Override
    public void die(boolean silent) {
        if( !silent ) {
            for(int i = 0; i < genome.codons.size(); i++) {
                if( Utils.random( energy + 1) > 0.6 ) {
                    Particle waste = new WasteParticle(genome.getCodonPos(i, Const.CODON_DIST, x, y));
                    Main.applet.world.addParticle(waste);
                }
            }
        }

        super.die(silent);
    }

    @Override
    public CellType getType() {
        return CellType.Normal;
    }

}
