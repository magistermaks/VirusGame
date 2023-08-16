package net.darktree.virus.codon.base;

import net.darktree.virus.cell.NormalCell;
import net.darktree.virus.codon.CodonArgs;
import net.darktree.virus.codon.CodonMetaInfo;
import net.darktree.virus.codon.arg.CodonArg;
import net.darktree.virus.codon.arg.CodonArgValue;

public class CodonBaseJump extends CodonBase {

	public CodonBaseJump(int code, CodonMetaInfo info) {
		super(code, new CodonArg[]{CodonArgs.NONE, CodonArgs.VALUE}, info);
	}

	@Override
	public int execute(NormalCell cell, CodonArg arg, int acc) {
		if (arg.is(CodonArgs.VALUE) && acc == 0) {
			cell.jump(((CodonArgValue) arg).value);
		}

		return acc;
	}

}
