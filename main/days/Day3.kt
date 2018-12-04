package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day3 : Day {
    override val day = 3


    val rectangles: List<Rectangle> by lazy {
        loadInput()
            .parseWithRegex("#(.*?) @ (.*?),(.*?): (.*?)x(.*?)")
            .map { (id, left, top, width, height) ->
                Rectangle(
                    id = id,
                    left = left.toInt(),
                    top = top.toInt(),
                    width = width.toInt(),
                    height = height.toInt()
                )
            }
    }
    val usageMap: Map<Int,Map<Int, List<String>>> by lazy {
        rectangles
            .fold(mutableMapOf<Int,MutableMap<Int,List<String>>>()) { acc, cur ->
                (0 until cur.width).map { x ->
                    val xMap = acc.getOrDefault(cur.left + x, mutableMapOf())
                    (0 until cur.height).map { y ->
                        xMap[cur.top + y] = xMap.getOrDefault(cur.top + y, listOf()) + cur.id
                    }
                    acc[cur.left + x] = xMap
                }
                acc
            }
    }

    override fun solvePart1() {
        usageMap.flatMap { it.value.values.map {  it.count()} }
            .filter { it > 1}
            .count()
            .solution(1)
    }

    override fun solvePart2() {
        usageMap.flatMap { it.value.values }
            .fold(rectangles.map { it.id to true }.toMap().toMutableMap()) { acc, cur ->
                if(cur.size > 1) {
                    cur.forEach {
                        acc[it] = false
                    }
                }
                acc
            }
            .toList()
            .single { it.second }
            .first
            .solution(2)
    }

    data class Rectangle(
        val id: String,
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int
    )
}


fun main(args: Array<String>) = solve<Day3>()
