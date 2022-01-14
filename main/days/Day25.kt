package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d4.Cord4D
import me.reckter.aoc.cords.d4.manhattenDistance
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day25 : Day {
    override val day = 25

    override fun solvePart1() {
        val stars = loadInput()
            .parseWithRegex("(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)")
            .map { (x, y, z, w) -> Cord4D(x.toInt(), y.toInt(), z.toInt(), w.toInt()) }

        val constellations = ArrayDeque(stars.map { mutableListOf(it) })

        var changed = true
        var last: List<List<Cord4D<Int>>> = constellations.sortedBy { it.toString() }
        var rounds = constellations.size

        while (changed) {
            val next = constellations.removeFirst()

            val match = constellations
                .find { it.any { a -> next.any { b -> a.manhattenDistance(b) <= 3 } } }

            if (match != null) {
                match.addAll(next)
            } else {
                constellations.add(next)
            }

            rounds--
            if (rounds <= 0) {
                val new = constellations.sortedBy { it.toString() }
                changed = new != last
                last = new
                rounds = constellations.size
            }
        }


        constellations.size
            .solution(1)
    }

    override fun solvePart2() {
    }
}

fun main(args: Array<String>) = solve<Day25>()
