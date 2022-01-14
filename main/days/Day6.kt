package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day6 : Day {
    override val day = 6

    val points by lazy {
        loadInput()
            .parseWithRegex("(.*?), (.*?)")
            .mapIndexed { index, (x, y) ->
                Point(
                    x = x.toInt(),
                    y = y.toInt(),
                    name = index.toString()
                )
            }
    }

    val map by lazy {

        val minX = points
            .map { it.x }
            .minOrNull()
            ?: error("lol")

        val maxX = points
            .map { it.x }
            .maxOrNull()
            ?: error("lol")

        val minY = points
            .map { it.y }
            .minOrNull()
            ?: error("lol")

        val maxY = points
            .map { it.y }
            .maxOrNull()
            ?: error("lol")

        (minX..maxX).flatMap { x ->
            (minY..maxY).map { y ->
                Point(x, y)
            }
        }
            .associateBy { it.x to it.y }
    }

    override fun solvePart1() {

        val map = map.toMutableMap()

        map.forEach { (coords, point) ->
            val nearest = points
                .groupBy {
                    Math.abs(it.x - point.x) + Math.abs(it.y - point.y)
                }
                .minByOrNull { it.key }
                ?.value
                .orEmpty()

            map[coords] = point.copy(
                nearest = nearest
                    .singleOrNull()
                    ?.name
            )
        }

        val rim = map.getRim()
            .map { it.nearest }
            .distinct()

        map.values
            .groupBy { it.nearest }
            .filter { (key, values) ->
                rim.none { it == key }
            }
            .maxByOrNull { it.value.count() }
            ?.value
            ?.count()
            .solution(1)
    }

    override fun solvePart2() {
        map
            .filter { (_, point) ->
                points.sumBy {
                    Math.abs(it.x - point.x) + Math.abs(it.y - point.y)
                } < 10000
            }
            .count()
            .solution(2)
    }

    data class Point(
        val x: Int,
        val y: Int,
        val name: String? = null,
        val nearest: String? = name
    )

    fun <E : Any> Map<Pair<Int, Int>, E>.getRim(): List<E> {
        val points = this.keys
        val minX = points
            .minOfOrNull { it.first }
            ?: error("lol")

        val maxX = points
            .maxOfOrNull { it.first }
            ?: error("lol")

        val minY = points
            .minOf { it.second }

        val maxY = points
            .maxOf { it.second }
            ?: error("lol")

        val xRim = (minX..maxX).flatMap { x ->
            listOfNotNull(
                this[x to minY],
                this[x to maxY]
            )
        }
        val yRim = (minY..maxY).flatMap { y ->
            listOfNotNull(
                this[minX to y],
                this[maxX to y]
            )
        }

        return yRim + xRim
    }
}

fun main(args: Array<String>) = solve<Day6>()
