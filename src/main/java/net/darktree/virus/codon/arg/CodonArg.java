package net.darktree.virus.codon.arg;

import net.darktree.virus.codon.CodonMetaInfo;

public class CodonArg {

	public final int code;
	public final CodonMetaInfo info;

	public CodonArg(int code, CodonMetaInfo info) {
		this.code = code;
		this.info = info;
	}

	public String getText() {
		return info.name;
	}

	public int getColor() {
		return info.color;
	}

	public String asDNA() {
		return "" + (char) (((int) 'a') + code);
	}

	public boolean is(CodonArg arg) {
		return this == arg;
	}

	@Override
	public CodonArg clone() {
		// No cloning needed for stateless arguments
		return this;
	}

	public void mutate() {
		// Only applies to complex args
	}

}
