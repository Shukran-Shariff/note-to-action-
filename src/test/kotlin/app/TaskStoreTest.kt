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
        val tasks = listOf("Task one", "Task two")
        val store = TaskStore(filePath)

        store.saveTasks(tasks)

        assertTrue(Files.exists(filePath))
        val loadedTasks = store.loadTasks()
        assertEquals(tasks, loadedTasks)

        val rawJson = Files.readString(filePath)
        assertTrue(rawJson.contains("Task one"))
        assertTrue(rawJson.contains("Task two"))
    }
}
