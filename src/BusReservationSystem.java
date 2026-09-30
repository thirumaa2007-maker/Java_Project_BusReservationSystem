import java.util.*;

class Bus {
    int busNumber;
    String source;
    String destination;
    double fare;
    int totalSeats;
    Set<Integer> bookedSeats = new HashSet<>();

    Bus(int busNumber, String source, String destination,
        double fare, int totalSeats) {

        this.busNumber = busNumber;
        this.source = source;
        this.destination = destination;
        this.fare = fare;
        this.totalSeats = totalSeats;
    }

    int availableSeats() {
        return totalSeats - bookedSeats.size();
    }

    void display() {
        System.out.println(
                "Bus No: " + busNumber +
                        " | " + source + " -> " + destination +
                        " | Fare: ₹" + fare +
                        " | Available Seats: " + availableSeats()
        );
    }
}

class Passenger {
    int pnr;
    String name;
    int age;
    int busNumber;
    int seatNumber;

    Passenger(int pnr, String name, int age,
              int busNumber, int seatNumber) {

        this.pnr = pnr;
        this.name = name;
        this.age = age;
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
    }

    void display() {
        System.out.println("\n----- Booking Details -----");
        System.out.println("PNR          : " + pnr);
        System.out.println("Passenger    : " + name);
        System.out.println("Age          : " + age);
        System.out.println("Bus Number   : " + busNumber);
        System.out.println("Seat Number  : " + seatNumber);
    }
}

public class BusReservationSystem {

    // ArrayList -> stores all buses
    static ArrayList<Bus> buses = new ArrayList<>();

    // HashMap -> quickly finds passenger using PNR
    static HashMap<Integer, Passenger> bookings = new HashMap<>();

    // Queue -> waiting list
    static Queue<String> waitingList = new LinkedList<>();

    static int nextPNR = 1001;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        addSampleBuses();

        while (true) {

            System.out.println("\n===== BUS RESERVATION SYSTEM =====");
            System.out.println("1. View Buses");
            System.out.println("2. Search Bus");
            System.out.println("3. Book Ticket");
            System.out.println("4. Cancel Ticket");
            System.out.println("5. Search Booking");
            System.out.println("6. View Waiting List");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    viewBuses();
                    break;

                case 2:
                    searchBus();
                    break;

                case 3:
                    bookTicket();
                    break;

                case 4:
                    cancelTicket();
                    break;

                case 5:
                    searchBooking();
                    break;

                case 6:
                    viewWaitingList();
                    break;

                case 7:
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // Add sample buses
    static void addSampleBuses() {

        buses.add(new Bus(
                101, "Chennai", "Coimbatore", 650, 40
        ));

        buses.add(new Bus(
                102, "Chennai", "Madurai", 550, 40
        ));

        buses.add(new Bus(
                103, "Coimbatore", "Chennai", 650, 40
        ));

        buses.add(new Bus(
                104, "Madurai", "Chennai", 550, 40
        ));
    }

    // Display all buses
    static void viewBuses() {

        System.out.println("\n===== AVAILABLE BUSES =====");

        for (Bus bus : buses) {
            bus.display();
        }
    }

    // Search bus
    static void searchBus() {

        sc.nextLine();

        System.out.print("Enter source: ");
        String source = sc.nextLine();

        System.out.print("Enter destination: ");
        String destination = sc.nextLine();

        boolean found = false;

        System.out.println("\n===== SEARCH RESULTS =====");

        for (Bus bus : buses) {

            if (bus.source.equalsIgnoreCase(source)
                    && bus.destination.equalsIgnoreCase(destination)) {

                bus.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bus found.");
        }
    }

    // Book ticket
    static void bookTicket() {

        System.out.print("Enter Bus Number: ");
        int busNumber = sc.nextInt();

        Bus selectedBus = findBus(busNumber);

        if (selectedBus == null) {
            System.out.println("Bus not found!");
            return;
        }

        if (selectedBus.availableSeats() == 0) {

            System.out.print("Bus is full.");
            System.out.print(" Enter your name for waiting list: ");

            sc.nextLine();
            String name = sc.nextLine();

            waitingList.add(name);

            System.out.println(
                    name + " added to waiting list."
            );

            return;
        }

        System.out.print("Enter Passenger Name: ");
        sc.nextLine();
        String name = sc.nextLine();

        System.out.print("Enter Age: ");
        int age = sc.nextInt();

        System.out.print("Enter Seat Number (1-"
                + selectedBus.totalSeats + "): ");

        int seat = sc.nextInt();

        if (seat < 1 || seat > selectedBus.totalSeats) {
            System.out.println("Invalid seat number!");
            return;
        }

        if (selectedBus.bookedSeats.contains(seat)) {
            System.out.println("Seat already booked!");
            return;
        }

        selectedBus.bookedSeats.add(seat);

        int pnr = nextPNR++;

        Passenger passenger =
                new Passenger(
                        pnr,
                        name,
                        age,
                        busNumber,
                        seat
                );

        bookings.put(pnr, passenger);

        System.out.println("\nTicket booked successfully!");
        passenger.display();
    }

    // Cancel ticket
    static void cancelTicket() {

        System.out.print("Enter PNR: ");
        int pnr = sc.nextInt();

        Passenger passenger = bookings.get(pnr);

        if (passenger == null) {
            System.out.println("Booking not found!");
            return;
        }

        Bus bus = findBus(passenger.busNumber);

        if (bus != null) {
            bus.bookedSeats.remove(passenger.seatNumber);
        }

        bookings.remove(pnr);

        System.out.println(
                "Ticket cancelled successfully."
        );

        // Move waiting passenger
        if (!waitingList.isEmpty()) {

            String nextPassenger =
                    waitingList.poll();

            System.out.println(
                    "Waiting-list passenger " +
                            nextPassenger +
                            " can now book the released seat."
            );
        }
    }

    // Search booking using PNR
    static void searchBooking() {

        System.out.print("Enter PNR: ");
        int pnr = sc.nextInt();

        Passenger passenger = bookings.get(pnr);

        if (passenger != null) {
            passenger.display();
        } else {
            System.out.println("Booking not found!");
        }
    }

    // Display waiting list
    static void viewWaitingList() {

        System.out.println("\n===== WAITING LIST =====");

        if (waitingList.isEmpty()) {
            System.out.println("Waiting list is empty.");
            return;
        }

        int position = 1;

        for (String name : waitingList) {

            System.out.println(
                    position + ". " + name
            );

            position++;
        }
    }

    // Find bus
    static Bus findBus(int busNumber) {

        for (Bus bus : buses) {

            if (bus.busNumber == busNumber) {
                return bus;
            }
        }

        return null;
    }
}