package model.dumper

import model.repository.dataClasses.Dump
import viewmodel.notification.Notification

sealed class DumpResult {
    data class Error(val notification: Notification) : DumpResult()

    data class Success(val dump: Dump, val message: String? = null) : DumpResult()
}
