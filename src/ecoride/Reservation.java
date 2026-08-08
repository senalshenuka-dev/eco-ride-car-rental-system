package ecoride;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


 // Represents a reservation made by a customer.
 // Contains methods to calculate prices and apply business rules.

public class Reservation {
    private final String reservationID;
    private final Customer customer;
    private final Car car;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double totalKm; // expected total kilometers for the rental
    private final double deposit = 5000.0; // refundable deposit charged upon booking
    private ReservationStatus status;
    private final LocalDate createdAt; // date reservation was created

    public Reservation(Customer customer, Car car, LocalDate startDate, LocalDate endDate, double totalKm) {
        this.reservationID = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.customer = customer;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalKm = totalKm;
        this.status = ReservationStatus.ACTIVE;
        this.createdAt = LocalDate.now();
    }

    public String getReservationID() {
        return reservationID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public double getDeposit() {
        return deposit;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public long getRentalDays() {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return Math.max(days, 1);
    }

    // Calculate base price before discounts/tax.

    public double calculateBasePrice() {
        return car.getDailyRentalPrice() * getRentalDays();
    }

    // Calculate extra km charge if totalKm exceeds free allocation (freeKmPerDay * days).

    public double calculateExtraKmCharge() {
        double freeAllocation = car.getCategory().getFreeKmPerDay() * getRentalDays();
        if (totalKm > freeAllocation) {
            double extraKm = totalKm - freeAllocation;
            return extraKm * car.getCategory().getExtraKmCharge();
        }
        return 0.0;
    }

  // Apply discount if rental days >= 7 (10% of base price).
  // Discount applies on base price before tax (per business rules).

    public double applyDiscount(double basePrice) {
        if (getRentalDays() >= 7) {
            return basePrice * 0.10; // 10%
        }
        return 0.0;
    }

    //Calculate final total amount (base + extra km - discount + tax - deposit).

    public double calculateTotalAmount() {
        double base = calculateBasePrice();
        double extra = calculateExtraKmCharge();
        double discount = applyDiscount(base);
        double subtotal = base + extra - discount;
        double tax = car.getCategory().calculateTax(subtotal);
        double finalAmount = subtotal + tax - deposit; // deposit deducted
        return finalAmount;
    }

    public String shortSummary() {
        return String.format("%s | %s | %s -> %s | Days: %d | Status: %s",
                reservationID, car.getCarID(), startDate.toString(), endDate.toString(), getRentalDays(), status.toString());
    }

    public String getDetailedInfo() {
        return String.format("Reservation ID: %s%nCustomer: %s%nCar: %s (%s)%nStart: %s%nEnd: %s%nDays: %d%nTotalKm: "
                + "%.2f%nDeposit: LKR %.2f%nStatus: %s",
                reservationID, customer.getName(), car.getModel(), car.getCarID(), startDate, endDate, getRentalDays(), 
                totalKm, deposit, status.toString());
    }
}
