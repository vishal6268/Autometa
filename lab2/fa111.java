package lab2;
import java.util.Scanner;

public class fa111 {

    // Define DFA states
    enum State {
        Q0, Q1, Q2, Q3
    }

    public static boolean containsThreeOnes(String input) {
        State currentState = State.Q0;

        for (char ch : input.toCharArray()) {
            if (ch != '0' && ch != '1') {
                System.out.println("Invalid character found: " + ch);
                return false;
            }

            switch (currentState) {
                case Q0:
                    currentState = (ch == '1') ? State.Q1 : State.Q0;
                    if(currentState == State.Q0 && ch == '0') {
                        System.out.println("Transition: Q0");
                    } else if (currentState == State.Q1 && ch == '1') {
                        System.out.println("Transition: Q1");
                    }
                    break;

                case Q1:
                    currentState = (ch == '1') ? State.Q2 : State.Q0;
                    if(currentState == State.Q1 && ch == '1') {
                        System.out.println("Transition: Q1");
                    } else if (currentState == State.Q0 && ch == '0') {
                        System.out.println("Transition: Q0");
                    }
                    break;

                case Q2:
                    currentState = (ch == '1') ? State.Q3 : State.Q0;
                    if(currentState == State.Q2 && ch == '1') {
                        System.out.println("Transition: Q2");
                    } else if (currentState == State.Q0 && ch == '0') {
                        System.out.println("Transition: Q0");
                    }
                    break;

                case Q3:
                    currentState = State.Q3; // Once reached, stay here
                    if (ch == '1') {
                        System.out.println("Transition: Q3");
                    } else {
                        System.out.println("Transition: Q3");
                    }
                    break;
            }
        }

        return currentState == State.Q3;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a binary string: ");
        String input = scanner.nextLine();
        while (!input.matches("[01]*")) {
            System.out.println("Invalid input. Please enter a binary string containing only '0' and '1'.");
            input = scanner.nextLine();
        }

        if (containsThreeOnes(input)) {
            System.out.println("Accepted: String contains '111' as a substring");
        } else {
            System.out.println("Rejected: String does not contain '111'");
        }

        scanner.close();
    }
}
