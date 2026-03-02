package dk.scuffed.whiteboardapp.utils

class Color(val r: Float, val g: Float, val b: Float, val a: Float) {
    init {
        assert(r in 0.0f..1.0f)
        assert(g in 0.0f..1.0f)
        assert(b in 0.0f..1.0f)
        assert(a in 0.0f..1.0f)
    }
}