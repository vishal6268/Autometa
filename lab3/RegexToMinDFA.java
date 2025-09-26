import java.util.*;

// Represents a state in the NFA
@SuppressWarnings("unchecked")
class NFAState {
    // Transitions on 'a' and 'b'
    public List<Integer>[] transitions = new List[]{new ArrayList<>(), new ArrayList<>()};
    // Epsilon transitions
    public List<Integer> epsilonTransitions = new ArrayList<>();
    public boolean isFinal = false;

    // A simple constructor for cleaner initialization
    public NFAState() {}
}

// Represents a state in the DFA
class DFAState {
    // Transitions on 'a' and 'b' to other DFA state indices
    public int[] transitions = {-1, -1};
    public boolean isFinal = false;

    // A simple constructor
    public DFAState() {}
}

// Represents a state in the Minimized DFA
class MinDFAState {
    // Transitions on 'a' and 'b' to other minimized DFA state indices
    public int[] transitions = {-1, -1};
    public boolean isFinal = false;

    // A simple constructor
    public MinDFAState() {}
}

@SuppressWarnings("unchecked")
public class RegexToMinDFA {

    private static List<NFAState> nfa = new ArrayList<>();
    private static List<DFAState> dfa = new ArrayList<>();
    private static Stack<Integer> stateStack = new Stack<>();
    private static String displayRegex;
    private static int nfaSize = 0;
    private static int dfaSize = 0;
    private static int minDfaSize = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String regex, postfix;

        System.out.println("Enter Regular Expression:");
        regex = scanner.nextLine();
        displayRegex = regex;

        // Step 1: Insert concatenation operators
        regex = insertConcat(regex);
        // Step 2: Convert to postfix notation
        postfix = regexToPostfix(regex);
        System.out.println("Postfix Expression: " + postfix);

        // Step 3: Convert postfix to NFA using Thompson's Construction
        postfixToNFA(postfix);
        int finalNfaState = stateStack.pop();
        int startNfaState = stateStack.pop();
        nfa.get(finalNfaState).isFinal = true;

        // Step 4: Convert NFA to DFA using Subset Construction
        nfaToDFA(startNfaState);

        // Step 5: Minimize the DFA
        Map.Entry<Integer, List<MinDFAState>> minimizedDFA = minimizeDFA();
        int startMinDFAState = minimizedDFA.getKey();
        List<MinDFAState> minDfa = minimizedDFA.getValue();
        minDfaSize = minDfa.size();

