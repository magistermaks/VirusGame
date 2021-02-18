package net.darktree.virus.cell;

import net.darktree.virus.Main;
import net.darktree.virus.codon.Codon;
import net.darktree.virus.genome.CellGenome;
import net.darktree.virus.particle.Particle;
import net.darktree.virus.particle.ParticleContainer;
import net.darktree.virus.particle.ParticleType;
import net.darktree.virus.particle.VirusParticle;
import net.darktree.virus.util.DrawContext;
import net.darktree.virus.util.Helpers;
import net.darktree.virus.util.Vec2f;
import processing.core.PApplet;

import java.util.ArrayList;

public class Cell implements DrawContext {

    public int x;
    public int y;
    public CellType type;
    public float wall;
    public float energy;
    public CellGenome genome;
    public float geneTimer;
    public boolean tampered = false;
    public ParticleContainer pc = new ParticleContainer();
    public ArrayList<Vec2f> laserCoor = new ArrayList<>();
    public Particle laserTarget = null;
    public int laserT = -9999;
    public String memory = "";
    public int dire;

    public Cell(int ex, int ey, CellType et, int ed, double ewh, String eg){
        x = ex;
        y = ey;
        type = et;
        dire = ed;
        wall = (float) ewh;
        genome = new CellGenome(eg);
        genome.selected = (int)(Math.random()*genome.codons.size());
        geneTimer = (float) (Math.random() * Main.applet.settings.gene_tick_time);
        energy = 0.5f;
    }

    public String getMemory() {
        return memory.length() == 0 ? "empty" : "\"" + memory + "\"";
    }

    public boolean hasGenome() {
        return type == CellType.Normal;
    }

    public boolean isHandInwards() {
        return genome.inwards;
    }

    private void drawCellBackground( int back ) {
        fill(Main.applet.COLOR_CELL_WALL);
        rect(0, 0, Main.BIG_FACTOR, Main.BIG_FACTOR);
        fill( back );
        float w = Main.BIG_FACTOR * 0.08f * wall;
        rect(w, w, Main.BIG_FACTOR - 2 * w, Main.BIG_FACTOR - 2 * w);
    }

    public void draw() {

        float posx = Main.applet.renderer.trueXtoAppX(x);
        float posy = Main.applet.renderer.trueYtoAppY(y);

        // only draw cells that are visible on screen
        if( posx < -Main.applet.renderer.camS || posy < -Main.applet.renderer.camS || posx > Main.applet.renderer.maxRight || posy > Main.applet.height ) {
            return;
        }

        push();
        translate( posx, posy );
        scale( Main.applet.renderer.camS / Main.BIG_FACTOR );
        noStroke();

        if(type == CellType.Locked) {

            fill(Main.applet.COLOR_CELL_LOCKED);
            rect(0, 0, Main.BIG_FACTOR, Main.BIG_FACTOR);

        }else if(type == CellType.Normal) {

            drawCellBackground( (tampered && Main.applet.settings.show_tampered) ? Main.applet.COLOR_CELL_TAMPERED : Main.applet.COLOR_CELL_BACK );

            push();
            translate(Main.BIG_FACTOR * 0.5f, Main.BIG_FACTOR * 0.5f);

            if(Main.applet.renderer.camS > Main.DETAIL_THRESHOLD) {
                genome.drawInterpreter(geneTimer);
                genome.drawCodons(Main.CODON_DIST);
            }

            drawEnergy();
            genome.drawHand();
            pop();

        }else if( type == CellType.Shell ) {

            drawCellBackground( Main.applet.COLOR_CELL_BACK );

            push();
            translate(Main.BIG_FACTOR * 0.5f, Main.BIG_FACTOR * 0.5f);
            drawEnergy();
            pop();
        }

        pop();

        if(type == CellType.Normal){
            drawLaser();
        }

    }

    public void drawLaser(){
        float time = laserT + Main.applet.settings.laser_linger_time - getFrameCount();

        if( time > 0 ){
            float alpha = time / Main.applet.settings.laser_linger_time;
            stroke( Helpers.addAlpha(Main.applet.COLOR_HAND, alpha) );
            strokeWeight( 0.03f * Main.BIG_FACTOR );

            Vec2f hand = getHandPos();
            if(laserTarget == null){
                for(Vec2f singleLaserCoor : laserCoor){
                    Main.applet.renderer.scaledLine(hand, singleLaserCoor);
                }
            }else{
                if( PApplet.dist(hand.x, hand.y, laserTarget.pos.x, laserTarget.pos.y) < 2 ) {
                    Main.applet.renderer.scaledLine(hand, laserTarget.pos);
                }
            }
        }
    }

