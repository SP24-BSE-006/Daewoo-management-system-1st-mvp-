public class PassengerLinkedList {
    private PassengerNode head;

    public void add(Passenger passenger) {
        PassengerNode newNode = new PassengerNode(passenger);
        if (head == null) {
            head = newNode;
        } else {
            PassengerNode temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
    }

    public void removeByBookingID(String bookingID) {
        if (head == null) return;

        if (head.data.bookingID.equals(bookingID)) {
            head = head.next;
            return;
        }

        PassengerNode prev = head;
        PassengerNode current = head.next;

        while (current != null) {
            if (current.data.bookingID.equals(bookingID)) {
                prev.next = current.next;
                return;
            }
            prev = current;
            current = current.next;
        }
    }

    public void forEach(java.util.function.Consumer<Passenger> action) {
        PassengerNode temp = head;
        while (temp != null) {
            action.accept(temp.data);
            temp = temp.next;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public java.util.List<Passenger> toList() {
        java.util.List<Passenger> list = new java.util.ArrayList<>();
        PassengerNode temp = head;
        while (temp != null) {
            list.add(temp.data);
            temp = temp.next;
        }
        return list;
    }

    public boolean containsBookingID(String bookingID) {
        PassengerNode temp = head;
        while (temp != null) {
            if (temp.data.bookingID.equals(bookingID)) return true;
            temp = temp.next;
        }
        return false;
    }

    public Passenger getByBookingID(String bookingID) {
        PassengerNode temp = head;
        while (temp != null) {
            if (temp.data.bookingID.equals(bookingID)) return temp.data;
            temp = temp.next;
        }
        return null;
    }
}
