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
import java.nio.file.Paths

fun main() {
    val taskService = TaskService(TaskStore(Paths.get("tasks.json")))

    embeddedServer(Netty, host = "localhost", port = 8080) {
        routing {
            get("/") {
                call.respondText(
                    text = HtmlPages.homePage(taskService.listTasks()),
                    contentType = ContentType.Text.Html
                )
            }

            post("/import") {
                val notes = call.receiveParameters()["notes"].orEmpty()
                val lines = notes.lines()
                taskService.processRawLines(lines)
                call.respondRedirect("/")
            }

            post("/done") {
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                if (id != null) {
                    taskService.markTaskDone(id)
                }
                call.respondRedirect("/")
            }

            post("/remove") {
                val id = call.request.queryParameters["id"]?.toIntOrNull()
                if (id != null) {
                    taskService.removeTask(id)
                }
                call.respondRedirect("/")
            }

            post("/clear") {
                taskService.clearTasks()
                call.respondRedirect("/")
            }
        }
    }.start(wait = true)
}
