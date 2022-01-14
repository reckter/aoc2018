package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.dijkstra
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day20 : Day {
    override val day = 20

    data class Room(
        val id: Cord2D<Int>,
        var north: Room? = null,
        var south: Room? = null,
        var west: Room? = null,
        var east: Room? = null,
    ) {
        override fun toString(): String {
            return id.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Room) return false
            return this.id == other.id
        }

        override fun hashCode(): Int {
            return this.id.hashCode()
        }
    }

    data class Buffer(
        private val src: String,
        var pointer: Int = 0,
    ) {
        fun readNext(): Char? {
            return src.getOrNull(pointer++)
        }

        fun back() {
            pointer--
        }

        fun nextIs(c: Char): Boolean {
            val next = src[pointer]
            return next == c
        }

        fun ensureNext(c: Char) {
            val next = readNext()
            if (next != c) error("Expected $c, received $next")
        }
    }

    enum class Direction {
        north,
        south,
        west,
        east
    }

    fun Cord2D<Int>.go(direction: Direction): Cord2D<Int> = when (direction) {
        Direction.north -> Cord2D(x, y - 1)
        Direction.south -> Cord2D(x, y + 1)
        Direction.west -> Cord2D(x - 1, y)
        Direction.east -> Cord2D(x + 1, y)
    }

    fun getRoom(map: MutableMap<Cord2D<Int>, Room>, current: Room, direction: Direction): Room {
        val nextCords = current.id.go(direction)
        return map.getOrPut(nextCords) { Room(nextCords) }
    }

    fun parse(
        map: MutableMap<Cord2D<Int>, Room>,
        start: Cord2D<Int>,
        path: Buffer
    ): List<Cord2D<Int>> {
        val next = path.readNext()
        when (next) {
            '(' -> {
                val ret = mutableListOf<Cord2D<Int>>()
                var end = false
                while (!end) {
                    ret += parse(map, start, path)
                    path.back()
                    end = path.nextIs(')')
                }
                return ret.distinct()
            }
            '^' -> return parse(map, start, path)
            '$' -> return emptyList()
            else -> {
                var currentRooms = listOf(map[start] ?: error("could not find room at $start"))
                var next = if (next == '|') path.readNext() else next
                while ((next ?: 'x') in "NEWS") {
                    val dir = when (next) {
                        'N' -> Direction.north
                        'E' -> Direction.east
                        'W' -> Direction.west
                        'S' -> Direction.south
                        else -> error("invalid direction!")
                    }
                    currentRooms = currentRooms.map { current ->
                        val nextRoom = getRoom(map, current, dir)
                        when (dir) {
                            Direction.north -> {
                                current.north = nextRoom
                                nextRoom.south = current
                            }
                            Direction.south -> {
                                current.south = nextRoom
                                nextRoom.north = current
                            }
                            Direction.west -> {
                                current.west = nextRoom
                                nextRoom.east = current
                            }
                            Direction.east -> {
                                current.east = nextRoom
                                nextRoom.west = current
                            }
                        }
                        nextRoom
                    }
                    next = path.readNext()
                    while ((next ?: 'x') !in "NEWS") {
                        if (next == null || next in "|)$") return currentRooms.map { it.id }
                        if (next == '(') {
                            path.back()
                            val afterRecursion = currentRooms
                                .map {
                                    val nextPath = path.copy()
                                    nextPath to parse(map, it.id, nextPath)
                                }
                            path.pointer = afterRecursion.first().first.pointer

                            currentRooms = afterRecursion
                                .flatMap { it.second }
                                .distinct()
                                .map { map[it]!! }
                            path.readNext()
                            next = path.readNext()
                        }
                    }
                }
                return currentRooms.map { it.id }
            }
        }
        error("wat")
    }


    val map by lazy {

        val buffer = loadInput()
            .first()
            .let { Buffer(it) }
        val map = mutableMapOf<Cord2D<Int>, Room>(Cord2D(0, 0) to Room(Cord2D(0, 0)))
        parse(map, Cord2D(0, 0), buffer)
         map.toMap()

    }
    override fun solvePart1() {
        val goal = map[Cord2D(0, 0)]!!

        dijkstra<Room, Int>(
            goal,
            0,
            Int::plus,
            { listOfNotNull(it.north, it.south, it.west, it.east) },
            { _, _ -> 1 }
        )
            .values
            .maxOf { it.second }
            .solution(1)
    }

    override fun solvePart2() {
        val goal = map[Cord2D(0, 0)]!!

        dijkstra<Room, Int>(
            goal,
            0,
            Int::plus,
            { listOfNotNull(it.north, it.south, it.west, it.east) },
            { _, _ -> 1 }
        )
            .values
            .count { it.second >= 1000}
            .solution(1)
    }
}

fun main(args: Array<String>) = solve<Day20>()
