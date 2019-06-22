package particle.kbetter


import java.awt.*

object Settings {
    internal val w = 1000
    internal val h = 800

    internal val BG = Color(20, 55, 75, 255)
    internal val LINK = Color(255, 230, 0, 100)

    internal val NODE_RADIUS = 5
    internal val NODE_COUNT = 800
    internal val MAX_DIST = 100
    internal val MAX_DIST2 = MAX_DIST * MAX_DIST
    internal val SPEED = 4f
    internal val SKIP_FRAMES = 1
    internal val BORDER = 30

    internal val fw = w / MAX_DIST + 1
    internal val fh = h / MAX_DIST + 1

    internal val LINK_FORCE = -0.015f
}
