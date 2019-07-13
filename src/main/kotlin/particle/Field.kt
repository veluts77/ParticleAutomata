package particle

class Field internal constructor() {

    private val particles: MutableList<Particle> = mutableListOf()

    internal val totalParticles: Int
        get() = particles.size

    internal fun add(p: Particle) {
        particles.add(p)
    }

    internal fun particleByIndex(i: Int): Particle {
        return particles[i]
    }

    internal fun removeAll(toRemove: MutableList<Particle>) {
        particles.removeAll(toRemove)
    }
}