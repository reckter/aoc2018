package me.reckter.aoc

import java.io.File
import java.nio.file.Files
import kotlin.reflect.full.createInstance

inline fun <reified T : Day> solve(
    enablePartOne: Boolean = true,
    enablePartTwo: Boolean = true
) {
    val day = T::class.createInstance()

    if (enablePartOne)
        day.solvePart1()

    if (enablePartTwo)
        day.solvePart2()
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

