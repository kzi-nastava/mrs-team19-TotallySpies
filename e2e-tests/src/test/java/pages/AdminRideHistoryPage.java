package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminRideHistoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public AdminRideHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
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

    @FindBy(css = "[data-testid='users-table'] tbody tr .view-btn")
    private List<WebElement> userViewHistoryButtons;

    @FindBy(css = "[data-testid='sort-pickupAddress']")
    private WebElement sortPickupAddressBtn;

    @FindBy(css = "[data-testid='sort-destinationAddress']")
    private WebElement sortDestinationAddressBtn;

    @FindBy(css = "[data-testid='sort-startTime']")
    private WebElement sortStartTimeBtn;

    @FindBy(css = "[data-testid='sort-endTime']")
    private WebElement sortEndTimeBtn;

    @FindBy(css = "[data-testid='sort-creationTime']")
    private WebElement sortCreationTimeBtn;

    @FindBy(css = "[data-testid='sort-totalPrice']")
    private WebElement sortTotalPriceBtn;

    @FindBy(css = "[data-testid='sort-isCancelled']")
    private WebElement sortIsCancelledBtn;

    @FindBy(css = "[data-testid='sort-userWhoCancelled']")
    private WebElement sortUserWhoCancelledBtn;

    @FindBy(css = "[data-testid='sort-panic']")
    private WebElement sortPanicBtn;

    private final By rideRowsBy = By.cssSelector("[data-testid='rides-table'] tbody tr");
    private final By rideDetailsBtnBy = By.cssSelector("[data-testid='rides-table'] tbody tr .view-btn");
    private final By noRidesCellBy = By.cssSelector("[data-testid='rides-table'] tbody tr td[colspan]");
    public AdminRideHistoryPage open(String frontUrl) {
        driver.get(frontUrl + "/admin-ride-history");
        wait.until(ExpectedConditions.visibilityOf(searchBtn));
        return this;
    }

    public AdminRideHistoryPage openFirstUserHistory() {
        wait.until(d -> userViewHistoryButtons.size() > 0);
        WebElement firstBtn = userViewHistoryButtons.get(0);
        wait.until(ExpectedConditions.elementToBeClickable(firstBtn)).click();
        waitTableAfterAction();
        return this;
    }
        public AdminRideHistoryPage setFrom(String date) {
        wait.until(ExpectedConditions.visibilityOf(fromDate));
        fromDate.clear();
        fromDate.sendKeys(date);
        return this;
    }

    public AdminRideHistoryPage setTo(String date) {
        wait.until(ExpectedConditions.visibilityOf(toDate));
        toDate.clear();
        toDate.sendKeys(date);
        return this;
    }
    public AdminRideHistoryPage clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public AdminRideHistoryPage clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public AdminRideHistoryPage sortByPickupAddress() {
        wait.until(ExpectedConditions.elementToBeClickable(sortPickupAddressBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public AdminRideHistoryPage sortByDestinationAddress() {
        wait.until(ExpectedConditions.elementToBeClickable(sortDestinationAddressBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public AdminRideHistoryPage sortByStartTime() {
        wait.until(ExpectedConditions.elementToBeClickable(sortStartTimeBtn)).click();
        waitTableAfterAction();
        return this;
    }

    public AdminRideHistoryPage sortByEndTime() {
        wait.until(ExpectedConditions.elementToBeClickable(sortEndTimeBtn)).click();
        waitTableAfterAction();
        return this;
    }

    public AdminRideHistoryPage sortByCreatedTime() {
        wait.until(ExpectedConditions.elementToBeClickable(sortCreationTimeBtn)).click();
        waitTableAfterAction();
        return this;
    }
    //* total price sort: sometimes first row doesn't change or data is identical.*/
    public AdminRideHistoryPage sortByTotalPrice() {
        List<String> before = snapshotColumnTexts(6); // totalPrice = td[6]
        wait.until(ExpectedConditions.elementToBeClickable(sortTotalPriceBtn)).click();
        waitTableAfterAction();
        // wait for column values to change; if not, don't fail here
        try {
            wait.until(d -> !snapshotColumnTexts(6).equals(before));
        } catch (TimeoutException ignored) { }
        return this;
    }
    public AdminRideHistoryPage sortByIsCancelled() {
        wait.until(ExpectedConditions.elementToBeClickable(sortIsCancelledBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public AdminRideHistoryPage sortByUserWhoCancelled() {
        wait.until(ExpectedConditions.elementToBeClickable(sortUserWhoCancelledBtn)).click();
        waitTableAfterAction();
        return this;
    }

    public AdminRideHistoryPage sortByPanic() {
        wait.until(ExpectedConditions.elementToBeClickable(sortPanicBtn)).click();
        waitTableAfterAction();
        return this;
    }
    public RideDetailsAdminPage openFirstRideDetails() {
        /*wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideDetailsBtnBy));
        WebElement btn = driver.findElements(rideDetailsBtnBy).get(0);
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        return new RideDetailsAdminPage(driver);*/
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideDetailsBtnBy));
        WebElement btn = driver.findElements(rideDetailsBtnBy).get(0);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", btn);
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        return new RideDetailsAdminPage(driver);
    }
    public boolean hasAnyRides() { //provjerava da li tabela sadrzi jendu pravu voznju
        return freshRideRows().stream()//za svaki red uzme sve td celije i prebroji ih
                .map(r -> r.findElements(By.tagName("td")).size())
                .anyMatch(size -> size > 6); // prava voznja ima preko 6 redova
    }
    public boolean isNoRidesFoundRowVisible() {
        try {
            WebElement cell = driver.findElement(noRidesCellBy);
            return cell.isDisplayed() && cell.getText().trim().equalsIgnoreCase("No rides found.");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getNoRidesFoundText() {
        wait.until(ExpectedConditions.presenceOfElementLocated(noRidesCellBy));
        return driver.findElement(noRidesCellBy).getText().trim();
    }
    public List<Long> getCreatedAtValues() {
        // createdAt is td[4]
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .filter(tds -> tds.size() > 4)
                .map(tds -> tds.get(4).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .map(AdminRideHistoryPage::parseDateToSortableLong)
                .collect(Collectors.toList());
    }
    public List<Long> getStartedAtValues() {
        // startedAt is td[2]
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .filter(tds -> tds.size() > 2)
                .map(tds -> tds.get(2).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .map(AdminRideHistoryPage::parseDateToSortableLong)
                .collect(Collectors.toList());
    }
    public List<Long> getEndedAtValues() {
        // endedAt is td[2]
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .filter(tds -> tds.size() > 3)
                .map(tds -> tds.get(3).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .map(AdminRideHistoryPage::parseDateToSortableLong)
                .collect(Collectors.toList());
    }
    public List<String> getPickupAddresses(){
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .map(tds -> tds.get(0).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .collect(Collectors.toList());
    }
    public List<String> getDestinationAddresses(){
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .map(tds -> tds.get(1).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .collect(Collectors.toList());
    }
    public List<String> getCancellations(){
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .map(tds -> tds.get(7).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .collect(Collectors.toList());
    }
    public List<String> getUsersWhoCancelled(){
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .map(tds -> tds.get(8).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .collect(Collectors.toList());
    }
    public List<String> getPanic(){
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .map(tds -> tds.get(9).getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> !t.equalsIgnoreCase("No rides found."))
                .collect(Collectors.toList());
    }

    public List<Double> getTotalPrices() { //sakulja sve totalPrice vrijednosti
        // totalPrice is td[6]
        return freshRideRows().stream()
                .map(row -> row.findElements(By.tagName("td")))
                .filter(tds -> tds.size() > 6)
                .map(tds -> tds.get(6).getText().trim())
                .filter(t -> !t.isBlank())
                .map(s -> s.replace(",", ".")) //zamijeni zarez sa tackom
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
    private List<WebElement> freshRideRows() { //vraca sve redove tabele cekajuci da se svi pojave
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideRowsBy));
        return driver.findElements(rideRowsBy);
    }
    /** in angular tables it can happen that after click (sort/search/reset) it may re-render DOM,
     * I wait for presence, then try waiting for staleness of previous first row,
     * if no re-render happens, I still proceed safely.
     */
    private void waitTableAfterAction() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideRowsBy));
        List<WebElement> rows = driver.findElements(rideRowsBy);
        if (rows.isEmpty()) return;
        WebElement firstRow = rows.get(0);
        try {
            wait.until(ExpectedConditions.stalenessOf(firstRow));
        } catch (TimeoutException ignored) {
            // no rerender occurred
        }
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rideRowsBy));
    }
    private List<String> snapshotColumnTexts(int tdIndex) {
        return freshRideRows().stream()
                .map(r -> r.findElements(By.tagName("td")))
                .filter(tds -> tds.size() > tdIndex)
                .map(tds -> tds.get(tdIndex).getText().trim())
                .collect(Collectors.toList());
    }
    private static long parseDateToSortableLong(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        LocalDateTime dt = LocalDateTime.parse(s, formatter);
        return Long.parseLong(dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }
}
