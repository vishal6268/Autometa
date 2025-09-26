package lab2;
import java.util.Scanner;

public class EndsWith01DFA {

    // DFA states
    enum State {
        Q0, Q1, Q2
    }

    // DFA transition function
    public static boolean endsWith01(String input) {
        State currentState = State.Q0;

        for (char ch : input.toCharArray()) {
            if (ch != '0' && ch != '1') {
                System.out.println("Invalid input: only 0 and 1 are allowed.");
                return false;
            }

            switch (currentState) {
                case Q0:
                    currentState = (ch == '0') ? State.Q1 : State.Q0;
                    if (currentState == State.Q0 && ch == '1') {
                        System.out.println("Transition: Q0");
                    } else if (currentState == State.Q1 && ch == '0') {
                        System.out.println("Transition: Q1");
                    }
                    break;

                case Q1:
                    currentState = (ch == '0') ? State.Q1 : State.Q2;
                    if (currentState == State.Q1 && ch == '0') {
                        System.out.println("Transition: Q1");
                    } else if (currentState == State.Q2 && ch == '1') {
                        System.out.println("Transition: Q2");
                    }
                    break;

                case Q2:
                    currentState = (ch == '0') ? State.Q1 : State.Q0;
                    if (currentState == State.Q1 && ch == '0') {
                        System.out.println("Transition: Q1");
                    } else if (currentState == State.Q0 && ch == '1') {
                        System.out.println("Transition: Q0");
                    }
                    break;
            }
        }

        return currentState == State.Q2;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a binary string: ");
        String input = scanner.nextLine();

        if (endsWith01(input)) {
            System.out.println("Accepted: String ends with '01'");
        } else {
            System.out.println("Rejected: String does not end with '01'");
        }

        scanner.close();
    }
}
