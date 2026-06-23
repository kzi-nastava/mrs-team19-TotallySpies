package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.RideOrderingPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FavoriteRouteRideTest extends BaseTest{
    private RideOrderingPage orderingPage;
    protected static final String PASSENGER_EMAIL = "passenger3@test.com";
    protected static final String PASSENGER_PASS = "password123";

    @BeforeEach
    void setUpOrdering() {
        loginAsPassenger(PASSENGER_EMAIL, PASSENGER_PASS);
        driver.get(FRONT_URL + "/display-info");
        orderingPage = new RideOrderingPage(driver);
        orderingPage.navigateViaMenu();
    }

    @Test
    @DisplayName("Successfully ride ordering from favorite routes")
    void shouldBookRideFromFavoritesSuccessfully() {
        orderingPage.clickPickFromFavorites();

        orderingPage.selectFirstFavoriteRoute();

        orderingPage.waitForPriceToLoad();

        orderingPage.clickBookNow();

        orderingPage.waitForDriverSearchToStart();

        String outcomeText = orderingPage.getDriverSearchResultText();
        System.out.println("Ishod pretrage vozaca: " + outcomeText);

        assertTrue(outcomeText.contains("Driver found!") || outcomeText.contains("No drivers available"),
                "Ocekivan je konacan ishod pretrage (Driver found! ili No drivers available), ali je dobijeno: " + outcomeText);

        orderingPage.closeDriverModal();
    }
}
