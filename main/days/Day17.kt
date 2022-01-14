package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.matchAndParse
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.min

class Day17 : Day {
    override val day = 17

    data class Tile(
        val pos: Cord2D<Int>,
        val type: Type
    ) {
        enum class Type {
            empty,
            wall,
            water_flowing,
            water_settled,
            water_settling_right,
            water_settling_left,
        }
    }

    val Cord2D<Int>.down: Cord2D<Int>
        get() {
            return Cord2D(x, y + 1)
        }
    val Cord2D<Int>.up: Cord2D<Int>
        get() {
            return Cord2D(x, y - 1)
        }

    val Cord2D<Int>.right: Cord2D<Int>
        get() {
            return Cord2D(x + 1, y)
        }
    val Cord2D<Int>.left: Cord2D<Int>
        get() {
            return Cord2D(x - 1, y)
        }

    fun Tile.down(map: Map<Cord2D<Int>, Tile>): Tile =
        map.getOrDefault(pos.down, Tile(pos.down, Tile.Type.empty))

    fun Tile.up(map: Map<Cord2D<Int>, Tile>): Tile =
        map.getOrDefault(pos.up, Tile(pos.up, Tile.Type.empty))

    fun Tile.right(map: Map<Cord2D<Int>, Tile>): Tile =
        map.getOrDefault(pos.right, Tile(pos.right, Tile.Type.empty))

    fun Tile.left(map: Map<Cord2D<Int>, Tile>): Tile =
        map.getOrDefault(pos.left, Tile(pos.left, Tile.Type.empty))

    val map by lazy {
        loadInput()
            .matchAndParse(
                "x=(\\d+), y=(\\d+)..(\\d+)" to { (xStr, minYStr, maxYStr) -> (xStr.toInt() to xStr.toInt()) to (minYStr.toInt() to maxYStr.toInt()) },
                "y=(\\d+), x=(\\d+)..(\\d+)" to { (yStr, minXStr, maxXStr) -> (minXStr.toInt() to maxXStr.toInt()) to (yStr.toInt() to yStr.toInt()) },
            )
            .fold(emptyMap<Cord2D<Int>, Tile>()) { map, cur ->
                map + (cur.first.first..cur.first.second).flatMap { x ->
                    (cur.second.first..cur.second.second).map { y ->
                        Cord2D(x, y) to Tile(Cord2D(x, y), Tile.Type.wall)
                    }
                }
            }
    }

