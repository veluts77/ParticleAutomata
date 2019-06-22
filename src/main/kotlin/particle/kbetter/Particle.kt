package particle.kbetter

import java.awt.*
import java.util.HashSet

class Particle(
        private val type: ParticleType,
        var x: Float,
        var y: Float
) {
    var sx: Float = 0.toFloat()
    var sy: Float = 0.toFloat()
    var links: Int = 0
    var bonds: Set<Particle>

    val color: Color
        get() = type.color


    init {
        this.sx = 0f
        this.sy = 0f
        this.links = 0
        this.bonds = HashSet()
    }

    fun getType(): Int {
        return type.type - 1
    }

}