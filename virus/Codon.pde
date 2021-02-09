class CodonState {
    public String str;
    public color col;
  
    public CodonState( String str, color col ) {
        this.str = str;
        this.col = col;
    }
}

class CodonArg {
    public final int code;
    protected final CodonState state;
    
    public CodonArg( int code, CodonState state ) {
        this.code = code;
        this.state = state;
    }
    
    public String getText() {
        return state.str; 
    }
    
    public color getColor() {
        return state.col; 
    }
    
    public String asDNA() {
        return "" + ((char) (((int) 'a') + code)); 
    }
    
    public boolean is( CodonArg arg ) {
         return this == arg;
    }
    
    public CodonArg clone() {
        // no cloning needed for stateless arguments
        return this;
    }
}

abstract class ComplexCodonArg extends CodonArg {
    protected String param;
    
    public ComplexCodonArg( int id, CodonState state ) {
        super( id, state );
    }
    
    public void setParam( String param ) {
        this.param = param;
    }
    
    public String getParam() {
        return this.param;
    }
    
    public String asDNA() {
        return super.asDNA() + param;
    }
    
    public abstract CodonArg clone();
    
    public abstract boolean is( CodonArg arg );
}

class CodonRangeArg extends ComplexCodonArg {
    public int start = 0;
    public int end = 0;
  
    public CodonRangeArg( int id, CodonState state ) {
        super( id, state );
    }
    
    public void setParam( String param ) {
        super.setParam( param );
        try{
            start = clamp( Integer.parseInt( param.substring(0, 2) ), 0, 40 ) - 20;
            end = clamp( Integer.parseInt( param.substring(2) ), 0, 40 ) - 20;
        }catch(Exception ex) {
            println( "Failed to parse Codon arg param: '" + param + "'!" );
            start = end = 0;
        }
    }
    
    public String getText() {
        return super.getText() + " (" + start + " to " + end + ")";
    }
  
    public CodonArg clone() {
        return new CodonRangeArg( code, state );
    }
    
    public boolean is( CodonArg arg ) {
        return this == arg || arg instanceof CodonRangeArg;
    }
}

abstract class CodonBase {
    public int code;
    public CodonArg[] args;
    public CodonState state;
    
    public CodonBase( int code, CodonArg[] args, CodonState state ) {
        this.code = code;
        this.args = args;
        this.state = state;
    }
 
    public String getText() {
        return state.str; 
    }
    
    public color getColor() {
        return state.col; 
    }
    
    public CodonArg[] getArgs() {
        return args;
    }
    
    public CodonArg getRandomArg() {
        return args[ (int) random( args.length ) ];
    }
    
    public String asDNA() {
        return "" + (char) (((int) 'A') + code); 
    }
    
    public abstract void tick( Cell cell, CodonArg arg );
}

class Codon {
    public float health = 1.0;
    public CodonArg arg;
    public CodonBase base;
    
    public Codon( Codon codon ) {
        this( codon.base, codon.arg );
        this.health = codon.health;
    }
    
    public Codon( CodonBase base, CodonArg arg ) {
        this.base = base;
        this.arg = arg;
    }
    
    public Codon() {
        this( Codons.None, CodonArgs.None );
    }
    
    public Codon( String dna ) {
      
        try{
            int codonCode = (int) dna.charAt(0) - (int) 'A';
            int argCode = (int) dna.charAt(1) - (int) 'a';
        
            base = Codons.get( codonCode );
            CodonArg arg = CodonArgs.get( argCode ).clone();
        
            if( arg instanceof ComplexCodonArg ) {
                ((ComplexCodonArg) arg).setParam( dna.substring(2) );
            }
            
            setArg( arg ); 
        }catch(Exception ex) {
            println( "Failed to create Codon from genome: '" + dna + "'!" );
            this.arg = CodonArgs.None;
            this.base = Codons.None;
        }
        
    }
    
    public String getArgText() {
        return arg.getText();
    }
    
