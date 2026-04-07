package model.parser.dataClasses

data class Bounds(val x: Float, val y: Float, val width: Float, val height: Float) {
    val area: Float
        get() = width * height

    operator fun times(factor: Float): Bounds {
        return Bounds(
            x = x * factor,
            y = y * factor,
            width = width * factor,
            height = height * factor,
        )
    }

    companion object {
        fun fromBoundsString(boundsString: String): Bounds {
            val bounds =
                boundsString.removeSurrounding("[", "]").split("][", ",").map {
                    try {
                        it.toFloat()
                    } catch (_: NumberFormatException) {
                        // If any of the coordinates is invalid immediately return empty bounds.
                        return Zero
                    }
                }

            return Bounds(
                x = bounds[0],
                y = bounds[1],
                width = bounds[2] - bounds[0],
                height = bounds[3] - bounds[1],
            )
        }

        val Zero = Bounds(x = 0f, y = 0f, width = 0f, height = 0f)
    }
}
