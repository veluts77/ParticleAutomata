package particle

object Settings {
    const val PARTICLE_RADIUS = 5
    const val PARTICLES_COUNT = 250
    const val MAX_DIST = 100
    const val w = 1000
    const val h = 800
    const val SPEED = 15f
    const val SKIP_FRAMES = 1
    const val BORDER = 30
    const val LINK_FORCE = -0.015f

    val COUPLING = arrayOf(
            floatArrayOf(1f, 1f, -1f),
            floatArrayOf(1f, 1f, 1f),
            floatArrayOf(1f, 1f, 1f)
    )

    val LINKS = intArrayOf(1, 3, 2)

    val LINKS_POSSIBLE = arrayOf(
            floatArrayOf(0f, 1f, 1f),
            floatArrayOf(1f, 2f, 1f),
            floatArrayOf(1f, 1f, 2f)
    )
}
