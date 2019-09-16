package particle

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Velocity(
        private val value: Point = Point()
) {
    internal fun slowDown() {
        value.x *= 0.98f
        value.y *= 0.98f
    }

    internal fun applyForceByAngle(force: Float, angle: Double) {
        value.x += cos(angle).toFloat() * force * Settings.SPEED
        value.y += sin(angle).toFloat() * force * Settings.SPEED
    }

    internal fun normalize() {
        val magnitude = sqrt(value.x * value.x + value.y * value.y)
        if (magnitude > 1f) {
            value.x /= magnitude
            value.y /= magnitude
        }
    }

    internal infix fun applyTo(position: Point) =
            Point(position.x + value.x, position.y + value.y)

    internal fun bounceX() {
        value.x *= -0.5f
    }

    internal fun bounceY() {
        value.y *= -0.5f
    }

    internal infix fun addToX(v: Float) {
        value.x += v
    }

    internal infix fun addToY(v: Float) {
        value.y += v
    }
}
