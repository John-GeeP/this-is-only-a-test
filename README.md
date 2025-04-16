# this-is-only-a-test: Wikipedia Automation Testing Project

This project demonstrates basic automation testing on Wikipedia using Selenium, TestNG, and Maven. It is organized as a multi‑module Maven project where each module addresses a distinct testing area:

- **module1-jgp-navigation:** Navigation tests
- **module2-tmi-authentication:** Authentication tests
- **module3-sen-content:** Content tests
- **module4-chf-search:** Search functionality tests

## Project Structure

```
this-is-only-a-test/
├── .gitignore
├── README.md
├── pom.xml
├── module1-jgp-navigation/
│   ├── pom.xml
│   ├── src/
│   │   └── test/java/edu/jgp/NavigationTest.java
│   └── testng.xml
├── module2-tmi-authentication/
│   ├── pom.xml
│   └── src/
│       └── test/java/edu/tmi/AuthenticationTest.java
│   └── testng.xml
├── module3-sen-content/
│   ├── pom.xml
│   └── src/
│       └── test/java/edu/sen/ContentTest.java
│   └── testng.xml
└── module4-chf-search/
    ├── pom.xml
    └── src/
        └── test/java/edu/chf/SearchTest.java
    └── testng.xml
```

## Prerequisites

- **JDK:** Install Java (we use JDK 24; adjust in `pom.xml` if needed).
- **Maven:** Ensure Maven is installed and available in your system PATH.
- **Chrome Browser:** Google Chrome must be installed.
- **ChromeDriver:** WebDriverManager automatically manages drivers.
chromedriver.exe is not required.

## Running the Project

### Build the Project

From the root directory, run:

```bash
mvn clean install
```

This command will compile all modules and install the artifacts into your local Maven repository.

### Run the Tests

You can run tests for individual modules or for all modules together. For example, to run the content tests (module3):

Navigate to the module directory:

```bash
cd module3-sen-content
```

Run the tests using Maven:

```bash
mvn clean test
```

Alternatively, open the corresponding `testng.xml` file in your IDE (IntelliJ IDEA, Eclipse, etc.) and run the suite directly.

## Future Development

Future enhancements include:

- Fully implementing navigation, authentication, and search modules.
- Extending functionality to cover scenarios such as clicking links, switching modes, signing in/out, editing articles, etc.
- Refining test assertions and integrating continuous integration.

## License

This project is provided as-is for educational purposes.

---
