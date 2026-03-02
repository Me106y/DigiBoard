package dk.scuffed.whiteboardapp.utils

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This class is used to store coordinates in a vector2 float.
 * @property x the x coordinate for a point.
 * @property y the y coordinate for a point.
 */
class Vec2Float(val x: Float, val y: Float) : IVec<Vec2Float, Float> {
    override fun plus(other: Vec2Float): Vec2Float {
        return Vec2Float(this.x + other.x, this.y + other.y)
    }

    override fun minus(other: Vec2Float): Vec2Float {
        return Vec2Float(this.x - other.x, this.y - other.y)
    }

    override fun times(other: Float): Vec2Float {
        return Vec2Float(this.x * other, this.y * other)
    }

    override fun div(other: Float): Vec2Float {
        return Vec2Float(x / other, y / other)
    }

    override fun length(): Float {
        return sqrt(x*x + y*y)
    }

    override fun distance(other: Vec2Float): Float {
        return sqrt((other.x - this.x).pow(2f) + (other.y - this.y).pow(2f))
    }

    override fun dot(other: Vec2Float): Float {
        return this.x*other.x + this.y*other.y
    }

    fun toVec2Int(): Vec2Int {
        return Vec2Int(this.x.toInt(), this.y.toInt())
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}