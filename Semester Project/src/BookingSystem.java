import java.util.*;

public class BookingSystem {
    private PassengerLinkedList passengerList = new PassengerLinkedList();
    private HashMap<String, Passenger> passengerMap = new HashMap<>();
    private HashMap<String, Ticket> ticketMap = new HashMap<>();
    private HashMap<String, Bus> busSchedule = new HashMap<>();
    private HashMap<String, Map<String, Integer>> routeMap = new HashMap<>();
    private HashMap<String, MyQueue> waitlistMap = new HashMap<>(); // Custom queue

    private int bookingCounter = 1000;
    private final int DEFAULT_BUS_SEATS = 40;

    public BookingSystem() {
        initializeRoutes();
        initializeBuses();
        loadFromFile();
    }

    private void initializeRoutes() {
        addFare("Lahore", "Islamabad", 1800);
        addFare("Lahore", "Multan", 2000);
        addFare("Lahore", "Faisalabad", 1200);
        addFare("Lahore", "Sahiwal", 1300);
        addFare("Lahore", "Karachi", 4500);
        addFare("Islamabad", "Multan", 2200);
        addFare("Multan", "Karachi", 4000);
        addFare("Karachi", "Faisalabad", 4300);
    }

    private void addFare(String from, String to, int fare) {
        routeMap.putIfAbsent(from, new HashMap<>());
        routeMap.putIfAbsent(to, new HashMap<>());
        routeMap.get(from).put(to, fare);
        routeMap.get(to).put(from, fare);
    }

    private void initializeBuses() {
        String[] timeSlots = {"10AM", "2PM", "6PM"};
        String[][] routes = {
                {"Lahore", "Islamabad"},
                {"Lahore", "Multan"},
                {"Lahore", "Faisalabad"},
                {"Lahore", "Sahiwal"},
                {"Lahore", "Karachi"},
                {"Islamabad", "Multan"},
                {"Multan", "Karachi"},
                {"Karachi", "Faisalabad"}
        };

        for (String[] route : routes) {
            for (String time : timeSlots) {
                Bus bus = new Bus(route[0], route[1], time, DEFAULT_BUS_SEATS);
                busSchedule.put(bus.getBusID(), bus);
            }
        }
    }

    public void bookTicketInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter passenger name: ");
        String name = sc.nextLine();

        System.out.print("Enter CNIC: ");
        String cnic = sc.nextLine();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        System.out.print("Enter Gender (Male/Female): ");
        String gender = sc.nextLine().trim();

        String fromCity = getCityChoice("From");
        String toCity = getCityChoice("To");
        if (fromCity.equals(toCity)) {
            System.out.println("Source and destination cannot be the same!");
            return;
        }

        System.out.print("Available time slots: 1. 10AM  2. 2PM  3. 6PM\nSelect slot (1-3): ");
        int timeChoice = sc.nextInt();
        sc.nextLine();
        String timeSlot = switch (timeChoice) {
            case 1 -> "10AM";
            case 2 -> "2PM";
            case 3 -> "6PM";
            default -> "10AM";
        };

        String busID = fromCity + "-" + toCity + "-" + timeSlot;
        Bus selectedBus = busSchedule.get(busID);
        if (selectedBus == null) {
            System.out.println("Bus not available.");
            return;
        }

        if (selectedBus.getAvailableSeats() == 0) {
            System.out.println("Bus is full. You will be added to the waitlist.");
            Passenger waitlisted = new Passenger(name, cnic, phone, "WL" + bookingCounter++, -1, busID, gender);
            waitlistMap.putIfAbsent(busID, new MyQueue());
            waitlistMap.get(busID).enqueue(waitlisted);
            return;
        }

        int seatNo = selectedBus.assignSeat(gender);
        if (seatNo == -1) {
            System.out.println("No suitable seat available based on gender restrictions.");
            return;
        }

        String bookingID = "BK" + bookingCounter++;
        int fare = routeMap.get(fromCity).get(toCity);

