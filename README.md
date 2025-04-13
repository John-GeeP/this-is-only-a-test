# Automation Testing Project

This project provides a basic setup for automation testing on Wikipedia using Selenium with Maven and TestNG. The project is organized as a multi‑module Maven project.

## Project Structure

- **module_1**: Contains a simple TestNG test that opens Chrome, navigates to [Wikipedia](https://www.wikipedia.com), holds for 2 seconds, validates that the title contains "Wikipedia", and closes the browser.
- **module_2 - module_4**: Placeholders for future test modules.

## Prerequisites

- **JDK**: Make sure you have Java (JDK 24 or adjust in `pom.xml` if necessary) installed.
- **Maven**: Install Maven and add it to your system PATH.
- **Chrome Browser**: Ensure that Google Chrome is installed.
- **ChromeDriver**:  
  Download the appropriate version of `chromedriver.exe` (from chrome driver-win64) and update the path in `module_1/src/test/java/com/example/tests/WikipediaTest.java`.  
  Alternatively, you can use [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) (already included in dependencies) to manage the driver automatically.

## Running the Tests

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/automation-testing-project.git
   cd automation-testing-project

2. **Import as a Maven Project in IntelliJ IDEA:**

- Open IntelliJ IDEA.
- Select File > Open and navigate to the project root.
- Open the root pom.xml to load all modules.

3. **ChromeDriver Configuration:**

- The chromedriver.exe is located in module1/drivers/.
- The test uses a relative path to locate the driver so no absolute path configuration is necessary.

4. **Build and Run Tests:**

To build the project, run:
```
mvn clean compile
```

To execute tests, run:
```
mvn clean test
```

## Project Structure
```
this-is-only-a-test/
├── .gitignore
├── README.md
├── pom.xml                          # Parent POM
├── module1/                         # Contains the Selenium test
│   ├── pom.xml                      # Module1 POM
│   ├── drivers/
│   │   └── chromedriver.exe
│   └── src/
│       ├── main/java/
│       │   └── edu/JGP/module1/      
│       └── test/java/
│           └── edu/JGP/module1/
│               └── WikipediaTest.java
├── module2/                         # Template module
│   ├── pom.xml
│   └── src/main/java/
│       └── edu/JGP/module2/
├── module3/                         # Template module
│   ├── pom.xml
│   └── src/main/java/
│       └── edu/JGP/module3/
└── module4/                         # Template module
├── pom.xml
└── src/main/java/
└── edu/JGP/module4/
```


## Future Development
Further tests, modules, and functionality 
(such as clicking links, changing modes, signing in, editing, etc.) 
will be added following the structure established in module1.
