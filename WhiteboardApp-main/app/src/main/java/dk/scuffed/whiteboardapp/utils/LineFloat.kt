package dk.scuffed.whiteboardapp.utils


class LineFloat(val startPoint: Vec2Float, val endPoint: Vec2Float) {
    fun intersect(line: LineFloat): Vec2Float? {
        val a = startPoint
        val b = endPoint
        val c = line.startPoint
        val d = line.endPoint

        // Line AB represented as a1x + b1y = c1
        val a1: Float = b.y - a.y
        val b1: Float = a.x - b.x
        val c1: Float = a1 * a.x + b1 * a.y

        // Line CD represented as a2x + b2y = c2
        val a2: Float = d.y - c.y
        val b2: Float = c.x - d.x
        val c2: Float = a2 * c.x + b2 * c.y
        val determinant = a1 * b2 - a2 * b1
        return if (determinant == 0.0f) {
            // The lines are parallel
            return null
        } else {
            val x = (b2 * c1 - b1 * c2) / determinant
            val y = (a1 * c2 - a2 * c1) / determinant
            Vec2Float(x, y)
        }
    }

    fun toLineInt(): LineInt {
        return LineInt(startPoint.toVec2Int(), endPoint.toVec2Int())
    }
}