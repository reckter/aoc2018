package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Day4 : Day {
    override val day = 4

    val guardDayMap by lazy {
        loadInput()
            .parseWithRegex("\\[(.*)\\] (.*)")
            .map { (time, rest) ->
                LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) to rest
            }
            .sortedBy { it.first }
            .groupBy {
                LocalDateTime.from(
                    Duration.of(1, ChronoUnit.HOURS).addTo(it.first)
                ).dayOfYear
            }
            .mapValues { (day, values) ->
                val (guard) = "Guard #(.*?) begins shift".toRegex()
                    .matchEntire(values.first().second)
                    ?.destructured ?: error("lol")

                val asleeps = values.drop(1)
                    .fold(mutableListOf<Asleep>()) { list, current ->
                        when (current.second) {
                            "falls asleep" -> list.add(
                                Asleep(
                                    guard.toInt(),
                                    current.first.toLocalTime(),
                                    null
                                )
                            )
                            "wakes up" -> list.last().copy(
                                until = current.first.toLocalTime()
                            ).let { list.set(list.lastIndex, it) }
                            else -> error("none?")
                        }
                        list
                    }

                if (asleeps.isNotEmpty() && asleeps.last().until == null) {
                    asleeps[asleeps.lastIndex] = asleeps.last().copy(
                        until = LocalTime.of(1, 0)
                    )
                }
                if (asleeps.isEmpty()) {
                    asleeps.add(Asleep(guard.toInt(), LocalTime.MIDNIGHT, LocalTime.MIDNIGHT))
                }
                asleeps
            }
            .toList()
            .groupBy { it.second.first().id }
    }

    fun List<Pair<Int, List<Asleep>>>.findMaxDay(): Int {
        return (0..59).maxByOrNull { minute ->
            val time = LocalTime.of(0, minute)
            this.flatMap { it.second }
                .count {
                    it.from <= time &&
                            time < it.until
                }
        } ?: error("lol no max day")
    }

    fun List<Pair<Int, List<Asleep>>>.getUsageOfMinute(minute: Int): Int {
        val time = LocalTime.of(0, minute)
        return this.flatMap { it.second }
            .count {
                it.from <= time &&
                        time < it.until
            }
    }

    fun Pair<Int, List<Pair<Int, List<Asleep>>>>.getMaxMinuteMultpliedWithId() =
        this.let { (id, values) ->
            values.findMaxDay() * id
        }

    override fun solvePart1() {
        guardDayMap
            .toList()
            .maxByOrNull { (id, values) ->
                values.sumBy { (day, asleeps) ->
                    asleeps.sumBy { it.from.until(it.until, ChronoUnit.MINUTES).toInt() }
                }
            }
            ?.getMaxMinuteMultpliedWithId()
            .solution(1)
    }

    override fun solvePart2() {
        guardDayMap
            .toList()
            .maxByOrNull { (_, values) ->
                val minute = values.findMaxDay()
                values.getUsageOfMinute(minute)
            }
            ?.getMaxMinuteMultpliedWithId()
            .solution(2)
    }

    data class Asleep(
        val id: Int,
        val from: LocalTime,
        val until: LocalTime?
    )
}

fun main(args: Array<String>) = solve<Day4>()
