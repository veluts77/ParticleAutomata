package particle.kbetter

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val f = Form()
        f.title = "Better Kotlin"
        Thread(f).start()
    }
}