package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.State
import me.reckter.aoc.bisectInt
import me.reckter.aoc.get
import me.reckter.aoc.instructionPointer
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.run
import me.reckter.aoc.runProgram
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.time

class Day21 : Day {
    override val day = 21

    override fun solvePart1() {
        val instructions = loadInput(1)
            .drop(1)

        val ip = loadInput(1)
            .parseWithRegex("#ip (\\d+)")
            .map { (str) -> str.toInt() }
            .first()


        State(
            register = mutableMapOf(0 to 0),
            instructionPointerRegister = ip
        )
            .runProgram(instructions)
            .first()
            .first
            .solution(1)
    }

    fun State.runProgram(program: List<String>): Sequence<Pair<Long, Int>> {
        return generateSequence<Pair<State, Pair<Long, Int>?>>(this to null) { (current, _) ->
            if (current.instructionPointer in program.indices) {
                val instruction = program[current.instructionPointer.toInt()]
                val next = current.run(instruction)
                if (next.instructionPointer == 28L) {
                    next to (next.get(3) to next.instructionCount)
                } else next to null
            } else null
        }
            .mapNotNull { it.second }
    }

    override fun solvePart2() {
        val instructions = loadInput(1)
            .drop(1)

        val ip = loadInput(1)
            .parseWithRegex("#ip (\\d+)")
            .map { (str) -> str.toInt() }
            .first()


        val seq = State(
            register = mutableMapOf(0 to 0),
            instructionPointerRegister = ip
        )
            .runProgram(instructions)

        val seen = mutableSetOf<Long>()
        var last = 0L
        for(next in seq) {
            if(next.first in seen) {
                break
            }
            last = next.first
            seen.add(last)
        }
        last.solution(2)
    }
}

fun main(args: Array<String>) = solve<Day21>()
