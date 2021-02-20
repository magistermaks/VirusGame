package net.darktree.virus.codon.base;

import net.darktree.virus.Main;
import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;

public abstract class CodonBase {

    public int code;
    public CodonArg[] args;
    public CodonMetaInfo info;

    public CodonBase(int code, CodonArg[] args, CodonMetaInfo info ) {
        this.code = code;
        this.args = args;
        this.info = info;
    }

    public String getText() {
        return info.name;
    }

    public int getColor() {
        return info.color;
    }

    public CodonArg[] getArgs() {
        return args;
    }

    public CodonArg getRandomArg() {
        return args[ (int) Main.applet.random( args.length ) ];
    }

    public String asDNA() {
        return "" + (char) (((int) 'A') + code);
    }

    public abstract void tick(NormalCell cell, CodonArg arg );

}
