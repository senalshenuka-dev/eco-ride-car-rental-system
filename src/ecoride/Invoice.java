package ecoride;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Represents an invoice generated for a reservation.
// Generates detailed invoice content as required by the business rules.

public class Invoice {
    private final String invoiceID;
    private final Reservation reservation;
    private final LocalDate issueDate;
    private final double basePrice;
    private final double discount;
    private final double extraKmCharge;
    private final double tax;
    private final double finalAmount;

    public Invoice(Reservation reservation) {
        this.invoiceID = "INV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.reservation = reservation;
        this.issueDate = LocalDate.now();
        this.basePrice = reservation.calculateBasePrice();
        this.extraKmCharge = reservation.calculateExtraKmCharge();
        this.discount = reservation.applyDiscount(basePrice);
        double subtotal = basePrice + extraKmCharge - discount;
        this.tax = reservation.getCar().getCategory().calculateTax(subtotal);
        this.finalAmount = subtotal + tax - reservation.getDeposit();
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void displayInvoice() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("======================================================");
        System.out.println("                    EcoRide Invoice                   ");
        System.out.println("======================================================");
        System.out.printf("Invoice ID: %s%nIssued On: %s%n", invoiceID, issueDate.format(f));
        System.out.println("------------------------------------------------------");
        System.out.printf("Reservation ID: %s%n", reservation.getReservationID());
        System.out.printf("Customer: %s (%s)%n", reservation.getCustomer().getName(), 
                reservation.getCustomer().getCustomerID());
        System.out.println("------------------------------------------------------");
        System.out.printf("Car: %s | %s%nCategory: %s%nDaily Rate: LKR %.2f%n",
                reservation.getCar().getCarID(),
                reservation.getCar().getModel(),
                reservation.getCar().getCategory().getCategoryName(),
                reservation.getCar().getDailyRentalPrice());
        System.out.println("------------------------------------------------------");
        System.out.printf("Rental Duration: %s to %s (%d days)%n", reservation.getStartDate(), 
                reservation.getEndDate(), reservation.getRentalDays());
        System.out.printf("Mileage (expected): %.2f km%n", reservation.getTotalKm());
        System.out.println("------------------------------------------------------");
        System.out.printf("Base Price: LKR %.2f%n", basePrice);
        System.out.printf("Extra Km Charge: LKR %.2f%n", extraKmCharge);
        System.out.printf("Discount: LKR %.2f%n", discount);
        System.out.printf("Tax: LKR %.2f%n", tax);
        System.out.printf("Deposit (deducted): LKR %.2f%n", reservation.getDeposit());
        System.out.println("------------------------------------------------------");
        System.out.printf("Final Payable Amount: LKR %.2f%n", finalAmount);
        System.out.println("======================================================");
    }
}
