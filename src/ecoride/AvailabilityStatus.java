package ecoride;

// Enum representing the availability status of a car.

public enum AvailabilityStatus {
    AVAILABLE,
    RESERVED,
    UNDER_MAINTENANCE;

    @Override
    public String toString() {
        switch (this) {
            case AVAILABLE: return "Available";
            case RESERVED: return "Reserved";
            case UNDER_MAINTENANCE: return "Under Maintenance";
            default: return name();
        }
    }
}





