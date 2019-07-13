package particle

import particle.Settings.LINK_FORCE


class Link(private val a: Particle, private val b: Particle) {

    init {
        doLink()
    }

    internal val screenX1: Int
        get() = a.screenX

    internal val screenY1: Int
        get() = a.screenY

    internal val screenX2: Int
        get() = b.screenX

    internal val screenY2: Int
        get() = b.screenY

    internal val squaredDistance: Float
        get() = a.squaredDistanceTo(b)

    private fun doLink() {
        a.linkWith(b)
    }

    internal fun unlink() {
        a.unlinkFrom(b)
    }

    internal fun adjustParticlesVelocity() {
        val angle = a.angleTo(b)
        a.addVelocityToPositiveDirection(angle, LINK_FORCE)
        b.addVelocityToNegativeDirection(angle, LINK_FORCE)
    }


}