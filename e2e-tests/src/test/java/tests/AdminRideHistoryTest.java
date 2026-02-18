package tests;
import org.junit.jupiter.api.Test;
import pages.AdminRideHistoryPage;
import pages.RideDetailsAdminPage;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminRideHistoryTest extends BaseTest{

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASS  = "andjela123";

    private AdminRideHistoryPage openHistoryForFirstUser() {
        loginAsAdmin(ADMIN_EMAIL, ADMIN_PASS);
        return new AdminRideHistoryPage(driver)
                .open(FRONT_URL)
                .openFirstUserHistory();
    }
    @Test
    void default_sorting_and_details_work() {
        AdminRideHistoryPage page = openHistoryForFirstUser();
        //default sort by createdAt DESC
        List<Long> created = page.getCreatedAtValues();
        assertTrue(created.size() > 0, "No rides available for selected user.");
        assertTrue(isSortedDesc(created), "Default should be createdAt DESC");
        RideDetailsAdminPage details = page.openFirstRideDetails();
        details.assertLoaded();
    }
    @Test
    void sort_by_pickup_address_work(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByPickupAddress();
        page.sortByPickupAddress();
        List<String> pickups = page.getPickupAddresses();
        assertTrue(isSortedAscString(pickups) || isSortedDescString(pickups),
                "Pickup addresses should be sorted after click");
    }
    @Test
    void sort_by_destination_address_work(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByDestinationAddress();
        page.sortByDestinationAddress();
        List<String> destinations = page.getDestinationAddresses();
        assertTrue(isSortedAscString(destinations) || isSortedDescString(destinations),
                "Destination addresses should be sorted after click");
    }
    @Test
    void sort_by_start_time_work(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByStartTime();
        page.sortByStartTime();
        List<Long> started = page.getStartedAtValues();
        assertTrue(started.size() > 0, "No rides available for selected user.");
        assertTrue(isSortedAsc(started) || isSortedDesc(started),
                "Should be sorted after click");
    }
    @Test
    void sort_by_end_time_work(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByEndTime();
        page.sortByEndTime();
        List<Long> ended = page.getEndedAtValues();
        assertTrue(ended.size() > 0, "No rides available for selected user.");
        assertTrue(isSortedAsc(ended) || isSortedDesc(ended),
                "Should be sorted after click");
    }
    @Test
    void sort_by_creation_time_work(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByCreatedTime();
        page.sortByCreatedTime();
        List<Long> created = page.getCreatedAtValues();
        assertTrue(created.size() > 0, "No rides available for selected user.");
        assertTrue(isSortedAsc(created) || isSortedDesc(created),
                "Should be sorted after click");
    }

    @Test
    void sort_by_total_price_work() {
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByTotalPrice();
        page.sortByTotalPrice();
        var prices = page.getTotalPrices();
        assertTrue(isSortedAscDouble(prices) || isSortedDescDouble(prices), "Prices should be sorted after click");
    }

    @Test
    void sort_by_user_who_cancelled_work() {
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByUserWhoCancelled();
        page.sortByUserWhoCancelled();
        var users = page.getUsersWhoCancelled();
        assertTrue(isSortedAscString(users) || isSortedDescString(users), "Should be sorted after click");
    }
    @Test
    void sort_by_panic_work() {
        AdminRideHistoryPage page = openHistoryForFirstUser();
        page.sortByPanic();
        page.sortByPanic();
        var panic = page.getPanic();
        assertTrue(isSortedAscString(panic) || isSortedDescString(panic), "Should be sorted after click");
    }
    @Test
    void wrong_filter_shows_no_rides_found()
    {
        AdminRideHistoryPage page = openHistoryForFirstUser();
        // filter by createdAt range
        page.setFrom("20-03-2026").setTo("31-12-2026").clickSearch();
        assertTrue(page.isNoRidesFoundRowVisible(), "Expected 'No rides found.' row.");
        assertEquals("No rides found.", page.getNoRidesFoundText());
    }
    @Test
    void filter_work_and_details_open(){
        AdminRideHistoryPage page = openHistoryForFirstUser();
        //wrong filter first
        page.setFrom("20-03-2026").setTo("31-12-2026").clickSearch();
        // reset
        page.clickReset();
        page.setFrom("01-01-2026").setTo("31-12-2026").clickSearch();
        // details page
        RideDetailsAdminPage details = page.openFirstRideDetails();
        details.assertLoaded();
    }
    private static boolean isSortedAsc(List<Long> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) return false;
        }
        return true;
    }
    private static boolean isSortedDesc(List<Long> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) < list.get(i + 1)) return false;
        }
        return true;
    }
    private static boolean isSortedDescDouble(List<Double> list){
        for(int i = 0; i < list.size() - 1; i ++){
            if(list.get(i) < list.get(i+1)) return false;
        }
        return true;
    }

    private static boolean isSortedAscDouble(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) return false;
        }
        return true;
    }
    private static boolean isSortedAscString(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            String a = safe(list.get(i));
            String b = safe(list.get(i + 1));
            if (a.compareToIgnoreCase(b) > 0) return false;
        }
        return true;
    }
    private static boolean isSortedDescString(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            String a = safe(list.get(i));
            String b = safe(list.get(i + 1));
            if (a.compareToIgnoreCase(b) < 0) return false;
        }
        return true;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

}
