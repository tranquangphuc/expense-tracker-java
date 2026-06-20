# AGENTS.md

## Cursor Cloud specific instructions

This repo is a single **command-line** Java app (Expense Tracker). There is no server, web UI, database, or container — everything runs in one JVM process and persists to a local JSON file. See `README.md` for the full command reference.

### Environment
- Build/run uses **Java 17+** (the VM has Java 21) and the bundled Maven wrapper `./mvnw` (Maven is not on `PATH`; always use `./mvnw`, not a system `mvn`).
- The update script runs `./mvnw -B dependency:go-offline` to pre-fetch dependencies into `~/.m2`.

### Build / test / run
- Build (produces a runnable jar): `./mvnw clean package`
- Test: `./mvnw test`
- There is no dedicated lint plugin configured; `./mvnw compile` is the closest static check.
- Run: `java -jar target/expense-tracker-1.0-SNAPSHOT.jar <command>` (e.g. `add`, `list`, `summary`, `budget`, `delete`, `export`; use `--help`).

### Gotchas
- The `pom.xml` applies the `spring-boot-maven-plugin` `repackage` goal, so the built jar is a **Spring Boot fat jar** (contains `BOOT-INF/`) even though the app itself is a plain PicoCLI/Guice CLI with no Spring runtime. Run it normally with `java -jar`.
- Persistence defaults to `data.json` **in the current working directory**, and the repo ships a sample `data.json`. To avoid mutating the committed sample data during testing, set `EXPENSE_TRACKER_DATA_FILE=/tmp/<file>.json` (or `-Dexpense.tracker.data.file=...`) to point at a throwaway file.
- Amounts are whole numbers; dates use `YYYY-MM-DD`.
