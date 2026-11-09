# Student Manager (Java Swing + SQLite)

Desktop app: add / update / delete / search students. Data is stored in `students.db` (created automatically).

## Run in IntelliJ
1. File > Open > select the `StudentManager` folder (it will import as a Maven project).
2. Wait for Maven to download dependencies.
3. Run `com.example.studentmanager.Main`.

## Build a runnable jar
    mvn package
    java -jar target/student-manager-1.0.jar

Requires JDK 17+.
