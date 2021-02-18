package net.darktree.virus.cell;

import net.darktree.virus.Const;
import net.darktree.virus.Main;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.CellGenome;
import net.darktree.virus.gui.Screen;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import processing.core.PApplet;

import java.util.ArrayList;

public class NormalCell extends ShellCell implements GenomeCell {

    public CellGenome genome;
    public float geneTimer;
    public boolean tampered = false;
    public ArrayList<Vec2f> laserCoor = new ArrayList<>();
    public Particle laserTarget = null;
    public int laserT = -9999;
    public String memory = "";

    public NormalCell(int ex, int ey, String dna) {
        super(ex, ey, CellType.Normal);
        genome = new CellGenome(dna);
        genome.selected = (int) (Math.random() * genome.codons.size());
        geneTimer = (float) (Math.random() * Const.GENE_TICK_TIME);
    }

    public NormalCell(int ex, int ey, ArrayList<Codon> codons) {
        super(ex, ey, CellType.Normal);
        genome = new CellGenome(codons);
        genome.selected = (int) (Math.random() * genome.codons.size());
        geneTimer = (float) (Math.random() * Const.GENE_TICK_TIME);
    }

    @Override
    public CellGenome getGenome() {
        return genome;
    }

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

    protected void unscaledDraw(Screen screen) {
        drawLaser(screen);
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

    public void drawLaser(Screen screen){
        float time = laserT + Const.LASER_LINGER_TIME - getFrameCount();

        if( time > 0 ){
            float alpha = time / Const.LASER_LINGER_TIME;
            stroke( Helpers.addAlpha(Const.COLOR_HAND, alpha) );
            strokeWeight( 0.03f * Const.BIG_FACTOR );

            Vec2f hand = getHandPos();
            if(laserTarget == null){
                for(Vec2f singleLaserCoor : laserCoor){
                    screen.scaledLine(hand, singleLaserCoor);
                }
            }else{
                if( PApplet.dist(hand.x, hand.y, laserTarget.pos.x, laserTarget.pos.y) < 2 ) {
                    screen.scaledLine(hand, laserTarget.pos);
                }
            }
        }
    }

    public void useEnergy() {
        useEnergy( Const.GENE_TICK_ENERGY );
    }

    public void useEnergy( float amount ){
        energy = Math.max(0, energy - amount);
    }

    public void laserWall(){
        laserT = getFrameCount();
        laserCoor.clear();
        for(int i = 0; i < 4; i++){
            laserCoor.add( new Vec2f( x + (i / 2), y + (i % 2) ) );
        }
        laserTarget = null;
    }

    public void shootLaserAt(Particle food){
        laserT = getFrameCount();
        laserTarget = food;
    }

    public Vec2f getHandPos(){
        float r = Const.HAND_DIST + ( genome.inwards ? -Const.HAND_LEN : Const.HAND_LEN ) ;
        return genome.getCodonPos( genome.pointed, r, x, y );
    }

    public void eat(Particle food){
        if(food.type == ParticleType.FOOD){
            Particle particle = new Particle(food.pos, Helpers.combineVelocity( food.velocity, Helpers.getRandomVelocity() ), ParticleType.WASTE, -99999);
            shootLaserAt(particle);
            Main.applet.world.addParticle(particle);
            food.removeParticle(this);
            giveEnergy();
        }else{
            shootLaserAt(food);
        }
    }

    public void readToMemory(int start, int end) {
        memory = "";
        laserTarget = null;
        laserCoor.clear();
        laserT = getFrameCount();

        StringBuilder dna = new StringBuilder(memory);

        for(int pos = start; pos <= end; pos++){
            int index = Helpers.loopItInt( genome.pointed + pos, genome.codons.size() );
            dna.append( genome.codons.get(index).asDNA() ).append('-');
            laserCoor.add( genome.getCodonPos(index, Const.CODON_DIST, x, y) );
        }

        memory = dna.substring(0, dna.length() - 1);
    }

    public void writeFromMemory(int start, int end){
        if(memory.length() != 0) {
            laserTarget = null;
            laserCoor.clear();
            laserT = getFrameCount();

            if( genome.inwards ){
                writeInwards(start, end);
            }else{
                writeOutwards();
            }
        }
    }

    private void writeOutwards() {
        float theta = (float) Math.random() * 2 * PI;
        float ugo_vx = PApplet.cos(theta);
        float ugo_vy = PApplet.sin(theta);
        Vec2f handPos = getHandPos();

        // TODO: Fix this!
        float[] coor = new float[]{handPos.x, handPos.y, handPos.x + ugo_vx, handPos.y + ugo_vy};
        VirusParticle ugo = new VirusParticle(coor, memory);
        ugo.mutate( Const.MUTABILITY );
        Main.applet.world.addParticle(ugo);
        laserTarget = ugo;

        String[] memoryParts = memory.split("-");
        for(int i = 0; i < memoryParts.length; i++){
            useEnergy();
        }
    }

    private void writeInwards(int start, int end){
        laserTarget = null;
        String[] memoryParts = memory.split("-");
        for(int pos = start; pos <= end; pos++){
            int index = Helpers.loopItInt(genome.pointed +pos,genome.codons.size());
            if(pos-start < memoryParts.length){
                String memoryPart = memoryParts[pos-start];
                genome.codons.set(index, new Codon( memoryPart ));
                laserCoor.add( genome.getCodonPos(index, Const.CODON_DIST, x, y) );
            }
            useEnergy();
        }
    }

    public void pushOut(Particle particle){
        int[][] dire = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        int chosen = -1;
        int iter = 0;

        while( iter < 16 && chosen == -1 ) {

            int c = (int) Main.applet.random(0, 4);

            if( Main.applet.world.isCellValid( x + dire[c][0], y + dire[c][1] ) && Main.applet.world.getCellAtUnscaled( y + dire[c][1], x + dire[c][0] ) == null ) {
                chosen = c;
            }

            iter ++;

        }

        if( chosen == -1 ) return;

        Vec2f old = particle.copyCoor();
        for(int dim = 0; dim < 2; dim++){
            if(dire[chosen][dim] == -1){
                particle.pos.set(dim, PApplet.floor(particle.pos.get(dim)) - EPSILON);
                particle.velocity.set(dim, -PApplet.abs(particle.velocity.get(dim)));
            }else if(dire[chosen][dim] == 1){
                particle.pos.set(dim, PApplet.ceil(particle.pos.get(dim)) + EPSILON);
                particle.velocity.set(dim, PApplet.abs(particle.velocity.get(dim)));
            }
            particle.loopCoor(dim);
        }

        ContainerCell pCell = Main.applet.world.getCellAt( (int) old.x, (int) old.y, ContainerCell.class );
        if( pCell != null ) pCell.removeParticle(particle);

        ContainerCell nCell = Main.applet.world.getCellAt( (int) particle.pos.x, (int) particle.pos.y, ContainerCell.class );
        if( nCell != null ) nCell.addParticle(particle);

        laserT = Main.applet.frameCount;
        laserTarget = particle;
    }

    public void tick(){
        if(energy > 0){
            float oldGT = geneTimer;
            geneTimer -= Const.PLAY_SPEED;

            if(geneTimer <= Const.GENE_TICK_TIME / 2.0f && oldGT > Const.GENE_TICK_TIME / 2.0f){
                Codon codon = genome.getSelected();
                if( codon != null ) {
                    genome.hurtCodons(this);
                    codon.tick(this);
                }
            }

            if(geneTimer <= 0){
                geneTimer += Const.GENE_TICK_TIME;
                genome.next();
            }
        }

        genome.update();
    }

    @Override
    public void die(boolean silent) {
        if( !silent ) {
            for(int i = 0; i < genome.codons.size(); i++){
                Particle waste = new Particle( genome.getCodonPos(i, Const.CODON_DIST, x, y), ParticleType.WASTE, -99999 );
                Main.applet.world.addParticle( waste );
            }
        }

        super.die(silent);
    }
}
