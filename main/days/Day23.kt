package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d3.Cord3D
import me.reckter.aoc.cords.d3.manhattenDistance
import me.reckter.aoc.cords.d3.plus
import me.reckter.aoc.memoize
import me.reckter.aoc.memoized
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.min
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

class Day23 : Day {
    override val day = 23

    data class Nanobot(
        val pos: Cord3D<Long>,
        val range: Long
    )

    val nanobots by lazy {
        loadInput()
            .parseWithRegex("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(-?\\d+)")
            .map { (x, y, z, r) ->
                Nanobot(Cord3D(x.toLong(), y.toLong(), z.toLong()), r.toLong())
            }
    }

    data class Cube(
        val min: Cord3D<Long>,
        val max: Cord3D<Long>,
    ) {
        val corners by lazy {
            listOf(
                min,
                Cord3D(min.x, min.y, max.z),
                Cord3D(min.x, max.y, min.z),
                Cord3D(max.x, min.y, min.z),

                Cord3D(max.x, max.y, min.z),
                Cord3D(max.x, min.y, max.z),
                Cord3D(min.x, max.y, max.z),
                max
            )
        }

        fun split(): List<Cube> {
            val tmp = (min + max)
            val mid = Cord3D(
                floor(tmp.x.toDouble() / 2.0).toLong(),
                floor(tmp.y.toDouble() / 2.0).toLong(),
                floor(tmp.z.toDouble() / 2.0).toLong()
            )
            return listOf(
                Cube(min, mid),
                Cube(min.copy(x = mid.x + 1), mid.copy(x = max.x)),
                Cube(min.copy(y = mid.y + 1), mid.copy(y = max.y)),
                Cube(min.copy(x = mid.x + 1, y = mid.y + 1), max.copy(z = mid.z)),

                Cube(min.copy(z = mid.z + 1), mid.copy(z = max.z)),
                Cube(mid.copy(x = min.x) + Cord3D(0, 1, 1), max.copy(x = mid.x)),
                Cube(mid.copy(y = min.y) + Cord3D(1, 0, 1), max.copy(y = mid.y)),
                Cube(mid + Cord3D(1, 1, 1), max),
            )
        }
    }

    fun intersect(cube: Cube, bot: Nanobot): Boolean {
        if (cube.corners.any { it.manhattenDistance(bot.pos) <= bot.range }) return true

        if (bot.pos.x in cube.min.x..cube.max.x && bot.pos.y in cube.min.y..cube.max.y) {
            return bot.pos.z in (cube.min.z - bot.range)..(cube.max.z + bot.range)
        }

        if (bot.pos.y in cube.min.y..cube.max.y && bot.pos.z in cube.min.z..cube.max.z) {
            return bot.pos.x in (cube.min.x - bot.range)..(cube.max.x + bot.range)
        }

        if (bot.pos.x in cube.min.x..cube.max.x && bot.pos.z in cube.min.z..cube.max.z) {
            return bot.pos.y in (cube.min.y - bot.range)..(cube.max.y + bot.range)
        }
        return false
    }

    override fun solvePart1() {
        val max = nanobots.maxOf { it.range }

        nanobots.filter { it.range == max }
            .maxOf { bot ->
                nanobots.count { it.pos.manhattenDistance(bot.pos) <= bot.range }
            }
            .solution(1)
    }

    val countBotsInRange by memoized { cord: Cord3D<Long> ->
        nanobots.count { it.pos.manhattenDistance(cord) <= it.range }
    }

    fun gradientDescent(start: Cord3D<Long>): Pair<Cord3D<Long>, Int> {
        return generateSequence(start to countBotsInRange(start)) { (pos, current) ->
            val range = 10L
            val next = (0L..range)
                .asSequence()
                .flatMap { x ->
                    (0..range - x)
                        .asSequence()
                        .flatMap { y ->
                            (0..range - x - y)
                                .asSequence()
                                .flatMap { z ->
                                    listOf(
                                        pos + Cord3D(x, y, z),
                                        pos + Cord3D(x, y, -z),
                                        pos + Cord3D(x, -y, z),
                                        pos + Cord3D(x, -y, -z),

                                        pos + Cord3D(-x, y, z),
                                        pos + Cord3D(-x, y, -z),
                                        pos + Cord3D(-x, -y, z),
                                        pos + Cord3D(-x, -y, -z),
                                    )
                                        .distinct()
                                }
                        }
                }
                .filter { it != pos }
                .map { it to countBotsInRange(it) }
                .maxByOrNull { it.second }

            if (next != null && next.second > current) {
                next
            } else null
        }
            .last()
    }

    fun countForCube(cube: Cube): Int {
        return nanobots.count { intersect(cube, it) }
    }

    fun guess(cube: Cube): Cord3D<Long> {
        val queue = PriorityQueue<Pair<Cube, Int>>(Comparator.comparing { -it.second })
        queue.add(cube to countForCube(cube))

        while (queue.isNotEmpty()) {
            val (next, nextCount) = queue.remove()

            if (next.min == next.max) {
                return next.min
            }

            next.split()
                .map { it to countForCube(it) }
                .forEach { queue.add(it) }
        }
        error("no")
    }

    override fun solvePart2() {
        val minX = nanobots.minOf { it.pos.x - it.range }
        val maxX = nanobots.maxOf { it.pos.x + it.range }

        val minY = nanobots.minOf { it.pos.y - it.range }
        val maxY = nanobots.maxOf { it.pos.y + it.range }

        val minZ = nanobots.minOf { it.pos.z - it.range }
        val maxZ = nanobots.maxOf { it.pos.z + it.range }

        val start = Cube(Cord3D(minX, minY, minZ), Cord3D(maxX, maxY, maxZ))
        val highest = generateSequence(start) {
            val guess = guess(it)
            println(guess)
            val distance = guess.manhattenDistance(Cord3D(0, 0, 0))
            val max = Cord3D(distance, distance, distance)
            val min = Cord3D(-max.x, -max.y, -max.z)

            val next = Cube(min, max)
            if (it == next) null else next
        }
            .last()
            .let { guess(it) }

        gradientDescent(highest)
            .first
            .manhattenDistance(Cord3D(0,0,0))
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day23>()
