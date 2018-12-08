package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import java.util.ArrayDeque

class Day8 : Day {
    override val day = 8

    val tree by lazy {
        val input = loadInput()
            .single()
            .split(" ")
            .toIntegers()
            .let {
                val ret = ArrayDeque<Int>()
                it.reversed().forEach { ret.push(it) }
                ret
            }

        val stack = ArrayDeque<Node>()
        var state = State.StartNode
        var lastChild: Node?

        while (input.isNotEmpty()) {
            state = when (state) {
                State.StartNode -> {
                    val newNode = Node(input.pop(), input.pop(), listOf(), listOf())
                    stack.push(newNode)
                    when {
                        newNode.childrenCount != 0 -> State.StartNode
                        newNode.metaDataCount != 0 -> State.MetaData
                        else -> State.EndNode
                    }
                }
                State.MetaData -> {
                    val currentNode = stack.pop()
                    val toSave = currentNode.copy(
                        metaDataCount = currentNode.metaDataCount - 1,
                        metaData = currentNode.metaData + input.pop()
                    )
                    stack.push(toSave)
                    when {
                        toSave.metaDataCount != 0 -> State.MetaData
                        else -> State.EndNode
                    }
                }
                State.EndNode -> {
                    lastChild = stack.pop()

                    val parent = stack.pop()
                    val toSave = parent.copy(
                        childrenCount = parent.childrenCount - 1,
                        children = parent.children + lastChild!!
                    )

                    stack.push(toSave)

                    when {
                        toSave.childrenCount != 0 -> State.StartNode
                        toSave.metaDataCount != 0 -> State.MetaData
                        else -> State.EndNode
                    }
                }
            }
        }

        stack.pop()
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

    enum class State {
        StartNode,
        MetaData,
        EndNode
    }

    data class Node(
        val childrenCount: Int,
        val metaDataCount: Int,
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
