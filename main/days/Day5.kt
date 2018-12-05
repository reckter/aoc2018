package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day5 : Day {
    override val day = 5

    fun String.collapse(): String {
        return ('a'..'z')
            .fold(this) { acc, cur ->
                acc.replace("$cur${cur.toUpperCase()}", "")
                    .replace("${cur.toUpperCase()}$cur", "")
            }
    }

    override fun solvePart1() {
        var input = loadInput()
            .single()

        input = input.fullyCollapse()

        input.length.solution(1)
    }

    private fun String.fullyCollapse(): String {
        var input = this
        var newInput = input

        do {
            input = newInput
            newInput = input.collapse()
        } while (newInput.length != input.length)
        return input
    }

    override fun solvePart2() {
        val input = loadInput()
            .single()
            .fullyCollapse()

        ('a'..'z')
            .toList()
            .stream()
            .parallel()
            .map {
                val tmp = input
                    .replace(it.toString(), "")
                    .replace(it.toString().toUpperCase(), "")
                tmp.fullyCollapse().length
            }
            .mapToInt { it }
            .min()
            .orElseGet(null)
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day5>()
