package app

class TaskService(
    private val taskStore: TaskStore
) {
    fun addTask(task: String) {
        taskStore.appendTask(task)
    }

    fun listTasks(): List<Task> = taskStore.loadTasks()

    fun markTaskDone(taskNumber: Int): Boolean {
        val index = taskNumber - 1
        if (index < 0) return false
        return taskStore.markTaskDone(index)
    }

    fun removeTask(taskNumber: Int): Boolean {
        val index = taskNumber - 1
        if (index < 0) return false
        return taskStore.removeTask(index)
    }


    fun clearTasks() {
        taskStore.clearTasks()
    }

    fun processRawLines(lines: List<String>): List<Task> {
        val existingTasks = taskStore.loadTasks()
        val importedTasks = TaskExtractor.extractTasks(lines).map { Task(it, false) }
        taskStore.saveTasks(existingTasks + importedTasks)
        return importedTasks
    }
}