    public String getBaseText() {
        return base.getText();
    }
    
    public color getArgColor() {
        return arg.getColor();
    }
    
    public color getBaseColor() {
        return base.getColor();
    }
    
    public CodonArg[] getArgs() {
        return base.getArgs(); 
    }
    
    public String asDNA() {
        return base.asDNA() + arg.asDNA(); 
    }
    
    public boolean hasSubstance() {
        return (base != Codons.None) || (arg != CodonArgs.None);
    }
    
    public void setArg( CodonArg arg ) {
        boolean flag = false;
      
        for( CodonArg ca : base.args ) {
            if( ca.is(arg) ) {
                flag = true;
                break;
            }
        }
        
        if( flag ) {
            this.arg = arg; 
        }else{
            this.arg = CodonArgs.None;
        }
    }
    
    public void setBase( CodonBase base ) {
        this.base = base;
        
        for( CodonArg ca : base.getArgs() ) {
            if( ca.is(this.arg) ) return; 
        }
        
        this.arg = CodonArgs.None;
    }
    
    public boolean hurt() {
        if( hasSubstance() ) {
            health -= Math.random() * settings.codon_degrade_speed;
            if(health <= 0) {
                health = 1;
                arg = CodonArgs.None;
                base = Codons.None;
                return true;
            }
        }
        return false;
    }
    
    public void tick( Cell cell ) {
        base.tick( cell, arg ); 
    }
}

class CodonArgsClass {
    ArrayList<CodonArg> registry = new ArrayList();
    
    CodonArg register( CodonArg arg ) {
        registry.add( arg );
        if( registry.size() != arg.code + 1 ) {
            throw new RuntimeException("Invalid Codon arg ID!");
        }
        return arg;
    }
    
    CodonArg get( int id ) {
        return registry.get( id );
    }
    
    int size() {
         return registry.size();
    }
    
    // Register all codon arguments
    public final CodonArg None = register( new CodonArg( 0, new CodonState( "None", color(0, 0, 0) ) ) );
    public final CodonArg Food = register( new CodonArg( 1, new CodonState( "Food", color(200, 50, 50) ) ) );
    public final CodonArg Waste = register( new CodonArg( 2, new CodonState( "Waste", color(100, 65, 0) ) ) );
    public final CodonArg Wall = register( new CodonArg( 3, new CodonState( "Wall", color(160, 80, 160) ) ) );
    public final CodonArg Inward = register( new CodonArg( 4, new CodonState( "Inward", color(0, 100, 100) ) ) );
    public final CodonArg Outward = register( new CodonArg( 5, new CodonState( "Outward", color(0, 200, 200) ) ) );
    public final CodonArg WeakLoc = register( new CodonArg( 6, new CodonState( "Weak Loc", color(80, 180, 80) ) ) );
    public final CodonArg Range = register( new CodonRangeArg( 7, new CodonState( "RGL", color(140, 140, 140) ) ) );
} 

class CodonsClass {
    ArrayList<CodonBase> registry = new ArrayList();
    
    public CodonBase register( CodonBase arg ) {
        registry.add( arg );
        if( registry.size() != arg.code + 1 ) {
            throw new RuntimeException("Invalid Codon ID!");
        }
        return arg;
    }
    
    public CodonBase get( int id ) {
        return registry.get( id );
    }
    
    // deprected
    public CodonBase rand() {
        return get( (int) random( registry.size() ) );
    }
    
    public int size() {
        return registry.size();
    }
  