    public void drawEnergy(){
        noStroke();
        fill(Main.applet.COLOR_TELOMERE);
        ellipse(0, 0, 17, 17);

        if( energy > 0 ) {
            fill(Main.applet.COLOR_ENERGY);
            ellipseMode(CENTER);
            ellipse(0, 0, 12 * energy + 2, 12 * energy + 2);
        }
    }

    public void tick(){
        if(type == CellType.Normal){
            if(energy > 0){
                float oldGT = geneTimer;
                geneTimer -= Main.PLAY_SPEED;

                if(geneTimer <= Main.applet.settings.gene_tick_time/2.0f && oldGT > Main.applet.settings.gene_tick_time/2.0f){
                    Codon codon = genome.getSelected();
                    if( codon != null ) {
                        genome.hurtCodons(this);
                        codon.tick(this);
                    }
                }

                if(geneTimer <= 0){
                    geneTimer += Main.applet.settings.gene_tick_time;
                    genome.next();
                }
            }

            genome.update();

        }
    }

    public void useEnergy() {
        useEnergy( Main.applet.settings.gene_tick_energy );
    }

    public void useEnergy( float amount ){
        energy = Math.max(0, energy - amount);
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
            laserCoor.add( genome.getCodonPos(index, Main.CODON_DIST, x, y) );
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
        ugo.mutate( Main.applet.settings.mutability );
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
                laserCoor.add( genome.getCodonPos(index, Main.CODON_DIST, x, y) );
            }
            useEnergy();
        }
    }

    public void healWall(){
        wall += (1-wall) * Main.E_RECIPROCAL;
    }

    public void giveEnergy() {
        energy += (1-energy) * Main.E_RECIPROCAL;
    }

    public void laserWall(){
        laserT = getFrameCount();
        laserCoor.clear();
        for(int i = 0; i < 4; i++){
            laserCoor.add( new Vec2f( x + (i / 2), y + (i % 2) ) );
        }
        laserTarget = null;
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

    public void shootLaserAt(Particle food){
        laserT = getFrameCount();
        laserTarget = food;
    }

    public Vec2f getHandPos(){
        float r = Main.HAND_DIST + ( genome.inwards ? -Main.HAND_LEN : Main.HAND_LEN ) ;
        return genome.getCodonPos( genome.pointed, r, x, y );
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

        Cell p_cell = Main.applet.world.getCellAt(old.x, old.y);
        if( p_cell != null ) p_cell.removeParticle(particle);

        Cell n_cell = Main.applet.world.getCellAt(particle.pos.x, particle.pos.y);
        if( n_cell != null ) n_cell.addParticle(particle);

        laserT = Main.applet.frameCount;
        laserTarget = particle;
    }

    public void hurtWall(double multi){
        if(type == CellType.Normal) {
            wall -= Main.applet.settings.wall_damage*multi;
            if(wall <= 0) die(false);
        }
    }

    public boolean tamper() {
        boolean old = tampered;
        tampered = true;
        return old;
    }

    public void die( boolean silent ){
        if( !silent ) {
            for(int i = 0; i < genome.codons.size(); i++){
                Particle waste = new Particle( genome.getCodonPos(i, Main.CODON_DIST, x, y), ParticleType.WASTE, -99999 );
                Main.applet.world.addParticle( waste );
            }
        }

        if(this == Main.applet.editor.selected){
            Main.applet.editor.close();
        }

        if( type == CellType.Shell ){
            Main.applet.world.shellCount --;
        }else if( type == CellType.Normal ) {
            Main.applet.world.aliveCount --;
        }

        if( !silent ) Main.applet.world.deadCount ++;
        type = CellType.Empty;
    }

    public void addParticle(Particle food){
        pc.get(food.type).add(food);
    }

    public void removeParticle(Particle p){
        pc.get(p.type).remove( p );
    }

    public Particle selectParticle(ParticleType type){
        ArrayList<Particle> myList = pc.get(type);
        if(myList.size() == 0){

            if( type == ParticleType.WASTE ) {
                return selectParticle( ParticleType.UGO );
            }

            return null;
        }else{
            int choiceIndex = (int)(Math.random() * myList.size());
            return myList.get(choiceIndex);
        }
    }

    public String getCellName(){
        if(x == -1){
            return "Custom UGO";
        }else if(type == CellType.Normal){
            return "Cell at ("+x+", "+y+")";
        }else if(type == CellType.Shell) {
            return "Cell Shell";
        }else if(type == CellType.Locked) {
            return "Wall";
        }

        return "Undefined";
    }

    public int getParticleCount(ParticleType t){
        if(t == null){
            return pc.count();
        }else{
            return pc.get(t).size();
        }
    }

}
