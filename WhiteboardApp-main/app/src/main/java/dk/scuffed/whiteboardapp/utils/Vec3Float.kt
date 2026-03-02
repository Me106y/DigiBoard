package dk.scuffed.whiteboardapp.utils

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This class is used to store coordinates in a vector2 float.
 * @property x the x coordinate for a point.
 * @property y the y coordinate for a point.
 */
class Vec3Float(val x: Float, val y: Float, val z: Float) : IVec<Vec3Float, Float> {
    override fun plus(other: Vec3Float): Vec3Float {
        return Vec3Float(this.x + other.x, this.y + other.y, this.z + other.z)
    }

    override fun minus(other: Vec3Float): Vec3Float {
        return Vec3Float(this.x - other.x, this.y - other.y, this.z - other.z)
    }

    override fun times(other: Float): Vec3Float {
        return Vec3Float(x * other, y * other, z * other)
    }

    override fun div(other: Float): Vec3Float {
        return Vec3Float(x / other, y / other, z / other)
    }

    override fun length(): Float {
        return sqrt(x*x + y*y + z*z)
    }

    override fun dot(other: Vec3Float): Float {
        return this.x * other.x + this.y * other.y + this.z * other.z
    }

    override fun distance(other: Vec3Float): Float {
        return sqrt(
            (other.x - this.x).pow(2f) + (other.y - this.y).pow(2f) + (other.z - this.z).pow(
                2f
            )
        )
    }
}