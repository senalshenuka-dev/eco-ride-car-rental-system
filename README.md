# EcoRide Car Rental System

EcoRide is a Java console application for managing an eco-focused car rental service. It provides separate admin and customer workflows for vehicles, customers, reservations, availability, and invoices.

## Features

### Customer features

- Register a customer account
- Browse available vehicles
- Make reservations using a car ID and rental dates
- View and search reservations
- Update or cancel reservations within the allowed time window
- View reservation invoices

### Admin features

- Authenticate with an admin password
- View customers and all reservations
- Add, update, and remove vehicles
- Change vehicle availability
- List all vehicles
- Generate and view invoices
- Change the admin password

## Business Rules

- Reservations must start at least three days from the current date.
- The end date cannot be before the start date.
- A vehicle must be available before it can be reserved.
- Reservations can be updated or cancelled within two days of creation.
- A refundable deposit of LKR 5,000 is charged on each reservation.
- Rentals of seven or more days receive a 10% discount on the base rental price.
- Extra-kilometre charges and tax are calculated according to the vehicle category.

## Vehicle Categories

| Category | Daily rental fee | Free kilometres/day | Extra kilometre fee | Tax rate |
| --- | ---: | ---: | ---: | ---: |
| Compact Petrol Car | LKR 100.00 | 50 | LKR 10.00 | 10% |
| Hybrid Car | LKR 150.00 | 60 | LKR 12.00 | 12% |
| Electric Car | LKR 200.00 | 40 | LKR 8.00 | 8% |
| Luxury SUV | LKR 250.00 | 75 | LKR 15.00 | 15% |

## Requirements

- Java Development Kit (JDK) 24 or later
- Apache Ant, or Apache NetBeans with Ant support

The project is configured as a NetBeans Java/Ant project and has no external library dependencies.

## Build and Run

From the project root, build the executable JAR with:

```bash
ant clean jar
```

Run the application with:

```bash
java -jar dist/EcoRideCarRentalSystem.jar
```

Alternatively, compile and run through NetBeans using the project’s default run action.

## Admin Login

The default admin password is:

```text
admin123
```

Change this password from the Admin menu after logging in. Passwords are stored in memory as SHA-256 hashes while the application is running.

## Project Structure

```text
src/ecoride/
├── Main.java              # Console user interface
├── EcoRideManager.java    # Core application and business operations
├── Car.java               # Vehicle model
├── Category.java          # Vehicle pricing category
├── Customer.java          # Customer model
├── Reservation.java       # Reservation and price calculations
├── Invoice.java            # Invoice generation
└── *Status.java            # Availability and reservation statuses
```

## Data Storage

The current implementation stores cars, customers, reservations, and the admin password in memory. Data is reset when the application exits; no database or file-based persistence is currently configured.

## License

No license has been specified for this project.
