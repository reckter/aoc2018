package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers

class Day1 : Day {
    override val day = 1

    override fun solvePart1() {
        loadInput()
            .toIntegers()
            .sum()
            .solution(1)
    }

    override fun solvePart2() {

        loadInput()
            .toIntegers()
            .let { input ->
                var index = 0
                generateSequence {
                    if (index == input.size)
                        index = 0
                    input[index++]
                }
            }
            .let { seq ->

                seq.fold(0 to mutableSetOf<Int>()) { (freq, frequencies), cur ->
                    val ret = freq + cur
                    if (ret in frequencies) {
                        return@let ret
                    }
                    frequencies.add(ret)

                    ret to frequencies
                }
            }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day1>()
