package tests;

import org.junit.jupiter.api.*;
import pages.PassengerRideHistoryPage;
import pages.ReviewDialogPage;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RideReviewTest extends BaseTest {

    private PassengerRideHistoryPage historyPage;
    protected static final String PASSENGER_EMAIL = "passenger@test.com";
    protected static final String PASSENGER_PASS = "password123";

    @BeforeEach
    void setUpForRating() {
        loginAsPassenger(PASSENGER_EMAIL, PASSENGER_PASS);
        historyPage = new PassengerRideHistoryPage(driver).open(FRONT_URL);
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    @Test
    @Order(1)
    void shouldRateDriverFromHistory() {
        assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");
        // assertTrue(historyPage.canRateFirstRide(), "First ride should be ratable (within 3 days)");

        ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

        dialog.selectRating(5)
              .enterComment("good")
              .submit();

        // wait until alert
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String alertText = dialog.getAlertText();
        assertEquals("Review sent successfully.", alertText);
        dialog.acceptAlert();

        boolean closed = dialog.waitForDialogToClose(3);
        assertTrue(closed, "Dialog should be closed after successful submission");
    }

    @Test
    @Order(2)
    void shouldRateVehicleFromHistory() {
        assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");
        // assertTrue(historyPage.canRateFirstRide(), "First ride should be ratable");

        ReviewDialogPage dialog = historyPage.rateVehicleForFirstRide();

        dialog.selectRating(2)
              .enterComment("dirty")
              .submit();

        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String alertText = dialog.getAlertText();
        assertEquals("Review sent successfully.", alertText);
        dialog.acceptAlert();

        boolean closed = dialog.waitForDialogToClose(3);
        assertTrue(closed, "Dialog should be closed after successful submission");
    }

    @Test
    @Order(3)
    void shouldNotSubmitWithoutComment() {
        // assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");

        ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

        dialog.selectRating(5)
              .submit();

        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String alertText = dialog.getAlertText();
        assertTrue(alertText.contains("Rating and comment are required") || 
                   alertText.toLowerCase().contains("required"), 
                   "Alert should mention required fields");
        dialog.acceptAlert();
        assertTrue(dialog.isDialogDisplayed(), "Dialog should stay open");
        dialog.close();
    }

    @Test
    @Order(4)
    void shouldNotSubmitWithoutRating() {
        // assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");

        ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

        dialog.enterComment("great")
              .submit();

        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String alertText = dialog.getAlertText();
        assertTrue(alertText.contains("Rating and comment are required") || 
                   alertText.toLowerCase().contains("required"), 
                   "Alert should mention required fields");
        dialog.acceptAlert();

        assertTrue(dialog.isDialogDisplayed(), "Dialog should stay open");
        
        dialog.close();
    }

    @Test
    @Order(5)
    void shouldNotSubmitEmptyForm() {
        // assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");

        ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

        dialog.submit();

        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String alertText = dialog.getAlertText();
        assertTrue(alertText.contains("Rating and comment are required"), 
                   "Alert should mention required fields");
        dialog.acceptAlert();

        assertTrue(dialog.isDialogDisplayed(), "Dialog should stay open");
        
        dialog.close();
    }

    @Test
    @Order(6)
    void shouldCloseDialogWithoutSubmitting() {
        // assertTrue(historyPage.hasAnyRides(), "Passenger should have at least one ride");

        ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

        dialog.close();

        boolean closed = dialog.waitForDialogToClose(3);
        assertTrue(closed, "Dialog should be closed after close button click");
    }

    @Test
    @Order(7)
    void shouldFilterAndRateRecentRides() {
        historyPage.setFrom("15-02-2026")
                  .setTo("18-02-2026")
                  .clickSearch();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        if (historyPage.hasAnyRides() && historyPage.canRateFirstRide()) {
            ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();

            dialog.selectRating(5)
                  .enterComment("Recent ride review")
                  .submit();

            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            String alertText = dialog.getAlertText();
            assertEquals("Review sent successfully.", alertText);
            dialog.acceptAlert();

            boolean closed = dialog.waitForDialogToClose(3);
            assertTrue(closed, "Dialog should be closed");
        } else {
            assertTrue(historyPage.isNoRidesFoundRowVisible() || !historyPage.hasAnyRides(),
                      "Either no rides in period or cannot rate");
        }
    }

    @Test
    @Order(8)
    void shouldResetFiltersAndRate() {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        historyPage.setFrom("15-02-2026")
                  .setTo("26-02-2026")
                  .clickSearch();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        if (historyPage.isNoRidesFoundRowVisible()) {
            System.out.println("No rides found with filter, resetting...");
            
            historyPage.clickReset();

            try { Thread.sleep(1000); } catch (InterruptedException e) {}

            assertTrue(historyPage.hasAnyRides(), "After reset should have rides");
            assertTrue(historyPage.canRateFirstRide(), "After reset should be able to rate");
            ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();
            
            try 
            { Thread.sleep(500); 
            } catch (InterruptedException e) {}
            
            dialog.selectRating(5)
                  .enterComment("After reset review")
                  .submit();

            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            String alertText = dialog.getAlertText();
            assertEquals("Review sent successfully.", alertText);
            dialog.acceptAlert();

            boolean closed = dialog.waitForDialogToClose(3);
            assertTrue(closed, "Dialog should be closed");
        } else {
            System.out.println("Filter returned rides");
            
            ReviewDialogPage dialog = historyPage.rateDriverForFirstRide();
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            dialog.selectRating(5)
                  .enterComment("Filtered ride review")
                  .submit();

            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
            String alertText = dialog.getAlertText();
            assertEquals("Review sent successfully.", alertText);
            dialog.acceptAlert();

            boolean closed = dialog.waitForDialogToClose(3);
            assertTrue(closed, "Dialog should be closed");
        }
    }
}