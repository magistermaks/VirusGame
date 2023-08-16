package net.darktree.virus.codon;

import net.darktree.virus.codon.base.*;
import net.darktree.virus.util.Utils;

import java.util.ArrayList;

public class CodonBases {

	private static final ArrayList<CodonBase> registry = new ArrayList<>();

	public static CodonBase register(CodonBase base) {
		registry.add(base);
		if (registry.size() != base.code + 1) {
			throw new RuntimeException("Invalid Codon ID!");
		}
		return base;
	}

	public static CodonBase get(int id) {
		return registry.get(id);
	}

	public static CodonBase rand() {
		return get(Utils.random(registry.size()));
	}

	public static int size() {
		return registry.size();
	}

	// Register all codon bases
	public static final CodonBase NONE = register(new CodonBaseNone(0, Codon.meta("None", 0, 0, 0)));
	public static final CodonBase DIGEST = register(new CodonBaseDigest(1, Codon.meta("Digest", 100, 0, 200)));
	public static final CodonBase REMOVE = register(new CodonBaseRemove(2, Codon.meta("Remove", 180, 160, 10)));
	public static final CodonBase REPAIR = register(new CodonBaseRepair(3, Codon.meta("Repair", 0, 150, 0)));
	public static final CodonBase MOVE_HAND = register(new CodonBaseMoveHand(4, Codon.meta("Move Hand", 200, 0, 100)));
	public static final CodonBase READ = register(new CodonBaseRead(5, Codon.meta("Read", 70, 70, 255)));
	public static final CodonBase WRITE = register(new CodonBaseWrite(6, Codon.meta("Write", 0, 0, 220)));
	public static final CodonBase JUMP = register(new CodonBaseJump(7, Codon.meta("Else Jump", 0, 128, 64)));
	public static final CodonBase SPLIT = register(new CodonBaseSplit(8, Codon.meta("Split", 255, 102, 153)));

}
