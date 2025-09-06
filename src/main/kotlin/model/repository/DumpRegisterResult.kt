package model.repository

import model.repository.dataClasses.Content
import viewmodel.notification.Notification

sealed class DumpRegisterResult {
    data class Error(val notification: Notification) : DumpRegisterResult()

    data class Success(val content: Content, val message: String? = null) : DumpRegisterResult()
}
