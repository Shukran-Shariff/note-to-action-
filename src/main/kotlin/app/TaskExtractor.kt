package app

object TaskExtractor {
    private const val TASK_PREFIX = "- [ ]"

    fun extractTasks(lines: List<String>): List<String> {
        return lines.mapNotNull { line ->
            val trimmedStart = line.trimStart()
            if (!trimmedStart.startsWith(TASK_PREFIX)) {
                return@mapNotNull null
            }

            val taskText = trimmedStart.removePrefix(TASK_PREFIX).trim()
            if (taskText.isEmpty()) null else taskText
        }
    }
}
