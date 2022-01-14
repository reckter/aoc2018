package me.reckter.aoc

data class State(
    var register: MutableMap<Int, Long>,
    var instructionPointerRegister: Int,
    var instructionCount: Int = 0
)

fun State.incrementInstructionPointer(): State {
    return write(instructionPointerRegister, instructionPointer + 1)
}

val State.instructionPointer: Long get() = get(instructionPointerRegister)

fun State.write(reg: String, value: Long): State {
    instructionCount++
    write(reg.toInt(), value)
    return this
}

fun State.write(reg: Int, value: Long): State {
    register[reg] = value
    return this
}

fun State.get(reg: String): Long = get(reg.toInt())
fun State.get(reg: Int): Long {
    return register.getOrDefault(reg, 0L)
}

fun State.run(instruction: String): State {
    val (instr, srcA, srcB, dest) = instruction.split(" ")

    return when (instr) {
        "addr" -> write(dest, get(srcA) + get(srcB))
        "addi" -> write(dest, get(srcA) + srcB.toLong())

        "mulr" -> write(dest, get(srcA) * get(srcB))
        "muli" -> write(dest, get(srcA) * srcB.toLong())

        "banr" -> write(dest, get(srcA) and get(srcB))
        "bani" -> write(dest, get(srcA) and srcB.toLong())

        "borr" -> write(dest, get(srcA) or get(srcB))
        "bori" -> write(dest, get(srcA) or srcB.toLong())

        "setr" -> write(dest, get(srcA))
        "seti" -> write(dest, srcA.toLong())

        "gtir" -> write(dest, if (srcA.toLong() > get(srcB)) 1 else 0)
        "gtri" -> write(dest, if (get(srcA) > srcB.toLong()) 1 else 0)
        "gtrr" -> write(dest, if (get(srcA) > get(srcB)) 1 else 0)

        "eqir" -> write(dest, if (srcA.toLong() == get(srcB)) 1 else 0)
        "eqri" -> write(dest, if (get(srcA) == srcB.toLong()) 1 else 0)
        "eqrr" -> write(dest, if (get(srcA) == get(srcB)) 1 else 0)
        "noop" -> this
        "divi" -> write(dest, get(srcA) / srcB.toInt())
        "outr" -> {
            println(register)
            this
        }
        else -> error("invalid instruction: $instruction")
    }.incrementInstructionPointer()
}

fun State.runProgram(program: List<String>, timeout: Int = 1000000): State {
    var current = this
    while (current.instructionPointer in program.indices && current.instructionCount < timeout) {
        val instruction = program[current.instructionPointer.toInt()]
        current = current.run(instruction)
    }
    return current
}
