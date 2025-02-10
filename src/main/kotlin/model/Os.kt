package model

enum class Os {

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

fun <T> on(win: T, mac: T, lin: T) = when(Os.current) {
    Os.WIN -> win
    Os.MACOS -> mac
    Os.LINUX -> lin
}

fun <T> on(win: () -> T, mac: () -> T, lin: () -> T) = when(Os.current) {
    Os.WIN -> win()
    Os.MACOS -> mac()
    Os.LINUX -> lin()
}