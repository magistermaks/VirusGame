package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;

public class CodonBaseNone extends CodonBase {

	public CodonBaseNone(int code, CodonMetaInfo info) {
		super(code, new CodonArg[]{CodonArgs.NONE}, info);
	}

	@Override
	public int execute(NormalCell cell, CodonArg arg, int acc) {
		return SUCCESS;
	}

}
