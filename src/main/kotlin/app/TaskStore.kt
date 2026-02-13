package app

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path

class TaskStore(private val filePath: Path) {
    private val json = Json { prettyPrint = true }

    fun saveTasks(tasks: List<Task>) {
        val content = json.encodeToString(ListSerializer(Task.serializer()), tasks)
        Files.writeString(filePath, content)
    }

    fun appendTask(task: String) {
        val tasks = loadTasks().toMutableList()
        tasks.add(Task(task, false))
        saveTasks(tasks)
    }

    fun markTaskDone(index: Int): Boolean {
        val tasks = loadTasks().toMutableList()
        if (index !in tasks.indices) {
            return false
        }

        val task = tasks[index]
        if (!task.done) {
            tasks[index] = task.copy(done = true)
            saveTasks(tasks)
        }
        return true
    }

    fun removeTask(index: Int): Boolean {
        val tasks = loadTasks().toMutableList()
        if (index !in tasks.indices) {
            return false
        }

        tasks.removeAt(index)
        saveTasks(tasks)
        return true
    }

    fun clearTasks() {
        saveTasks(emptyList())
    }

    fun loadTasks(): List<Task> {
        if (!Files.exists(filePath)) return emptyList()

        val content = Files.readString(filePath).trim()
        if (content.isBlank()) return emptyList()

        return try {
            parseTasks(content)
        } catch (_: Exception) {
            emptyList()
        }
    }


    private fun parseTasks(content: String): List<Task> {
        val root = json.parseToJsonElement(content)
        if (root !is JsonArray) {
            return emptyList()
        }

        return root.jsonArray.mapNotNull { item ->
            when (item) {
                is JsonPrimitive -> {
                    item.contentOrNull?.let { Task(it, false) }
                }
                else -> {
                    val obj = item.jsonObject
                    val text = obj["text"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                    val done = obj["done"]?.jsonPrimitive?.booleanOrNull ?: false
                    Task(text, done)
                }
            }
        }
    }
}
