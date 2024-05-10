package model.utils

object CommandExecutor {

    private val runtime: Runtime = Runtime.getRuntime()

    fun executeAndWait(command: String) = runtime.exec(command).waitFor()
}