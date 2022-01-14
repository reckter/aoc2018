package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.bisectInt
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day24 : Day {
    override val day = 24

    data class Group(
        val id: String,
        val units: Int,
        val hitpoints: Int,

        val initiative: Int,
        val damage: Int,
        val damageType: String,
        val immunities: List<String>,
        val weaknesses: List<String>,
        val type: Type
    ) {
        enum class Type {
            immuneSystem,
            infection
        }
    }

    fun parseGroup(string: String, type: Group.Type, id: String): Group {
        val (units, hitpoints, immunitiesString, damage, damageType, initiative) = Regex("(\\d+) units each with (\\d+) hit points ?(\\(.*?\\))? with an attack that does (\\d+) (\\w*?) damage at initiative (\\d+)")
            .matchEntire(string)
            ?.destructured ?: error("could not parse: $string")

        val parts = immunitiesString
            .removePrefix("(")
            .removeSuffix(")")
            .split(";")
            .map { it.trim() }

        val immunities = parts.parseWithRegex("immune to (.*)")
            .flatMap { (it) -> it.split(", ") }
        val weaknesses = parts.parseWithRegex("weak to (.*)")
            .flatMap { (it) -> it.split(", ") }

        return Group(
            id,
            units.toInt(),
            hitpoints.toInt(),
            initiative.toInt(),
            damage.toInt(),
            damageType,
            immunities,
            weaknesses,
            type
        )
    }

    fun calculateDamage(from: Group, to: Group): Int {
        val power = from.damage * from.units
        if (from.damageType in to.immunities) return 0

        if (from.damageType in to.weaknesses) return power * 2
        return power
    }

    fun doDamage(from: Group, to: Group): Group {
        return to.copy(
            units = to.units - calculateDamage(from, to) / to.hitpoints
        )
    }

    fun chooseTargets(groups: List<Group>): Map<String, String> {
        val order = groups.sortedWith(Comparator.comparingInt<Group?> { -it.damage * it.units }
            .thenComparingInt { -it.initiative })
        val targets = mutableMapOf<String, String>()
        order
            .forEach { attacker ->
                val target = groups
                    .filter { it.type != attacker.type }
                    .filter { it.id !in targets.values }
                    .filter { calculateDamage(attacker, it) > 0 }
                    .maxWithOrNull(Comparator
                        .comparingInt<Group> { calculateDamage(attacker, it) }
                        .thenComparingInt { it.damage * it.units }
                        .thenComparingInt { it.initiative }
                    )
                if (target != null) {
                    targets[attacker.id] = target.id
                }
            }

        return targets
    }

    fun tick(groups: List<Group>): List<Group> {
        val map = groups.associateBy { it.id }
            .toMutableMap()
        val targets = chooseTargets(groups)

        groups
            .asSequence()
            .sortedByDescending { it.initiative }
            .mapNotNull { map[it.id] }
            .filter { it.units > 0 }
            .mapNotNull { targets[it.id]?.let { targetId -> it to map[targetId]!! } }
            .forEach { (attacker, target) ->
                map[target.id] = doDamage(attacker, target)
            }
        return map.values.filter { it.units > 0 }.toList()
    }

    val part = 0
    val immuneSystem by lazy {
        loadInput(part, trim = false)
            .dropWhile { it != "Immune System:" }
            .drop(1)
            .takeWhile { it != "" }
            .mapIndexed { i, it -> parseGroup(it, Group.Type.immuneSystem, "immune $i") }
    }

    val infection by lazy {
        loadInput(part, trim = false)
            .dropWhile { it != "Infection:" }
            .drop(1)
            .takeWhile { it != "" }
            .mapIndexed { i, it -> parseGroup(it, Group.Type.infection, "infection $i") }
    }

    private fun fight(
        initialGroups: List<Group>
    ) = generateSequence(initialGroups.map { it.copy() }.sortedBy { it.id }) { groups ->
        if (groups.isEmpty()) null
        else if (groups.map { it.type }.distinct().size == 1) null
        else {
            val next = tick(groups).sortedBy { it.id }
            if (next == groups)
                emptyList()
            else next
        }
    }
        .last()

    override fun solvePart1() {
        fight(immuneSystem + infection)
            .sumOf { it.units }
            .solution(1)
    }

    override fun solvePart2() {
        generateSequence(1) { it + 1 }
            .map { boost ->
                boost to fight(immuneSystem.map { it.copy(damage = it.damage + boost) } + infection)
            }
            .first { (_, result) ->
                result.isNotEmpty() && result.first().type == Group.Type.immuneSystem
            }
            .second
            .sumOf { it.units }
            .solution(2)
    }
}

fun main(args: Array<String>) = solve<Day24>()
