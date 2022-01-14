package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.bisectInt
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day15 : Day {
    override val day = 15

    val part: Int = 0

    data class Entity(
        val id: String,
        val type: Type,
        val pos: Cord2D<Int>,
        val hitpoints: Int = 200,
        val power: Int = 3
    ) {
        enum class Type {
            elf,
            goblin
        }
    }

    val map by lazy {
        loadInput(part)
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    Cord2D(x, y) to when (c) {
                        '#' -> false
                        'E' -> true
                        'G' -> true
                        '.' -> true
                        else -> error("Invalid tile: $c")
                    }
                }
            }
            .filter { it.second }
            .toMap()
    }

    fun round(characters: List<Entity>): Pair<List<Entity>, Boolean> {
        var endedInMiddleOfTurn = false
        val initiative = characters.sortedWith(Comparator.comparingInt<Entity> { it.pos.y }
            .thenComparingInt { it.pos.x })
        val chars = characters.associateBy { it.id }.toMutableMap()

        initiative.forEach { it ->

            if (chars[it.id] == null) return@forEach

            if (chars.values.map { it.type }.distinct().size == 1) {
                endedInMiddleOfTurn = true
            }
            val cur = chars[it.id]!!

            // find suitable spot to walk to
            val spots = chars.values.filter { it.type != cur.type }
                .flatMap { it.pos.getNeighbors(true) }
                .distinct()
                .filter { map[it] ?: false }

            val allWays = spots
                .mapNotNull { pos ->
                    dijkstraInt(
                        cur.pos,
                        pos,
                        {
                            it
                                .getNeighbors(true)
                                .filter { map[it] ?: false }
                                .filter { pos -> chars.values.none { it.pos == pos } }
                        },
                        { _, _ -> 1 },
                        Comparator.comparingInt<List<Cord2D<Int>>> { it[1].y }
                            .thenComparingInt { it[1].x }
                    )?.let { pos to it }
                }

            val goto = allWays
                .sortedWith(Comparator.comparingInt<Pair<Cord2D<Int>, Pair<List<Cord2D<Int>>, Int>>?> { it.second.second }
                    .thenComparingInt { it.first.y }.thenComparingInt { it.first.x })
                .firstOrNull()

            if (goto != null && goto.first != cur.pos) {
                chars[cur.id] = cur.copy(pos = goto.second.first[1])
            }

            val afterMove = chars[cur.id]!!

            val enemyToHit = afterMove.pos.getNeighbors(true)
                .mapNotNull { pos -> chars.values.find { it.pos == pos } }
                .filter { it.type != afterMove.type }
                .sortedWith(Comparator.comparingInt<Entity> { it.hitpoints }
                    .thenComparingInt { it.pos.y }.thenComparingInt { it.pos.x })
                .firstOrNull()

            if (enemyToHit != null) {
                val newEnemy = enemyToHit.copy(hitpoints = enemyToHit.hitpoints - afterMove.power)
                if (newEnemy.hitpoints <= 0)
                    chars.remove(newEnemy.id)
                else chars[newEnemy.id] = newEnemy
            }
        }
        return chars.values.toList() to endedInMiddleOfTurn
    }

    fun simulate(characters: List<Entity>): Pair<List<Entity>, Int> {
        return generateSequence(characters to 0) { (cur, count) ->
            if (cur.map { it.type }.distinct().size == 1) {
                return@generateSequence null
            }
            val (next, endedInMiddle) = round(cur)
            next to (count + if (endedInMiddle) 0 else 1)
        }
            .last()
    }

    override fun solvePart1() {
        val characters = loadInput(part)
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    when (c) {
                        'E' -> Entity("$x-$y", Entity.Type.elf, Cord2D(x, y))
                        'G' -> Entity("$x-$y", Entity.Type.goblin, Cord2D(x, y))
                        else -> null
                    }
                }
            }
            .filterNotNull()

        simulate(characters)
            .let { it.second * it.first.sumOf { it.hitpoints } }
            .solution(1)
    }

    fun printMap(chars: List<Entity>) {
        val minX = map.minOf { it.key.x } - 1
        val maxX = map.maxOf { it.key.x } + 1
        val minY = map.minOf { it.key.y } - 1
        val maxY = map.maxOf { it.key.y } + 1

        println("\nmap")
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                val pos = Cord2D(x, y)
                val entity = chars.find { it.pos == pos }
                if (entity != null) {
                    print(
                        when (entity.type) {
                            Entity.Type.elf -> "E"
                            Entity.Type.goblin -> "G"
                        }
                    )
                } else {
                    print(if (map.contains(pos)) "." else "#")
                }
            }
            println(
                " ${
                    chars.filter { it.pos.y == y }.sortedBy { it.pos.x }.joinToString(", ") {
                        "${
                            when (it.type) {
                                Entity.Type.elf -> "E"
                                Entity.Type.goblin -> "G"
                            }
                        }(${it.hitpoints})"
                    }
                }"
            )
        }
    }

    override fun solvePart2() {
        val characters = loadInput(part)
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    when (c) {
                        'E' -> Entity("$x-$y", Entity.Type.elf, Cord2D(x, y))
                        'G' -> Entity("$x-$y", Entity.Type.goblin, Cord2D(x, y))
                        else -> null
                    }
                }
            }
            .filterNotNull()

        val powerNeeded = bisectInt(4, 100) { power ->
            println("evaulating $power")
            val chars = characters.map {
                if (it.type == Entity.Type.elf)
                    it.copy(power = power)
                else it
            }

            val endState = simulate(chars)

            endState.first.count { it.type == Entity.Type.elf } == characters.count { it.type == Entity.Type.elf }
        }
        val chars = characters.map {
            if (it.type == Entity.Type.elf)
                it.copy(power = powerNeeded)
            else it
        }
        simulate(chars)
            .let { it.second * it.first.sumOf { it.hitpoints } }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day15>()
