package particle.kbetter


class Force {

    internal fun couplingLength(): Int {
        return COUPLING.size
    }

    internal fun applyForce(a: Particle, b: Particle): Float {
        var d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
        var canLink = false
        if (d2 < Settings.MAX_DIST2) {
            var dA = COUPLING[a.getType()][b.getType()] / d2
            var dB = COUPLING[b.getType()][a.getType()] / d2
            if (a.links < LINKS[a.getType()] && b.links < LINKS[b.getType()]) {
                if (d2 < Settings.MAX_DIST2 / 4) {
                    if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                        var typeCountA = 0
                        for (p in a.bonds) {
                            if (p.getType() == b.getType()) typeCountA++
                        }
                        var typeCountB = 0
                        for (p in b.bonds) {
                            if (p.getType() == a.getType()) typeCountB++
                        }
                        if (typeCountA < LINKS_POSSIBLE[a.getType()][b.getType()] && typeCountB < LINKS_POSSIBLE[b.getType()][a.getType()]) {
                            canLink = true
                        }
                    }
                }
            } else {
                if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                    dA = 1 / d2
                    dB = 1 / d2
                }
            }
            val angle = Math.atan2((a.y - b.y).toDouble(), (a.x - b.x).toDouble())
            if (d2 < 1) d2 = 1f
            if (d2 < Settings.NODE_RADIUS * Settings.NODE_RADIUS * 4) {
                dA = 1 / d2
                dB = 1 / d2
            }
            a.sx += Math.cos(angle).toFloat() * dA * Settings.SPEED
            a.sy += Math.sin(angle).toFloat() * dA * Settings.SPEED
            b.sx -= Math.cos(angle).toFloat() * dB * Settings.SPEED
            b.sy -= Math.sin(angle).toFloat() * dB * Settings.SPEED
        }
        return if (canLink) {
            d2
        } else -1f
    }

    companion object {

        private val COUPLING = arrayOf(floatArrayOf(1f, 1f, -1f), floatArrayOf(1f, 1f, 1f), floatArrayOf(1f, 1f, 1f))

        private val LINKS = intArrayOf(1, 3, 2)

        private val LINKS_POSSIBLE = arrayOf(floatArrayOf(0f, 1f, 1f), floatArrayOf(1f, 2f, 1f), floatArrayOf(1f, 1f, 2f))
    }

}
