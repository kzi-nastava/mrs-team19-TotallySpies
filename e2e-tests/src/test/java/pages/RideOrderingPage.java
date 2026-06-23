package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RideOrderingPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public RideOrderingPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".order-link")
    private WebElement orderRideMenuLink;

    @FindBy(css = ".favorites")
    private WebElement pickFromFavoritesBtn;

    @FindBy(css = ".confirm-btn")
    private WebElement bookNowBtn;

    @FindBy(css = ".total-price")
    private WebElement totalPriceElement;

    @FindBy(css = ".modal-backdrop")
    private WebElement favoritesModalBackdrop;

    @FindBy(css = ".fav-card")
    private WebElement firstFavoriteCard;

    @FindBy(css = ".driver-modal")
    private WebElement driverModal;

    @FindBy(css = ".driver-modal h2")
    private WebElement modalStatusTitle;

    @FindBy(css = ".driver-modal button")
    private WebElement modalCloseBtn;

    public RideOrderingPage navigateViaMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(orderRideMenuLink)).click();
        wait.until(ExpectedConditions.visibilityOf(pickFromFavoritesBtn));
        return this;
    }

    public RideOrderingPage clickPickFromFavorites() {
        wait.until(ExpectedConditions.elementToBeClickable(pickFromFavoritesBtn)).click();
        wait.until(ExpectedConditions.visibilityOf(favoritesModalBackdrop));
        return this;
    }

    public RideOrderingPage selectFirstFavoriteRoute() {
        wait.until(ExpectedConditions.elementToBeClickable(firstFavoriteCard)).click();
        wait.until(ExpectedConditions.invisibilityOf(favoritesModalBackdrop));
        return this;
    }

    public RideOrderingPage waitForPriceToLoad() {
        wait.until(ExpectedConditions.visibilityOf(totalPriceElement));
        wait.until(d -> totalPriceElement.getText().contains("Price:"));
        return this;
    }

    public RideOrderingPage clickBookNow() {
        wait.until(ExpectedConditions.elementToBeClickable(bookNowBtn)).click();
        return this;
    }

    public void waitForDriverSearchToStart() {
        wait.until(ExpectedConditions.visibilityOf(driverModal));
    }

    public String getDriverSearchResultText() {
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(modalStatusTitle, "Finding a driver...")));
        return modalStatusTitle.getText().trim();
    }

    public void closeDriverModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalCloseBtn)).click();
        wait.until(ExpectedConditions.invisibilityOf(driverModal));
    }
}
