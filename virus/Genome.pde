
class GenomeBase {
 
    public ArrayList<Codon> codons = new ArrayList();
    
    public GenomeBase( String dna ) {
        for( String part : dna.split("-") ) {
            codons.add( new Codon( part ) );
        }
    }
    
    public GenomeBase( ArrayList<Codon> codons ) {
        this.codons = codons;
    }
    
    public void drawCodons( float distance ){
        final float codonAngle = 1.0f / max(3, codons.size()) * TWO_PI;
        final float partAngle = codonAngle / 5.0f;
        int i = 0;
        
        for( Codon c : codons ) {
        
            pushMatrix();
            rotate( (i ++) * codonAngle - HALF_PI );
            
            if(c.health < 0.97){
                beginShape();
                fill(TELOMERE_COLOR);
                for(int v = 0; v < TELOMERE_SHAPE.length; v++){
                    final float[] cv = TELOMERE_SHAPE[v];
                    final float ang = cv[0] * partAngle;
                    final float dist = cv[1] * CODON_WIDTH + distance;
                    vertex(cos(ang) * dist, sin(ang) * dist);
                }
                endShape(CLOSE);
            }

            for(int p = 0; p < 2; p++){
                beginShape();
                if( p == 0 ) {
                    fill( c.getBaseColor() );
                }else{
                    fill( c.getArgColor() );
                }
                for(int v = 0; v < CODON_SHAPE.length; v++){
                    final float[] cv = CODON_SHAPE[v];
                    final float ang = cv[0] * partAngle * c.health;
                    final float dist = cv[1] * (2 * p - 1) * CODON_WIDTH + distance;
                    vertex(cos(ang) * dist, sin(ang) * dist);
                }    
                endShape(CLOSE);
            }
            
            popMatrix();
          
        }
    }

    void hurtCodons( Cell cell ){
        for(int i = 0; i < codons.size(); i++){
            Codon c = codons.get(i);
            if(c.hasSubstance()){
                if( c.hurt() ) {
                    if( cell != null ) {
                        Particle newWaste = new Particle( getCodonCoor(i, CODON_DIST, cell.x, cell.y), ParticleType.Waste, -99999 );
                        world.addParticle( newWaste );
                    }
                  
                    codons.remove(i);
                    return;
                }
            }
        }
    }
    
    public double[] getCodonCoor(int i, double r, int x, int y){
        final double theta = (float)(i*TWO_PI)/(codons.size())-HALF_PI;
        final double r2 = r/BIG_FACTOR;
        final double cx = x+0.5+r2*Math.cos(theta);
        final double cy = y+0.5+r2*Math.sin(theta);
        return new double[] {cx, cy};
    }
    
    public String asDNA( int length ) {
        length = clamp( length, 0, codons.size() );
        String dna = "";
      
        for(int i = 0; i < length; i++){
           dna += codons.get(i).asDNA() + "-";
        }
        
        return dna.substring(0, dna.length() - 1);
    }
    
    public String asDNA() {
        return asDNA( codons.size() );
    }
  
}

class Genome extends GenomeBase {
  
    int rotateOn = 0;
    int performerOn = 0;
    boolean inwards = false;
    double appRO = 0;
    double appPO = 0;
    double appDO = 0;
  
    public Genome( String dna ){
        super( dna );
    }
    
    public Genome( ArrayList<Codon> codons ) {
        super( codons );
    }
  
    public Codon getSelected() {
        if( codons.size() == 0 ) {
            return null;
        }
        return codons.get(rotateOn);
    }
  
    public void next() {
        int s = codons.size();
        rotateOn = ((s == 0) ? 0 : ((rotateOn + 1) % s)); 
    }
    
    public void mutate( double m ) {
        
        if( m > random(0, 1) ) {
            
            if( random(0, 1) < 0.3 && codons.size() > 1 ) { // delete
                codons.remove( (int) random( 0, codons.size() ) );
                return;
            }
            
            if( random(0, 1) < 0.4 ) { // replace
                CodonBase base = Codons.rand();
                CodonArg arg = base.getRandomArg();
                codons.set( (int) random( 0, codons.size() ), new Codon( base, arg ) );
                return;
            }
            
            if( random(0, 1) < 0.5 ) { // add
                CodonBase base = Codons.rand();
                CodonArg arg = base.getRandomArg();
                codons.add( new Codon( base, arg ) );
                return;
            }
          
            if( random(0, 1) < 0.6 ) { // swap
            
                int a = (int) random( 0, codons.size() );
                int b = (int) random( 0, codons.size() );
            
                if( a != b ) {
            
                    Codon ca = codons.get(a);
                    Codon cb = codons.get(b);
                
                    codons.set(a, cb);
                    codons.set(b, ca);
                    return;
              
                }
              
            }
          
        }
      
    }
  
    public void update(){
        int s = codons.size();
        if( s != 0 ) {
            appRO += loopIt( rotateOn % settings.codons_per_page - appRO, s, true) * VISUAL_TRANSITION * PLAY_SPEED;
            appPO += loopIt( performerOn-appPO, s, true) * VISUAL_TRANSITION * PLAY_SPEED;
            appDO += ((inwards?1:0) - appDO) * VISUAL_TRANSITION * PLAY_SPEED;
            appRO = loopIt( appRO, s, false);
            appPO = loopIt( appPO, s, false);
        }else{
            appRO = 0;
            appPO = 0;
        }
    }
    
    public void drawHand(){
        double appPOAngle = (float)(appPO*2*PI/codons.size());
        double appDOAngle = (float)(appDO*PI);
        strokeWeight(1);
        noFill();
        stroke(transperize(HAND_COLOR,0.5));
        ellipse(0,0,HAND_DIST*2,HAND_DIST*2);
        pushMatrix();
        rotate((float)appPOAngle);
        translate(0,-HAND_DIST);
        rotate((float)appDOAngle);
        noStroke();
        fill(HAND_COLOR);
        beginShape();
        vertex(5,0);
        vertex(-5,0);
        vertex(0,-HAND_LEN);
        endShape(CLOSE);
        popMatrix();
    }
  
    int getWeakestCodon(){
        double record = 9999;
        int holder = -1;
        for(int i = 0; i < codons.size(); i++){
            double val = codons.get(i).health;
            if(val < record){
                record = val;
                holder = i;
            }
        }
        return holder;
    }
  
    String getGenomeString(){
        return asDNA();
    }
  
    String getGenomeStringShortened(){
        return asDNA( codons.size() - 1 );
    }
  
    String getGenomeStringLengthened(){
        return asDNA() + "-Aa";
    }
    
}
