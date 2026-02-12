# note-to-action-

A simple Kotlin console app that extracts tasks from notes.

## 🚀 What It Does

- Accepts multi-line user input
- Input ends when the user types: `END`
- Extracts tasks that start with: [ ]
- Prints extracted tasks as a numbered list
- Saves tasks to `tasks.json`

---

## 🛠 Tech Stack

- Kotlin (JVM)
- Gradle (Kotlin DSL)
- JUnit (for tests)

---

## ▶ How to Run (IntelliJ)

1. Open the project in IntelliJ
2. Open `Main.kt`
3. Click the green ▶ button next to `fun main`
4. Paste your note
5. Type `END` on a new line
6. View extracted tasks in console

---

## ▶ How to Run (Terminal)

From project root:

### Run tests
./gradlew test

### Run application
./gradlew run

On Windows:
gradlew.bat test
gradlew.bat run

---

## 🧪 Example Input

Meeting notes:
[Prepare slide deck]
[Email client]
Random text
[Review budget]
END

## Example Output
1. Prepare slide deck
2. Email client 
3. Review budget
---

## 📂 Project Structure
src/main/kotlin/app
src/test/kotlin/app

---

## 🧠 Learning Goals

- Basic Kotlin syntax
- File I/O
- JSON persistence
- Unit testing
- Git workflow

---

## 📌 Next Improvements

- Add CLI commands (add/list/done)
- Add timestamps
- Add simple web UI

