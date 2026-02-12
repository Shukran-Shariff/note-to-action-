package app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TaskExtractorTest {
    @Test
    fun `no tasks returns empty list`() {
        val lines = listOf(
            "Meeting notes",
            "Discuss roadmap",
            "Follow up next week"
        )

        val result = TaskExtractor.extractTasks(lines)

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `multiple tasks are extracted`() {
        val lines = listOf(
            "- [ ] Prepare slide deck",
            "- [ ] Email client",
            "- [ ] Review budget"
        )

        val result = TaskExtractor.extractTasks(lines)

        assertEquals(listOf("Prepare slide deck", "Email client", "Review budget"), result)
    }

    @Test
    fun `tasks with leading spaces are extracted`() {
        val lines = listOf(
            "   - [ ] Buy groceries",
            "\t- [ ] Book flights"
        )

        val result = TaskExtractor.extractTasks(lines)

        assertEquals(listOf("Buy groceries", "Book flights"), result)
    }

    @Test
    fun `mixed lines only return task entries`() {
        val lines = listOf(
            "Project notes",
            "- [ ] Write docs",
            "Random line",
            "  - [ ] Create demo",
            "- [x] Done task"
        )

        val result = TaskExtractor.extractTasks(lines)

        assertEquals(listOf("Write docs", "Create demo"), result)
    }
}
