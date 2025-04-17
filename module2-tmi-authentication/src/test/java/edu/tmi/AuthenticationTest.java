package edu.tmi;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class Main {
    // Hardcoded test credentials
    private static final String username = "Meeatbaag";
    private static final String password = "Hkforty7!";

    // Wikipedia URLs
    private static final String BASE_URL = "https://en.wikipedia.org/wiki/Main_Page";

    // WebDriver and wait
    private WebDriver driver;
    private WebDriverWait wait;

    // Pause duration in milliseconds (2 seconds)
    private static final long PAUSE_DURATION = 2000;

    @BeforeMethod
    public void setupBrowser() {
        // Let Selenium locate and use the ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        // Initialize driver and wait
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("Browser started");
    }

    @AfterMethod
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }

    /**
     * Helper method to pause execution to make tests easier to follow
     */
    private void pause() {
        try {
            Thread.sleep(PAUSE_DURATION);
        } catch (InterruptedException e) {
            System.out.println("Pause interrupted: " + e.getMessage());
        }
    }

    /**
     * Overloaded pause with custom duration
     */
    private void pause(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            System.out.println("Pause interrupted: " + e.getMessage());
        }
    }

    @DataProvider(name = "emptyCredentialsProvider")
    public Object[][] provideEmptyCredentials() {
        return new Object[][]{
                {"", password, "Empty username"},
                {username, "", "Empty password"},
                {"", "", "Both empty"}
        };
    }

    @DataProvider(name = "sqlInjectionProvider")
    public Object[][] provideSqlInjection() {
        return new Object[][]{
                {"' OR '1'='1"},
                {"admin' --"},
                {"' OR 1=1;--"},
                {"' UNION SELECT 1,username,password FROM users--"}
        };
    }

    // 1) Incorrect login scenarios
    @Test(priority = 1)
    public void testInvalidUsername() {
        System.out.println("\nTest Case: Verify login failure with invalid username");

        // Navigate to login page
        navigateToLoginPage();
        pause();

        // Login with invalid username
        String invalidUsername = "invalid_user_" + System.currentTimeMillis();
        System.out.println("  Attempting login with invalid username: " + invalidUsername);
        performLogin(invalidUsername, password);
        pause();

        // Verify error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[2]/div/div[3]/main/div[3]/div[3]/div[2]/div/form/div[1]/div")));

        Assert.assertTrue(errorMsg.isDisplayed() &&
                        errorMsg.getText().contains("Incorrect username or password"),
                "Error should indicate invalid credentials");
        System.out.println("TEST PASSED: Error message displayed for invalid username");
    }

    @Test(priority = 2)
    public void testInvalidPassword() {
        System.out.println("\nTest Case: Verify login failure with invalid password");

        // Navigate to login page
        navigateToLoginPage();
        pause();

        // Login with invalid password
        String invalidPassword = "invalid_pass_" + System.currentTimeMillis();
        System.out.println("  Attempting login with invalid password");
        performLogin(username, invalidPassword);
        pause();

        // Verify error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[2]/div/div[3]/main/div[3]/div[3]/div[2]/div/form/div[1]/div")));

        Assert.assertTrue(errorMsg.isDisplayed() &&
                        errorMsg.getText().contains("Incorrect username or password"),
                "Error should indicate invalid credentials");
        System.out.println("TEST PASSED: Error message displayed for invalid password");
    }

    @Test(priority = 3, dataProvider = "emptyCredentialsProvider")
    public void testEmptyCredentials(String testUsername, String testPassword, String testDescription) {
        System.out.println("\nTest Case: Test login with " + testDescription);

        // Navigate to login page
        navigateToLoginPage();
        pause();

        System.out.println("  Testing: " + testDescription);

        // Fill in credentials (intentionally empty)
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpName1")));
        WebElement passwordField = driver.findElement(By.id("wpPassword1"));
        WebElement loginButton = driver.findElement(By.id("wpLoginAttempt"));

        usernameField.clear();
        usernameField.sendKeys(testUsername);

        passwordField.clear();
        passwordField.sendKeys(testPassword);

        pause();
        loginButton.click();
        pause();

        // Check for field validation (either HTML5 validation or error message)
        boolean validationError = false;

        // Check for error message
        List<WebElement> errorMsgs = driver.findElements(By.cssSelector(".mw-message-box-error"));
        if (!errorMsgs.isEmpty()) {
            validationError = true;
        }

        // Check if the field has a "required" attribute
        if (testUsername.isEmpty()) {
            String required = usernameField.getAttribute("required");
            if (required != null && !required.isEmpty()) {
                validationError = true;
            }
        }

        if (testPassword.isEmpty()) {
            String required = passwordField.getAttribute("required");
            if (required != null && !required.isEmpty()) {
                validationError = true;
            }
        }

        Assert.assertTrue(validationError, "Validation error should be detected for " + testDescription);
        System.out.println("  TEST PASSED: Validation error detected for " + testDescription);
    }

    @Test(priority = 4)
    public void testPasswordMasking() {
        System.out.println("\nTest Case: Verify password is masked during entry");

        // Navigate to login page
        navigateToLoginPage();
        pause();

        // Demonstrate masking by entering password slowly
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpPassword1")));
        String demoPassword = "Password123";
        passwordField.clear();
        for (char c : demoPassword.toCharArray()) {
            passwordField.sendKeys(String.valueOf(c));
            pause(500);
        }

        // Now check the attribute
        String fieldType = passwordField.getAttribute("type");
        pause();

        Assert.assertEquals(fieldType, "password", "Password field should be of type 'password'");
        System.out.println("TEST PASSED: Password field is masked (type=\"password\")");
    }

    @Test(priority = 6, dataProvider = "sqlInjectionProvider")
    public void testSqlInjectionPrevention(String sqlInjection) {
        System.out.println("\nTest Case: Test SQL injection prevention with: " + sqlInjection);

        // Navigate to login page
        navigateToLoginPage();
        pause();

        System.out.println("  Testing SQL injection: " + sqlInjection);

        // Attempt SQL injection login
        performLogin(sqlInjection, sqlInjection);
        pause();

        // Verify login failed
        List<WebElement> userLinks = driver.findElements(
                By.xpath("//li[@id='pt-userpage-2']/a"));

        boolean injectionFailed = userLinks.isEmpty() || !userLinks.get(0).getText().equals(sqlInjection);
        Assert.assertTrue(injectionFailed, "SQL Injection attempt should not result in successful login");
        System.out.println("  PASSED: SQL injection attempt failed as expected");
    }

    @Test(priority = 5)
    public void testSuccessfulLogin() {
        System.out.println("\nTest Case: Verify successful login with valid credentials");

        // Navigate to login page
        navigateToLoginPage();
        pause();

        // Login with valid credentials
        System.out.println("  Attempting login with valid credentials...");
        performLogin(username, password);
        pause();

        // Verify successful login
        WebElement userLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@id='pt-userpage-2']/a/span[text()='Meeatbaag']")));

        Assert.assertTrue(userLink.isDisplayed(), "User should be logged in successfully");
        System.out.println("  LOGIN TEST PASSED: Successfully logged in with valid credentials");
    }

    @Test(priority = 7, dependsOnMethods = "testSuccessfulLogin")
    public void testSuccessfulLogout() {
        System.out.println("\nTest Case: Verify successful logout after login");

        // First log in
        navigateToLoginPage();
        performLogin(username, password);
        pause();

        // Now test logout functionality
        System.out.println("  Now testing logout functionality...");

        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('vector-user-links-dropdown-checkbox').click();");

        pause();

        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@id='pt-logout']/a")));
        logoutLink.click();
        System.out.println("  Clicked logout link");
        pause();

        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@id='pt-login-2']/a")));

        Assert.assertTrue(loginLink.isDisplayed(), "Login link should be visible after logout");
        System.out.println("  LOGOUT TEST PASSED: Successfully logged out");
    }

    @Test(priority = 8)
    public void testCaptchaAfterMultipleFailedAttempts() {
        System.out.println("\nTest Case: Check for CAPTCHA after multiple failed attempts");

        navigateToLoginPage();
        pause();

        boolean captchaFound = false;
        int attempts = 0;
        int maxAttempts = 5;

        System.out.println("  Attempting failed logins to trigger CAPTCHA...");

        while (attempts < maxAttempts && !captchaFound) {
            attempts++;
            System.out.println("  Attempt " + attempts + "...");

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpName1")));
            WebElement passwordField = driver.findElement(By.id("wpPassword1"));
            WebElement loginButton = driver.findElement(By.id("wpLoginAttempt"));

            usernameField.clear();
            usernameField.sendKeys("invalid_user_" + System.currentTimeMillis());

            passwordField.clear();
            passwordField.sendKeys("invalid_pass_" + System.currentTimeMillis());

            pause();
            loginButton.click();
            pause();

            captchaFound = isCaptchaPresent();
            if (captchaFound) {
                System.out.println("  CAPTCHA found on attempt " + attempts);
                break;
            } else {
                driver.navigate().refresh();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpName1")));
            }

            pause();
        }

        if (captchaFound) {
            System.out.println("TEST PASSED: CAPTCHA was displayed after " + attempts + " failed attempts");
        } else {
            System.out.println("TEST WARNING: No CAPTCHA was displayed after " + maxAttempts + " failed login attempts.");
        }
    }

    /**
     * Helper method to check if CAPTCHA is present
     */
    private boolean isCaptchaPresent() {
        List<WebElement> captchaContainers = driver.findElements(
                By.cssSelector(".mw-captcha-container, #mw-input-captcha, .captcha"));
        if (!captchaContainers.isEmpty()) return true;

        List<WebElement> captchaInputs = driver.findElements(
                By.cssSelector("input[name='wpCaptchaWord'], input.mw-captcha-input"));
        if (!captchaInputs.isEmpty()) return true;

        List<WebElement> captchaImages = driver.findElements(
                By.cssSelector("img.captcha, img[src*='captcha']"));
        if (!captchaImages.isEmpty()) return true;

        String pageSource = driver.getPageSource().toLowerCase();
        return pageSource.contains("captcha") || pageSource.contains("robot") || pageSource.contains("human verification");
    }

    /**
     * Helper method to navigate to login page
     */
    private void navigateToLoginPage() {
        driver.get(BASE_URL);
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@id='pt-login-2']/a")));
        loginLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpName1")));
    }

    /**
     * Helper method to perform login
     */
    private void performLogin(String user, String pass) {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wpName1")));
        WebElement passwordField = driver.findElement(By.id("wpPassword1"));
        WebElement loginButton = driver.findElement(By.id("wpLoginAttempt"));

        usernameField.clear();
        usernameField.sendKeys(user);

        passwordField.clear();
        passwordField.sendKeys(pass);

        loginButton.click();
    }
}
