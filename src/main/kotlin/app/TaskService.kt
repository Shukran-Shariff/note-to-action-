package app

class TaskService(
    private val taskStore: TaskStore
) {
    fun addTask(task: String) {
        taskStore.appendTask(task)
    }

    fun listTasks(): List<String> = taskStore.loadTasks()

    fun clearTasks() {
        taskStore.clearTasks()
    }

    fun processRawLines(lines: List<String>): List<String> {
        val tasks = TaskExtractor.extractTasks(lines)
        taskStore.saveTasks(tasks)
        return tasks
    }
}
