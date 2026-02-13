package app

import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val taskService = TaskService(TaskStore(Paths.get("tasks.json")))
    val commandHandler = CommandHandler(taskService)
    exitProcess(commandHandler.handle(args))
}
