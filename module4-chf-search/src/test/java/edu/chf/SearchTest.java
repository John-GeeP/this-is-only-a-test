package edu.chf;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Placeholder test for Wikipedia search functionality.
 */
public class SearchTest {

    @Test
    public void testSearchPlaceholder() {
        System.out.println("Module4 - SearchTest: Placeholder test executed.");
        Assert.assertTrue(true, "Placeholder assertion passed.");
    }
}

/*
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class C4 {
    private WebDriver driver;
    private WebDriverWait wait;

    // Add a constant for the delay between actions
    private static final int ACTION_DELAY_MS = 2000; // 2 seconds delay

    // Helper method to add delay between actions
    private void delayBetweenActions() {
        try {
            Thread.sleep(ACTION_DELAY_MS);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
    }

    public void setUp() {
        // Set up ChromeDriver path - update this with your chromedriver location
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\cjfos\\drivers_new\\chromedriver.exe");

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // Uncomment next line if you want headless mode
        // options.addArguments("--headless");

        // Initialize the WebDriver
        driver = new ChromeDriver(options);

        // Create a wait object with 10 second timeout
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void testBasicSearch() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter basic search term
            String searchTerm = "Albert Einstein";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.titleContains("Einstein"));
            delayBetweenActions();

            // Verify we're on the correct article page
            String pageTitle = driver.getTitle();
            if (!pageTitle.contains("Albert Einstein")) {
                throw new AssertionError("Basic search failed: Page title should contain 'Albert Einstein'");
            }

            System.out.println("Basic search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Basic search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchWithMultipleKeywords() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter multiple keywords
            String searchTerm = "quantum physics nobel prize";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the search results to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results")));
            delayBetweenActions();

            // Verify search results contain our keywords
            WebElement searchResults = driver.findElement(By.className("mw-search-results"));
            String resultsText = searchResults.getText().toLowerCase();

            boolean containsRelevantResults = resultsText.contains("quantum") ||
                    resultsText.contains("physics") ||
                    resultsText.contains("nobel");

            if (!containsRelevantResults) {
                throw new AssertionError("Multiple keyword search failed: Results don't contain any relevant keywords");
            }

            System.out.println("Multiple keyword search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Multiple keyword search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchWithSpecialCharacters() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter search with special characters
            String searchTerm = "C++";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("C++"),
                    ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
            ));
            delayBetweenActions();

            // Verify we either got the C++ page or relevant search results
            String pageTitle = driver.getTitle();
            boolean foundCorrectPage = pageTitle.contains("C++");

            boolean foundSearchResults = false;
            try {
                WebElement searchResults = driver.findElement(By.className("mw-search-results"));
                foundSearchResults = searchResults.isDisplayed();
            } catch (Exception e) {
                // Element not found, which is fine if we're on the direct page
            }

            if (!foundCorrectPage && !foundSearchResults) {
                throw new AssertionError("Special character search failed: Neither direct page nor search results found");
            }

            System.out.println("Special character search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Special character search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchSuggestions() {
        try {
            // Navigate to main page to ensure clean start
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Type slowly to trigger suggestions - with additional delays between characters
            String searchTerm = "United Stat";
            for (char c : searchTerm.toCharArray()) {
                searchInput.sendKeys(String.valueOf(c));
                Thread.sleep(500); // 500ms between characters to show typing
            }
            delayBetweenActions(); // Additional pause after typing

            // Wait for suggestions to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("suggestion-link")));
            delayBetweenActions();

            // Verify suggestions appear
            WebElement suggestionBox = driver.findElement(By.className("suggestion-link"));
            if (!suggestionBox.isDisplayed()) {
                throw new AssertionError("Search suggestions test failed: No suggestions displayed");
            }

            System.out.println("Search suggestions test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Search suggestions test FAILED: " + e.getMessage());
        }
    }

    public void testEmptySearch() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Store the original URL before search
            String originalUrl = driver.getCurrentUrl();
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Submit empty search
            searchInput.clear();  // Make sure it's empty
            delayBetweenActions();

            searchInput.submit();
            delayBetweenActions();

            // Get current URL after search attempt
            String currentUrl = driver.getCurrentUrl();

            // Check what happened - we should either stay on the same page
            // or be redirected to a search page without an error message
            boolean stayedOnSamePage = currentUrl.equals(originalUrl);
            boolean wentToSearchPage = currentUrl.contains("search") || currentUrl.contains("Special:");

            // Modified error detection logic to be less sensitive
            boolean hasError = false;
            if (!stayedOnSamePage && !wentToSearchPage) {
                // Only check for error messages if we went somewhere unexpected
                String pageSource = driver.getPageSource().toLowerCase();
                hasError = pageSource.contains("error message") ||
                        pageSource.contains("search error") ||
                        (pageSource.contains("error") && pageSource.contains("search"));
            }

            // If we got redirected to an error page, that's a failure
            if (hasError) {
                throw new AssertionError("Empty search test failed: Error page displayed");
            }

            System.out.println("Empty search test PASSED" +
                    (stayedOnSamePage ? " (Stayed on same page)" :
                            wentToSearchPage ? " (Went to search page)" : " (Other behavior)"));
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Empty search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchInDifferentLanguage() {
        try {
            // Navigate to Spanish Wikipedia
            driver.get("https://es.wikipedia.org/");
            delayBetweenActions();

            // Wait for page to fully load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));

            // Find AND use the search input field in one block
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            searchInput.sendKeys("Madrid");
            delayBetweenActions();

            // Submit right after interacting
            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.titleContains("Madrid"));
            delayBetweenActions();

            // Verify we're on the correct article page in Spanish
            String pageTitle = driver.getTitle();
            if (!pageTitle.contains("Madrid")) {
                throw new AssertionError("Different language search failed: Not on Madrid page");
            }

            // Verify the content is in Spanish
            WebElement content = driver.findElement(By.id("mw-content-text"));
            String contentText = content.getText().toLowerCase();
            if (!(contentText.contains("españa") || contentText.contains("capital"))) {
                throw new AssertionError("Different language search failed: Content doesn't appear to be in Spanish");
            }

            System.out.println("Different language search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Different language search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchWithQuotationMarks() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter search with quotation marks for exact phrase
            String searchTerm = "\"to be or not to be\"";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the search results to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("Hamlet"),
                    ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
            ));
            delayBetweenActions();

            // Get the page content
            String pageContent = driver.findElement(By.tagName("body")).getText();

            // Check if the exact phrase or Hamlet is mentioned
            boolean containsPhrase = pageContent.toLowerCase().contains("to be or not to be") ||
                    pageContent.toLowerCase().contains("hamlet");

            if (!containsPhrase) {
                throw new AssertionError("Quotation mark search failed: Results don't contain phrase or Hamlet");
            }

            System.out.println("Quotation mark search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Quotation mark search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchWithNumbersAndDates() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter search with numbers and date
            String searchTerm = "World War 1914";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("World War"),
                    ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
            ));
            delayBetweenActions();

            // Get the page content
            String pageContent = driver.findElement(By.tagName("body")).getText();

            // Check if we found content about World War I
            boolean containsRelevantInfo = pageContent.contains("World War") &&
                    (pageContent.contains("1914") ||
                            pageContent.contains("WWI") ||
                            pageContent.contains("First World War"));

            if (!containsRelevantInfo) {
                throw new AssertionError("Number/date search failed: Results don't contain relevant World War I information");
            }

            System.out.println("Number and date search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Number and date search test FAILED: " + e.getMessage());
        }
    }

    public void testSearchCaseSensitivity() {
        try {
            // First search with lowercase
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            String lowercaseSearch = "python programming";
            searchInput.sendKeys(lowercaseSearch);
            delayBetweenActions();

            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("Python"),
                    ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
            ));
            delayBetweenActions();

            // Store the URL of the first search result
            String lowercaseResultUrl = driver.getCurrentUrl();
            delayBetweenActions();

            // Return to main page
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Second search with uppercase first letters
            searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            String uppercaseSearch = "Python Programming";
            searchInput.sendKeys(uppercaseSearch);
            delayBetweenActions();

            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("Python"),
                    ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
            ));
            delayBetweenActions();

            // Store the URL of the second search result
            String uppercaseResultUrl = driver.getCurrentUrl();
            delayBetweenActions();

            // Compare results - they should be similar as Wikipedia search should be case-insensitive
            if (!lowercaseResultUrl.equals(uppercaseResultUrl) &&
                    !lowercaseResultUrl.contains("Python") && !uppercaseResultUrl.contains("Python")) {
                throw new AssertionError("Case sensitivity test failed: Different results for same search with different case");
            }

            System.out.println("Case sensitivity search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Case sensitivity search test FAILED: " + e.getMessage());
        }
    }

    public void testMisspelledSearch() {
        try {
            // Navigate to Wikipedia's homepage
            driver.get("https://www.wikipedia.org/");
            delayBetweenActions();

            // Find the search input field
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            delayBetweenActions();

            // Enter basic search term
            String searchTerm = "Albrt Einstien";
            searchInput.sendKeys(searchTerm);
            delayBetweenActions();

            // Submit the search form
            searchInput.submit();
            delayBetweenActions();

            // Wait for the page to load
            wait.until(ExpectedConditions.titleContains("Einstein"));
            delayBetweenActions();

            // Verify we're on the correct article page
            String pageTitle = driver.getTitle();
            if (!pageTitle.contains("Albert Einstein")) {
                throw new AssertionError("Misspelled search failed: Page title should contain 'Albert Einstein'");
            }

            System.out.println("Misspelled search test PASSED");
            delayBetweenActions();
        } catch (Exception e) {
            System.out.println("Misspelled search test FAILED: " + e.getMessage());
        }
    }

    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    public void runAllTests() {
        setUp();

        try {
            System.out.println("\n========== RUNNING ALL TESTS ==========\n");

            System.out.println("\n----- Test 1: Basic Search -----");
            testBasicSearch();

            System.out.println("\n----- Test 2: Search With Multiple Keywords -----");
            testSearchWithMultipleKeywords();

            System.out.println("\n----- Test 3: Search With Special Characters -----");
            testSearchWithSpecialCharacters();

            System.out.println("\n----- Test 4: Search Suggestions -----");
            testSearchSuggestions();

            System.out.println("\n----- Test 5: Empty Search -----");
            testEmptySearch();

            System.out.println("\n----- Test 6: Search In Different Language -----");
            testSearchInDifferentLanguage();

            System.out.println("\n----- Test 7: Search With Quotation Marks -----");
            testSearchWithQuotationMarks();

            System.out.println("\n----- Test 8: Search With Numbers And Dates -----");
            testSearchWithNumbersAndDates();

            System.out.println("\n----- Test 9: Search Case Sensitivity -----");
            testSearchCaseSensitivity();

            System.out.println("\n----- Test 10: Missplelled Search -----");
            testMisspelledSearch();

            System.out.println("\n========== ALL TESTS COMPLETED ==========");
        } finally {
            tearDown();
        }
    }

    public static void main(String[] args) {
        C4 tests = new C4();
        tests.runAllTests();
    }
}*/
