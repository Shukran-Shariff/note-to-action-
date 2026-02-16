package app

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

fun main() {
    val port = System.getenv("PORT")
        ?.toIntOrNull()
        ?.takeIf { it in 1..65535 }
        ?: 8080
    val tasksFile = System.getenv("TASKS_FILE")
        ?.takeIf { it.isNotBlank() }
        ?: "tasks.json"
    val taskService = TaskService(TaskStore(Paths.get(tasksFile)))

    println("Server running on http://localhost:$port using $tasksFile")

    embeddedServer(Netty, host = "localhost", port = port) {
        routing {
            get("/") {
                val message = call.request.queryParameters["msg"]
                val filter = parseFilter(call.request.queryParameters["filter"])
                call.respondText(
                    text = HtmlPages.homePage(taskService.listTasks(), message, filter),
                    contentType = ContentType.Text.Html
                )
            }

            post("/add") {
                val filter = parseFilter(call.request.queryParameters["filter"])
                val task = call.receiveParameters()["task"].orEmpty().trim()
                val message = if (task.isEmpty()) {
                    "Task cannot be empty"
                } else {
                    taskService.addTask(task)
                    "Task added"
                }
                call.respondRedirect(redirectHome(message, filter))
            }

            post("/import") {
                val filter = parseFilter(call.request.queryParameters["filter"])
                val notes = call.receiveParameters()["notes"].orEmpty()
                val lines = notes.lines()
                taskService.processRawLines(lines)
                call.respondRedirect(redirectHome("Tasks imported", filter))
            }

            post("/toggleDone") {
                val filter = parseFilter(call.request.queryParameters["filter"])
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                val message = if (id != null && taskService.toggleTaskDone(id)) {
                    "Task updated"
                } else {
                    "Task not found"
                }
                call.respondRedirect(redirectHome(message, filter))
            }

            post("/remove") {
                val filter = parseFilter(call.request.queryParameters["filter"])
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                val message = if (id != null && taskService.removeTask(id)) {
                    "Task removed"
                } else {
                    "Task not found"
                }
                call.respondRedirect(redirectHome(message, filter))
            }

            post("/clear") {
                val filter = parseFilter(call.request.queryParameters["filter"])
                taskService.clearTasks()
                call.respondRedirect(redirectHome("All tasks cleared", filter))
            }
        }
    }.start(wait = true)
}

private fun encodeMessage(message: String): String =
    URLEncoder.encode(message, StandardCharsets.UTF_8)

private fun parseFilter(value: String?): String {
    return when (value?.lowercase()) {
        "open", "done" -> value.lowercase()
        else -> "all"
    }
}

private fun redirectHome(message: String, filter: String): String {
    val encodedMessage = encodeMessage(message)
    return if (filter == "all") {
        "/?msg=$encodedMessage"
    } else {
        "/?msg=$encodedMessage&filter=$filter"
    }
}