        while (true) {
            printMenu();
            char choice = scanner.next().charAt(0);
            System.out.println(); // For spacing

            switch (choice) {
                case '1':
                    displayNFA();
                    break;
                case '2':
                    printDFA();
                    break;
                case '3':
                    printMinDFA(minDfa);
                    break;
                case '4':
                    simulate(startMinDFAState, minDfa, scanner);
                    break;
                default:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
            }
        }
    }

    // Adds a '.' for concatenation where needed in the regex
    private static String insertConcat(String regex) {
        StringBuilder result = new StringBuilder();
        char prevChar;
        for (int i = 0; i < regex.length(); i++) {
            char currentChar = regex.charAt(i);
            if (i > 0) {
                prevChar = regex.charAt(i - 1);
                // Insert a '.' between a closing parenthesis, star, or character
                // and a character or opening parenthesis
                if (prevChar != '(' && prevChar != '+' && prevChar != '.' &&
                    currentChar != '*' && currentChar != '+' && currentChar != '.' &&
                    prevChar != '|' && currentChar != '|' && prevChar != '(' &&
                    prevChar != '+' && currentChar != ')' &&
                    (prevChar != '(' && currentChar != ')' && prevChar != '+' && currentChar != '+' && prevChar != '*' && currentChar != '*')) {
                    
                     if (prevChar == 'a' || prevChar == 'b' || prevChar == ')' || prevChar == '*') {
                         if (currentChar == 'a' || currentChar == 'b' || currentChar == '(') {
                             result.append('.');
                         }
                     }
                }
            }
            result.append(currentChar);
        }
        return result.toString();
    }
    

    // Converts infix regex to postfix
    private static String regexToPostfix(String regex) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put('+', 1);
        precedence.put('.', 2);
        precedence.put('*', 3);

        for (char c : regex.toCharArray()) {
            if (c == 'a' || c == 'b') {
                postfix.append(c);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfix.append(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Pop '('
                }
            } else {
                while (!operators.isEmpty() && operators.peek() != '(' &&
                       precedence.getOrDefault(operators.peek(), 0) >= precedence.getOrDefault(c, 0)) {
                    postfix.append(operators.pop());
                }
                operators.push(c);
            }
        }
        while (!operators.isEmpty()) {
            postfix.append(operators.pop());
        }
        return postfix.toString();
    }

    // Builds the NFA from a postfix regex
    private static void postfixToNFA(String postfix) {
        for (char c : postfix.toCharArray()) {
            switch (c) {
                case 'a':
                case 'b':
                    character(c);
                    break;
                case '*':
                    kleeneStar();
                    break;
                case '.':
                    concatenation();
                    break;
                case '+':
                    union();
                    break;
            }
        }
    }
    
    // NFA construction for a single character
    private static void character(char c) {
        nfa.add(new NFAState());
        nfa.add(new NFAState());
        int charIndex = (c == 'a') ? 0 : 1;
        nfa.get(nfaSize).transitions[charIndex].add(nfaSize + 1);
        stateStack.push(nfaSize);
        nfaSize++;
        stateStack.push(nfaSize);
        nfaSize++;
    }

    // NFA construction for union (+)
    private static void union() {
        int d = stateStack.pop();
        int c = stateStack.pop();
        int b = stateStack.pop();
        int a = stateStack.pop();
        nfa.add(new NFAState());
        nfa.add(new NFAState());
        nfa.get(nfaSize).epsilonTransitions.add(a);
        nfa.get(nfaSize).epsilonTransitions.add(c);
        nfa.get(b).epsilonTransitions.add(nfaSize + 1);
        nfa.get(d).epsilonTransitions.add(nfaSize + 1);
        stateStack.push(nfaSize);
        nfaSize++;
        stateStack.push(nfaSize);
        nfaSize++;
    }

    // NFA construction for concatenation (.)
    private static void concatenation() {
        int d = stateStack.pop();
        int c = stateStack.pop();
        int b = stateStack.pop();
        int a = stateStack.pop();
        nfa.get(b).epsilonTransitions.add(c);
        stateStack.push(a);
        stateStack.push(d);
    }
    
    // NFA construction for Kleene star (*)
    private static void kleeneStar() {
        int b = stateStack.pop();
        int a = stateStack.pop();
        nfa.add(new NFAState());
        nfa.add(new NFAState());
        nfa.get(nfaSize).epsilonTransitions.add(a);
        nfa.get(nfaSize).epsilonTransitions.add(nfaSize + 1);
        nfa.get(b).epsilonTransitions.add(a);
        nfa.get(b).epsilonTransitions.add(nfaSize + 1);
        stateStack.push(nfaSize);
        nfaSize++;
        stateStack.push(nfaSize);
        nfaSize++;
    }

    // Prints the NFA transition table
    private static void displayNFA() {
        System.out.println("Phase 1: Regex to NFA conversion using Thompson's Construction Algorithm");
        System.out.println("------------------------------------------------------------------------");
        System.out.printf("%-5s| %-5s| %-5s| %-5s| %-5s%n", "State", "a", "b", "eps", "Final");
        System.out.println("------------------------------------------------------------------------");
        for (int i = 0; i < nfa.size(); i++) {
            NFAState state = nfa.get(i);
            System.out.printf("%-5d| %-5s| %-5s| %-5s| %-5s%n", i, state.transitions[0], state.transitions[1], state.epsilonTransitions, state.isFinal ? "Yes" : "No");
        }
        System.out.println("------------------------------------------------------------------------");
    }

    // NFA to DFA conversion using Subset Construction
    private static void nfaToDFA(int startState) {
        Map<Set<Integer>, Integer> stateMap = new HashMap<>();
        Queue<Set<Integer>> unprocessedStates = new LinkedList<>();

        // Epsilon closure of the NFA start state
        Set<Integer> initialDFAState = new HashSet<>();
        initialDFAState.add(startState);
        initialDFAState = epsilonClosure(initialDFAState);
        
        stateMap.put(initialDFAState, dfaSize++);
        unprocessedStates.add(initialDFAState);
        dfa.add(new DFAState());

        while (!unprocessedStates.isEmpty()) {
            Set<Integer> currentNfaSet = unprocessedStates.poll();
            int currentDfaIndex = stateMap.get(currentNfaSet);
            
            // Check if final state
            for(int nfaState : currentNfaSet){
                if(nfa.get(nfaState).isFinal){
                    dfa.get(currentDfaIndex).isFinal = true;
                    break;
                }
            }

            // Transitions for 'a'
            Set<Integer> nextNfaSetA = move(currentNfaSet, 0);
            nextNfaSetA = epsilonClosure(nextNfaSetA);
            if (!nextNfaSetA.isEmpty()) {
                if (!stateMap.containsKey(nextNfaSetA)) {
                    stateMap.put(nextNfaSetA, dfaSize++);
                    unprocessedStates.add(nextNfaSetA);
                    dfa.add(new DFAState());
                }
                dfa.get(currentDfaIndex).transitions[0] = stateMap.get(nextNfaSetA);
            }

            // Transitions for 'b'
            Set<Integer> nextNfaSetB = move(currentNfaSet, 1);
            nextNfaSetB = epsilonClosure(nextNfaSetB);
            if (!nextNfaSetB.isEmpty()) {
                if (!stateMap.containsKey(nextNfaSetB)) {
                    stateMap.put(nextNfaSetB, dfaSize++);
                    unprocessedStates.add(nextNfaSetB);
                    dfa.add(new DFAState());
                }
                dfa.get(currentDfaIndex).transitions[1] = stateMap.get(nextNfaSetB);
            }
        }
    }
    
    // Finds the epsilon closure for a set of NFA states
    private static Set<Integer> epsilonClosure(Set<Integer> states) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> closure = new HashSet<>(states);
        
        for (int state : states) {
            stack.push(state);
        }

        while (!stack.isEmpty()) {
            int currentState = stack.pop();
            for (int nextState : nfa.get(currentState).epsilonTransitions) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.push(nextState);
                }
            }
        }
        return closure;
    }

    // Finds the states reachable from a set of NFA states on a given input
    private static Set<Integer> move(Set<Integer> states, int symbolIndex) {
        Set<Integer> result = new HashSet<>();
        for (int state : states) {
            result.addAll(nfa.get(state).transitions[symbolIndex]);
        }
        return result;
    }
    
    // Prints the DFA transition table
    private static void printDFA() {
        System.out.println("NFA to DFA Conversion");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-5s| %-5s| %-5s| %-5s%n", "STATE", "a", "b", "FINAL");
        System.out.println("---------------------------------------------------------");
        for (int i = 0; i < dfa.size(); i++) {
            DFAState state = dfa.get(i);
            System.out.printf("%-5d| %-5s| %-5s| %-5s%n", i, state.transitions[0], state.transitions[1], state.isFinal);
        }
        System.out.println("---------------------------------------------------------");
    }

    // Minimizes the DFA
    private static Map.Entry<Integer, List<MinDFAState>> minimizeDFA() {
        List<Integer> grp = new ArrayList<>(Collections.nCopies(dfa.size(), 0));
        List<List<Integer>> partitions = new ArrayList<>();
        partitions.add(new ArrayList<>());
        partitions.add(new ArrayList<>());

        // Initial partition based on final and non-final states
        for (int i = 0; i < dfa.size(); i++) {
            if (dfa.get(i).isFinal) {
                grp.set(i, 1);
                partitions.get(1).add(i);
            } else {
                grp.set(i, 0);
                partitions.get(0).add(i);
            }
        }
        if (partitions.get(1).isEmpty()) partitions.remove(1);
        if (partitions.get(0).isEmpty()) partitions.remove(0);

        boolean changed = true;
        while (changed) {
            changed = false;
            int initialPartitionsSize = partitions.size();
            for (int i = 0; i < initialPartitionsSize; i++) {
                List<Integer> currentPartition = partitions.get(i);
                Map<Integer, List<Integer>> newSubPartitions = new HashMap<>();

                for (int stateIndex : currentPartition) {
                    DFAState currentState = dfa.get(stateIndex);
                    // Create a unique key based on the group numbers of transitions
                    int transitionGroupKey = (currentState.transitions[0] == -1 ? -1 : grp.get(currentState.transitions[0])) * 100 +
                                             (currentState.transitions[1] == -1 ? -1 : grp.get(currentState.transitions[1]));
                    
                    newSubPartitions.computeIfAbsent(transitionGroupKey, k -> new ArrayList<>()).add(stateIndex);
                }

                if (newSubPartitions.size() > 1) {
                    changed = true;
                    partitions.remove(i);
                    i--;
                    initialPartitionsSize--;
                    for (List<Integer> subPartition : newSubPartitions.values()) {
                        partitions.add(subPartition);
                        int newGroupIndex = partitions.size() - 1;
                        for (int stateIndex : subPartition) {
                            grp.set(stateIndex, newGroupIndex);
                        }
                    }
                }
            }
        }

        List<MinDFAState> minDfa = new ArrayList<>();
        int startStateGroup = grp.get(0);
        
        for(int i = 0; i < partitions.size(); i++){
            minDfa.add(new MinDFAState());
            minDfa.get(i).isFinal = dfa.get(partitions.get(i).get(0)).isFinal;
            
            // Check for trap states before getting the group
            int aTransition = dfa.get(partitions.get(i).get(0)).transitions[0];
            if (aTransition != -1) {
                minDfa.get(i).transitions[0] = grp.get(aTransition);
            } else {
                minDfa.get(i).transitions[0] = -1;
            }

            int bTransition = dfa.get(partitions.get(i).get(0)).transitions[1];
            if (bTransition != -1) {
                minDfa.get(i).transitions[1] = grp.get(bTransition);
            } else {
                minDfa.get(i).transitions[1] = -1;
            }
        }
        
        return new AbstractMap.SimpleEntry<>(startStateGroup, minDfa);
    }
    
    // Prints the minimized DFA transition table
    private static void printMinDFA(List<MinDFAState> minDfa) {
        System.out.println("Minimized DFA");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-5s| %-5s| %-5s| %-5s%n", "STATE", "a", "b", "FINAL");
        System.out.println("---------------------------------------------------------");
        for (int i = 0; i < minDfa.size(); i++) {
            MinDFAState state = minDfa.get(i);
            System.out.printf("%-5d| %-5s| %-5s| %-5s%n", i, state.transitions[0], state.transitions[1], state.isFinal ? "Yes" : "No");
        }
        System.out.println("---------------------------------------------------------");
    }

    // Simulates the minimized DFA with an input string
    private static void simulate(int startState, List<MinDFAState> minDfa, Scanner scanner) {
        System.out.print("Enter string : ");
        scanner.nextLine(); // Consume the newline
        String input = scanner.nextLine();
        
        int currentState = startState;
        System.out.println("-----------------------------------------");
        System.out.printf("%-5s| %-10s| %-10s%n", "Input", "Current", "Next");
        System.out.println("-----------------------------------------");
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                System.out.println("Invalid character: " + c);
                currentState = -1;
                break;
            }
            int nextState = -1;
            if (currentState != -1) {
                int charIndex = (c == 'a') ? 0 : 1;
                nextState = minDfa.get(currentState).transitions[charIndex];
            }
            System.out.printf("%-5c| %-10d| %-10s%n", c, currentState, nextState == -1 ? "Trap" : nextState);
            currentState = nextState;
        }

        System.out.println("-----------------------------------------");
        System.out.print("Verdict: ");
        if (currentState != -1 && minDfa.get(currentState).isFinal) {
            System.out.println("Accepted");
        } else {
            System.out.println("Rejected");
        }
    }
    
    // Prints the main menu
    private static void printMenu() {
        System.out.println("\n---------------------------------------");
        System.out.println("Input Regex: " + displayRegex);
        System.out.println("1. NFA");
        System.out.println("2. Intermediate DFA");
        System.out.println("3. Minimized DFA");
        System.out.println("4. Simulation");
        System.out.println("Press any other key to exit...");
        System.out.println("---------------------------------------\n");
    }
}
