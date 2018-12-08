package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.uppercaseAlphabetString

class Day7 : Day {
    override val day = 7

    val map by lazy {
        loadInput()
            .parseWithRegex("Step (.*) must be finished before step (.*) can begin.")
            .map { (dependency, step) ->
                Step(step, listOf(dependency))
            }
            .groupBy { it.name }
            .mapValues { (name, steps) ->
                Step(
                    name,
                    steps.flatMap { it.directDependencies }
                )
            }
            .let {
                var map = it
                val steps = it.flatMap { listOf(it.key) + it.value.directDependencies }
                steps.forEach { name ->
                    if (!map.containsKey(name)) {
                        map += (name to Step(name, listOf()))
                    }
                }
                map
            }
    }

    override fun solvePart1() {
        var map = map.toMutableMap()

        var result = ""
        while (map.isNotEmpty()) {
            val (name, step) = map.filter { it.value.allDependencies.isEmpty() }
                .toList()
                .sortedBy { it.first }
                .first()

            result += name

            map.remove(name)

            map = map.mapValues { (_, step) ->
                step.copy(
                    allDependencies = step.allDependencies - name
                )
            }
                .toMutableMap()
        }

        result.solution(1)
    }

    override fun solvePart2() {
        val fixedTime = 60
        var map = map.toMutableMap()
        var time = -1
        val agents = (0..4).map { it to listOf<Pair<Step, Int>>() }
            .toMap()
            .toMutableMap()

        while (map.isNotEmpty()) {
            time++
            agents
                .toList()
                .filter { (agent, steps) ->
                    val (step, startTime) = steps.lastOrNull()
                        ?: return@filter false

                    startTime + fixedTime + uppercaseAlphabetString.indexOf(step.name) + 1 == time
                }
                .forEach { (agent, steps) ->
                    val (step,  _) = steps.last()
                    val name = step.name

                    map = map.mapValues { (_, step) ->
                        step.copy(
                            allDependencies = step.allDependencies - name
                        )
                    }
                        .toMutableMap()
                }

            val stepsToStart= map.filter { it.value.allDependencies.isEmpty() }
                .toList()
                .sortedBy { it.first }

            stepsToStart.forEach {(name, step) ->
                val (agentId, jobs) = agents
                    .toList()
                    .find { (agent, steps) ->
                        val (step, startTime) = steps.lastOrNull()
                            ?: return@find true
                        startTime + fixedTime + uppercaseAlphabetString.indexOf(step.name) + 1 <= time
                    }
                    ?: return@forEach

                map.remove(name)

                agents[agentId] = jobs + (step to time)
            }
        }

        val endTime = agents.map { (id, jobs) ->
            val (step, startTime) = jobs.lastOrNull()
                ?: return@map 0

            startTime + fixedTime + uppercaseAlphabetString.indexOf(step.name) + 1
        }.max()

        endTime.solution(2)
    }

    data class Step(
        val name: String,
        val directDependencies: List<String>,
        val allDependencies: List<String> = directDependencies
    )
}

fun main(args: Array<String>) = solve<Day7>()
