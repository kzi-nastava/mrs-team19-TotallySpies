package tests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import pages.LoginPage;

import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String FRONT_URL = "http://localhost:4200";
    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }
    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
    protected void loginAsAdmin(String email, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(FRONT_URL);
        loginPage.typeEmail(email);
        loginPage.typePassword(password);
        loginPage.submit();
        loginPage.waitUntilLoggedIn();
        wait.until(d -> !d.getCurrentUrl().contains("/login"));
    }
}
