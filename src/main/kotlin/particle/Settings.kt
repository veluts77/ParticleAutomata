package particle

object Settings {
    internal const val NODE_RADIUS = 5
    internal const val NODE_COUNT = 500
    internal const val MAX_DIST = 100
    internal const val w = 1000
    internal const val h = 800
    internal const val SPEED = 15f
    internal const val SKIP_FRAMES = 1
    internal const val BORDER = 30
    internal const val LINK_FORCE = -0.015f

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
