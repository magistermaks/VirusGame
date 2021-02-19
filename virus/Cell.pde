class Cell{
  
    int x;
    int y;
    CellType type;
    float wall;
    float energy = 0;
    Genome genome;
    float geneTimer = 0;
    boolean tampered = false;
    ParticleContainer pc = new ParticleContainer();
    ArrayList<Vec2f> laserCoor = new ArrayList<Vec2f>();
    Particle laserTarget = null;
    int laserT = -9999;
    String memory = "";
  
    public Cell(int ex, int ey, CellType et, String eg){
        x = ex;
        y = ey;
        type = et;
        genome = new Genome(eg);
        genome.rotateOn = (int)(Math.random()*genome.codons.size());
        geneTimer = (float) (Math.random() * settings.gene_tick_time);
        energy = 0.5;
        wall = 1.0;
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
    
    private void drawCellBackground( color back ) {
        fill(COLOR_CELL_WALL);
        rect(0, 0, BIG_FACTOR, BIG_FACTOR);
        fill( back );
        float w = BIG_FACTOR * 0.08 * wall;
        rect(w, w, BIG_FACTOR - 2 * w, BIG_FACTOR - 2 * w);
    }
  
    void draw() {
      
        float posx = renderer.trueXtoAppX(x);
        float posy = renderer.trueYtoAppY(y);
      
        // only draw cells that are visible on screen
        if( posx < -renderer.camS || posy < -renderer.camS || posx > renderer.maxRight || posy > height ) {
            return;
        }
    
        pushMatrix();
        translate( posx, posy );
        scale( renderer.camS / BIG_FACTOR );
        noStroke();
        
        if(type == CellType.Locked) {
          
            fill(COLOR_CELL_LOCKED);
            rect(0, 0, BIG_FACTOR, BIG_FACTOR);
          
        }else if(type == CellType.Normal) {
          
            drawCellBackground( (tampered && settings.show_tampered) ? COLOR_CELL_TAMPERED : COLOR_CELL_BACK );
        
            pushMatrix();
            translate(BIG_FACTOR * 0.5, BIG_FACTOR * 0.5);
      
            if(renderer.camS > DETAIL_THRESHOLD) {
                drawInterpreter();
                genome.drawCodons(CODON_DIST);
            }
      
            drawEnergy();
            genome.drawHand();
            popMatrix();
          
        }else if( type == CellType.Shell ) {
          
            drawCellBackground( COLOR_CELL_BACK );
      
            pushMatrix();
            translate(BIG_FACTOR * 0.5, BIG_FACTOR * 0.5);
            drawEnergy();
            popMatrix();
        }
    
        popMatrix();

        if(type == CellType.Normal){
            drawLaser();
        }
      
    }
  
    private void drawInterpreter(){
        float angle = 1.0 / genome.codons.size() * PI;
        float delta = geneTimer / settings.gene_tick_time;
        color col = delta < 0.5 ? int( min(1, (0.5 - delta) * 4) * 255 ) : 255;
        
        pushMatrix();
        rotate( -HALF_PI + angle * 2 * genome.appRO );
        fill( col );
        beginShape();
        strokeWeight(1);
        stroke(80);
        vertex(0, 0);
        vertex( INTERPRETER_LENGTH * cos(angle), INTERPRETER_LENGTH * sin(angle) );
        vertex( INTERPRETER_LENGTH * cos(-angle), INTERPRETER_LENGTH * sin(-angle) );
        endShape(CLOSE);
        noStroke();
        popMatrix();
    }
  
    public void drawLaser(){
        float time = laserT + settings.laser_linger_time - frameCount;
      
        if( time > 0 ){
            float alpha = time / settings.laser_linger_time;
            stroke( transperize(COLOR_HAND, alpha) );
            strokeWeight( 0.03 * BIG_FACTOR ); 
            
            Vec2f hand = getHandCoor();
            if(laserTarget == null){
                for(Vec2f singleLaserCoor : laserCoor){
                    renderer.scaledLine(hand, singleLaserCoor);
                }
            }else{
                if( dist(hand.x, hand.y, laserTarget.pos.x, laserTarget.pos.y) < 2 ) {
                    renderer.scaledLine(hand, laserTarget.pos);
                }
            }
        }
    }
  
    public void drawEnergy(){
        noStroke();
        fill(0);
        ellipse(0, 0, 17, 17);
    
        if( energy > 0 ) {
            fill(COLOR_ENERGY);
            ellipseMode(CENTER);
            ellipse(0, 0, 12 * energy + 2, 12 * energy + 2);
        }
    }
  
    public void tick(){
        if(type == CellType.Normal){
            if(energy > 0){
                float oldGT = geneTimer;
                geneTimer -= PLAY_SPEED;
                
                if(geneTimer <= settings.gene_tick_time/2.0 && oldGT > settings.gene_tick_time/2.0){
                    Codon codon = genome.getSelected();
                    if( codon != null ) {
                        genome.hurtCodons(this);
                        codon.tick(this);
                    }
                }
                
                if(geneTimer <= 0){
                    geneTimer += settings.gene_tick_time;
                    genome.next();
                }
            }
            
            genome.update();
            
        }
    }
    
    void useEnergy() {
        useEnergy( settings.gene_tick_energy );
    }
  
    void useEnergy( float amount ){
        energy = Math.max(0, energy - amount);
    }
  
    void readToMemory(int start, int end){
        memory = "";
        laserTarget = null;
        laserCoor.clear();
        laserT = frameCount;
        
        for(int pos = start; pos <= end; pos++){
            int index = loopItInt( genome.performerOn + pos, genome.codons.size() );
            memory += genome.codons.get(index).asDNA();
            
            if(pos < end){
                memory += "-";
            }
            
            laserCoor.add( genome.getCodonCoor(index, CODON_DIST, x, y) );
        }
    }
    
    public void writeFromMemory(int start, int end){
        if(memory.length() != 0) {
            laserTarget = null;
            laserCoor.clear();
            laserT = frameCount;
            
            if( genome.inwards ){
                writeInwards(start, end);
            }else{
                writeOutwards();
            }
        }
    }
    
    private void writeOutwards() {
        float theta = (float) Math.random() * 2 * PI;
        float ugo_vx = cos(theta);
        float ugo_vy = sin(theta);
        Vec2f startCoor = getHandCoor();
        float[] pos = new float[] { startCoor.x, startCoor.y, startCoor.x + ugo_vx, startCoor.y + ugo_vy };
        UGO ugo = new UGO(pos, memory);
        ugo.mutate( settings.mutability );
        world.addParticle(ugo);
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
            int index = loopItInt(genome.performerOn+pos,genome.codons.size());
            if(pos-start < memoryParts.length){
                String memoryPart = memoryParts[pos-start];
                genome.codons.set(index, new Codon( memoryPart ));
                laserCoor.add( genome.getCodonCoor(index, CODON_DIST, x, y) );
            }
            useEnergy();
        }
    }
    
    public void healWall(){
        wall += (1-wall) * E_RECIPROCAL;
    }
    
    public void giveEnergy() {
        energy += (1-energy)*E_RECIPROCAL;
    }
    
    public void laserWall(){
        laserT = frameCount;
        laserCoor.clear();
        for(int i = 0; i < 4; i++){
            laserCoor.add( new Vec2f( x + (i / 2), y + (i % 2) ) );
        }
        laserTarget = null;
    }
  
    public void eat(Particle food){
        if(food.type == ParticleType.FOOD){
            Particle newWaste = new Particle(food.pos, combineVelocity( food.velocity, getRandomVelocity() ), ParticleType.WASTE,-99999);
            shootLaserAt(newWaste);
            world.addParticle( newWaste );
            food.removeParticle(this);
            giveEnergy();
        }else{
            shootLaserAt(food);
        }
    }
    
    void shootLaserAt(Particle food){
        laserT = frameCount;
        laserTarget = food;
    }
    
    public Vec2f getHandCoor(){
        float r = HAND_DIST + ( genome.inwards ? -HAND_LEN : HAND_LEN ) ;
        return genome.getCodonCoor( genome.performerOn, r, x, y );
    }
    
    public void pushOut(Particle particle){
        int[][] dire = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        int chosen = -1;
        int iter = 0;
        
        while( iter < 16 && chosen == -1 ) {
          
            int c = (int) random(0, 4);
            
            if( world.isCellValid( x + dire[c][0], y + dire[c][1] ) && world.cells[ y + dire[c][1] ][ x + dire[c][0] ] == null ) {
                chosen = c;
            }
            
            iter ++;
          
        }
        
        if( chosen == -1 ) return;
         
        Vec2f old = particle.copyCoor();
        for(int dim = 0; dim < 2; dim++){
            if(dire[chosen][dim] == -1){
                particle.pos.set(dim, floor(particle.pos.get(dim))-EPSILON);
                particle.velocity.set(dim, -abs(particle.velocity.get(dim)));
            }else if(dire[chosen][dim] == 1){
                particle.pos.set(dim, ceil(particle.pos.get(dim))+EPSILON);
                particle.velocity.set(dim, abs(particle.velocity.get(dim)));
            }
            particle.loopCoor(dim);
        }
        
        Cell p_cell = world.getCellAt(old.x, old.y);
        if( p_cell != null ) p_cell.removeParticle(particle);
        
        Cell n_cell = world.getCellAt(particle.pos.x, particle.pos.y);
        if( n_cell != null ) n_cell.addParticle(particle);
        
        laserT = frameCount;
        laserTarget = particle;
    }
  
    public void hurtWall(double multi){
        if(type == CellType.Normal) {
            wall -= settings.wall_damage*multi;
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
                Particle newWaste = new Particle( genome.getCodonCoor(i, CODON_DIST, x, y), ParticleType.WASTE, -99999 );
                world.addParticle( newWaste );
            }
        }
        
        if(this == editor.selected){
            editor.close();
        }
        
        if( type == CellType.Shell ){
            world.shellCount --;
        }else if( type == CellType.Normal ) {
            world.aliveCount --;
        }
        
        if( !silent ) world.deadCount ++;
        type = CellType.Empty;
    }
  
    public void addParticle(Particle food){
        pc.get(food.type).add(food);
    }
  
    public void removeParticle(Particle p){
        pc.get( p.type ).remove( p );
    }
  
    public Particle selectParticle(ParticleType type){
        ArrayList<Particle> myList = pc.get(type);
        if(myList.size() == 0){
            
            if( type == ParticleType.WASTE ) {
                return selectParticle( ParticleType.UGO );
            }
          
            return null;
        }else{
            int choiceIndex = (int)(Math.random()*myList.size());
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

enum CellType {
    Empty,
    Locked,
    Normal,
    Shell;
    
    public boolean isAlive() {
         return this == Normal || this == Shell;
    }
    
    public boolean isHurtable() {
        return this == Normal; 
    }
    
}
