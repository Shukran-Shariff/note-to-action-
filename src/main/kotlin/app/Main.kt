package app

import java.nio.file.Paths

fun main(args: Array<String>) {
    val taskService = TaskService(TaskStore(Paths.get("tasks.json")))
    val commandHandler = CommandHandler(taskService)

    if (args.isEmpty()) {
        commandHandler.handleInteractiveImport()
        return
    }

    commandHandler.handle(args)
}
