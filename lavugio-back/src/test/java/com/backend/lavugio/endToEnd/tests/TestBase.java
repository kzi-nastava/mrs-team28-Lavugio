package com.backend.lavugio.endToEnd.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v141.browser.model.PermissionType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
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

    @AfterSuite
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
