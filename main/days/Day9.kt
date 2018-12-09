package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.rotate
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.util.LinkedList

class Day9 : Day {
    override val day = 9

    val marbles: Int
    val players: Int

    init {
        loadInput()
            .single()
            .let {
                "(.*?) players; last marble is worth (.*?) points"
                    .toRegex()
                    .matchEntire(it)!!
                    .destructured
            }.let { (players, marbles) ->
                this.players = players.toInt()
                this.marbles = marbles.toInt()
            }
    }

    fun play(players: Int, marbles: Int): MutableMap<Int, MutableList<Int>> {
        var marbleToPlace = 3
        var currentPlayer = 3

        val circle = LinkedList(listOf(2, 1, 0))

        val scores = mutableMapOf<Int, MutableList<Int>>()

        while (marbleToPlace <= marbles) {
            if (marbleToPlace % 23 == 0) {
                val score = scores.getOrDefault(currentPlayer, mutableListOf())
                score.add(marbleToPlace)

                circle.rotate(-7)

                score.add(circle.first)
                circle.removeFirst()

                scores[currentPlayer] = score

                marbleToPlace++
                currentPlayer = (currentPlayer + 1) % players
            } else {
                circle.rotate(2)
                circle.addFirst(marbleToPlace)

                marbleToPlace++
                currentPlayer = (currentPlayer + 1) % players
            }
        }

        return scores
    }

    fun playAndScore(players: Int, marbles: Int) =
        play(players, marbles)
            .map { it.key to it.value.map { it.toLong() }.sum() }
            .maxBy { it.second }
            ?.second

    override fun solvePart1() {
        playAndScore(players, marbles)
            .solution(1)
    }

    override fun solvePart2() {
        playAndScore(players, marbles * 100)
            .solution(1)
    }
}

fun main(args: Array<String>) = solve<Day9>()
