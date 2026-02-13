# demo.ps1 (run from project root)

Write-Host "== Demo: Note to Action =="

.\gradlew.bat run --% --args="clear"
.\gradlew.bat run --% --args="add Prepare slides"
.\gradlew.bat run --% --args="add Email client"
.\gradlew.bat run --% --args="list"
.\gradlew.bat run --% --args="done 2"
.\gradlew.bat run --% --args="list"