        Passenger passenger = new Passenger(name, cnic, phone, bookingID, seatNo, busID, gender);
        Ticket ticket = new Ticket(bookingID, passenger, fare);

        passengerMap.put(bookingID, passenger);
        ticketMap.put(bookingID, ticket);
        passengerList.add(passenger);

        FileHandler.savePassenger(passenger, fare);

        System.out.println("\n✅ Ticket Booked Successfully!");
        System.out.println("🔖 YOUR BOOKING ID: " + bookingID);
        System.out.println(passenger);
        System.out.println(ticket);
    }

    public void cancelTicket(String bookingID) {
        if (!passengerMap.containsKey(bookingID)) {
            System.out.println("❌ Booking ID not found.");
            return;
        }

        Passenger p = passengerMap.get(bookingID);
        String busID = p.busID;

        passengerMap.remove(bookingID);
        ticketMap.remove(bookingID);
        passengerList.removeByBookingID(bookingID);

        if (busSchedule.containsKey(busID)) {
            busSchedule.get(busID).cancelSeat(p.seatNo);
        }

        FileHandler.rewriteFile(passengerList);

        System.out.println("✅ Ticket with Booking ID " + bookingID + " has been cancelled.");

        if (waitlistMap.containsKey(busID)) {
            MyQueue queue = waitlistMap.get(busID);
            while (!queue.isEmpty()) {
                Passenger next = queue.peek();
                int assignedSeat = busSchedule.get(busID).assignSeat(next.gender);
                if (assignedSeat != -1) {
                    queue.dequeue();
                    String newBookingID = "BK" + bookingCounter++;
                    Passenger confirmed = new Passenger(next.name, next.cnic, next.phone, newBookingID, assignedSeat, busID, next.gender);
                    int fare = routeMap.get(busID.split("-")[0]).get(busID.split("-")[1]);
                    Ticket t = new Ticket(newBookingID, confirmed, fare);

                    passengerMap.put(newBookingID, confirmed);
                    ticketMap.put(newBookingID, t);
                    passengerList.add(confirmed);
                    FileHandler.savePassenger(confirmed, fare);

                    System.out.println("✅ Seat assigned to waitlisted passenger: " + confirmed.name +
                            " (Booking ID: " + newBookingID + ")");
                    break;
                } else {
                    break;
                }
            }
        }
    }

    private String getCityChoice(String type) {
        Scanner sc = new Scanner(System.in);
        String[] cities = {"Lahore", "Islamabad", "Multan", "Faisalabad", "Sahiwal", "Karachi"};

        System.out.println("Select " + type + " City:");
        for (int i = 0; i < cities.length; i++) {
            System.out.println((i + 1) + ". " + cities[i]);
        }
        System.out.print("Enter choice (1-6): ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice >= 1 && choice <= cities.length) {
            return cities[choice - 1];
        } else {
            System.out.println("Invalid choice. Defaulting to Lahore.");
            return "Lahore";
        }
    }

    private void loadFromFile() {
        List<String[]> records = FileHandler.loadPassengerData();
        for (String[] r : records) {
            String bookingID = r[0];
            String name = r[1];
            String cnic = r[2];
            String phone = r[3];
            int seatNo = Integer.parseInt(r[4]);
            String busID = r[5];
            double fare = Double.parseDouble(r[6]);
            String gender = r[7];

            Passenger p = new Passenger(name, cnic, phone, bookingID, seatNo, busID, gender);
            Ticket t = new Ticket(bookingID, p, fare);

            passengerMap.put(bookingID, p);
            ticketMap.put(bookingID, t);
            passengerList.add(p);

            if (busSchedule.containsKey(busID)) {
                busSchedule.get(busID).restoreSeat(seatNo, gender);
            }

            bookingCounter = Math.max(bookingCounter, Integer.parseInt(bookingID.substring(2)) + 1);
        }
        System.out.println("Loaded " + records.size() + " passenger(s) from file.");
    }
}
