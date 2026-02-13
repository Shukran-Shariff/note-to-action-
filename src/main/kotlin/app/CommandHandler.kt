package app

class CommandHandler(
    private val taskService: TaskService,
    private val readLinesUntilEnd: () -> List<String> = ::defaultReadLinesUntilEnd
) {
    fun handle(args: Array<String>): Int {
        when (args.firstOrNull()?.lowercase()) {
            null, "help" -> {
                printUsage()
                return 0
            }
            "add" -> handleAdd(args.drop(1))
            "list" -> handleList()
            "done" -> handleDone(args.drop(1))
            "remove" -> handleRemove(args.drop(1))
            "clear" -> handleClear()
            "import" -> handleInteractiveImport()
            else -> {
                printUnknownCommand(args.first())
                printUsage()
                return 1
            }
        }

        return 0
    }

    fun handleInteractiveImport() {
        println("Paste your notes. Type END on a new line to finish.")

        val lines = readLinesUntilEnd()
        val tasks = taskService.processRawLines(lines)

        printExtractedTasks(tasks)
        println("Saved ${tasks.size} task(s) to tasks.json")
    }

    private fun handleAdd(taskParts: List<String>) {
        val task = taskParts.joinToString(" ").trim()
        if (task.isEmpty()) {
            println("Usage: add \"task description\"")
            return
        }

        taskService.addTask(task)
        println("Added 1 task.")
    }

    private fun handleList() {
        val tasks = taskService.listTasks()
        if (tasks.isEmpty()) {
            println("No tasks found.")
            return
        }

        tasks.forEachIndexed { index, task ->
            val status = if (task.done) "[x]" else "[ ]"
            println("${index + 1}. $status ${task.text}")
        }
    }

    private fun handleDone(args: List<String>) {
        val taskNumber = args.firstOrNull()?.toIntOrNull()
        if (taskNumber == null) {
            println("Please provide a task number. Example: done 2")
            return
        }

        val updated = taskService.markTaskDone(taskNumber)
        if (!updated) {
            println("Could not mark task as done. Please use a valid task number.")
            return
        }

        println("Marked task $taskNumber as done.")
    }

    private fun handleRemove(args: List<String>) {
        val taskNumber = args.firstOrNull()?.toIntOrNull()
        if (taskNumber == null) {
            println("Please provide a task number. Example: remove 2")
            return
        }

        val removed = taskService.removeTask(taskNumber)
        if (!removed) {
            println("Could not remove task. Please use a valid task number.")
            return
        }

        println("Removed task $taskNumber.")
    }

    private fun handleClear() {
        taskService.clearTasks()
        println("Cleared all tasks.")
    }

    private fun printUsage() {
        println("Usage: add \"task description\" | list | done <number> | remove <number> | clear | import")
    }

    private fun printUnknownCommand(command: String) {
        println("Error: unknown command \"$command\".")
    }

    private companion object {
        fun defaultReadLinesUntilEnd(): List<String> {
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
    }

    private fun printExtractedTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            println("No tasks found.")
            return
        }

        println("Extracted tasks:")
        tasks.forEachIndexed { index, task ->
            println("${index + 1}. ${task.text}")
        }
    }
}
