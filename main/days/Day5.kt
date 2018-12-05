package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day5 : Day {
    override val day = 5

    fun collapse(it: String): String {
        return ('a'..'z')
            .fold(it) {acc, cur ->
                acc.replace("$cur${cur.toUpperCase()}", "")
                    .replace("${cur.toUpperCase()}$cur", "")
            }
    }

    override fun solvePart1() {
        var input = loadInput()
            .single()

        input = fullyCollapse(input)

        input.length.solution(1)
    }

    private fun fullyCollapse(input: String): String {
        var input1 = input
        var newInput = input1

        do {
            input1 = newInput
            newInput = collapse(input1)
        } while (newInput.length != input1.length)
        return input1
    }

    override fun solvePart2() {
        val input = loadInput()
            .single()

        ('a'..'z')
            .toList()
            .stream()
            .parallel()
            .map {
                val tmp = input
                    .replace(it.toString(), "")
                    .replace(it.toString().toUpperCase(), "")
                fullyCollapse(tmp).length
            }
            .mapToInt { it }
            .min()
            .orElseGet(null)
            .solution(2)

    }
}

fun main(args: Array<String>) = solve<Day5>()
