package me.reckter.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class AllCombinationsTest {

    @Test
    fun `should spit out all combinations`() {
        val start = listOf(1,2,3)

        val combis = start.allCombinations()
            .toList()

        assertTrue {
            combis == listOf(
                1 to 2,
                1 to 3,

                2 to 1,
                2 to 3,

                3 to 1,
                3 to 2
            )
        }
    }

    @Test
    fun `should spit out all combinations including the same element`() {
        val start = listOf(1,2,3)

        val combis = start.allCombinations(includeSelf = true)
            .toList()

        assertTrue {
            combis == listOf(
                1 to 1,
                1 to 2,
                1 to 3,

                2 to 1,
                2 to 2,
                2 to 3,

                3 to 1,
                3 to 2,
                3 to 3
            )
        }
    }

    @Test
    fun `should spit out all combinations including the same element, bot only once`() {
        val start = listOf(1,2,3)

        val combis = start.allCombinations(includeSelf = true, bothDirections = false)
            .toList()

        assertTrue {
            combis == listOf(
                1 to 1,
                1 to 2,
                1 to 3,

                2 to 2,
                2 to 3,

                3 to 3
            )
        }
    }
    @Test
    fun `should spit out all combinations bot only once`() {
        val start = listOf(1,2,3)

        val combis = start.allCombinations(bothDirections = false)
            .toList()

        assertTrue {
            combis == listOf(
                1 to 2,
                1 to 3,

                2 to 3
            )
        }
    }
}
