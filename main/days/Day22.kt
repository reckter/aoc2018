package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.memoized
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.lang.Integer.max

class Day22 : Day {
    override val day = 22

    val part = 0

    val target by lazy {
        loadInput(part)
            .parseWithRegex("target: (\\d+),(\\d+)")
            .map { (x, y) -> Cord2D(x.toInt(), y.toInt()) }
            .first()
    }
    val depth by lazy {
        loadInput(part)
            .parseWithRegex("depth: (\\d+)")
            .map { (it) -> it.toInt() }
            .first()
            .toLong()
    }

    val geologicIndex by memoized { cord: Cord2D<Int> ->
        when {
            cord == Cord2D(0, 0) -> 0L
            cord == target -> 0L
            cord.y == 0 -> cord.x * 16807L
            cord.x == 0 -> cord.y * 48271L
            else -> erosion(cord.copy(x = cord.x - 1)) * erosion(cord.copy(y = cord.y - 1))
        }
    }

    val erosion: (Cord2D<Int>) -> Long by memoized { cord: Cord2D<Int> ->
        (geologicIndex(cord) + depth) % 20183L
    }

    val type by memoized { cord: Cord2D<Int> ->
        when (erosion(cord) % 3) {
            0L -> Type.rocky
            1L -> Type.wet
            2L -> Type.narrow
            else -> error("impossibru ${erosion(cord)}")
        }
    }

    enum class Type {
        rocky,
        wet,
        narrow
    }

    enum class Tool {
        torch,
        gear,
        neither
    }

    override fun solvePart1() {
        val minX = 0
        val maxX = target.x
        val minY = 0
        val maxY = target.y

        (minX..maxX)
            .asSequence()
            .flatMap { x ->
                (minY..maxY)
                    .asSequence()
                    .map { y ->
                        Cord2D(x, y)
                    }
            }
            .sumOf { erosion(it) % 3L }
            .solution(1)
    }

    val allowedTools = mapOf(
        Type.rocky to listOf(Tool.torch, Tool.gear),
        Type.wet to listOf(Tool.gear, Tool.neither),
        Type.narrow to listOf(Tool.torch, Tool.neither)
    )

    val allowedRegions = Tool.values().associateWith { tool -> allowedTools.filter { tool in it.value}.keys }

    override fun solvePart2() {
        print("warming up cache...")
        val minX = 0
        val maxX = target.x + 100
        val minY = 0
        val maxY = target.y + 100

        (minX..maxX)
            .asSequence()
            .flatMap { x ->
                (minY..maxY)
                    .asSequence()
                    .map { y ->
                        Cord2D(x, y)
                    }
            }
            .forEach { type(it) }
        println("done")

        dijkstraInt(
            Cord2D(0, 0) to Tool.torch,
            target to Tool.torch,
            { cur ->
                val moving = cur.first.getNeighbors(true)
                    .filter { it.x >= 0 && it.y >= 0 }
                    .filter {
                        type(it) in allowedRegions[cur.second]!!
                    }
                    .map { it to cur.second }

                val equip = Tool.values()
                    .filter {
                        it in allowedTools[type(cur.first)]!!
                    }
                    .filter { it != cur.second }
                    .map { cur.first to it }

                moving + equip
            },
            { a, b ->
                if (a.second != b.second) 7
                else 1
            }
        )
            .also { printWay(it?.first!!) }
            .also { println("way: $it") }
            ?.second
            .solution(2)
    }

    fun printWay(way: List<Pair<Cord2D<Int>, Tool>>) {
        val minX = 0
        val maxX = max(target.x, way.maxOf { it.first.x })
        val minY = 0
        val maxY = max(target.y, way.maxOf { it.first.y })
        val path = way.map { it.first }.toSet()


        println("map:")
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                when (val it = Cord2D(x, y)) {
                    Cord2D(0, 0) -> print("M")
                    target -> print("T")
                    in path -> print("X")
                    else -> print(
                        when (type(it)) {
                            Type.rocky -> "."
                            Type.wet -> "="
                            Type.narrow -> "|"
                        }
                    )
                }
            }
            println()
        }
    }
}

fun main(args: Array<String>) = solve<Day22>()
