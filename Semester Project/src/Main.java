import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookingSystem system = new BookingSystem();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n🚌 Welcome to Daewoo Management System");
            System.out.println("1. Book a Ticket");
            System.out.println("2. Cancel a Ticket");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Invalid input. Enter 1, 2, or 3: ");
                sc.next();
            }

            choice = sc.nextInt();
            sc.nextLine(); // clear input buffer

            switch (choice) {
                case 1:
                    system.bookTicketInteractive();
                    break;
                case 2:
                    System.out.print("Enter Booking ID to cancel: ");
                    String cancelID = sc.nextLine();
                    system.cancelTicket(cancelID);
                    break;
                case 3:
                    System.out.println("Thank you for using Daewoo Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 3);
    }
}
