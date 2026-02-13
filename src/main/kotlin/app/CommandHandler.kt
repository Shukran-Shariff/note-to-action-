package app

class CommandHandler(
    private val taskService: TaskService
) {
    fun handle(args: Array<String>) {
        when (args.firstOrNull()?.lowercase()) {
            "add" -> handleAdd(args.drop(1))
            "list" -> handleList()
            "clear" -> handleClear()
            else -> printUsage()
        }
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
            println("${index + 1}. $task")
        }
    }

    private fun handleClear() {
        taskService.clearTasks()
        println("Cleared all tasks.")
    }

    private fun printUsage() {
        println("Usage: add \"task description\" | list | clear")
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

    private fun printExtractedTasks(tasks: List<String>) {
        if (tasks.isEmpty()) {
            println("No tasks found.")
            return
        }

        println("Extracted tasks:")
        tasks.forEachIndexed { index, task ->
            println("${index + 1}. $task")
        }
    }
}
