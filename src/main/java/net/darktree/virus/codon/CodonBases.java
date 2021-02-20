package net.darktree.virus.codon;

import net.darktree.virus.Main;
import net.darktree.virus.codon.base.*;
import net.darktree.virus.util.Utils;

import java.util.ArrayList;

public class CodonBases {

    public static ArrayList<CodonBase> registry = new ArrayList<>();

    public static CodonBase register( CodonBase base ) {
        registry.add( base );
        if( registry.size() != base.code + 1 ) {
            throw new RuntimeException("Invalid Codon ID!");
        }
        return base;
    }

    public static CodonBase get(int id ) {
        return registry.get( id );
    }

    public static CodonBase rand() {
        return get( (int) Main.applet.random( registry.size() ) );
    }

    public static int size() {
        return registry.size();
    }

    // Register all codons
    public static final CodonBase NONE = register( new CodonBaseNone( 0, new CodonMetaInfo( "None", Utils.color(0, 0, 0) ) ) );
    public static final CodonBase DIGEST = register( new CodonBaseDigest( 1, new CodonMetaInfo( "Digest", Utils.color(100, 0, 200) ) ) );
    public static final CodonBase REMOVE = register( new CodonBaseRemove( 2, new CodonMetaInfo( "Remove", Utils.color(180, 160, 10) ) ) );
    public static final CodonBase REPAIR = register( new CodonBaseRepair( 3, new CodonMetaInfo( "Repair", Utils.color(0, 150, 0) ) ) );
    public static final CodonBase MOVE_HAND = register( new CodonBaseMoveHand( 4, new CodonMetaInfo( "Move Hand", Utils.color(200, 0, 100) ) ) );
    public static final CodonBase READ = register( new CodonBaseRead( 5, new CodonMetaInfo( "Read", Utils.color(70, 70, 255) ) ) );
    public static final CodonBase WRITE = register( new CodonBaseWrite( 6, new CodonMetaInfo( "Write", Utils.color(0, 0, 220) ) ) );

}
