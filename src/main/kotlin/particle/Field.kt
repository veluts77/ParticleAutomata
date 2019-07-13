package particle

class Field internal constructor() {

    private val particles: MutableList<Particle> = mutableListOf()

    internal fun add(p: Particle) {
        particles.add(p)
    }

    internal fun totalParticles(): Int {
        return particles.size
    }

    internal fun particleByIndex(i: Int): Particle {
        return particles[i]
    }

    internal fun remove(p: Particle) {
        particles.remove(p)
    }

    internal fun removeAll(toRemove: MutableList<Particle>) {
        particles.removeAll(toRemove)
    }
}