
import java.util.*;

public class NFAContains00 {

    // States
    enum State {
        Q0, Q1, Q2
    }

    // Simulate NFA using a set of current states
    public static boolean contains00(String input) {
        Set<State> currentStates = new HashSet<>();
        currentStates.add(State.Q0);

        for (char symbol : input.toCharArray()) {
            if (symbol != '0' && symbol != '1') {
                System.out.println("Invalid input: only 0 and 1 allowed.");
                return false;
            }

            Set<State> nextStates = new HashSet<>();

            for (State state : currentStates) {
                switch (state) {
                    case Q0:
                        if (symbol == '0') {
                            nextStates.add(State.Q0); // stay in q0
                            nextStates.add(State.Q1); // move to q1
                            System.out.println("Transition: Q0 or Q1");
                        } else {
                            nextStates.add(State.Q0); // stay in q0
                            System.out.println("Transition: Q0");
                        }
                        break;

                    case Q1:
                        if (symbol == '0') {
                            nextStates.add(State.Q2); // move to q2
                            System.out.println("Transition: Q2");
                        }
                        break;

                    case Q2:
                        nextStates.add(State.Q2); // stay in q2 regardless
                        System.out.println("Transition: Q2");
                        break;
                }
            }

            currentStates = nextStates;
        }

        return currentStates.contains(State.Q2);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a binary string: ");
        String input = scanner.nextLine();

        if (contains00(input)) {
            System.out.println("Accepted: String contains '00' as a substring");
        } else {
            System.out.println("Rejected: String does not contain '00'");
        }

        scanner.close();
    }
}