package me.reckter.aoc.sausage

import java.util.ArrayDeque

fun main(args: Array<String>) {
    val Size = 20
    val start = Triple(Size, Size, listOf<Pair<Int, Int>>())

    val queue = ArrayDeque<Triple<Int,Int,List<Pair<Int,Int>>>>()
    queue.add(start)

    val winnerCount = mutableMapOf(
        1 to 0L,
        0 to 0L,
        -1 to 0L
    )
    val bestFirstMove = mutableMapOf<Int,Long>()

    var i = 0
    while (queue.isNotEmpty()) {
        val elem = queue.poll()
        if(i == 5000000) {
            i = 0
            elem.third.joinToString { it.toString() }.let { println(it) }
            println("winnerCount:")
            printWinnerCount(winnerCount)
            println("best First Move:")
            printWinnerCount(bestFirstMove)
        }
        i++
        val winner = elem.third.getWinner()
        if(winner != 0) {
            val firstMove = elem.third.first().first
            bestFirstMove[firstMove] = bestFirstMove.getOrDefault(firstMove, 0) + winner
            winnerCount[winner] = winnerCount[winner]?.plus(1) ?: 1L
//            elem.third.joinToString { it.toString() }.let { println(it) }
//            printWinnerCount(winnerCount)
            continue
        }
        if(elem.notWinnable()) {
            val firstMove = elem.third.first().first
            bestFirstMove[firstMove] = bestFirstMove.getOrDefault(firstMove, 0) + winner
            winnerCount[0] = winnerCount[0]?.plus(1) ?: 1
            continue
        }

        val next = elem.generateAllMoves()

        next.forEach(queue::addFirst)
    }


    println("winnerCount:")
    printWinnerCount(winnerCount)
    println("best First Move:")
    printWinnerCount(bestFirstMove)
    println("done")


}

private fun Triple<Int, Int, List<Pair<Int, Int>>>.notWinnable(): Boolean =
        this.third.size >= 5

fun printWinnerCount(winnerCount: Map<Int, Long>) {
    winnerCount
        .map { it }
        .joinToString("\n") { "${it.key}:${it.value}" }
        .let { println(it) }
}

fun Triple<Int, Int, List<Pair<Int, Int>>>.generateAllMoves(): List<Triple<Int, Int, List<Pair<Int, Int>>>> {
    val (firstSausage, secondSausage, game) = this
    return (0..firstSausage).flatMap { first ->
        (0..secondSausage).map { second ->
            Triple(firstSausage - first, secondSausage - second, game + (first to second))
        }
    }
}

fun List<Pair<Int, Int>>.getWinner(): Int =
    this.map {
        it.first.compareTo(it.second)
    }
        .groupBy { it }
        .filter { it.value.size >= 5 }
        .maxBy { it.value.size }
        ?.let { it.key }
        ?: 0 // not over yet
