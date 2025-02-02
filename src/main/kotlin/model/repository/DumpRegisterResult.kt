package model.repository

import model.repository.dataClasses.Content

sealed class DumpRegisterResult {
    data class Error(val reason: String) : DumpRegisterResult()

    data class Success(
        val content: Content,
        val message: String? = null
    ) : DumpRegisterResult()
}