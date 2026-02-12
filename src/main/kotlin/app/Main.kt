package app

import java.nio.file.Paths

fun main() {
    println("Paste your notes. Type END on a new line to finish.")

    val lines = readLinesUntilEnd()
    val tasks = TaskExtractor.extractTasks(lines)

    printTasks(tasks)

    val store = TaskStore(Paths.get("tasks.json"))
    store.saveTasks(tasks)
    println("Saved ${tasks.size} task(s) to tasks.json")
}

private fun readLinesUntilEnd(): List<String> {
    val lines = mutableListOf<String>()

    while (true) {
        val line = readlnOrNull() ?: break
        if (line == "END") {
            break
        }
        lines.add(line)
    }

    return lines
}

private fun printTasks(tasks: List<String>) {
    if (tasks.isEmpty()) {
        println("No tasks found.")
        return
    }

    println("Extracted tasks:")
    tasks.forEachIndexed { index, task ->
        println("${index + 1}. $task")
    }
}
