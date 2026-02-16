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
                call.respondText(
                    text = HtmlPages.homePage(taskService.listTasks(), message),
                    contentType = ContentType.Text.Html
                )
            }

            post("/import") {
                val notes = call.receiveParameters()["notes"].orEmpty()
                val lines = notes.lines()
                taskService.processRawLines(lines)
                call.respondRedirect("/?msg=${encodeMessage("Tasks imported")}")
            }

            post("/done") {
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                val message = if (id != null && taskService.markTaskDone(id)) {
                    "Task marked done"
                } else {
                    "Task not found"
                }
                call.respondRedirect("/?msg=${encodeMessage(message)}")
            }

            post("/remove") {
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                val message = if (id != null && taskService.removeTask(id)) {
                    "Task removed"
                } else {
                    "Task not found"
                }
                call.respondRedirect("/?msg=${encodeMessage(message)}")
            }

            post("/clear") {
                taskService.clearTasks()
                call.respondRedirect("/?msg=${encodeMessage("All tasks cleared")}")
            }
        }
    }.start(wait = true)
}

private fun encodeMessage(message: String): String =
    URLEncoder.encode(message, StandardCharsets.UTF_8)
