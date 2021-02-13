package net.darktree.virus.codon;

import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonRangeArg;
import net.darktree.virus.util.Utils;

import java.util.ArrayList;

public class CodonArgs {

    private static final ArrayList<CodonArg> registry = new ArrayList<>();

    public static CodonArg register( CodonArg arg ) {
        registry.add( arg );
        if( registry.size() != arg.code + 1 ) {
            throw new RuntimeException("Invalid Codon arg ID!");
        }
        return arg;
    }

    public static CodonArg get( int id ) {
        return registry.get( id );
    }

    public static int size() {
        return registry.size();
    }

    // Register all codon arguments
    public static final CodonArg NONE = register( new CodonArg( 0, new CodonMetaInfo( "None", Utils.color(0, 0, 0) ) ) );
    public static final CodonArg FOOD = register( new CodonArg( 1, new CodonMetaInfo( "Food", Utils.color(200, 50, 50) ) ) );
    public static final CodonArg WASTE = register( new CodonArg( 2, new CodonMetaInfo( "Waste", Utils.color(100, 65, 0) ) ) );
    public static final CodonArg WALL = register( new CodonArg( 3, new CodonMetaInfo( "Wall", Utils.color(160, 80, 160) ) ) );
    public static final CodonArg INWARD = register( new CodonArg( 4, new CodonMetaInfo( "Inward", Utils.color(0, 100, 100) ) ) );
    public static final CodonArg OUTWARD = register( new CodonArg( 5, new CodonMetaInfo( "Outward", Utils.color(0, 200, 200) ) ) );
    public static final CodonArg WEAK_LOC = register( new CodonArg( 6, new CodonMetaInfo( "Weak Loc", Utils.color(80, 180, 80) ) ) );
    public static final CodonArg RANGE = register( new CodonRangeArg( 7, new CodonMetaInfo( "RGL", Utils.color(140, 140, 140) ) ) );

}
