class Cell{
  
    int x;
    int y;
    CellType type;
    float wall;
    double energy = 0;
    Genome genome;
    double geneTimer = 0;
    boolean tampered = false;
    ParticleContainer pc = new ParticleContainer();
    ArrayList<float[]> laserCoor = new ArrayList<float[]>();
    Particle laserTarget = null;
    int laserT = -9999;
    String memory = "";
    int dire;
  
    public Cell(int ex, int ey, CellType et, int ed, double ewh, String eg){
        x = ex;
        y = ey;
        type = et;
        dire = ed;
        wall = (float) ewh;
        genome = new Genome(eg);
        genome.rotateOn = (int)(Math.random()*genome.codons.size());
        geneTimer = Math.random()*settings.gene_tick_time;
        energy = 0.5;
    }
  
    public String getMemory() {
        if(memory.length() == 0){
            return "[NOTHING]";
        }else{
            return "\"" + memory + "\"";
        }
    }
    
    public boolean hasGenome() {
        return type == CellType.Normal; 
    }
    
    public boolean isHandInwards() {
        return genome.inwards;
    }
    
    private void drawCellBackground( color back ) {
        fill(COLOR_CELL_WALL);
        rect(0,0,BIG_FACTOR,BIG_FACTOR);
        fill( back );
        float w = BIG_FACTOR * 0.08 * wall;
        rect(w, w, BIG_FACTOR - 2 * w, BIG_FACTOR - 2 * w);
    }
  
    void draw() {
      
        float posx = renderer.trueXtoAppX(x);
        float posy = renderer.trueYtoAppY(y);
      
        if( posx < -renderer.camS || posy < -renderer.camS || posx > renderer.maxRight || posy > height ) {
            return;
        }
    
        pushMatrix();
        translate( posx, posy );
        scale( renderer.camS / BIG_FACTOR );
        noStroke();
        
        if(type == CellType.Locked){
          
            fill(COLOR_CELL_LOCKED);
            rect(0, 0, BIG_FACTOR, BIG_FACTOR);
          
        }else if(type == CellType.Normal){
          
            drawCellBackground( (tampered && settings.show_tampered) ? COLOR_CELL_TAMPERED : COLOR_CELL_BACK );
        
            pushMatrix();
            translate(BIG_FACTOR * 0.5, BIG_FACTOR * 0.5);
            stroke(0);
            strokeWeight(1);
      
            if(renderer.camS > DETAIL_THRESHOLD) {
                drawInterpreter();
                noStroke();
                genome.drawCodons(CODON_DIST);
            }
      
            drawEnergy();
            genome.drawHand();
            popMatrix();
          
        }else if( type == CellType.Shell ) {
          
            drawCellBackground( COLOR_CELL_BACK );
      
            pushMatrix();
            translate(BIG_FACTOR*0.5,BIG_FACTOR*0.5);
            stroke(0);
            strokeWeight(1);
            drawEnergy();
            popMatrix();
        }
    
        popMatrix();
    
        if(type == CellType.Normal){
            drawLaser();
        }
      
    }
  
    public void drawInterpreter(){
    
        int GENOME_LENGTH = genome.codons.size();
        double CODON_ANGLE = (double)(1.0)/GENOME_LENGTH*2*PI;
        double INTERPRETER_SIZE = 23;
        double col = 1;
        double gtf = geneTimer/settings.gene_tick_time;
        
        if(gtf < 0.5){
            col = Math.min(1,(0.5-gtf)*4);
        }
        
        pushMatrix();
        rotate((float)(-PI/2+CODON_ANGLE*genome.appRO));
        fill((float)(col*255));
        beginShape();
        strokeWeight(BIG_FACTOR*0.01);
        stroke(80);
        vertex(0,0);
        vertex((float)(INTERPRETER_SIZE*Math.cos(CODON_ANGLE*0.5)),(float)(INTERPRETER_SIZE*Math.sin(CODON_ANGLE*0.5)));
        vertex((float)(INTERPRETER_SIZE*Math.cos(-CODON_ANGLE*0.5)),(float)(INTERPRETER_SIZE*Math.sin(-CODON_ANGLE*0.5)));
        endShape(CLOSE);
        popMatrix();
    }
  
    public void drawLaser(){
        if(frameCount < laserT+settings.laser_linger_time){
            double alpha = (double)((laserT+settings.laser_linger_time)-frameCount)/settings.laser_linger_time;
            stroke(transperize(COLOR_HAND,alpha));
            strokeWeight((float)(0.033333*BIG_FACTOR));
            float[] handCoor = getHandCoor();
            if(laserTarget == null){
                for(float[] singleLaserCoor : laserCoor){
                    renderer.scaledLine(handCoor, singleLaserCoor);
                }
            }else{
                if( dist((float)handCoor[0], (float)handCoor[1], (float)laserTarget.coor[0], (float)laserTarget.coor[1]) < 2 ) {
                    renderer.scaledLine(handCoor, laserTarget.coor);
                }
            }
        }
    }
  
