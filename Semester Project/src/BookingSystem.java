import java.util.*;

public class BookingSystem {
    private LinkedList<Passenger> passengerList = new LinkedList<>();
    private LinkedList<Ticket> ticketList = new LinkedList<>();
    private HashMap<String, Passenger> passengerMap = new HashMap<>();
    private HashMap<String, Ticket> ticketMap = new HashMap<>();
    private HashMap<String, Map<String, Integer>> routeMap = new HashMap<>();
    private HashMap<String, Bus> busSchedule = new HashMap<>();  // busID -> Bus
    private final int DEFAULT_BUS_SEATS = 40;

    private int bookingCounter = 1000;
    private int nextSeatNo = 1;

    public BookingSystem() {
        initializeRoutes();
        initializeBuses();
        loadFromFile();
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
        routeMap.get(to).put(from, fare); // bidirectional
    }


    public void bookTicketInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter passenger name: ");
        String name = sc.nextLine();
        System.out.print("Enter CNIC: ");
        String cnic = sc.nextLine();
        System.out.print("Enter Phone no: ");
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
        sc.nextLine(); // Clear buffer
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

        System.out.println("Bus Info: " + selectedBus);
        if (selectedBus.getAvailableSeats() == 0) {
            System.out.println("Sorry! No seats available.");
            return;
        }

        int seatNo = selectedBus.assignSeat(gender);
        if (seatNo == -1) {
            System.out.println("No suitable seat available based on gender restrictions.");
            return;
        }

        String bookingID = "BK" + bookingCounter++;
        Passenger passenger = new Passenger(name, cnic, phone, bookingID, seatNo, busID, gender);

        int fare = routeMap.get(fromCity).get(toCity);
        Ticket ticket = new Ticket(bookingID, passenger, fare);

        passengerList.add(passenger);
        ticketList.add(ticket);
        passengerMap.put(bookingID, passenger);
        ticketMap.put(bookingID, ticket);

        System.out.println("\n✅ Ticket Booked Successfully!");
        System.out.println(passenger);
        System.out.println(ticket);
        System.out.println("Saving to file...");
        FileHandler.savePassenger(passenger, fare);
    }

        private String getCityChoice (String type){
            Scanner sc = new Scanner(System.in);
            String[] cities = {"Lahore", "Islamabad", "Multan", "Faisalabad", "Sahiwal", "Karachi"};

            System.out.println("Select " + type + " City:");
            for (int i = 0; i < cities.length; i++) {
                System.out.println((i + 1) + ". " + cities[i]);
            }
            System.out.print("Enter choice (1-6): ");
            int choice = sc.nextInt();
            sc.nextLine(); // Clear newline

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

            passengerList.add(p);
            ticketList.add(t);
            passengerMap.put(bookingID, p);
            ticketMap.put(bookingID, t);

            if (busSchedule.containsKey(busID)) {
                busSchedule.get(busID).restoreSeat(seatNo, gender);
            }

            // Update counters to avoid duplicates
            bookingCounter = Math.max(bookingCounter, Integer.parseInt(bookingID.substring(2)) + 1);
            nextSeatNo = Math.max(nextSeatNo, seatNo + 1);
        }
        System.out.println("Loaded " + records.size() + " passenger(s) from file.");
    }

    public void cancelTicket(String bookingID) {
        if (!passengerMap.containsKey(bookingID)) {
            System.out.println("❌ Booking ID not found.");
            return;
        }

        Passenger p = passengerMap.get(bookingID);
        String busID = p.busID;

        // Remove from in-memory storage
        passengerMap.remove(bookingID);
        ticketMap.remove(bookingID);
        passengerList.removeIf(passenger -> passenger.bookingID.equals(bookingID));
        ticketList.removeIf(ticket -> ticket.bookingID.equals(bookingID));

        // Free seat on the bus
        if (busSchedule.containsKey(busID)) {
            busSchedule.get(busID).cancelSeat(p.seatNo);
        }

        // Update file
        FileHandler.rewriteFile(passengerList);

        System.out.println("✅ Ticket with Booking ID " + bookingID + " has been cancelled.");
    }

}