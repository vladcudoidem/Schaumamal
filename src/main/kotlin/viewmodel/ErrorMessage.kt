package viewmodel

@JvmInline value class ErrorMessage(val value: String)

fun err(value: String): ErrorMessage = ErrorMessage(value)
