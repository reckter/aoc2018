package me.reckter.aoc.cords.d4

import me.reckter.aoc.cords.d3.Cord3D
import me.reckter.aoc.cords.d3.plus

data class Cord4D<T : Number>(
    val x: T,
    val y: T,
    val z: T,
    val w: T
)

operator fun Cord4D<Int>.plus(other: Cord4D<Int>): Cord4D<Int> {
    return Cord4D(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)
}

fun Cord4D<Int>.getNeighbors(): List<Cord4D<Int>> {
    return (-1..1).flatMap { xOffset ->
        (-1..1).flatMap { yOffset ->
            (-1..1).flatMap { zOffset ->
                (-1..1).map { wOffset ->
                    this + Cord4D(xOffset, yOffset, zOffset, wOffset)
                }
            }
        }
    }
        .filter { it != this }
}

fun Cord4D<Int>.manhattenDistance(to: Cord4D<Int>): Int {
    return Math.abs(this.x - to.x) + Math.abs(this.y - to.y) + Math.abs(this.z - to.z) + Math.abs(this.w - to.w)
}
