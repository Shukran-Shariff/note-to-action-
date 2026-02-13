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
    fun `import appends tasks instead of overwriting existing`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        // Existing tasks (including a done one) should be preserved
        store.saveTasks(listOf(Task("Existing task", true)))

        var lineReaderCalled = false
        val handler = CommandHandler(
            taskService = TaskService(store),
            readLinesUntilEnd = {
                lineReaderCalled = true
                listOf("- [ ] Imported task")
            }
        )

        val exitCode = handler.handle(arrayOf("import"))
        assertEquals(0, exitCode)
        assertTrue(lineReaderCalled)

        assertEquals(
            listOf(
                Task("Existing task", true),
                Task("Imported task", false)
            ),
            store.loadTasks()
        )
    }

    @Test
    fun `add appends task to store`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        val handler = CommandHandler(TaskService(store))

        val exitCode = handler.handle(arrayOf("add", "Buy", "milk"))

        assertEquals(0, exitCode)
        assertEquals(listOf(Task("Buy milk", false)), store.loadTasks())
    }

    @Test
    fun `list prints numbered tasks with status`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Task one", false), Task("Task two", false)))
        val handler = CommandHandler(TaskService(store))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("list"))
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("1. [ ] Task one"))
        assertTrue(output.contains("2. [ ] Task two"))
    }

    @Test
    fun `clear removes all tasks`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Task one", false), Task("Task two", false)))
        val handler = CommandHandler(TaskService(store))

        val exitCode = handler.handle(arrayOf("clear"))

        assertEquals(0, exitCode)
        assertEquals(emptyList<Task>(), store.loadTasks())
    }

    @Test
    fun `done marks correct task`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("First", false), Task("Second", false)))
        val handler = CommandHandler(TaskService(store))

        val exitCode = handler.handle(arrayOf("done", "2"))

        assertEquals(0, exitCode)
        assertEquals(listOf(Task("First", false), Task("Second", true)), store.loadTasks())
    }

    @Test
    fun `list shows done marker after done command`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("First", false), Task("Second", false)))
        val handler = CommandHandler(TaskService(store))

        handler.handle(arrayOf("done", "2"))

        val output = captureStandardOutput {
            handler.handle(arrayOf("list"))
        }

        assertTrue(output.contains("1. [ ] First"))
        assertTrue(output.contains("2. [x] Second"))
    }

    @Test
    fun `invalid done index prints friendly error`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Only task", false)))
        val handler = CommandHandler(TaskService(store))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("done", "5"))
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("Could not mark task as done. Please use a valid task number."))
        assertEquals(listOf(Task("Only task", false)), store.loadTasks())
    }

    @Test
    fun `remove deletes task by number`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("First", false), Task("Second", true), Task("Third", false)))
        val handler = CommandHandler(TaskService(store))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("remove", "2"))
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("Removed task 2."))
        assertEquals(listOf(Task("First", false), Task("Third", false)), store.loadTasks())
    }

    @Test
    fun `invalid remove index prints friendly error`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        store.saveTasks(listOf(Task("Only task", false)))
        val handler = CommandHandler(TaskService(store))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("remove", "5"))
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("Could not remove task. Please use a valid task number."))
        assertEquals(listOf(Task("Only task", false)), store.loadTasks())
    }

    @Test
    fun `no args prints usage`(@TempDir tempDir: Path) {
        val handler = CommandHandler(TaskService(TaskStore(tempDir.resolve("tasks.json"))))

        val output = captureStandardOutput {
            val exitCode = handler.handle(emptyArray())
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("Usage: add \"task description\" | list | done <number> | remove <number> | clear | import"))
    }

    @Test
    fun `help prints usage`(@TempDir tempDir: Path) {
        val handler = CommandHandler(TaskService(TaskStore(tempDir.resolve("tasks.json"))))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("help"))
            assertEquals(0, exitCode)
        }

        assertTrue(output.contains("Usage: add \"task description\" | list | done <number> | remove <number> | clear | import"))
    }

    @Test
    fun `unknown command prints error and usage`(@TempDir tempDir: Path) {
        val handler = CommandHandler(TaskService(TaskStore(tempDir.resolve("tasks.json"))))

        val output = captureStandardOutput {
            val exitCode = handler.handle(arrayOf("nope"))
            assertEquals(1, exitCode)
        }

        assertTrue(output.contains("Error: unknown command \"nope\"."))
        assertTrue(output.contains("Usage: add \"task description\" | list | done <number> | remove <number> | clear | import"))
    }

    @Test
    fun `import prints prompt and reads lines`(@TempDir tempDir: Path) {
        val store = TaskStore(tempDir.resolve("tasks.json"))
        var called = false
        val handler = CommandHandler(
            taskService = TaskService(store),
            readLinesUntilEnd = {
                called = true
                listOf("- [ ] Imported task")
            }
        )

        val output = captureStandardOutput {
            handler.handle(arrayOf("import"))
        }

        assertTrue(called)
        assertTrue(output.contains("Paste your notes. Type END on a new line to finish."))
        assertTrue(store.loadTasks().any { it.text == "Imported task" })
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
