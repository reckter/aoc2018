package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import org.magicwerk.brownies.collections.BigList

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
        var currentMarble = 1
        var currentPlayer = 3

        val circle = BigList(listOf(2, 1, 0))

        val scores = mutableMapOf<Int, MutableList<Int>>()

        while (marbleToPlace <= marbles) {
            if (marbleToPlace % 23 == 0) {
                val score = scores.getOrDefault(currentPlayer, mutableListOf())
                score.add(marbleToPlace)

                currentMarble = (currentMarble - 7) posMod circle.size

                score.add(circle[currentMarble])
                circle.removeAt(currentMarble)

                scores[currentPlayer] = score

                marbleToPlace++
                currentPlayer = (currentPlayer + 1) posMod players
            } else {
                currentMarble =
                        if (currentMarble + 2 > circle.size)
                            (currentMarble + 3) posMod (circle.size + 1)
                        else
                            (currentMarble + 2) posMod (circle.size + 1)

                circle.add(currentMarble, marbleToPlace)
                marbleToPlace++
                currentPlayer = (currentPlayer + 1) posMod players
            }
        }

        return scores
    }

    infix fun Int.posMod(mod: Int): Int {
        val ret = this % mod
        if (ret < 0)
            return ret + mod
        return ret
    }

    fun playAndScore(players: Int, marbles: Int)=
        play(players, marbles)
            .map { it.key to it.value.map { it.toLong() }.sum() }
            .maxOf { it.second }

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
