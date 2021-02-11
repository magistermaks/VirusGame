class UGO extends Particle {
  
    GenomeBase genome;
    boolean divine = false;
 
    public UGO( float[] coor, String data ) {
        super( coor, ParticleType.UGO, frameCount );
        genome = new GenomeBase( data );
        
        float dx = coor[2] - coor[0];
        float dy = coor[3] - coor[1];
        float dist = sqrt(dx * dx + dy * dy);
        float sp = dist * ( SPEED_HIGH - SPEED_LOW ) + SPEED_LOW;
        velo = new float[]{ dx / dist * sp, dy / dist * sp};
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
                removeParticle( world.getCellAt(coor[0], coor[1]) ); 
                Particle p = new Particle( coor, velo, ParticleType.Waste, -99999 );
                world.addParticle( p );
            }
        }
    }
    
    color getColor() {
        return 0;
    }
    
    void draw() {
      
        float posx = renderer.trueXtoAppX(coor[0]);
        float posy = renderer.trueYtoAppY(coor[1]);
                
        if( posx > 0 && posy > 0 && posx < width && posy < width ) {
          
            super.draw();
            if( renderer.camS > DETAIL_THRESHOLD && genome != null ) genome.drawCodons(CODON_DIST_UGO);
        
        }
    
    }
    
    protected boolean interact( float[] future, CellType ct, CellType ft ) {
      
        Cell fc = world.getCellAt(future[0], future[1]);
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
        removeParticle( world.getCellAt(coor[0], coor[1]) );
        Particle p = new Particle(coor, combineVelocity( this.velo, getRandomVelocity() ), ParticleType.Waste, -99999);
        world.addParticle( p );
            
        return true;
        
    }

}