    public void drawEnergy(){
        noStroke();
        fill(0,0,0);
        ellipse(0,0,17,17);
    
        if( energy > 0 ) {
            fill(255,255,0);
            ellipseMode(CENTER);
            ellipse(0, 0, 12 * (float) energy + 2, 12 * (float) energy + 2);
        }
    }
  
    public void tick(){
        if(type == CellType.Normal){
            if(energy > 0){
                double oldGT = geneTimer;
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
  
    void useEnergy( double amount ){
        energy = Math.max(0, energy - amount);
    }
  
    void readToMemory(int start, int end){
      
        memory = "";
        laserTarget = null;
        laserCoor.clear();
        laserT = frameCount;
        
        for(int pos = start; pos <= end; pos++){
            int index = loopItInt(genome.performerOn+pos,genome.codons.size());
            Codon c = genome.codons.get(index);
            memory = memory + c.asDNA();//infoToString(c.info);
            if(pos < end){
                memory = memory+"-";
            }
            laserCoor.add(genome.getCodonCoor(index,CODON_DIST,x,y));
        }
    }
    
    public void writeFromMemory(int start, int end){
        if(memory.length() == 0) return;
        laserTarget = null;
        laserCoor.clear();
        laserT = frameCount;
        if( genome.inwards ){
            writeInwards(start,end);
        }else{
            writeOutwards();
        }
    }
    
    private void writeOutwards() {
        float theta = (float) Math.random() * 2 * PI;
        float ugo_vx = cos(theta);
        float ugo_vy = sin(theta);
        float[] startCoor = getHandCoor();
        float[] newUGOcoor = new float[]{startCoor[0],startCoor[1],startCoor[0]+ugo_vx,startCoor[1]+ugo_vy};
        UGO ugo = new UGO(newUGOcoor, memory);
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
            //Codon c = genome.codons.get(index);
            if(pos-start < memoryParts.length){
                String memoryPart = memoryParts[pos-start];
                //c.setFullInfo(stringToInfo(memoryPart));
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
            float[] result = {x+(i/2), y+(i%2)};
            laserCoor.add(result);
        }
        laserTarget = null;
    }
  
    public void eat(Particle food){
        if(food.type == ParticleType.Food){
            Particle newWaste = new Particle(food.coor, combineVelocity( food.velo, getRandomVelocity() ), ParticleType.Waste,-99999);
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
    
    public float[] getHandCoor(){
        float r = HAND_DIST;
        if( genome.inwards ){
            r -= HAND_LEN;
        }else{
            r += HAND_LEN;
        }
        return genome.getCodonCoor(genome.performerOn,r,x,y);
    }
    
    public void pushOut(Particle waste){
        int[][] dire = {{0,1},{0,-1},{1,0},{-1,0}};
        int chosen = -1;
        int iter = 0;
        
        while( iter < 64 && chosen == -1 ) {
          
            int c = (int) random(0, 4);
            
            if( world.isCellValid( x + dire[c][0], y + dire[c][1] ) && world.cells[ y + dire[c][1] ][ x + dire[c][0] ] == null ) {
                chosen = c;
            }
            
            iter ++;
          
        }
        
        if( chosen == -1 ) return;
         
        float[] oldCoor = waste.copyCoor();
        for(int dim = 0; dim < 2; dim++){
            if(dire[chosen][dim] == -1){
                waste.coor[dim] = floor(waste.coor[dim])-EPSILON;
                waste.velo[dim] = -abs(waste.velo[dim]);
            }else if(dire[chosen][dim] == 1){
                waste.coor[dim] = ceil(waste.coor[dim])+EPSILON;
                waste.velo[dim] = abs(waste.velo[dim]);
            }
            waste.loopCoor(dim);
        }
        
        Cell p_cell = world.getCellAt(oldCoor[0], oldCoor[1]);
        if( p_cell != null ) p_cell.removeParticle(waste);
        
        Cell n_cell = world.getCellAt(waste.coor[0], waste.coor[1]);
        if( n_cell != null ) n_cell.addParticle(waste);
        
        laserT = frameCount;
        laserTarget = waste;
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
                Particle newWaste = new Particle( genome.getCodonCoor(i, CODON_DIST, x, y), ParticleType.Waste, -99999 );
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
            
            if( type == ParticleType.Waste ) {
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
