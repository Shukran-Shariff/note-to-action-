package app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class TaskServiceTest {
    @Test
    fun `add appends tasks and persists`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        val service = TaskService(store)

        service.addTask("First task")
        service.addTask("Second task")

        assertEquals(
            listOf(
                Task("First task", false),
                Task("Second task", false)
            ),
            store.loadTasks()
        )
    }

    @Test
    fun `clear removes all tasks`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Task one", false), Task("Task two", true)))
        val service = TaskService(store)

        service.clearTasks()

        assertEquals(emptyList<Task>(), store.loadTasks())
    }

    @Test
    fun `processRawLines appends extracted tasks to existing tasks`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Existing open", false), Task("Existing done", true)))
        val service = TaskService(store)

        val imported = service.processRawLines(
            listOf(
                "random note",
                "- [ ] Imported one",
                "- [ ] Imported two"
            )
        )

        assertEquals(
            listOf(
                Task("Imported one", false),
                Task("Imported two", false)
            ),
            imported
        )
        assertEquals(
            listOf(
                Task("Existing open", false),
                Task("Existing done", true),
                Task("Imported one", false),
                Task("Imported two", false)
            ),
            store.loadTasks()
        )
    }

    @Test
    fun `toggleTaskDone uses one based task number`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Task one", false)))
        val service = TaskService(store)

        assertTrue(service.toggleTaskDone(1))
        assertEquals(listOf(Task("Task one", true)), store.loadTasks())
        assertFalse(service.toggleTaskDone(2))
    }
}