    fun MutableMap<Cord2D<Int>, Tile>.step(schedule: List<Cord2D<Int>>): Pair<MutableMap<Cord2D<Int>, Tile>, List<Cord2D<Int>>> {
        val scheduled =
            schedule
                .distinct()
                .mapNotNull { this[it] }

        val changed = scheduled.map { it ->
            when (it.type) {
                Tile.Type.empty -> {
                    val up = it.up(this)
                    if (up.type in listOf(
                            Tile.Type.water_flowing,
                            Tile.Type.water_settling_right,
                            Tile.Type.water_settling_left
                        )
                    ) {
                        return@map it.copy(type = Tile.Type.water_flowing)
                    }

                    val down = it.down(this)
                    if (down.type in listOf(Tile.Type.water_settled, Tile.Type.wall)) {
                        if (it.right(this).type in listOf(
                                Tile.Type.water_flowing,
                                Tile.Type.water_settled,
                                Tile.Type.water_settling_right
                            ) && it.right(this)
                                .down(this).type in listOf(
                                Tile.Type.water_settled, Tile.Type.wall
                            )
                        ) {
                            return@map it.copy(type = Tile.Type.water_flowing)
                        }
                        if (it.left(this).type in listOf(
                                Tile.Type.water_flowing,
                                Tile.Type.water_settled,
                                Tile.Type.water_settling_left
                            ) && it.left(this)
                                .down(this).type in listOf(
                                Tile.Type.water_settled, Tile.Type.wall
                            )
                        ) {
                            return@map it.copy(type = Tile.Type.water_flowing)
                        }
                    }

                    if (down.right(this).type == Tile.Type.wall && it.right(this).type in listOf(
                            Tile.Type.water_flowing,
                            Tile.Type.water_settled,
                            Tile.Type.water_settling_right, Tile.Type.water_settling_left
                        )
                    ) {
                        return@map it.copy(type = Tile.Type.water_flowing)
                    }

                    if (down.left(this).type == Tile.Type.wall && it.left(this).type in listOf(
                            Tile.Type.water_flowing,
                            Tile.Type.water_settled,
                            Tile.Type.water_settling_right, Tile.Type.water_settling_left
                        )
                    ) {
                        return@map it.copy(type = Tile.Type.water_flowing)
                    }

                    it
                }
                Tile.Type.wall -> it
                Tile.Type.water_flowing -> {
                    val down = it.down(this)
                    if (down.type in listOf(Tile.Type.water_settled, Tile.Type.wall)) {

                        val right = it.right(this)
                        if (right.type in listOf(
                                Tile.Type.water_settling_right,
                                Tile.Type.wall
                            )
                        ) {
                            return@map it.copy(type = Tile.Type.water_settling_right)
                        }
                        val left = it.left(this)
                        if (left.type == Tile.Type.water_settling_left || left.type == Tile.Type.wall) {
                            return@map it.copy(type = Tile.Type.water_settling_left)
                        }
                    }

                    it
                }
                Tile.Type.water_settled -> it
                Tile.Type.water_settling_right -> {
                    if (it.left(this).type in listOf(
                            Tile.Type.water_settling_left,
                            Tile.Type.water_settled,
                            Tile.Type.wall
                        )
                    ) {
                        return@map it.copy(type = Tile.Type.water_settled)
                    }
                    it
                }
                Tile.Type.water_settling_left -> {
                    if (it.right(this).type in listOf(
                            Tile.Type.water_settling_right,
                            Tile.Type.water_settled,
                            Tile.Type.wall
                        )
                    ) {
                        return@map it.copy(type = Tile.Type.water_settled)
                    }
                    it
                }
            }
        }

        changed
            .forEach { this[it.pos] = it }

        return this to (changed
            .filter { it !in scheduled }
            .map { it.pos.getNeighbors() + it.pos }
            .flatten()
            .distinct())
    }

    fun printMap(map: Map<Cord2D<Int>, Tile>) {
        val minX = map.minOf { it.key.x }
        val maxX = map.maxOf { it.key.x }
        val minY = map.minOf { it.key.y }
        val maxY = map.maxOf { it.key.y }

        println("\nmap")
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                val pos = Cord2D(x, y)
                val tile = map[pos] ?: error("No tile found for $pos")
                print(
                    when (tile.type) {
                        Tile.Type.empty -> " "
                        Tile.Type.wall -> "#"
                        Tile.Type.water_flowing -> "|"
                        Tile.Type.water_settled -> "~"
                        Tile.Type.water_settling_right -> "<"
                        Tile.Type.water_settling_left -> ">"
                    }
                )
            }
            println("|")
        }
    }

    val lastState by lazy {
        val realMinY = map.minOf { it.key.y }
        val minX = map.minOf { it.key.x } - 1
        val maxX = map.maxOf { it.key.x } + 1
        val minY = min(realMinY, 0)
        val maxY = map.maxOf { it.key.y }

        val base = (minY..maxY).flatMap { y ->
            (minX..maxX).map { x ->
                Cord2D(x, y) to Tile(Cord2D(x, y), Tile.Type.empty)
            }
        }
            .toMap()

        val start = base + map + (Cord2D(500, 0) to Tile(Cord2D(500, 0), Tile.Type.water_flowing))

        generateSequence(start.toMutableMap() to listOf(Cord2D(500, 1))) { (it, schedule) ->
            val (next, schedule) = it.step(schedule)
            if (schedule.isNotEmpty())
                next to schedule
            else
                null
        }
            .map { it.first }
            .last()
    }
    override fun solvePart1() {
        val minY = map.minOf { it.key.y }
        val maxY = map.maxOf { it.key.y }
        lastState
            .values
            .count {
                it.pos.y in minY..maxY &&
                        it.type in listOf(
                    Tile.Type.water_flowing,
                    Tile.Type.water_settled,
                    Tile.Type.water_settling_left,
                    Tile.Type.water_settling_right
                )
            }
            .solution(1)
    }

    override fun solvePart2() {
        val minY = map.minOf { it.key.y }
        val maxY = map.maxOf { it.key.y }
        lastState
            .values
            .count {
                it.pos.y in minY..maxY &&
                        it.type in listOf(
                    Tile.Type.water_settled,
                )
            }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day17>()
