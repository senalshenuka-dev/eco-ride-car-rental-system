package ecoride;

// Core manager which handles cars, customers, reservations and admin password.
// anages domain collections and business operations.

import ecoride.AvailabilityStatus;
import ecoride.Car;
import ecoride.Category;
import ecoride.Customer;
import ecoride.Invoice;
import ecoride.Reservation;
import ecoride.ReservationStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class EcoRideManager {
    private final List<Car> cars = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    // Pre-populate categories for the system (Table 1)
    private final Category compact = new Category("Compact Petrol Car", 5000.0, 100.0, 50.0, 10.0);
    private final Category hybrid = new Category("Hybrid Car", 7500.0, 150.0, 60.0, 12.0);
    private final Category electric = new Category("Electric Car", 10000.0, 200.0, 40.0, 8.0);
    private final Category luxury = new Category("Luxury SUV", 15000.0, 250.0, 75.0, 15.0);

    // Admin password (stored as SHA-256 hash). Default password: "admin123"
    private String adminPasswordHash;

    public EcoRideManager() {
        // Seed a few sample cars
        addCar(new Car("Toyota Aqua", compact));
        addCar(new Car("Nissan Leaf", electric));
        addCar(new Car("Toyota Prius Hybrid", hybrid));
        addCar(new Car("BMW X5", luxury));

        // initialize default admin password (changeable via Admin menu)
        this.adminPasswordHash = hashPassword("admin123");
    }

    // Admin password management
    // Validates an admin password (plain text).

    public boolean validateAdminPassword(String plainPassword) {
        if (plainPassword == null) return false;
        String hashed = hashPassword(plainPassword);
        return hashed.equals(adminPasswordHash);
    }

    // Sets a new admin password (plain text).

    public void setAdminPassword(String newPlainPassword) {
        if (newPlainPassword == null) return;
        this.adminPasswordHash = hashPassword(newPlainPassword);
    }

    // Hashes a password using SHA-256 and returns hex string.

    private String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            // convert to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Should not happen - SHA-256 is supported. Fallback to plain (not recommended).
            return plain;
        }
    }

    // Car operations 
    public void addCar(Car car) {
        cars.add(car);
    }

    public boolean removeCar(String carID) {
        return cars.removeIf(c -> c.getCarID().equalsIgnoreCase(carID));
    }

    public Optional<Car> findCarByID(String carID) {
        return cars.stream().filter(c -> c.getCarID().equalsIgnoreCase(carID)).findFirst();
    }

    public List<Car> listAllCars() {
        return new ArrayList<>(cars);
    }

    public List<Car> listAvailableCars() {
        return cars.stream().filter(c -> c.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public void updateCar(String carID, String newModel, Category newCategory) {
        findCarByID(carID).ifPresent(car -> {
            car.setModel(newModel);
            car.setCategory(newCategory);
        });
    }

    public boolean changeAvailability(String carID, AvailabilityStatus status) {
        Optional<Car> c = findCarByID(carID);
        if (c.isPresent()) {
            c.get().updateAvailability(status);
            return true;
        }
        return false;
    }

    // Customer operations
    public Customer registerCustomer(String nic, String name, String contact, String email) {
        Customer c = new Customer(nic, name, contact, email);
        customers.add(c);
        return c;
    }

    public List<Customer> listCustomers() {
        return new ArrayList<>(customers);
    }

    public Optional<Customer> findCustomerByName(String name) {
        return customers.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Customer> findCustomerByID(String id) {
        return customers.stream().filter(c -> c.getCustomerID().equalsIgnoreCase(id)).findFirst();
    }

    // Reservation operations 
    // Make reservation. Validations:Car must be Available , Start date at least 3 days from now

    public Optional<Reservation> makeReservation(Customer customer, String carID, LocalDate startDate, 
            LocalDate endDate, double totalKm) {
        Optional<Car> carOpt = findCarByID(carID);
        if (!carOpt.isPresent()) {
            return Optional.empty();
        }
        Car car = carOpt.get();
        if (car.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            return Optional.empty();
        }
        LocalDate now = LocalDate.now();
        if (!startDate.isAfter(now.plusDays(2))) { // must schedule at least prior 3 days -> startDate >= now+3 -> startDate.isAfter(now+2)
            return Optional.empty();
        }
        if (endDate.isBefore(startDate)) {
            return Optional.empty();
        }
        Reservation r = new Reservation(customer, car, startDate, endDate, totalKm);
        reservations.add(r);
        // mark car reserved
        car.updateAvailability(AvailabilityStatus.RESERVED);
        return Optional.of(r);
    }

    public List<Reservation> listAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> viewBookingsByDate(LocalDate date) {
        return reservations.stream()
                .filter(r -> ( !date.isBefore(r.getStartDate()) && !date.isAfter(r.getEndDate())))
                .collect(Collectors.toList());
    }

    public Optional<Reservation> searchReservationByID(String reservationID) {
        return reservations.stream().filter(r -> r.getReservationID().equalsIgnoreCase(reservationID)).findFirst();
    }

    public List<Reservation> searchReservationByName(String name) {
        return reservations.stream()
                .filter(r -> r.getCustomer().getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // Cancel reservation if within 2 days from reservation creation

    public boolean cancelReservation(String reservationID) {
        Optional<Reservation> opt = searchReservationByID(reservationID);
        if (!opt.isPresent()) return false;
        Reservation r = opt.get();
        LocalDate limit = r.getCreatedAt().plusDays(2);
        if (LocalDate.now().isAfter(limit)) {
            return false; // cannot cancel
        }
        r.setStatus(ReservationStatus.CANCELLED);
        // free the car
        r.getCar().updateAvailability(AvailabilityStatus.AVAILABLE);
        return true;
    }

    // Update reservation (only allow changes within 2 days of reservation creation).
    // For simplicity, allow updating startDate, endDate and totalKm if car still available for changed dates.

    public boolean updateReservation(String reservationID, LocalDate newStart, LocalDate newEnd, double newTotalKm) {
        Optional<Reservation> opt = searchReservationByID(reservationID);
        if (!opt.isPresent()) return false;
        Reservation r = opt.get();
        LocalDate limit = r.getCreatedAt().plusDays(2);
        if (LocalDate.now().isAfter(limit)) {
            return false; // cannot update
        }
        // Validate new start date at least 3 days from now
        if (!newStart.isAfter(LocalDate.now().plusDays(2))) return false;
        if (newEnd.isBefore(newStart)) return false;

        // Create a new Reservation instance to replace old (simpler immutability)
        Reservation updated = new Reservation(r.getCustomer(), r.getCar(), newStart, newEnd, newTotalKm);
        // keep same ID? We'll keep a new ID but mark old cancelled to preserve audit.
        r.setStatus(ReservationStatus.CANCELLED);
        reservations.add(updated);
        updated.getCar().updateAvailability(AvailabilityStatus.RESERVED);
        return true;
    }

    // Invoice generation
    public Optional<Invoice> generateInvoiceForReservation(String reservationID) {
        Optional<Reservation> opt = searchReservationByID(reservationID);
        if (!opt.isPresent()) return Optional.empty();
        Reservation r = opt.get();
        Invoice inv = new Invoice(r);
        return Optional.of(inv);
    }

    // Category accessors 
    // Getters for categories so UI can reference them
    public Category getCompactCategory() { return compact; }
    public Category getHybridCategory() { return hybrid; }
    public Category getElectricCategory() { return electric; }
    public Category getLuxuryCategory() { return luxury; }
}