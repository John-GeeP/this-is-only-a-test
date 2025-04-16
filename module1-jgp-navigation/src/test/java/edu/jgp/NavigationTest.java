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

/**
 * Navigation tests for Wikipedia using Selenium, TestNG, and WebDriverManager.
 */
public class NavigationTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String PORTAL_URL       = "https://www.wikipedia.org/";
    private static final String EN_MAIN_PAGE_URL = "https://en.wikipedia.org/wiki/Main_Page";
    private static final String WIKI_BASE        = "https://en.wikipedia.org/wiki";
    private static final String ARTICLE_URL      = WIKI_BASE + "/Selenium_(software)";

    @BeforeClass
    public static void globalSetup() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions().addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(EN_MAIN_PAGE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Verify the Wikipedia portal page loads with 'Wikipedia' in the title")
    public void testOpenHomePage() {
        driver.get(PORTAL_URL);
        Assert.assertTrue(driver.getTitle().contains("Wikipedia"),
                "Portal page title should contain 'Wikipedia'");
    }

    @Test(description = "From an article, click the logo to return to the main page")
    public void testClickLogoReturnsHome() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement logo = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("a.mw-logo"))
        );
        logo.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.urlToBe(EN_MAIN_PAGE_URL));
        Assert.assertEquals(driver.getCurrentUrl(), EN_MAIN_PAGE_URL,
                "Clicking logo should navigate back to Main Page");
    }

    @Test(description = "Click the first internal link in an article's content")
    public void testClickFirstInternalLink() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement firstLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("div#mw-content-text p a[href*='/wiki/']:not([href*=':'])")
                )
        );
        String originalUrl = driver.getCurrentUrl();

        firstLink.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.urlMatches(WIKI_BASE + "/.+"));
        String newUrl = driver.getCurrentUrl();
        Assert.assertTrue(newUrl.startsWith(WIKI_BASE + "/"),
                "Should navigate to another article");
        Assert.assertNotEquals(newUrl, originalUrl,
                "URL must change after clicking the first link");
    }

    @Test(description = "Use the Random Article link to open a random page")
    public void testOpenRandomArticle() throws InterruptedException {
        driver.get(ARTICLE_URL);

        WebElement menuCheckbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("vector-main-menu-dropdown-checkbox"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuCheckbox);
        Thread.sleep(2000);

        WebElement menuList = wait.until(
                ExpectedConditions.visibilityOf(
                        driver.findElement(By.cssSelector("ul.vector-menu-content-list"))
                )
        );

        By randomLink = By.cssSelector("li#n-randompage a");
        wait.until(ExpectedConditions.elementToBeClickable(randomLink)).click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.urlMatches(WIKI_BASE + "/.+"));
        Assert.assertFalse(driver.getTitle().isEmpty(),
                "Random article should have a non-empty title");
    }

    @Test(description = "Use browser back and forward navigation between pages")
    public void testNavigateBackAndForward() throws InterruptedException {
        driver.get(EN_MAIN_PAGE_URL);
        Thread.sleep(2000);
        driver.get(ARTICLE_URL);
        Thread.sleep(2000);

        driver.navigate().back();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlToBe(EN_MAIN_PAGE_URL));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("Main_Page"),
                "Back navigation should return to Main Page");

        driver.navigate().forward();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.urlContains("Selenium_(software)"));
        Assert.assertTrue(driver.getCurrentUrl().contains("Selenium_(software)"),
                "Forward navigation should return to the article");
    }

    @Test(description = "Switch an article to French via the interlanguage link")
    public void testChangeLanguageLink() {
        driver.get(ARTICLE_URL);

        // Expand ULS menu
        WebElement toggle = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("p-lang-btn"))
        );
        toggle.click();

        // Wait for ULS panel and locate French link
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.uls-menu ul")
        ));
        By frenchLink = By.cssSelector("li.interwiki-fr > a.autonym[lang='fr']");
        WebElement french = wait.until(
                ExpectedConditions.elementToBeClickable(frenchLink)
        );

        // Ensure in view, click
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'})", french);
        french.click();

        // Wait for subdomain change
        wait.until(ExpectedConditions.urlContains("fr.wikipedia.org"));
        Assert.assertTrue(
                driver.getCurrentUrl().contains("fr.wikipedia.org"),
                "Should be on the French Wikipedia subdomain"
        );

        // Verify <html> tag’s lang attribute is “fr”
        String htmlLang = driver.findElement(By.tagName("html"))
                .getAttribute("lang");
        Assert.assertEquals(
                htmlLang, "fr",
                "The page’s <html> lang attribute should be 'fr'"
        );

        // Check heading reflects French title
        String heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading"))
        ).getText();
        Assert.assertTrue(
                heading.contains("(informatique)"),
                "Heading should show the French article title"
        );
    }

    @Test(description = "Open the revision history of an article")
    public void testOpenHistoryTab() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement historyTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#ca-history a"))
        );
        historyTab.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagehistory")));
        Assert.assertTrue(driver.findElement(By.id("pagehistory")).isDisplayed(),
                "Revision history list should be displayed");
    }

    @Test(description = "Open the Talk page of an article")
    public void testOpenTalkPage() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement talkTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#ca-talk a"))
        );
        talkTab.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.urlContains("Talk:Selenium_(software)"));
        Assert.assertTrue(driver.getTitle().startsWith("Talk:"),
                "Talk page title should begin with 'Talk:'");
    }

    @Test(description = "Navigate to a category page via the category link in the footer")
    public void testNavigateToCategoryPage() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement categoryLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".mw-normal-catlinks ul li a")
                )
        );
        categoryLink.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.urlContains("/wiki/Category:"));
        Assert.assertTrue(driver.getTitle().startsWith("Category:"),
                "Category page title should start with 'Category:'");
    }

    @Test(description = "Click a Table of Contents entry and verify fragment navigation")
    public void testTableOfContentsNavigation() throws InterruptedException {
        driver.get(ARTICLE_URL);
        WebElement tocLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("ul#mw-panel-toc-list li a[href*='#']:not([href$='#'])")
                )
        );
        tocLink.click();
        Thread.sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("#"),
                "URL should contain a fragment after clicking a TOC entry");

        String fragment = currentUrl.substring(currentUrl.indexOf('#') + 1);
        Assert.assertFalse(fragment.isEmpty(),
                "Fragment after '#' should not be empty");

        WebElement sectionHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id(fragment))
        );
        Assert.assertTrue(sectionHeading.isDisplayed(),
                "Section heading should be visible");
    }
}
