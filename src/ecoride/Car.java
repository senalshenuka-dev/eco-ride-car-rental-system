package ecoride;
import java.util.UUID;

 // Represents a car record in the EcoRide system.
 // Responsibilities: Hold car details, Update availability

public class Car {
    private final String carID;
    private String model;
    private Category category;
    private double dailyRentalPrice;
    private AvailabilityStatus availabilityStatus;

    public Car(String model, Category category) {
        this.carID = "CAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.model = model;
        this.category = category;
        this.dailyRentalPrice = category.getDailyRentalFee();
        this.availabilityStatus = AvailabilityStatus.AVAILABLE;
    }

    public String getCarID() {
        return carID;
    }

    public String getModel() {
        return model;
    }

    public Category getCategory() {
        return category;
    }

    public double getDailyRentalPrice() {
        return dailyRentalPrice;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.dailyRentalPrice = category.getDailyRentalFee();
    }

    public void updateAvailability(AvailabilityStatus status) {
        this.availabilityStatus = status;
    }

    public String getCarDetails() {
        return String.format("%s | %s | %s | LKR %.2f | %s",
                carID, model, category.getCategoryName(), dailyRentalPrice, availabilityStatus.toString());
    }
}



