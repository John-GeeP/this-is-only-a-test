package edu.jgp;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class NavigationTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String PORTAL_URL       = "https://www.wikipedia.org/";
    private static final String EN_MAIN_PAGE_URL = "https://en.wikipedia.org/wiki/Main_Page";
    private static final String WIKI_BASE        = "https://en.wikipedia.org/wiki";
    private static final String ARTICLE_URL      = WIKI_BASE + "/Selenium_(software)";

    // Pause helper: delay for visibility
    public static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Pause interrupted: " + e.getMessage());
        }
    }

    @BeforeClass
    public static void globalSetup() {
        System.out.println("BeforeClass: setup WebDriverManager");
        WebDriverManager.chromedriver().setup();
        System.out.println("BeforeClass: WebDriverManager ready");
    }

    @BeforeMethod
    public void setUp() {
        System.out.println("BeforeMethod: starting browser session");
        ChromeOptions options = new ChromeOptions().addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("BeforeMethod: navigating to main page");
        driver.get(EN_MAIN_PAGE_URL);
        System.out.println("BeforeMethod: main page loaded");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("AfterMethod: closing browser session");
        if (driver != null) {
            driver.quit();
        }
        System.out.println("AfterMethod: browser closed");
    }

    // Test 1: verify portal page loads with 'Wikipedia' in title
    @Test(priority = 1, description = "Verify portal page loads with 'Wikipedia' in title")
    public void testOpenHomePage() {
        System.out.println("Test 1: verify portal page loads with 'Wikipedia' in title");
        System.out.println("Navigating to portal URL: " + PORTAL_URL);
        driver.get(PORTAL_URL);

        System.out.println("Retrieving page title");
        pause();
        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        try {
            Assert.assertTrue(title.contains("Wikipedia"),
                    "Portal page title should contain 'Wikipedia'");
            System.out.println("Test 1 SUCCESS: title contains 'Wikipedia'");
        } catch (AssertionError e) {
            System.out.println("Test 1 FAILURE: title does not contain 'Wikipedia'");
            throw e;
        }
        pause();
    }

    // Test 2: click logo to return to main page
    @Test(priority = 2, description = "From an article, click logo to return to main page")
    public void testClickLogoReturnsHome() {
        System.out.println("Test 2: click logo to return to main page");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Waiting for logo element");
        pause();
        WebElement logo = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a.mw-logo"))
        );

        System.out.println("Clicking logo");
        pause();
        logo.click();

        System.out.println("Waiting for main page URL");
        pause();
        wait.until(ExpectedConditions.urlToBe(EN_MAIN_PAGE_URL));

        String current = driver.getCurrentUrl();
        System.out.println("Current URL: " + current);

        try {
            Assert.assertEquals(current, EN_MAIN_PAGE_URL,
                    "Clicking logo should navigate back to Main Page");
            System.out.println("Test 2 SUCCESS: navigated back to main page");
        } catch (AssertionError e) {
            System.out.println("Test 2 FAILURE: did not navigate back to main page");
            throw e;
        }
        pause();
    }

    // Test 3: click first internal link in article content
    @Test(priority = 3, description = "Click first internal link in article content")
    public void testClickFirstInternalLink() {
        System.out.println("Test 3: click first internal link in article content");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Waiting for first internal link");
        pause();
        WebElement firstLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("div#mw-content-text p a[href*='/wiki/']:not([href*=':'])")
                )
        );

        String original = driver.getCurrentUrl();
        System.out.println("Original URL: " + original);

        System.out.println("Clicking first internal link");
        pause();
        firstLink.click();

        System.out.println("Waiting for new article URL");
        pause();
        wait.until(ExpectedConditions.urlMatches(WIKI_BASE + "/.+"));

        String current = driver.getCurrentUrl();
        System.out.println("New URL: " + current);

        try {
            Assert.assertTrue(current.startsWith(WIKI_BASE + "/"),
                    "Should navigate to another article");
            Assert.assertNotEquals(current, original,
                    "URL must change after clicking the first link");
            System.out.println("Test 3 SUCCESS: navigated to new article");
        } catch (AssertionError e) {
            System.out.println("Test 3 FAILURE: navigation did not occur as expected");
            throw e;
        }
        pause();
    }

    // Test 4: open random article via menu
    @Test(priority = 4, description = "Use Random Article link to open random page")
    public void testOpenRandomArticle() {
        System.out.println("Test 4: open random article via menu");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Opening main menu");
        pause();
        WebElement menuCheckbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("vector-main-menu-dropdown-checkbox"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuCheckbox);

        System.out.println("Waiting for menu list");
        pause();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("ul.vector-menu-content-list")
        ));

        System.out.println("Clicking random article link");
        pause();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("li#n-randompage a")
        )).click();

        System.out.println("Waiting for random article URL");
        pause();
        wait.until(ExpectedConditions.urlMatches(WIKI_BASE + "/.+"));

        String title = driver.getTitle();
        System.out.println("Random article title: " + title);

        try {
            Assert.assertFalse(title.isEmpty(),
                    "Random article should have a non-empty title");
            System.out.println("Test 4 SUCCESS: random article opened");
        } catch (AssertionError e) {
            System.out.println("Test 4 FAILURE: random article title empty");
            throw e;
        }
        pause();
    }

    // Test 5: browser back and forward navigation
    @Test(priority = 5, description = "Use browser back and forward navigation")
    public void testNavigateBackAndForward() {
        System.out.println("Test 5: browser back and forward navigation");
        System.out.println("Navigating to main page");
        driver.get(EN_MAIN_PAGE_URL);

        System.out.println("Navigating to article page");
        pause();
        driver.get(ARTICLE_URL);

        System.out.println("Navigating back");
        pause();
        driver.navigate().back();

        System.out.println("Waiting for main page URL");
        pause();
        wait.until(ExpectedConditions.urlToBe(EN_MAIN_PAGE_URL));
        System.out.println("Back navigation URL: " + driver.getCurrentUrl());

        System.out.println("Navigating forward");
        pause();
        driver.navigate().forward();

        System.out.println("Waiting for article URL");
        pause();
        wait.until(ExpectedConditions.urlContains("Selenium_(software)"));
        System.out.println("Forward navigation URL: " + driver.getCurrentUrl());

        try {
            Assert.assertTrue(driver.getCurrentUrl().contains("Selenium_(software)"),
                    "Forward navigation should return to the article");
            System.out.println("Test 5 SUCCESS: navigation back and forward works");
        } catch (AssertionError e) {
            System.out.println("Test 5 FAILURE: back/forward did not work as expected");
            throw e;
        }
        pause();
    }

    // Test 6: switch article to French via language link
    @Test(priority = 6, description = "Switch article to French via language link")
    public void testChangeLanguageLink() {
        System.out.println("Test 6: switch article to French via language link");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Opening language menu");
        pause();
        WebElement toggle = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("p-lang-btn"))
        );
        toggle.click();

        System.out.println("Waiting for French link");
        pause();
        By frenchLink = By.cssSelector("li.interwiki-fr > a.autonym[lang='fr']");
        WebElement french = wait.until(
                ExpectedConditions.elementToBeClickable(frenchLink)
        );

        System.out.println("Clicking French link");
        pause();
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'})", french);
        french.click();

        System.out.println("Waiting for French subdomain");
        pause();
        wait.until(ExpectedConditions.urlContains("fr.wikipedia.org"));

        String htmlLang = driver.findElement(By.tagName("html"))
                .getAttribute("lang");
        System.out.println("HTML lang attribute: " + htmlLang);

        try {
            Assert.assertEquals(htmlLang, "fr",
                    "HTML lang attribute should be 'fr'");
            System.out.println("Test 6 SUCCESS: language switched to French");
        } catch (AssertionError e) {
            System.out.println("Test 6 FAILURE: language did not switch");
            throw e;
        }
        pause();
    }

    // Test 7: open revision history tab
    @Test(priority = 7, description = "Open revision history tab")
    public void testOpenHistoryTab() {
        System.out.println("Test 7: open revision history tab");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Clicking history tab");
        pause();
        WebElement historyTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#ca-history a"))
        );
        historyTab.click();

        System.out.println("Waiting for history section");
        pause();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagehistory")));

        try {
            Assert.assertTrue(driver.findElement(By.id("pagehistory")).isDisplayed(),
                    "Revision history list should be displayed");
            System.out.println("Test 7 SUCCESS: history tab opened");
        } catch (AssertionError e) {
            System.out.println("Test 7 FAILURE: history not displayed");
            throw e;
        }
        pause();
    }

    // Test 8: open talk page
    @Test(priority = 8, description = "Open talk page")
    public void testOpenTalkPage() {
        System.out.println("Test 8: open talk page");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Clicking talk tab");
        pause();
        WebElement talkTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#ca-talk a"))
        );
        talkTab.click();

        System.out.println("Waiting for talk page URL");
        pause();
        wait.until(ExpectedConditions.urlContains("Talk:Selenium_(software)"));

        try {
            Assert.assertTrue(driver.getTitle().startsWith("Talk:"),
                    "Talk page title should begin with 'Talk:'");
            System.out.println("Test 8 SUCCESS: talk page opened");
        } catch (AssertionError e) {
            System.out.println("Test 8 FAILURE: talk page did not open");
            throw e;
        }
        pause();
    }

    // Test 9: navigate to category page
    @Test(priority = 9, description = "Navigate to category page")
    public void testNavigateToCategoryPage() {
        System.out.println("Test 9: navigate to category page");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Clicking category link");
        pause();
        WebElement categoryLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".mw-normal-catlinks ul li a")
                )
        );
        categoryLink.click();

        System.out.println("Waiting for category page URL");
        pause();
        wait.until(ExpectedConditions.urlContains("/wiki/Category:"));

        try {
            Assert.assertTrue(driver.getTitle().startsWith("Category:"),
                    "Category page title should start with 'Category:'");
            System.out.println("Test 9 SUCCESS: category page opened");
        } catch (AssertionError e) {
            System.out.println("Test 9 FAILURE: category page did not open");
            throw e;
        }
        pause();
    }

    // Test 10: table of contents fragment navigation
    @Test(priority = 10, description = "Table of Contents fragment navigation")
    public void testTableOfContentsNavigation() {
        System.out.println("Test 10: table of contents fragment navigation");
        System.out.println("Navigating to article URL: " + ARTICLE_URL);
        driver.get(ARTICLE_URL);

        System.out.println("Clicking TOC entry");
        pause();
        WebElement tocLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("ul#mw-panel-toc-list li a[href*='#']:not([href$='#'])")
                )
        );
        tocLink.click();

        System.out.println("Waiting for fragment in URL");
        pause();
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        String fragment = currentUrl.substring(currentUrl.indexOf('#') + 1);
        System.out.println("Fragment: " + fragment);

        System.out.println("Verifying section heading visibility");
        pause();
        WebElement sectionHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id(fragment))
        );

        try {
            Assert.assertTrue(sectionHeading.isDisplayed(),
                    "Section heading should be visible");
            System.out.println("Test 10 SUCCESS: fragment navigation works");
        } catch (AssertionError e) {
            System.out.println("Test 10 FAILURE: fragment navigation failed");
            throw e;
        }
        pause();
    }
}
