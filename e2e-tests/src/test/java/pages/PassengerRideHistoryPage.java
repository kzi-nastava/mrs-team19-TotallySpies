package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class PassengerRideHistoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public PassengerRideHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "input[formcontrolname='filterFrom']")
    private WebElement fromDate;

    @FindBy(css = "input[formcontrolname='filterTo']")
    private WebElement toDate;

    @FindBy(css = ".search-btn")
    private WebElement searchBtn;

    @FindBy(css = ".reset-btn")
    private WebElement resetBtn;

    private final By rideRowsBy = By.cssSelector(".rides-table tbody tr");
    private final By noRidesCellBy = By.cssSelector(".rides-table tbody tr td[colspan]");

    public PassengerRideHistoryPage open(String frontUrl) {
        driver.get(frontUrl + "/passenger-ride-history");
        waitForTable();
        return this;
    }

    public PassengerRideHistoryPage setFrom(String date) {
        wait.until(ExpectedConditions.visibilityOf(fromDate));
        fromDate.clear();
        fromDate.sendKeys(date);
        return this;
    }

    public PassengerRideHistoryPage setTo(String date) {
        wait.until(ExpectedConditions.visibilityOf(toDate));
        toDate.clear();
        toDate.sendKeys(date);
        return this;
    }

    public PassengerRideHistoryPage clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
        waitForTable();
        return this;
    }

    public PassengerRideHistoryPage clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetBtn)).click();
        waitForTable();
        return this;
    }

    public boolean hasAnyRides() {
        try {
            List<WebElement> rows = getValidRideRows();
            return !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNoRidesFoundRowVisible() {
        try {
            WebElement cell = driver.findElement(noRidesCellBy);
            return cell.isDisplayed() && cell.getText().trim().equalsIgnoreCase("No rides found.");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean canRateFirstRide() {
        try {
            List<WebElement> rows = getValidRideRows();
            if (rows.isEmpty()) return false;
            
            // if first row has rate buttons
            List<WebElement> rateButtons = rows.get(0).findElements(By.cssSelector(".rate-small-btn"));
            return !rateButtons.isEmpty() && rateButtons.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public ReviewDialogPage rateDriverForFirstRide() {
        List<WebElement> rows = getValidRideRows();
        if (rows.isEmpty()) {
            throw new NoSuchElementException("No rides found to rate");
        }
        
        WebElement firstRow = rows.get(0);
        List<WebElement> rateButtons = firstRow.findElements(By.cssSelector(".rate-small-btn"));
        
        if (rateButtons.isEmpty()) {
            throw new NoSuchElementException("No rate buttons found for first ride");
        }
        
        WebElement rateDriverBtn = rateButtons.get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", rateDriverBtn);
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(rateDriverBtn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rateDriverBtn);
        }
        
        return new ReviewDialogPage(driver);
    }

    public ReviewDialogPage rateVehicleForFirstRide() {
        List<WebElement> rows = getValidRideRows();
        if (rows.isEmpty()) {
            throw new NoSuchElementException("No rides found to rate");
        }
        
        WebElement firstRow = rows.get(0);
        List<WebElement> rateButtons = firstRow.findElements(By.cssSelector(".rate-small-btn"));
        
        if (rateButtons.size() < 2) {
            throw new NoSuchElementException("No vehicle rate button found for first ride");
        }
        
        WebElement rateVehicleBtn = rateButtons.get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", rateVehicleBtn);
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(rateVehicleBtn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rateVehicleBtn);
        }
        
        return new ReviewDialogPage(driver);
    }

    private List<WebElement> getValidRideRows() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideRowsBy));
        return driver.findElements(rideRowsBy).stream()
                .filter(row -> {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    return !cells.isEmpty() && 
                           !cells.get(0).getText().trim().equalsIgnoreCase("No rides found.");
                })
                .collect(Collectors.toList());
    }

    private void waitForTable() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideRowsBy));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
    }
}