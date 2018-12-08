package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers

class Day8 : Day {
    override val day = 8

    val tree by lazy {
        val input = loadInput()
            .single()
            .split(" ")
            .toIntegers()

        var pos = 0

        fun parseNode(): Node {
            val childrenCount = input[pos++]
            val metaCount = input[pos++]
            return Node(
                children = (0 until childrenCount).map { parseNode() },
                metaData = (0 until metaCount).map { input[pos++] }
            )
        }
         parseNode()
    }

    override fun solvePart1() {
        tree
            .foldChildren(0) { sum, node ->
                sum + node.metaData.sum()
            }
            .solution(1)
    }

    override fun solvePart2() {
        score(tree)
            .solution(2)
    }

    fun score(node: Node): Int =
        when {
            node.children.isEmpty() -> node.metaData.sum()
            else -> node.metaData
                .mapNotNull {
                    node.children.getOrNull(it - 1)
                }
                .sumBy { score(it) }
        }

    data class Node(
        val metaData: List<Int>,
        val children: List<Node>
    ) {
        fun <E> foldChildren(initial: E, fold: (E, Node) -> E): E {
            return this.children.fold(fold(initial, this)) { acc, node ->
                node.foldChildren(acc, fold)
            }
        }
    }
}

fun main(args: Array<String>) = solve<Day8>()
