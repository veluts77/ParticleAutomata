package particle

import kotlin.math.atan2

open class Point(
        var x: Float = 0f,
        var y: Float = 0f
) {
    internal val screenX: Int
        get() = x.toInt()

    internal val screenY: Int
        get() = y.toInt()

    internal infix fun squaredDistanceTo(another: Point) =
            (x - another.x) * (x - another.x) +
            (y - another.y) * (y - another.y)

    internal infix fun angleTo(another: Point): Double =
            atan2((y - another.y).toDouble(), (x - another.x).toDouble())
}