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

class Particle(
        private val particleType: ParticleType,
        private var x: Float,
        private var y: Float
) {
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    private var linksUsed: Int = 0
    private val bonds: MutableSet<Particle> = HashSet()

    val color: Color
        get() = particleType.color

    private val type: Int
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
        return linksUsed < LINKS[type]
    }

    internal fun mayLinkTo(another: Particle): Boolean {
        return linksStillAvailableFor(another) &&
                another.linksStillAvailableFor(this)
    }

    private fun linksStillAvailableFor(another: Particle): Boolean {
        val usedLinks = bonds.count { it.type == another.type }
        return usedLinks < LINKS_POSSIBLE[type][another.type]
    }

    internal fun adjustPositionBasedOnVelocity() {
        x += velocityX
        y += velocityY
    }

    internal fun slowDownVelocity() {
        velocityX *= 0.98f
        velocityY *= 0.98f
    }

    internal fun addVelocityToPositiveDirection(angle: Double, d: Float) {
        velocityX += cos(angle).toFloat() * d * SPEED
        velocityY += sin(angle).toFloat() * d * SPEED
    }

    internal fun addVelocityToNegativeDirection(angle: Double, d: Float) {
        addVelocityToPositiveDirection(angle, -d)
    }

    internal fun normalizeVelocity() {
        val magnitude = sqrt(velocityX * velocityX + velocityY * velocityY)
        if (magnitude > 1f) {
            velocityX /= magnitude
            velocityY /= magnitude
        }
    }

    internal fun detectBorders() {
        // border repulsion
        if (x < BORDER) {
            velocityX += SPEED * 0.05f
            if (x < 0) {
                x = -x
                velocityX *= -0.5f
            }
        } else if (x > w - BORDER) {
            velocityX -= SPEED * 0.05f
            if (x > w) {
                x = w * 2 - x
                velocityX *= -0.5f
            }
        }
        if (y < BORDER) {
            velocityY += SPEED * 0.05f
            if (y < 0) {
                y = -y
                velocityY *= -0.5f
            }
        } else if (y > h - BORDER) {
            velocityY -= SPEED * 0.05f
            if (y > h) {
                y = h * 2 - y
                velocityY *= -0.5f
            }
        }
    }

    internal fun linkWith(another: Particle) {
        this.bonds.add(another)
        another.bonds.add(this)
        this.linksUsed++
        another.linksUsed++
    }

    internal fun unlinkFrom(another: Particle) {
        this.linksUsed--
        another.linksUsed--
        this.bonds.remove(another)
        another.bonds.remove(this)
    }
}