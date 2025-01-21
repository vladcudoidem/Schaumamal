package model.repository

sealed class DumpRegisterResult {
    data class Error(val reason: String) : DumpRegisterResult()

    data class Success(
        val content: Content,
        val message: String? = null
    ) : DumpRegisterResult()
}