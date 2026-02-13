package app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class TaskStoreTest {
    @Test
    fun `save and load tasks in temporary folder`(@TempDir tempDir: Path) {
        val filePath = tempDir.resolve("tasks.json")
        val tasks = listOf(Task("Task one", false), Task("Task two", true))
        val store = TaskStore(filePath)

        store.saveTasks(tasks)

        assertTrue(Files.exists(filePath))
        val loadedTasks = store.loadTasks()
        assertEquals(tasks, loadedTasks)

        val rawJson = Files.readString(filePath)
        assertTrue(rawJson.contains("\"text\""))
        assertTrue(rawJson.contains("\"done\""))
        assertTrue(rawJson.contains("Task one"))
        assertTrue(rawJson.contains("Task two"))
    }

    @Test
    fun `load supports old string array format`(@TempDir tempDir: Path) {
        val filePath = tempDir.resolve("tasks.json")
        Files.writeString(filePath, "[\"Legacy one\", \"Legacy two\"]")
        val store = TaskStore(filePath)

        val loadedTasks = store.loadTasks()

        assertEquals(listOf(Task("Legacy one", false), Task("Legacy two", false)), loadedTasks)
    }
}
