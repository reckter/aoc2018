package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allCombinations
import me.reckter.aoc.allPairings
import me.reckter.aoc.hammingDistance
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.streams.asStream

class Day2 : Day {
    override val day = 2

    override fun solvePart1() {
        loadInput()
            .flatMap { id ->
                id.groupBy { it }
                    .map { it.value.size }
                    .filter { it in 2..3 }
                    .distinctBy { it }
            }
            .groupBy { it }
            .map { it.value.count().toLong() }
            .fold(1L) { a, b -> a * b }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .allPairings(bothDirections = false)
            .asStream()
            .parallel()
            .filter { hammingDistance(it.first, it.second) == 1 }
            .findFirst()
            .orElseGet(null)
            ?.let { (a,b) ->
                a.zip(b)
                    .filter { it.first == it.second }
                    .map { it.first }
                    .joinToString("")
            }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day2>()
