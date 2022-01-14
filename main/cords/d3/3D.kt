package me.reckter.aoc.cords.d3

import java.lang.Math.abs

/**
 *           3D
 */

data class Cord3D<T : Number>(
    val x: T,
    val y: T,
    val z: T
)


operator fun Cord3D<Long>.plus(other: Cord3D<Long>): Cord3D<Long> {
    return Cord3D(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z
    )
}

fun Cord3D<Long>.getNeighbors(): List<Cord3D<Long>> {
    return (-1L..1L).flatMap { xOffset ->
        (-1L..1L).flatMap { yOffset ->
            (-1L..1L).map { zOffset ->
                this + Cord3D(xOffset, yOffset, zOffset)
            }
        }
    }
        .filter { it != this }
}

fun Cord3D<Int>.manhattenDistance(to: Cord3D<Int>): Int {
    return abs(this.x - to.x) + abs(this.y - to.y) + abs(this.z - to.z)
}

fun Cord3D<Long>.manhattenDistance(to: Cord3D<Long>): Long {
    return abs(this.x - to.x) + abs(this.y - to.y) + abs(this.z - to.z)
}
