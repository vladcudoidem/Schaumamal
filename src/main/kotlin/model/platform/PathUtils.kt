package model.platform

// This constant allows an elegant way of starting at root while using the "to" syntax.
val Root = ""

infix fun String.to(subElement: String) = "$this/$subElement"