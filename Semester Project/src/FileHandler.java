import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String FILE_NAME = "passengers.txt";

    // Save passenger to file (append mode)
    public static void savePassenger(Passenger p, double fare) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(p.bookingID + "," + p.name + "," + p.cnic + "," + p.phone + "," +
                    p.seatNo + "," + p.busID + "," + fare + "," + p.gender);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    // Load all passengers from file and return list
    public static List<String[]> loadPassengerData() {
        List<String[]> data = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return data;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 8) {  // now includes gender
                    data.add(fields);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
        return data;
    }

    public static void rewriteFile(PassengerLinkedList passengerList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            passengerList.forEach(p -> {
                try {
                    bw.write(p.bookingID + "," + p.name + "," + p.cnic + "," + p.phone + "," +
                            p.seatNo + "," + p.busID + "," + getFareFromBusID(p.busID) + "," + p.gender);
                    bw.newLine();
                } catch (IOException e) {
                    System.out.println("Error writing passenger: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.out.println("Error rewriting file: " + e.getMessage());
        }
    }

    // Helper to extract fare — optional if fare is stored separately
    private static double getFareFromBusID(String busID) {
        if (busID.contains("Islamabad")) return 1800;
        if (busID.contains("Multan")) return 2000;
        if (busID.contains("Faisalabad")) return 1200;
        if (busID.contains("Sahiwal")) return 1300;
        if (busID.contains("Karachi")) return 4500;
        return 1500;
    }
}

