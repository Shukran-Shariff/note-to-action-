package app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Path

class CommandHandlerTest {
    @Test
    fun `add appends task to store`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        val handler = CommandHandler(TaskService(store))

        handler.handle(arrayOf("add", "Buy", "milk"))

        assertEquals(listOf("Buy milk"), store.loadTasks())
    }

    @Test
    fun `list prints numbered tasks`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf("Task one", "Task two"))
        val handler = CommandHandler(TaskService(store))

        val output = captureStandardOutput {
            handler.handle(arrayOf("list"))
        }

        assertTrue(output.contains("1. Task one"))
        assertTrue(output.contains("2. Task two"))
    }

    @Test
    fun `clear removes all tasks`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf("Task one", "Task two"))
        val handler = CommandHandler(TaskService(store))

        handler.handle(arrayOf("clear"))

        assertEquals(emptyList<String>(), store.loadTasks())
    }

    private fun captureStandardOutput(block: () -> Unit): String {
        val originalOut = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer))
        return try {
            block()
            buffer.toString().replace("\r\n", "\n")
        } finally {
            System.setOut(originalOut)
        }
    }
}
