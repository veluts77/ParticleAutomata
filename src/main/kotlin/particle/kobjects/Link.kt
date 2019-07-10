package particle.kobjects

import particle.kobjects.Settings.LINK_FORCE


class Link(private val a: Particle, private val b: Particle) {

    init {
        doLink()
    }

    private fun doLink() {
        a.linkWith(b)
    }

    internal fun unlink() {
        a.unlinkFrom(b)
    }

    internal fun squaredDistance(): Float {
        return a.squaredDistanceTo(b)
    }

    internal fun adjustParticlesVelocity() {
        val angle = a.angleTo(b)
        a.addVelocityToPositiveDirection(angle, LINK_FORCE)
        b.addVelocityToNegativeDirection(angle, LINK_FORCE)
    }

    internal fun screenX1(): Int {
        return a.screenX
    }

    internal fun screenY1(): Int {
        return a.screenY
    }

    internal fun screenX2(): Int {
        return b.screenX
    }

    internal fun screenY2(): Int {
        return b.screenY
    }
}