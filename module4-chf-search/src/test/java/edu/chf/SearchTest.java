package edu.chf;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class SearchTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://www.wikipedia.org/";

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

    @BeforeClass
    public static void globalSetup() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions().addArguments("--start-maximized");
        // For CI, consider headless options:
        // options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1, description = "Basic search for Albert Einstein")
    public void testBasicSearch() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        searchInput.sendKeys("Albert Einstein");
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.titleContains("Albert Einstein"));
        Assert.assertTrue(driver.getTitle().contains("Albert Einstein"),
                "Page title should contain 'Albert Einstein'");
        delayBetweenActions();
    }

    @Test(priority = 2, description = "Search with multiple keywords")
    public void testSearchWithMultipleKeywords() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        delayBetweenActions();
        searchInput.sendKeys("quantum physics nobel prize");
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results")));
        String text = driver.findElement(By.className("mw-search-results")).getText().toLowerCase();
        Assert.assertTrue(
                text.contains("quantum") || text.contains("physics") || text.contains("nobel"),
                "Results should contain at least one keyword"
        );
    }

    @Test(priority = 3, description = "Search with special characters (C++)")
    public void testSearchWithSpecialCharacters() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        delayBetweenActions();
        searchInput.sendKeys("C++");
        delayBetweenActions();
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("C++"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        String body = driver.findElement(By.tagName("body")).getText();
        boolean pageOK = driver.getTitle().contains("C++");
        boolean resultsOK = driver.findElements(By.className("mw-search-results")).size() > 0;
        Assert.assertTrue(pageOK || resultsOK,
                "Should find C++ page or search results");
    }

    @Test(priority = 4, description = "Search suggestions appear on typing")
    public void testSearchSuggestions() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        for (char c : "United Stat".toCharArray()) {
            searchInput.sendKeys(String.valueOf(c));
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("suggestion-link")));
        Assert.assertTrue(
                driver.findElement(By.className("suggestion-link")).isDisplayed(),
                "Suggestions should be displayed"
        );
    }

    @Test(priority = 5, description = "Submit an empty search and verify behavior")
    public void testEmptySearch() {
        // Go to main page
        driver.get("https://www.wikipedia.org/");
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );

        // Capture original URL
        String originalUrl = driver.getCurrentUrl();

        // Clear and submit empty search
        searchInput.clear();
        delayBetweenActions();
        searchInput.submit();

        // Wait until either staying on the same URL or landing on search results page
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(originalUrl),
                ExpectedConditions.urlContains("Special:Search")
        ));

        String currentUrl = driver.getCurrentUrl();
        delayBetweenActions();

        // Assert that behavior matches one of two acceptable outcomes
        Assert.assertTrue(
                currentUrl.equals(originalUrl) ||
                        currentUrl.contains("Special:Search"),
                "Empty search should stay on main page or go to search results; was: " + currentUrl
        );
    }

    @Test(priority = 6, description = "Search in Spanish Wikipedia")
    public void testSearchInDifferentLanguage() {
        driver.get("https://es.wikipedia.org/");
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        searchInput.sendKeys("Madrid");
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.titleContains("Madrid"));
        String content = driver.findElement(By.id("mw-content-text")).getText().toLowerCase();
        Assert.assertTrue(content.contains("españa") || content.contains("capital"),
                "Content should be in Spanish about Madrid");
    }

    @Test(priority = 7, description = "Search with quotation marks for exact phrase")
    public void testSearchWithQuotationMarks() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        String phrase = "\"to be or not to be\"";
        for (char c : phrase.toCharArray()) {
            searchInput.sendKeys(String.valueOf(c));
        }
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("To be, or not to be"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                body.contains("to be or not to be"),
                "Results should contain the exact phrase 'to be or not to be'"
        );
        delayBetweenActions();
    }

    @Test(priority = 8, description = "Search with numbers and dates (World War 1914)")
    public void testSearchWithNumbersAndDates() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        searchInput.sendKeys("World War 1914");
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("World War"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        String body = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(
                body.contains("world war") && (body.contains("1914") || body.contains("wwi") || body.contains("first world war")),
                "Results should mention World War I"
        );
    }

    @Test(priority = 9, description = "Search case sensitivity check")
    public void testSearchCaseSensitivity() {
        driver.get(BASE_URL);
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchInput")));
        input.sendKeys("python programming");
        delayBetweenActions();
        input.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Python"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        String lowerUrl = driver.getCurrentUrl();
        delayBetweenActions();

        driver.get(BASE_URL);
        WebElement input2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchInput")));
        input2.sendKeys("Python Programming");
        delayBetweenActions();
        input2.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Python"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        String upperUrl = driver.getCurrentUrl();
        Assert.assertEquals(lowerUrl, upperUrl,
                "Search should be case-insensitive and yield same URL");
        delayBetweenActions();
    }

    @Test(priority = 10, description = "Misspelled search suggestion or redirect for Einstein")
    public void testMisspelledSearch() {
        driver.get(BASE_URL);
        WebElement searchInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("searchInput"))
        );
        searchInput.sendKeys("Albrt Einstien");
        delayBetweenActions();
        searchInput.submit();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Einstein"),
                ExpectedConditions.presenceOfElementLocated(By.className("mw-search-results"))
        ));
        delayBetweenActions();
        String body = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(
                body.toLowerCase().contains("einstein"),
                "Misspelled search should include suggestions or content about 'Einstein'"
        );
    }
}
