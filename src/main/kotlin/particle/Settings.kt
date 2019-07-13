package particle

object Settings {
    internal val NODE_RADIUS = 5
    internal val NODE_COUNT = 500
    internal val MAX_DIST = 100
    internal val w = 1000
    internal val h = 800
    internal val SPEED = 15f
    internal val SKIP_FRAMES = 1
    internal val BORDER = 30
    internal val LINK_FORCE = -0.015f

    internal val COUPLING = arrayOf(
            floatArrayOf(1f, 1f, -1f),
            floatArrayOf(1f, 1f, 1f),
            floatArrayOf(1f, 1f, 1f)
    )

    internal val LINKS = intArrayOf(1, 3, 2)

    internal val LINKS_POSSIBLE = arrayOf(
            floatArrayOf(0f, 1f, 1f),
            floatArrayOf(1f, 2f, 1f),
            floatArrayOf(1f, 1f, 2f)
    )
}
