package me.reckter.aoc

import java.io.File
import java.nio.file.Files
import kotlin.reflect.full.createInstance
import kotlin.system.measureNanoTime

fun Long.logTime(soluion: String) {
    val timeSString = when {
        this > 1_000_000_000 -> "${this.toDouble() / 1_000_000_000.0}s"
        this > 1_000_000 -> "${this.toDouble() / 1_000_000.0}ms"
        this > 1_000 -> "${this.toDouble() / 1_000.0}μs"
        else -> "${this}ns"
    }
    println("$soluion took: $timeSString")
}
inline fun <reified T : Day> solve(
    enablePartOne: Boolean = true,
    enablePartTwo: Boolean = true
) {
    val day = T::class.createInstance()

    val partOneNanos = if (enablePartOne) {
        measureNanoTime { day.solvePart1() }
    } else null

    val partTwoNanos = if (enablePartTwo) {
        measureNanoTime {  day.solvePart2() }
    } else null

    println()
    partOneNanos?.logTime("solution 1")
    partTwoNanos?.logTime("solution 2")

}

fun readLines(file: String): List<String> {
    return Files.readAllLines(File(file).toPath())
}

fun List<String>.toIntegers(): List<Int> = this.map { it.toInt() }

fun List<String>.toDoubles(): List<Double> = this.map { it.toDouble() }

fun List<String>.parseWithRegex(regexString: String): List<MatchResult.Destructured> =
    this
        .mapNotNull(Regex(regexString)::matchEntire)
        .map { it.destructured }

fun List<String>.categorizeWithRegex(vararg regexes: String): List<List<MatchResult.Destructured>> =
    regexes
        .map {
            this.parseWithRegex(it)
        }

fun <E> List<String>.matchWithRegexAndParse(vararg matchers: Pair<Regex, (MatchResult.Destructured) -> E>): List<E> =
    this
        .map { line ->
            matchers
                .mapNotNull { (regex, parser) ->
                    val match = regex.matchEntire(line)
                    match?.destructured?.to(parser)
                }
                .map { (match, parser) -> parser(match) }
                .first()
        }

fun <E> List<String>.matchAndParse(vararg matchers: Pair<String, (MatchResult.Destructured) -> E>): List<E> =
    this.matchWithRegexAndParse(*matchers.map { Regex(it.first) to it.second }.toTypedArray())

fun <E> List<E>.pairWithIndex(indexer: (index: Int) -> Int): List<Pair<E, E>> =
    this.mapIndexed { index, elem -> elem to this[indexer(index) % this.size] }

fun <E> List<E>.pairWithIndexAndSize(indexer: (index: Int, size: Int) -> Int): List<Pair<E, E>> =
    this.mapIndexed { index, elem -> elem to this[indexer(index, this.size) % this.size] }

fun Any?.print(name: String) = println("$name: $this")

fun Any?.solution(part: Int) = this.print("Solution $part")

fun <E> List<E>.allCombinations(
    includeSelf: Boolean = false,
    bothDirections: Boolean = true
): Sequence<Pair<E, E>> {
    return this
        .asSequence()
        .mapIndexed { index, it ->
            val other = if (bothDirections)
                this
            else
                this.subList(index, this.size)

            other.mapNotNull { other ->
                if (it != other || includeSelf)
                    it to other
                else null
            }
        }
        .flatten()
}

fun hammingDistance(a: String, b: String) =
    a.zip(b)
        .count { (a, b) -> a != b } +
            a.length - b.length


val alphabet = ('a'..'z').toList()
