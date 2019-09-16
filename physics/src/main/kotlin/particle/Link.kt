package particle

import particle.Settings.LINK_FORCE
import java.awt.Color


class Link(private val a: Particle, private val b: Particle) {

    companion object {
        val color = Color(255, 230, 0, 100)
    }

    init {
        doLink()
    }

    val screenX1: Int
        get() = a.screenX

    val screenY1: Int
        get() = a.screenY

    val screenX2: Int
        get() = b.screenX

    val screenY2: Int
        get() = b.screenY

    internal val squaredDistance: Float
        get() = a squaredDistanceTo b

    private fun doLink() {
        a linkWith b
    }

    internal fun unlink() {
        a unlinkFrom b
    }

    internal fun adjustParticlesVelocity() {
        val angle = a.position angleTo b.position
        a.velocity.applyForceByAngle(LINK_FORCE, angle)
        b.velocity.applyForceByAngle(-LINK_FORCE, angle)
    }
}