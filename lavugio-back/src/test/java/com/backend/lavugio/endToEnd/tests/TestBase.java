package com.backend.lavugio.endToEnd.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v141.browser.model.PermissionType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class TestBase extends AbstractTestNGSpringContextTests {

    public static WebDriver driver;

    @BeforeSuite
    public void initializeWebDriver() {

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions options = new ChromeOptions();

        // Ako želiš headless:
        // options.addArguments("--headless=new");

        options.addArguments("--use-fake-ui-for-media-stream");
        options.addArguments("--use-fake-device-for-media-stream");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        // ===============================
        // DEVTOOLS - DOZVOLA GEOLOKACIJE
        // ===============================

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(
                org.openqa.selenium.devtools.v141.browser.Browser.grantPermissions(
                        List.of(PermissionType.GEOLOCATION),
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    /**
     * Reset state before each test - logout if logged in
     */
    @BeforeMethod
    public void resetState() {
        try {
            driver.get("http://localhost:4200");
            
            // Wait a bit for page to load
            Thread.sleep(1000);
            
            // Try to find and click logout button if user is logged in
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Logout')]")));
                logoutBtn.click();
                Thread.sleep(500);
            } catch (Exception e) {
                // User is not logged in, no need to logout
            }
            
            // Clear local storage and session storage
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
            
            // Refresh to apply cleared storage
            driver.navigate().refresh();
            Thread.sleep(500);
            
        } catch (Exception e) {
            // Ignore errors in reset, test will handle its own setup
        }
    }

    @AfterSuite
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
