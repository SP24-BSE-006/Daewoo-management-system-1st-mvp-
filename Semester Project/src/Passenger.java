public class Passenger {
    public String name;
    public String cnic;
    public String phone;
    public String bookingID;
    public int seatNo;
    public String busID;
    public String gender;

    public Passenger(String name, String cnic, String phone, String bookingID, int seatNo, String busID, String gender) {
        this.name = name;
        this.cnic = cnic;
        this.phone = phone;
        this.bookingID = bookingID;
        this.seatNo = seatNo;
        this.busID = busID;
        this.gender = gender;
    }

    public String toString() {
        return "Booking ID: " + bookingID + ", Name: " + name + ", Gender: " + gender +
                ", Seat No: " + seatNo + ", Bus ID: " + busID;
    }
}

