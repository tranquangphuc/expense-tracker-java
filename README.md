# Expense Tracker

This Expense Tracker project is a simple command-line application for recording and reviewing personal expenses, following the roadmap outlined in [Expense Tracker](https://roadmap.sh/projects/expense-tracker). It lets you add expenses, filter them by date or category, view summaries, set monthly budgets, delete entries, and export data to CSV.

## Features

- Add new expenses with amount, description, category, and date
- List expenses with optional filters for year, month, and category
- View category-based summaries and monthly budget status
- Set monthly budgets
- Delete expenses by ID
- Export selected expenses to a CSV file
- Persist data automatically to a JSON file

## Requirements

- Java 17 or newer
- Maven (or use the included Maven wrapper)

## Build the project

From the project root, run:

```bash
./mvnw clean package
```

This will generate a runnable JAR file in the `target` folder.

## Run the application

You can run the app with:

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar --help
```

The CLI supports the following commands:

- `add`
- `list`
- `summary`
- `budget`
- `delete`
- `export`

## Common usage examples

### Add an expense

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar add 2500 "Lunch" -c Food -d 2026-06-13
```

- `2500` is the amount
- `Lunch` is the description
- `-c Food` sets the category
- `-d 2026-06-13` sets the date

If you omit the category, the app uses `Other`.

### List expenses

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar list
```

You can also filter by date or category:

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar list --year 2026 --month 6 --category Food
```

### Show a summary

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar summary
```

You can scope the summary to a specific month or category:

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar summary --year 2026 --month 6 --category Food
```

### Set a monthly budget

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar budget --year 2026 --month 6 --amount 5000
```

### Delete an expense

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar delete --id 1
```

### Export expenses to CSV

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar export --output expenses.csv
```

You can filter what gets exported:

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar export --output expenses.csv --year 2026 --month 6 --category Food
```

## Data storage

Expenses and budgets are stored in a JSON file named `data.json` by default.

The app creates the file automatically on first run. You can override the location with the environment variable:

```bash
EXPENSE_TRACKER_DATA_FILE=/path/to/your/data.json
```

## Getting help

Each command supports built-in help. For example:

```bash
java -jar target/expense-tracker-1.0-SNAPSHOT.jar add --help
```

## Notes

- Amounts are entered as whole numbers.
- Dates should use the `YYYY-MM-DD` format.
- Use `--help` on any command to see its available options.
