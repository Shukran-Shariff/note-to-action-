package app

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class TaskStore(private val filePath: Path) {
    private val json = Json { prettyPrint = true }

    fun saveTasks(tasks: List<String>) {
        val content = json.encodeToString(ListSerializer(String.serializer()), tasks)
        Files.writeString(filePath, content)
    }

    fun loadTasks(): List<String> {
        if (!Files.exists(filePath)) {
            return emptyList()
        }

        val content = Files.readString(filePath)
        return json.decodeFromString(ListSerializer(String.serializer()), content)
    }
}
