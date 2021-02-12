class Particle{
  
    protected Vec2f pos;
    protected Vec2f velocity;
    protected boolean removed = false;
    protected int birthFrame;
    protected ParticleType type;
  
    public Particle(Vec2f pos, ParticleType type, int b){
        this(pos, getRandomVelocity(), type, b);
    }
    
    public Particle(Vec2f pos, Vec2f velocity, ParticleType type, int b){
        this.pos = pos;
        this.velocity = velocity;
        this.type = type;
        this.birthFrame = b;
         
        if( type == ParticleType.FOOD ) world.totalFoodCount ++; else
        if( type == ParticleType.WASTE ) world.totalWasteCount ++;
    }
    
    color getColor() {
        return type == ParticleType.FOOD ? COLOR_FOOD : COLOR_WASTE;
    }
  
    void draw() {
      
        float posx = renderer.trueXtoAppX(pos.x);
        float posy = renderer.trueYtoAppY(pos.y);
                        
        if( posx > 0 && posy > 0 && posx < renderer.maxRight && posy < height ) {
          
            translate( posx, posy );
            double ageScale = Math.min(1.0, (frameCount - birthFrame) * settings.age_grow_speed);
            scale( (float) (renderer.camS / BIG_FACTOR * ageScale) );
            noStroke();
            fill( getColor() );
            ellipseMode(CENTER);
            ellipse(0, 0, 0.1 * BIG_FACTOR, 0.1 * BIG_FACTOR);
        
        }
    
    }
    
    public void tick() {
        Vec2f future = new Vec2f();
        CellType ct = world.getCellTypeAt(pos.x, pos.y);
        
        if( ct == CellType.Locked ) removeParticle( world.getCellAt(pos.x, pos.y) );
        
        float visc = ct == CellType.Empty ? 1 : 0.5;
        
        future.x = pos.x + velocity.x * visc * PLAY_SPEED;
        future.y = pos.y + velocity.y * visc * PLAY_SPEED;
        
        boolean cta = floor(pos.x) != floor(future.x);
        boolean ctb = floor(pos.y) != floor(future.y);
            
        if( cta || ctb ) {
            
            CellType ft = world.getCellTypeAt(future.x, future.y);
            
            if( interact( future, ct, ft ) ) return;
            
            if(ft == CellType.Locked || (type != ParticleType.FOOD && (ct != CellType.Empty || ft != CellType.Empty))) {
        
                Cell b_cell = world.getCellAt(future.x, future.y);
                if(b_cell != null && b_cell.type.isHurtable()){
                    b_cell.hurtWall( cta && ctb ? 2 : 1 );
                }
                
                if( cta ) {
                    if(velocity.x >= 0){
                        future.x = ceil(pos.x) - EPSILON;
                    }else{
                        future.x = floor(pos.x) + EPSILON;
                    } 
                    
                    velocity.x = -velocity.x;
                }
            
                if( ctb ) {
                    if(velocity.y >= 0){
                        future.y = ceil(pos.y) - EPSILON;
                    }else{
                        future.y = floor(pos.y) + EPSILON;
                    }
                    
                    velocity.y = -velocity.y;
                }
                
                Cell t_cell = world.getCellAt(pos.x, pos.y);
                if(t_cell != null && t_cell.type.isHurtable()){
                    t_cell.hurtWall( cta && ctb ? 2 : 1 );
                }
            
            }else{
              
                if(future.x >= settings.world_size) { future.x -= settings.world_size; border(); } else
                if(future.x < 0) { future.x += settings.world_size; border(); } else
                if(future.y >= settings.world_size) { future.y -= settings.world_size; border(); } else
                if(future.y < 0) { future.y += settings.world_size; border(); }
                
                hurtWall( pos, false );
                hurtWall( future, true );
            }
            
        }
        
        pos = future;
          
    }
    
    public void randomTick() {
        if( type == ParticleType.WASTE ) {
            if( random(0, 1) < settings.waste_disposal_chance_random && world.getCellAt(pos.x, pos.y) == null ) removeParticle(null);
        }
    }
    
    private void border() {
        if( type == ParticleType.WASTE ) {
            if( world.pc.wastes.size() > settings.max_waste && random(0, 1) < settings.waste_disposal_chance_high ) removeParticle(null);
            if( random(0, 1) < settings.waste_disposal_chance_low ) removeParticle(null);
        }
    }
    
    public Vec2f copyCoor(){
        return pos.copy();
    }
    
    protected void hurtWall(Vec2f pos, boolean add) {
        Cell cell = world.getCellAt(pos.x, pos.y);
        if( cell != null ) {
            if(cell.type.isHurtable()){
                cell.hurtWall(1);
            }
            
            if( add ) {
                cell.addParticle(this);
            }else{
                cell.removeParticle(this);
            }
        }
    }
    
    public void removeParticle( Cell c ) {
         removed = true;
         if(c != null) c.removeParticle(this);
    }
  
    public void addToCellList(){
        Cell c = world.getCellAt(pos.x, pos.y);
        if( c != null ) c.addParticle(this);
    }
    
    protected boolean interact( Vec2f future, CellType cType, CellType fType ) {
        return false;
    }
  
    // TODO: remove this 'thing'
    public void loopCoor(int d){
        if( d == 0 ) {
            while(pos.x >= settings.world_size){
                pos.x -= settings.world_size;
            }
    
            while(pos.x < 0){
                pos.x += settings.world_size;
            }
        }else{
            while(pos.y >= settings.world_size){
                pos.y -= settings.world_size;
            }
    
            while(pos.y < 0){
                pos.y += settings.world_size;
            }
        }
    }
}

enum ParticleType {
    FOOD,
    WASTE,
    UGO;
    
    public static ParticleType fromId( int id ) {
        switch(id){
            case 0: return ParticleType.FOOD;
            case 1: return ParticleType.WASTE;
            case 2: return ParticleType.UGO;
        }
        return null;
    }
}
