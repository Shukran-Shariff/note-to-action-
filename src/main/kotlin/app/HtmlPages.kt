package app

object HtmlPages {
    fun homePage(tasks: List<Task>): String {
        val taskItems = buildString {
            tasks.forEachIndexed { index, task ->
                val number = index + 1
                val status = if (task.done) "[x]" else "[ ]"
                val escapedText = escapeHtml(task.text)

                append("<li>")
                append("$number. $status $escapedText")

                if (!task.done) {
                    append(" ")
                    append("<form method=\"post\" action=\"/done?id=$number\" style=\"display:inline;\">")
                    append("<button type=\"submit\">Mark done</button>")
                    append("</form>")
                }

                append(" ")
                append("<form method=\"post\" action=\"/remove?id=$number\" style=\"display:inline;\">")
                append("<button type=\"submit\">Remove</button>")
                append("</form>")
                append("</li>")
            }
        }

        val tasksSection = if (tasks.isEmpty()) {
            "<p>No tasks yet.</p>"
        } else {
            "<ol>$taskItems</ol>"
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Note To Action</title>
            </head>
            <body>
                <h1>Note To Action</h1>
                <form method="post" action="/import">
                    <label for="notes">Notes</label><br>
                    <textarea id="notes" name="notes" rows="10" cols="80" placeholder="- [ ] Example task"></textarea><br>
                    <button type="submit">Import Tasks</button>
                </form>
                <hr>
                <h2>Tasks</h2>
                $tasksSection
                <form method="post" action="/clear">
                    <button type="submit">Clear All Tasks</button>
                </form>
            </body>
            </html>
        """.trimIndent()
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
