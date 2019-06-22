package particle.raw

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val f = Form()
        Thread(f).start()
    }
}