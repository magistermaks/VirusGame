package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.util.Utils;

public abstract class CodonBase {

	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;

	public static int getDefault(CodonArg arg) {
		return arg.is(CodonArgs.NONE) ? SUCCESS : FAILURE;
	}

	public final int code;
	public final CodonArg[] args;
	public final CodonMetaInfo info;

	public CodonBase(int code, CodonArg[] args, CodonMetaInfo info) {
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
		return args[Utils.random(args.length)];
	}

	public String asDNA() {
		return "" + (char) (((int) 'A') + code);
	}

	public abstract int execute(NormalCell cell, CodonArg arg, int acc);

}
