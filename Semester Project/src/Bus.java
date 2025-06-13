import java.util.HashMap;

public class Bus {
    private String busID;
    private String fromCity;
    private String toCity;
    private String timeSlot;
    private int maxSeats;
    private int availableSeats;
    private HashMap<Integer, String> seatGenderMap = new HashMap<>();

    public Bus(String fromCity, String toCity, String timeSlot, int maxSeats) {
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.timeSlot = timeSlot;
        this.maxSeats = maxSeats;
        this.availableSeats = maxSeats;
        this.busID = fromCity + "-" + toCity + "-" + timeSlot;
    }

    public String getBusID() {
        return busID;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean isSeatAllowed(int seatNo, String gender) {
        int adjacentSeat = (seatNo % 2 == 0) ? seatNo - 1 : seatNo + 1;
        if (seatGenderMap.containsKey(seatNo)) return false;

        if (seatGenderMap.containsKey(adjacentSeat)) {
            return seatGenderMap.get(adjacentSeat).equalsIgnoreCase(gender);
        }
        return true;
    }

    public int assignSeat(String gender) {
        for (int seatNo = 1; seatNo <= maxSeats; seatNo++) {
            if (isSeatAllowed(seatNo, gender)) {
                seatGenderMap.put(seatNo, gender);
                availableSeats--;
                return seatNo;
            }
        }
        return -1; // No compatible seat available
    }

    public void cancelSeat(int seatNo) {
        if (seatGenderMap.containsKey(seatNo)) {
            seatGenderMap.remove(seatNo);
            availableSeats++;
        }
    }

    public void restoreSeat(int seatNo, String gender) {
        if (!seatGenderMap.containsKey(seatNo)) {
            seatGenderMap.put(seatNo, gender);
            availableSeats--;
        }
    }

    @Override
    public String toString() {
        return "Bus ID: " + busID + ", From: " + fromCity + ", To: " + toCity +
                ", Time: " + timeSlot + ", Available Seats: " + availableSeats + "/" + maxSeats;
    }
}
