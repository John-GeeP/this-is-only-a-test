package edu.JGP.module1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WikipediaTest {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        // Locate chromedriver.exe using a relative path
        String driverPath = new java.io.File("drivers/chromedriver.exe").getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", driverPath);

        // Set up Chrome options
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    public void testWikipediaHomePage() throws InterruptedException {
        // Navigate to Wikipedia and wait for 2 seconds
        driver.get("https://www.wikipedia.com/");
        Thread.sleep(2000);

        // Validate that the title contains "Wikipedia"
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Wikipedia"), "The page title does not contain 'Wikipedia'");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
