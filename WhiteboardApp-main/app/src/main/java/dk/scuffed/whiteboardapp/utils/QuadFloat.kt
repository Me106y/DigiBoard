package dk.scuffed.whiteboardapp.utils

import dk.scuffed.whiteboardapp.pipeline.areaOfTriangle

fun QuadFloat(): QuadFloat {
    return QuadFloat(Vec2Float(0f, 0f), Vec2Float(0f, 0f), Vec2Float(0f, 0f), Vec2Float(0f, 0f))
}

class QuadFloat(val a: Vec2Float, val b: Vec2Float, val c: Vec2Float, val d: Vec2Float) {
    fun area(): Float {
        return areaOfTriangle(a, b, c) + areaOfTriangle(a, c, d)
    }

    fun toVec2FloatArray(): Array<Vec2Float> {
        return arrayOf(a, b, c, d)
    }

    override fun toString(): String {
        return """
        a: $a
        b: $b
        c: $c
        d: $d
        """.trimIndent()
    }
}
