package ecoride;
import java.util.UUID;

// Represents a customer. Responsible for holding customer details.

public class Customer {
    private final String customerID;
    private final String nicOrPassport;
    private final String name;
    private final String contactNumber;
    private final String email;

    public Customer(String nicOrPassport, String name, String contactNumber, String email) {
        this.customerID = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.nicOrPassport = nicOrPassport;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getNicOrPassport() {
        return nicOrPassport;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerDetails() {
        return String.format("%s | %s | %s | %s", customerID, name, contactNumber, email);
    }
}