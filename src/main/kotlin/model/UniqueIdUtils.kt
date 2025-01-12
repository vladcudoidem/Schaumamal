package model

import java.util.UUID

fun hash(): String = UUID.randomUUID().toString().replace(oldValue = "-", newValue = "")