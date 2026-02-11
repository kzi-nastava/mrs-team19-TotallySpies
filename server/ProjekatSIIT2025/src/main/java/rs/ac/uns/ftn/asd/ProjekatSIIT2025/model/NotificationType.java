package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

public enum NotificationType {
    NEW_RIDE,           // nova voznja dodijeljena korisniku ili vozacu
    RIDE_REJECTED,      // voznja odbijena
    RIDE_REMINDER,      // podsjetnik 15min prije i svakih 5 min
    PANIC_ALERT,        // aktivirano PANIC dugme
    SYSTEM_ALERT,       // generalna obavestenja
    LINKED_TO_RIDE,     // putnik ulinkovan na voznju
    RIDE_COMPLETED      // zavrsena voznja
}
