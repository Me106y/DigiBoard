package dk.scuffed.whiteboardapp.utils

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This class is used to store coordinates in a vector2 integer.
 * @property x the x coordinate for a point.
 * @property y the y coordinate for a point.
 */
class Vec2Int(val x: Int, val y: Int) : IVec<Vec2Int, Int> {
    override fun plus(other: Vec2Int): Vec2Int {
        return Vec2Int(this.x + other.x, this.y + other.y)
    }

    override fun minus(other: Vec2Int): Vec2Int {
        return Vec2Int(this.x - other.x, this.y - other.y)
    }

    override fun times(other: Int): Vec2Int {
        return Vec2Int(x * other, y * other)
    }

    override fun div(other: Int): Vec2Int {
        return Vec2Int(x / other, y / other)
    }

    override fun length(): Float {
        return sqrt((x*x + y*y).toFloat())
    }

    override fun dot(other: Vec2Int): Float {
        return this.x.toFloat()*other.x.toFloat() + this.y.toFloat()*other.y.toFloat()
    }

    override fun distance(other: Vec2Int): Float {
        return sqrt((other.x - this.x).toFloat().pow(2f) + (other.y - this.y).toFloat().pow(2f))
    }

    fun toVec2Float(): Vec2Float {
        return Vec2Float(x.toFloat(), y.toFloat())
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}