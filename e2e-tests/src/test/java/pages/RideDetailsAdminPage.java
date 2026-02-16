package pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RideDetailsAdminPage{
    private final WebDriver webDriver;
    private final WebDriverWait wait;

    public RideDetailsAdminPage(WebDriver webDriver){
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        PageFactory.initElements(webDriver, this);
    }
    @FindBy(css = ".map-container")
    private WebElement mapContainer;

    @FindBy(xpath = "//*[contains(text(),'Driver details')]")
    private WebElement driverDetailsHeader;

    @FindBy(xpath = "//*[contains(text(),'Passengers')]")
    private WebElement passengersHeader;

    public RideDetailsAdminPage assertLoaded() {
        wait.until(ExpectedConditions.visibilityOf(mapContainer));
        wait.until(ExpectedConditions.visibilityOf(driverDetailsHeader));
        wait.until(ExpectedConditions.visibilityOf(passengersHeader));
        return this;
    }
}