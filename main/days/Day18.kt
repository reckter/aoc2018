package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day18 : Day {
    override val day = 18

    enum class Tile {
        open,
        tree,
        lumber
    }

    fun Map<Cord2D<Int>, Tile>.tick(): Map<Cord2D<Int>, Tile> {
        return this.mapValues { (pos, tile) ->
            val neighbors = pos.getNeighbors().mapNotNull { this[it] }
            val lumbers = neighbors.count { it == Tile.lumber }
            val trees = neighbors.count { it == Tile.tree }
            when (tile) {
                Tile.open -> {
                    if (trees >= 3) Tile.tree
                    else Tile.open
                }
                Tile.tree -> {
                    if (lumbers >= 3) Tile.lumber
                    else Tile.tree
                }
                Tile.lumber -> {
                    if (lumbers > 0 && trees > 0) Tile.lumber
                    else Tile.open
                }
            }
        }
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
                    when (tile) {
                        Tile.open -> "."
                        Tile.tree -> "|"
                        Tile.lumber -> "#"
                    }
                )
            }
            println("|")
        }
    }

    val start by lazy {
        loadInput()
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, char ->
                    Cord2D(x, y) to when (char) {
                        '.' -> Tile.open
                        '|' -> Tile.tree
                        '#' -> Tile.lumber
                        else -> error("invalid chat $char")
                    }
                }
            }
            .toMap()
    }

    override fun solvePart1() {
        (0 until 10)
            .fold(start) { acc, _ ->
                acc.tick()
            }
            .let {
                it.values.count { it == Tile.tree } * it.values.count { it == Tile.lumber }
            }
            .solution(1)
    }

    override fun solvePart2() {
        val cache = mutableMapOf<Map<Cord2D<Int>, Tile>, Int>()
        val stepCache = mutableMapOf<Int, Map<Cord2D<Int>, Tile>>()

        var found = false
        var map = start
        var step = 0
        while (!found) {
            map = map.tick()
            step += 1
            found = cache.contains(map)
            if (!found) {
                cache[map] = step
                stepCache[step] = map
            }
        }

        val loopStart = cache[map]!!
        val loopEnd = step
        val loop = loopEnd - loopStart

        val leftForLoop = (1000000000 - loopStart)
        val leftAfterLoop = leftForLoop % loop

        val final = stepCache[loopStart + leftAfterLoop]!!


        final.let {
            it.values.count { it == Tile.tree } * it.values.count { it == Tile.lumber }
        }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day18>()
