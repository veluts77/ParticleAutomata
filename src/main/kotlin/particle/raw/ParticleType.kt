package particle.raw

import java.awt.*

enum class ParticleType constructor(val type: Int, val color: Color) {

    RED(1, Color(250, 20, 20)),
    YELLOW(2, Color(200, 140, 100)),
    BLUE(3, Color(80, 170, 140))

}