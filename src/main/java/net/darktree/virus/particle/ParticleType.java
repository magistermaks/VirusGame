package net.darktree.virus.particle;

public enum ParticleType {
	FOOD,
	WASTE,
	VIRUS;

	public static ParticleType fromId(int id) {
		switch (id) {
			case 0:
				return ParticleType.FOOD;
			case 1:
				return ParticleType.WASTE;
			case 2:
				return ParticleType.VIRUS;
		}
		return null;
	}
}