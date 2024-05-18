package model.utils

object CommandManager {

    private val runtime: Runtime = Runtime.getRuntime()

    fun executeAndWait(command: String) = runtime.exec(command).waitFor()
}