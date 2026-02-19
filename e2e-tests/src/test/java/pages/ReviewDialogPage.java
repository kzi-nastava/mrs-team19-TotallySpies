package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ReviewDialogPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ReviewDialogPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".review-container")));
    }

    @FindBy(css = ".title")
    private WebElement title;

    private List<WebElement> getStars() {
        return driver.findElements(By.cssSelector(".stars-row span"));
    }

    @FindBy(css = ".review-input")
    private WebElement reviewInput;

    @FindBy(css = ".send-btn")
    private WebElement sendBtn;

    @FindBy(css = ".close-x")
    private WebElement closeBtn;

    public ReviewDialogPage selectRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".stars-row span")));
        
        List<WebElement> stars = getStars();
        System.out.println("Found " + stars.size() + " stars");
        
        if (stars.isEmpty()) {
            throw new NoSuchElementException("No stars found on the page!");
        }
        
        WebElement star = stars.get(rating - 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", star);
        wait.until(ExpectedConditions.elementToBeClickable(star)).click();
        
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        
        return this;
    }

    public ReviewDialogPage enterComment(String comment) {
        wait.until(ExpectedConditions.visibilityOf(reviewInput));
        reviewInput.clear();
        reviewInput.sendKeys(comment);
        return this;
    }

    public ReviewDialogPage submit() {
        wait.until(ExpectedConditions.elementToBeClickable(sendBtn)).click();
        try {
            wait.until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
        }
        
        return this;
    }

    public ReviewDialogPage close() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        }
        return this;
    }

    public boolean isRatingSelected(int rating) {
        List<WebElement> stars = getStars();
        if (stars.size() < rating) return false;
        
        WebElement star = stars.get(rating - 1);
        String classAttr = star.getAttribute("class");
        return classAttr != null && classAttr.contains("active");
    }

    public String getCommentText() {
        wait.until(ExpectedConditions.visibilityOf(reviewInput));
        return reviewInput.getAttribute("value");
    }

    public boolean isSendButtonEnabled() {
        wait.until(ExpectedConditions.visibilityOf(sendBtn));
        return sendBtn.isEnabled();
    }

    public void acceptAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (TimeoutException e) {
            System.out.println("No alert present");
        }
    }

    public String getAlertText() {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        return alert.getText();
    }

    public boolean isDialogDisplayed() {
        try {
            WebElement container = driver.findElement(By.cssSelector(".review-container"));
            return container.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean waitForDialogToClose(int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return shortWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".review-container")));
        } catch (TimeoutException e) {
            return false;
        }
    }
}