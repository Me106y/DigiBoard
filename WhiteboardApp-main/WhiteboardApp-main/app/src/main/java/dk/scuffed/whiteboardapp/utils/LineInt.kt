package dk.scuffed.whiteboardapp.utils

class LineInt(val startPoint: Vec2Int, val endPoint: Vec2Int) {
    fun toLineFloat(): LineFloat {
        return LineFloat(startPoint.toVec2Float(), endPoint.toVec2Float())
    }
}