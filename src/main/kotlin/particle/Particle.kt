package particle


import particle.Settings.BORDER
import particle.Settings.COUPLING
import particle.Settings.LINKS
import particle.Settings.LINKS_POSSIBLE
import particle.Settings.MAX_DIST
import particle.Settings.SPEED
import particle.Settings.h
import particle.Settings.w
import java.awt.*
import java.util.HashSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Particle(private val particleType: ParticleType, private var x: Float, private var y: Float) {
    private var sx: Float = 0.toFloat()
    private var sy: Float = 0.toFloat()
    private var links: Int = 0
    private val bonds: MutableSet<Particle>

    init {
        this.sx = 0f
        this.sy = 0f
        this.links = 0
        this.bonds = HashSet()
    }

    val color: Color
        get() = particleType.color

    val type: Int
        get() = particleType.type - 1

    internal val screenX: Int
        get() = x.toInt()

    internal val screenY: Int
        get() = y.toInt()

    internal val xField: Int
        get() = (x / MAX_DIST).toInt()

    internal val yField: Int
        get() = (y / MAX_DIST).toInt()

    internal fun squaredDistanceTo(b: Particle): Float {
        return (x - b.x) * (x - b.x) + (y - b.y) * (y - b.y)
    }

    internal fun angleTo(another: Particle): Double {
        return atan2((y - another.y).toDouble(), (x - another.x).toDouble())
    }

    internal fun couplingWith(another: Particle): Float {
        return COUPLING[type][another.type]
    }

    internal fun isNotLinkedTo(another: Particle): Boolean {
        return !bonds.contains(another)
    }

    internal fun freeLinksAvailable(): Boolean {
        return links < LINKS[type]
    }

    internal fun mayLinkTo(another: Particle): Boolean {
        var typeCountA = 0
        for (p in bonds) {
            if (p.type == another.type) typeCountA++
        }
        var typeCountB = 0
        for (p in another.bonds) {
            if (p.type == type) typeCountB++
        }
        return typeCountA < LINKS_POSSIBLE[type][another.type] && typeCountB < LINKS_POSSIBLE[another.type][type]
    }

    internal fun adjustPosition() {
        x += sx
        y += sy
    }

    internal fun slowDownVelocity() {
        sx *= 0.98f
        sy *= 0.98f
    }

    internal fun addVelocityToPositiveDirection(angle: Double, d: Float) {
        sx += cos(angle).toFloat() * d * SPEED
        sy += sin(angle).toFloat() * d * SPEED
    }

    internal fun addVelocityToNegativeDirection(angle: Double, d: Float) {
        addVelocityToPositiveDirection(angle, -d)
    }

    internal fun normalizeVelocity() {
        // velocity normalization
        // idk if it is still necessary
        val magnitude = sqrt((sx * sx + sy * sy).toDouble()).toFloat()
        if (magnitude > 1f) {
            sx /= magnitude
            sy /= magnitude
        }
        // border repulsion
        if (x < BORDER) {
            sx += SPEED * 0.05f
            if (x < 0) {
                x = -x
                sx *= -0.5f
            }
        } else if (x > w - BORDER) {
            sx -= SPEED * 0.05f
            if (x > w) {
                x = w * 2 - x
                sx *= -0.5f
            }
        }
        if (y < BORDER) {
            sy += SPEED * 0.05f
            if (y < 0) {
                y = -y
                sy *= -0.5f
            }
        } else if (y > h - BORDER) {
            sy -= SPEED * 0.05f
            if (y > h) {
                y = h * 2 - y
                sy *= -0.5f
            }
        }
    }

    internal fun linkWith(another: Particle) {
        this.bonds.add(another)
        another.bonds.add(this)
        this.links++
        another.links++
    }

    internal fun unlinkFrom(another: Particle) {
        this.links--
        another.links--
        this.bonds.remove(another)
        another.bonds.remove(this)
    }
}