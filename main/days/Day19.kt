package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.State
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.runProgram
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day19 : Day {
    override val day = 19

    override fun solvePart1() {

        val instructions = loadInput()
            .drop(1)

        val ip = loadInput()
            .parseWithRegex("#ip (\\d+)")
            .map { (str) -> str.toInt() }
            .first()

        val start = State(
            register = (0..3).associateWith { 0L }.toMutableMap(),
            instructionPointerRegister = ip
        )

        start.runProgram(instructions, 10000000)
            .print("end state")
            .register[0]
            .solution(1)

    }

    override fun solvePart2() {

    }
}

fun main(args: Array<String>) = solve<Day19>()
