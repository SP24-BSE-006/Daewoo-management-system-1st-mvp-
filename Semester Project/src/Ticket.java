import java.time.LocalDate;

public class Ticket {
    String bookingID;
    Passenger passenger;
    String busID;
    int seatNo;
    double price;
    LocalDate issueDate;

    public Ticket(String bookingID, Passenger passenger, double price) {
        this.bookingID = bookingID;
        this.passenger = passenger;
        this.busID = passenger.busID;
        this.seatNo = passenger.seatNo;
        this.price = price;
        this.issueDate = LocalDate.now();
    }

    public String toString() {
        return "Ticket [" + bookingID + "] for " + passenger.name + " on Bus " + busID +
                ", Seat " + seatNo + ", Price: Rs." + price + ", Date: " + issueDate;
    }
}

