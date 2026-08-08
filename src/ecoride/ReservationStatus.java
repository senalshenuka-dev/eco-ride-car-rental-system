package ecoride;
 
 // Enum representing reservation lifecycle state.

public enum ReservationStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED;

    @Override
    public String toString() {
        switch (this) {
            case ACTIVE: return "Active";
            case CANCELLED: return "Cancelled";
            case COMPLETED: return "Completed";
            default: return name();
        }
    }
}