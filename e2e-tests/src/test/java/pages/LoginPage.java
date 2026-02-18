package pages;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    WebDriver webDriver;
    WebDriverWait wait;

    public LoginPage(WebDriver webDriver){
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        PageFactory.initElements(webDriver, this);
    }
    @FindBy(css = "input[name ='email']")
    private WebElement emailInput;

    @FindBy(css = ".login-card h1")
    private WebElement title;

    @FindBy(css = "input[name ='password'] ")
    private WebElement passwordInput;

    @FindBy(css = "form button[type='submit']")
    private WebElement loginBtn;

    public LoginPage open(String frontUrl) {
        webDriver.get(frontUrl + "/login");
        wait.until(ExpectedConditions.visibilityOf(title));
        return this;
    }

    public LoginPage typeEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);
        return this;
    }

    public LoginPage typePassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    public LoginPage submit() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
        return this;
    }
    public void waitUntilLoggedIn() {
        // wait til token is in the local storage
        wait.until(d -> {
            Object token = ((JavascriptExecutor) d)
                    .executeScript("return window.localStorage.getItem('token');");
            return token != null && !token.toString().isBlank();
        });
    }

}
