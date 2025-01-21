package model.dumper

import model.repository.Dump

sealed class DumpResult {
    data class Error(val reason: String) : DumpResult()

    data class Success(
        val dump: Dump,
        val message: String? = null
    ) : DumpResult()
}