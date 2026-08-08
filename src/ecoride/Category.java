package ecoride;

// Represents a vehicle category in the EcoRide system.
// This class only holds category data and tax calculation.

public class Category {
    private final String categoryName;
    private final double dailyRentalFee;
    private final double freeKmPerDay;
    private final double extraKmCharge;
    private final double taxRate; // percentage (e.g., 10 for 10%)

    public Category(String categoryName, double dailyRentalFee, double freeKmPerDay, double extraKmCharge, double taxRate) {
        this.categoryName = categoryName;
        this.dailyRentalFee = dailyRentalFee;
        this.freeKmPerDay = freeKmPerDay;
        this.extraKmCharge = extraKmCharge;
        this.taxRate = taxRate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getDailyRentalFee() {
        return dailyRentalFee;
    }

    public double getFreeKmPerDay() {
        return freeKmPerDay;
    }

    public double getExtraKmCharge() {
        return extraKmCharge;
    }

    public double getTaxRate() {
        return taxRate;
    }

    // Calculate tax amount on a given amount using this category's tax rate.
    public double calculateTax(double amount) {
        return amount * (taxRate / 100.0);
    }

    public String getCategoryDetails() {
        return String.format("%s (Daily: LKR %.2f, FreeKm/day: %.0fkm, Extra: LKR %.2f/km, Tax: %.0f%%)",
                categoryName, dailyRentalFee, freeKmPerDay, extraKmCharge, taxRate);
    }
}