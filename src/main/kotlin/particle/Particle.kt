package particle


import particle.Settings.BORDER
import particle.Settings.COUPLING
import particle.Settings.LINKS
import particle.Settings.LINKS_POSSIBLE
import particle.Settings.MAX_DIST
import particle.Settings.SPEED
import particle.Settings.h
import particle.Settings.w
import java.awt.Color
import java.util.*

class Particle(
        private val particleType: ParticleType,
        internal val position: Point
) {
    internal val color: Color
        get() = particleType.color

    internal val screenX: Int
        get() = position.screenX

    internal val screenY: Int
        get() = position.screenY

    internal val xField: Int
        get() = (position.x / MAX_DIST).toInt()

    internal val yField: Int
        get() = (position.y / MAX_DIST).toInt()

    internal val velocity = Velocity()

    private val squaredMaxDist = MAX_DIST * MAX_DIST
    private var linksUsed: Int = 0
    private val bonds: MutableSet<Particle> = HashSet()

    internal fun squaredDistanceTo(b: Particle): Float {
        return position.squaredDistanceTo(b.position)
    }

    internal fun adjustPositionBasedOnVelocity() {
        val newPos = velocity applyTo position
        position.x = newPos.x
        position.y = newPos.y
    }

    internal fun detectBorders() {
        if (position.x < BORDER) {
            velocity addToX SPEED * 0.05f
            if (position.x < 0) {
                position.x = -position.x
                velocity.bounceY()
            }
        } else if (position.x > w - BORDER) {
            velocity addToX -SPEED * 0.05f
            if (position.x > w) {
                position.x = w * 2 - position.x
                velocity.bounceX()
            }
        }
        if (position.y < BORDER) {
            velocity addToY SPEED * 0.05f
            if (position.y < 0) {
                position.y = -position.y
                velocity.bounceY()
            }
        } else if (position.y > h - BORDER) {
            velocity addToY -SPEED * 0.05f
            if (position.y > h) {
                position.y = h * 2 - position.y
                velocity.bounceY()
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

    internal infix fun applyForceTo(another: Particle): Float {
        var d2 = squaredDistanceTo(another)
        var canLink = false
        if (d2 < squaredMaxDist) {
            var dA = couplingWith(another) / d2
            var dB = another.couplingWith(this) / d2
            if (freeLinksAvailable() && another.freeLinksAvailable()) {
                canLink = d2 < squaredMaxDist / 4f &&
                        notYetLinkedWith(another) &&
                        mayLinkTo(another)
            } else {
                if (notYetLinkedWith(another)) {
                    dA = 1 / d2
                    dB = 1 / d2
                }
            }
            if (d2 < 1) d2 = 1f
            if (d2 < Settings.NODE_RADIUS * Settings.NODE_RADIUS * 4) {
                dA = 1 / d2
                dB = 1 / d2
            }
            val angle = position angleTo another.position
            velocity.applyForceByAngle(dA, angle)
            another.velocity.applyForceByAngle(-dB, angle)
        }
        return if (canLink) d2 else -1f
    }

    private fun couplingWith(another: Particle): Float {
        return COUPLING[type][another.type]
    }

    private val type: Int
        get() = particleType.type - 1

    private fun hasNoLinkTo(another: Particle): Boolean {
        return !bonds.contains(another)
    }

    private infix fun notYetLinkedWith(another: Particle): Boolean {
        return hasNoLinkTo(another) && another.hasNoLinkTo(this)
    }

    private fun freeLinksAvailable(): Boolean {
        return linksUsed < LINKS[type]
    }

    private fun mayLinkTo(another: Particle): Boolean {
        return linksStillAvailableFor(another) &&
                another.linksStillAvailableFor(this)
    }

    private fun linksStillAvailableFor(another: Particle): Boolean {
        val usedLinks = bonds.count { it.type == another.type }
        return usedLinks < LINKS_POSSIBLE[type][another.type]
    }
}