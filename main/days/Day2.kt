package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.allCombinations
import me.reckter.aoc.hammingDistance
import me.reckter.aoc.solution
import me.reckter.aoc.solve

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
            .map { it.value.count()}
            .fold(1){ a, b -> a * b }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .allCombinations(bothDirections = false)
            .single { hammingDistance(it.first, it.second) == 1 }
            .let { (a,b) ->
                a.zip(b)
                    .filter { it.first == it.second }
                    .map { it.first }
                    .joinToString("")
            }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day2>()
