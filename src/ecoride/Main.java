package ecoride;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Main Class

public class Main {
    private static final EcoRideManager manager = new EcoRideManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static void printHeader(String title) {
        System.out.println("==============================================================");
        System.out.printf("                     %s%n", title);
        System.out.println("==============================================================");
    }

    private static void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        while (true) {
            printHeader("Welcome to EcoRide Car Rental System");
            System.out.println("1) Admin");
            System.out.println("2) Customer");
            System.out.println("0) Exit");
            System.out.print("Choose an option: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    if (adminAuthenticate()) {
                        adminMenu();
                    } else {
                        System.out.println("Authentication failed. Returning to main menu.");
                    }
                    break;
                case "2": customerMenu(); break;
                case "0":
                    System.out.println("Exiting... Thank you for using EcoRide.");
                    System.exit(0);
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Prompt for admin password and validate using manager.
    // Allows up to 3 attempts.
    
    private static boolean adminAuthenticate() {
        printHeader("Admin Authentication");
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Enter admin password: ");
            String pwd = scanner.nextLine();
            if (manager.validateAdminPassword(pwd)) {
                return true;
            }
            attempts++;
            System.out.println("Incorrect password. Attempts left: " + (3 - attempts));
        }
        return false;
    }

    // Admin Menu
    private static void adminMenu() {
        while (true) {
            printHeader("Admin Menu");
            System.out.println("1) View Customers");
            System.out.println("2) View All Reservations");
            System.out.println("3) Add Vehicle");
            System.out.println("4) Update Vehicle");
            System.out.println("5) Remove Vehicle");
            System.out.println("6) Manage Vehicle Availability");
            System.out.println("7) Generate Invoice");
            System.out.println("8) View Invoice (print by reservation ID)");
            System.out.println("9) List All Cars");
            System.out.println("10) Change Admin Password");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1": viewCustomers(); break;
                case "2": viewAllReservations(); break;
                case "3": addVehicle(); break;
                case "4": updateVehicle(); break;
                case "5": removeVehicle(); break;
                case "6": manageVehicleAvailability(); break;
                case "7": generateInvoice(); break;
                case "8": viewInvoice(); break;
                case "9": listAllCars(); break;
                case "10": changeAdminPassword(); break;
                case "0": return;
                default: System.out.println("Invalid option."); break;
            }
            pause();
        }
    }

    private static void changeAdminPassword() {
        printHeader("Change Admin Password");
        System.out.print("Enter current password: ");
        String current = scanner.nextLine();
        if (!manager.validateAdminPassword(current)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        System.out.print("Enter new password: ");
        String p1 = scanner.nextLine();
        if (p1 == null || p1.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }
        System.out.print("Confirm new password: ");
        String p2 = scanner.nextLine();
        if (!p1.equals(p2)) {
            System.out.println("Passwords do not match.");
            return;
        }
        manager.setAdminPassword(p1);
        System.out.println("Admin password changed successfully.");
    }

    private static void viewCustomers() {
        printHeader("Customers");
        List<Customer> customers = manager.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        customers.forEach(c -> System.out.println(c.getCustomerDetails()));
    }

    private static void viewAllReservations() {
        printHeader("All Reservations");
        List<Reservation> reservations = manager.listAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        reservations.forEach(r -> System.out.println(r.getDetailedInfo() + "\n"));
    }

    private static void addVehicle() {
        printHeader("Add Vehicle");
        System.out.print("Model: ");
        String model = scanner.nextLine().trim();
        Category cat = selectCategory();
        if (cat == null) {
            System.out.println("Category selection cancelled.");
            return;
        }
        Car car = new Car(model, cat);
        manager.addCar(car);
        System.out.println("Added: " + car.getCarDetails());
    }

    private static Category selectCategory() {
        System.out.println("Select Category:");
        System.out.println("1) " + manager.getCompactCategory().getCategoryDetails());
        System.out.println("2) " + manager.getHybridCategory().getCategoryDetails());
        System.out.println("3) " + manager.getElectricCategory().getCategoryDetails());
        System.out.println("4) " + manager.getLuxuryCategory().getCategoryDetails());
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return manager.getCompactCategory();
            case "2": return manager.getHybridCategory();
            case "3": return manager.getElectricCategory();
            case "4": return manager.getLuxuryCategory();
            default: System.out.println("Invalid category."); return null;
        }
    }

    private static void updateVehicle() {
        printHeader("Update Vehicle");
        listAllCars();
        System.out.print("Enter Car ID to update: ");
        String id = scanner.nextLine().trim();
        Optional<Car> opt = manager.findCarByID(id);
        if (!opt.isPresent()) { System.out.println("Car not found."); return; }
        Car car = opt.get();
        System.out.print("New model (" + car.getModel() + "): ");
        String model = scanner.nextLine().trim();
        if (model.isEmpty()) model = car.getModel();
        System.out.println("Select new category (or press Enter to keep current):");
        System.out.println("Current: " + car.getCategory().getCategoryDetails());
        Category newCat = selectCategory();
        if (newCat == null) newCat = car.getCategory();
        manager.updateCar(id, model, newCat);
        System.out.println("Updated: " + car.getCarDetails());
    }

    private static void removeVehicle() {
        printHeader("Remove Vehicle");
        listAllCars();
        System.out.print("Enter Car ID to remove: ");
        String id = scanner.nextLine().trim();
        if (manager.removeCar(id)) {
            System.out.println("Car removed.");
        } else {
            System.out.println("Car not found or could not be removed.");
        }
    }

    private static void manageVehicleAvailability() {
        printHeader("Manage Availability");
        listAllCars();
        System.out.print("Enter Car ID: ");
        String id = scanner.nextLine().trim();
        Optional<Car> opt = manager.findCarByID(id);
        if (!opt.isPresent()) { System.out.println("Car not found."); return; }
        System.out.println("Select status:");
        System.out.println("1) Available");
        System.out.println("2) Reserved");
        System.out.println("3) Under Maintenance");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();
        AvailabilityStatus status;
        switch (ch) {
            case "1": status = AvailabilityStatus.AVAILABLE; break;
            case "2": status = AvailabilityStatus.RESERVED; break;
            case "3": status = AvailabilityStatus.UNDER_MAINTENANCE; break;
            default: System.out.println("Invalid choice."); return;
        }
        manager.changeAvailability(id, status);
        System.out.println("Updated availability.");
    }

    private static void generateInvoice() {
        printHeader("Generate Invoice");
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine().trim();
        Optional<Invoice> inv = manager.generateInvoiceForReservation(id);
        if (!inv.isPresent()) {
            System.out.println("Reservation not found.");
            return;
        }
        inv.get().displayInvoice();
    }

    private static void viewInvoice() {
        printHeader("View Invoice");
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine().trim();
        Optional<Reservation> r = manager.searchReservationByID(id);
        if (!r.isPresent()) { System.out.println("Reservation not found."); return; }
        Invoice invoice = new Invoice(r.get());
        invoice.displayInvoice();
    }

    private static void listAllCars() {
        printHeader("Cars");
        List<Car> cars = manager.listAllCars();
        cars.forEach(c -> System.out.println(c.getCarDetails()));
    }

    // Customer Menu
    private static void customerMenu() {
        while (true) {
            printHeader("Customer Menu");
            System.out.println("1) Register Account");
            System.out.println("2) Browse Available Cars");
            System.out.println("3) Make Reservation");
            System.out.println("4) View My Bookings");
            System.out.println("5) Search Reservation by ID");
            System.out.println("6) Cancel Reservation");
            System.out.println("7) Update Reservation");
            System.out.println("8) View Invoice");
            System.out.println("0) Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1": registerAccount(); break;
                case "2": browseAvailableCars(); break;
                case "3": makeReservation(); break;
                case "4": viewMyBookings(); break;
                case "5": searchReservationByID(); break;
                case "6": cancelReservation(); break;
                case "7": updateReservation(); break;
                case "8": customerViewInvoice(); break;
                case "0": return;
                default: System.out.println("Invalid option."); break;
            }
            pause();
        }
    }

    private static void registerAccount() {
        printHeader("Register Account");
        System.out.print("NIC/Passport: ");
        String nic = scanner.nextLine().trim();
        if (nic.isEmpty()) { System.out.println("NIC/Passport required."); return; }
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name required."); return; }
        System.out.print("Contact Number: ");
        String contact = scanner.nextLine().trim();
        if (contact.isEmpty()) { System.out.println("Contact required."); return; }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) { System.out.println("Email required."); return; }
        Customer c = manager.registerCustomer(nic, name, contact, email);
        System.out.println("Registered: " + c.getCustomerDetails());
    }

    private static void browseAvailableCars() {
        printHeader("Available Cars");
        List<Car> cars = manager.listAvailableCars();
        if (cars.isEmpty()) {
            System.out.println("No cars available.");
            return;
        }
        cars.forEach(c -> System.out.println(c.getCarDetails()));
    }

    private static void makeReservation() {
        printHeader("Make Reservation");
        // Need customer
        System.out.print("Enter your registered Name (exact match): ");
        String name = scanner.nextLine().trim();
        Optional<Customer> custOpt = manager.findCustomerByName(name);
        if (!custOpt.isPresent()) {
            System.out.println("Customer not found. Please register first.");
            return;
        }
        Customer cust = custOpt.get();
        browseAvailableCars();
        System.out.print("Enter Car ID to book: ");
        String carID = scanner.nextLine().trim();
        Optional<Car> carOpt = manager.findCarByID(carID);
        if (!carOpt.isPresent()) { System.out.println("Car not found."); return; }
        Car car = carOpt.get();
        System.out.print("Start Date (yyyy-MM-dd): ");
        LocalDate start;
        try {
            start = LocalDate.parse(scanner.nextLine().trim(), df);
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid date format.");
            return;
        }
        System.out.print("End Date (yyyy-MM-dd): ");
        LocalDate end;
        try {
            end = LocalDate.parse(scanner.nextLine().trim(), df);
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid date format.");
            return;
        }
        System.out.print("Total expected kilometers for the rental (numeric): ");
        double km;
        try {
            km = Double.parseDouble(scanner.nextLine().trim());
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.out.println("Invalid kilometers value.");
            return;
        }
        Optional<Reservation> res = manager.makeReservation(cust, carID, start, end, km);
        if (!res.isPresent()) {
            System.out.println("Could not make reservation. Check car availability, booking date "
                    + "(must be at least 3 days from today), or date range.");
            return;
        }
        System.out.println("Reservation successful:");
        System.out.println(res.get().getDetailedInfo());
        System.out.printf("Note: A refundable deposit of LKR %.2f is charged upon booking.%n", res.get().getDeposit());
    }

    private static void viewMyBookings() {
        printHeader("View My Bookings");
        System.out.print("Enter your registered Name (exact match): ");
        String name = scanner.nextLine().trim();
        List<Reservation> list = manager.searchReservationByName(name);
        if (list.isEmpty()) {
            System.out.println("No bookings found for the given name.");
            return;
        }
        list.forEach(r -> System.out.println(r.shortSummary()));
    }

    private static void searchReservationByID() {
        printHeader("Search Reservation");
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine().trim();
        Optional<Reservation> opt = manager.searchReservationByID(id);
        if (!opt.isPresent()) {
            System.out.println("Reservation not found.");
            return;
        }
        System.out.println(opt.get().getDetailedInfo());
    }

    private static void cancelReservation() {
        printHeader("Cancel Reservation");
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine().trim();
        boolean ok = manager.cancelReservation(id);
        if (ok) System.out.println("Reservation cancelled. Car availability updated.");
        else System.out.println("Could not cancel. Either not found or cancellation window "
                + "(within 2 days of creation) has passed.");
    }

    private static void updateReservation() {
        printHeader("Update Reservation");
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine().trim();
        Optional<Reservation> opt = manager.searchReservationByID(id);
        if (!opt.isPresent()) { System.out.println("Reservation not found."); return; }
        System.out.print("New Start Date (yyyy-MM-dd): ");
        LocalDate start;
        try { start = LocalDate.parse(scanner.nextLine().trim(), df); } catch (Exception ex) { System.out.println
        ("Invalid date."); return; }
        System.out.print("New End Date (yyyy-MM-dd): ");
        LocalDate end;
        try { end = LocalDate.parse(scanner.nextLine().trim(), df); } catch (Exception ex) { System.out.println
        ("Invalid date."); return; }
        System.out.print("New total kilometers (numeric): ");
        double km;
        try { km = Double.parseDouble(scanner.nextLine().trim()); if (km < 0) throw new NumberFormatException(); } 
        catch (Exception ex) { System.out.println("Invalid km."); return; }
        boolean ok = manager.updateReservation(id, start, end, km);
        if (ok) System.out.println("Reservation updated; original reservation cancelled and a new reservation created.");
        else System.out.println("Could not update. Either update window (within 2 days of creation) has passed "
                + "or new dates invalid.");
    }

    // Customer-specific invoice viewing.
    // Prompts for registered name and reservation ID, verifies ownership, then displays invoice.

    private static void customerViewInvoice() {
        printHeader("View Invoice (Customer)");
        System.out.print("Enter your registered Name (exact match): ");
        String name = scanner.nextLine().trim();
        Optional<Customer> custOpt = manager.findCustomerByName(name);
        if (!custOpt.isPresent()) {
            System.out.println("Customer not found. Please register first.");
            return;
        }
        System.out.print("Enter Reservation ID to view invoice: ");
        String resId = scanner.nextLine().trim();
        Optional<Reservation> resOpt = manager.searchReservationByID(resId);
        if (!resOpt.isPresent()) {
            System.out.println("Reservation not found.");
            return;
        }
        Reservation res = resOpt.get();
        if (!res.getCustomer().getName().equalsIgnoreCase(name)) {
            System.out.println("This reservation does not belong to the provided customer name.");
            return;
        }
        Optional<Invoice> inv = manager.generateInvoiceForReservation(resId);
        if (!inv.isPresent()) {
            System.out.println("Could not generate invoice for this reservation.");
            return;
        }
        inv.get().displayInvoice();
    }
}