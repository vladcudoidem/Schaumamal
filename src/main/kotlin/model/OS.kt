package model

enum class OS {

    WIN, MACOS, LINUX;

    companion object {

        val current by lazy {
            with(System.getProperty("os.name").lowercase()) {
                when {
                    contains("win") -> WIN
                    contains("mac") -> MACOS
                    contains("nix") or contains("nux") or contains("aix") -> LINUX
                    else -> error("Could not detect that operating system is either Windows, MacOS or Linux.")
                }
            }
        }
    }
}

fun <T> on(win: T, mac: T, lin: T) = when(OS.current) {
    OS.WIN -> win
    OS.MACOS -> mac
    OS.LINUX -> lin
}

@Suppress("unused")
fun <T> on(win: () -> T, mac: () -> T, lin: () -> T) = when(OS.current) {
    OS.WIN -> win()
    OS.MACOS -> mac()
    OS.LINUX -> lin()
}