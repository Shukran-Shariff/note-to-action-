package app

object HtmlPages {
    fun homePage(tasks: List<Task>, message: String? = null, filter: String = "all"): String {
        val currentFilter = normalizeFilter(filter)
        var visibleCount = 0
        val taskItems = buildString {
            tasks.forEachIndexed { index, task ->
                if (!matchesFilter(task, currentFilter)) {
                    return@forEachIndexed
                }
                visibleCount += 1

                val number = index + 1
                val status = if (task.done) "[x]" else "[ ]"
                val escapedText = escapeHtml(task.text)

                append("<li>")
                append("$number. $status $escapedText")

                append(" ")
                append("<form method=\"post\" action=\"/toggleDone?id=$number&filter=$currentFilter\" style=\"display:inline;\">")
                append("<button type=\"submit\">Toggle done</button>")
                append("</form>")

                append(" ")
                append("<form method=\"post\" action=\"/remove?id=$number&filter=$currentFilter\" style=\"display:inline;\">")
                append("<button type=\"submit\">Remove</button>")
                append("</form>")
                append("</li>")
            }
        }

        val tasksSection = if (visibleCount == 0) {
            if (tasks.isEmpty()) "<p>No tasks yet.</p>" else "<p>No tasks for this filter.</p>"
        } else {
            "<ol>$taskItems</ol>"
        }
        val messageBanner = message
            ?.takeIf { it.isNotBlank() }
            ?.let { "<div>${escapeHtml(it)}</div>" }
            .orEmpty()
        val filterLinks = buildFilterLinks(currentFilter)

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Note To Action</title>
            </head>
            <body>
                $messageBanner
                <h1>Note To Action</h1>
                <form method="post" action="/add?filter=$currentFilter">
                    <label for="task">Task</label><br>
                    <input id="task" name="task" type="text"><br>
                    <button type="submit">Add Task</button>
                </form>
                <hr>
                <p>Filter: $filterLinks</p>
                <form method="post" action="/import?filter=$currentFilter">
                    <label for="notes">Notes</label><br>
                    <textarea id="notes" name="notes" rows="10" cols="80" placeholder="- [ ] Example task"></textarea><br>
                    <button type="submit">Import Tasks</button>
                </form>
                <hr>
                <h2>Tasks</h2>
                $tasksSection
                <form method="post" action="/clear?filter=$currentFilter">
                    <button type="submit">Clear All Tasks</button>
                </form>
            </body>
            </html>
        """.trimIndent()
    }

    private fun normalizeFilter(filter: String): String {
        return when (filter) {
            "open", "done" -> filter
            else -> "all"
        }
    }

    private fun matchesFilter(task: Task, filter: String): Boolean {
        return when (filter) {
            "open" -> !task.done
            "done" -> task.done
            else -> true
        }
    }

    private fun buildFilterLinks(currentFilter: String): String {
        fun link(filter: String, label: String): String {
            return if (filter == currentFilter) {
                "<strong>$label</strong>"
            } else {
                "<a href=\"/?filter=$filter\">$label</a>"
            }
        }

        return "${link("all", "All")} | ${link("open", "Open")} | ${link("done", "Done")}"
    }

    private fun escapeHtml(value: String): String {
        return buildString(value.length) {
            value.forEach { ch ->
                when (ch) {
                    '&' -> append("&amp;")
                    '<' -> append("&lt;")
                    '>' -> append("&gt;")
                    '"' -> append("&quot;")
                    '\'' -> append("&#39;")
                    else -> append(ch)
                }
            }
        }
    }
}