    // Register all codons
    public final CodonBase None = register( new CodonNone( 0, new CodonState( "None", color(0, 0, 0) ) ) );
    public final CodonBase Digest = register( new CodonDigest( 1, new CodonState( "Digest", color(100, 0, 200) ) ) );
    public final CodonBase Remove = register( new CodonRemove( 2, new CodonState( "Remove", color(180, 160, 10) ) ) );
    public final CodonBase Repair = register( new CodonRepair( 3, new CodonState( "Repair", color(0, 150, 0) ) ) );
    public final CodonBase MoveHand = register( new CodonMoveHand( 4, new CodonState( "Move Hand", color(200, 0, 100) ) ) );
    public final CodonBase Read = register( new CodonRead( 5, new CodonState( "Read", color(70, 70, 255) ) ) );
    public final CodonBase Write = register( new CodonWrite( 6, new CodonState( "Write", color(0, 0, 220) ) ) );
}

/////////////////////////////////////
/// BEGIN CODON BASES DEFINITIONS ///
/////////////////////////////////////

class CodonNone extends CodonBase {
    
    public CodonNone( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        // nop
    }
    
}

class CodonDigest extends CodonBase {
    
    public CodonDigest( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Food, CodonArgs.Waste, CodonArgs.Wall }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) { 
        if( !cell.isHandInwards() ) {
            if(arg == CodonArgs.Wall){
                cell.hurtWall(25);
                cell.laserWall();
                cell.useEnergy( (1 - cell.energy) * E_RECIPROCAL * -0.2 );
            }else{
                Particle p = null;
                cell.useEnergy();
              
                if(arg == CodonArgs.Food) {
                    p = cell.selectParticle( ParticleType.Food );
                }else if(arg == CodonArgs.Waste) {
                    p = cell.selectParticle( ParticleType.Waste );
                }
              
                if(p != null) cell.eat(p);
            }
        }
    }
    
}

class CodonRemove extends CodonBase {
    
    public CodonRemove( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Food, CodonArgs.Waste, CodonArgs.Wall }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        if( !cell.isHandInwards() ){
            if(arg == CodonArgs.Waste){
                Particle p = cell.selectParticle( ParticleType.Waste );
                if(p != null) cell.pushOut(p); 
                cell.useEnergy();
            }else if(arg == CodonArgs.Food) {
                Particle p = cell.selectParticle( ParticleType.Food );
                if(p != null) cell.pushOut(p);
                cell.useEnergy();
            }else if(arg == CodonArgs.Wall){
                cell.die(false);
            }
        }
    }
    
}

class CodonRepair extends CodonBase {
    
    public CodonRepair( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Wall }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        if( !cell.isHandInwards() ){
            if(arg == CodonArgs.Wall){
                cell.healWall();
                cell.laserWall();
                cell.useEnergy();
            }
        }
    }
    
}

class CodonMoveHand extends CodonBase {
  
    public CodonMoveHand( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Inward, CodonArgs.Outward, CodonArgs.WeakLoc, CodonArgs.Range }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        if(arg == CodonArgs.WeakLoc){
            cell.genome.performerOn = cell.genome.getWeakestCodon();
        }else if(arg == CodonArgs.Inward){
            cell.genome.inwards = true;
        }else if(arg == CodonArgs.Outward){
            cell.genome.inwards = false;
        }else if(arg instanceof CodonRangeArg){
            cell.genome.performerOn = loopItInt(cell.genome.rotateOn + ((CodonRangeArg) arg).start, cell.genome.codons.size());
        }
    }
  
}

class CodonRead extends CodonBase {
 
    public CodonRead( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Range }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        if( cell.isHandInwards() && arg instanceof CodonRangeArg ){
            CodonRangeArg carg = (CodonRangeArg) arg;
            cell.readToMemory( carg.start, carg.end );
            cell.useEnergy();
        }
    }
  
}

class CodonWrite extends CodonBase {
 
    public CodonWrite( int code, CodonState state ) {
        super( code, new CodonArg[] { CodonArgs.None, CodonArgs.Range }, state ); 
    }
    
    public void tick( Cell cell, CodonArg arg ) {
        if( arg instanceof CodonRangeArg ) {
            CodonRangeArg carg = (CodonRangeArg) arg;
            cell.writeFromMemory( carg.start, carg.end );
            cell.useEnergy();
        }
    }
  
}
