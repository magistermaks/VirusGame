class UGO extends Particle {
  
    GenomeBase genome;
    boolean divine = false;
    
    // TODO: temporary fix, remove later
    // deprected
    public UGO( float[] pos, String data ) {
        super( new Vec2f( pos[0], pos[1] ), ParticleType.UGO, frameCount );
        genome = new GenomeBase( data );
        Vec2f coor = new Vec2f( pos[2] - pos[0], pos[3] - pos[1] );

        float dist = sqrt(coor.x * coor.x + coor.y * coor.y);
        float sp = dist * ( SPEED_HIGH - SPEED_LOW ) + SPEED_LOW;
        velocity = new Vec2f( coor.x / dist * sp, coor.y / dist * sp );
        world.totalUGOCount ++;
    }
 
    public UGO( Vec2f coor, String data ) {
        super( coor, ParticleType.UGO, frameCount );
        genome = new GenomeBase( data );

        float dist = sqrt(coor.x * coor.x + coor.y * coor.y);
        float sp = dist * ( SPEED_HIGH - SPEED_LOW ) + SPEED_LOW;
        velocity = new Vec2f( coor.x / dist * sp, coor.y / dist * sp );
        world.totalUGOCount ++;
    }
    
    public void markDivine() {
         divine = true;
    }
    
    public void mutate( double mutability ) {
         //genome.mutate( mutability );
    }
    
    public void tick() {
        super.tick();
        
        if( frameCount % settings.gene_tick_time == 0 ) {
            genome.hurtCodons(null);
            if( genome.codons.size() == 0 ) {
                removeParticle( world.getCellAt(pos.x, pos.y) ); 
                Particle p = new Particle( pos, velocity, ParticleType.WASTE, -99999 );
                world.addParticle( p );
            }
        }
    }
    
    color getColor() {
        return 0;
    }
    
    void draw() {
      
        float posx = renderer.trueXtoAppX(pos.x);
        float posy = renderer.trueYtoAppY(pos.y);
                
        if( posx > 0 && posy > 0 && posx < width && posy < width ) {
          
            super.draw();
            if( renderer.camS > DETAIL_THRESHOLD && genome != null ) genome.drawCodons(CODON_DIST_UGO);
        
        }
    
    }
    
    protected boolean interact( Vec2f future, CellType ct, CellType ft ) {
      
        Cell fc = world.getCellAt(future.x, future.y);
        if( fc != null ) {
          
            if( divine || fc.wall * settings.cell_wall_protection < random(0,1) || fc.type == CellType.Shell ) {
                
                if(type == ParticleType.UGO && ct == CellType.Empty && ft == CellType.Normal && genome.codons.size()+fc.genome.codons.size() <= settings.max_codon_count){
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
            c.genome.rotateOn = 0;
            c.genome.performerOn = 0;
            world.shellCount --;
            world.aliveCount ++;
                
        }else{
              
            int injectionLocation = c.genome.rotateOn;
            ArrayList<Codon> toInject = genome.codons;
            int size = genome.codons.size();
    
            for(int i = 0; i < toInject.size(); i++){
                c.genome.codons.add( injectionLocation+i, new Codon( toInject.get(i) ) );
            }
                
            if(c.genome.performerOn >= c.genome.rotateOn){
                c.genome.performerOn += size;
            }
                
            c.genome.rotateOn += size;
        }
            
        if( !c.tamper() ) world.infectedCount ++;
        removeParticle( world.getCellAt(pos.x, pos.y) );
        Particle p = new Particle(pos, combineVelocity( this.velocity, getRandomVelocity() ), ParticleType.WASTE, -99999);
        world.addParticle( p );
            
        return true;
        
    }

}
