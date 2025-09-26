import java.util.*;

// This class represents the NFA. We'll keep it simple for clarity.
class NFA {
    // Each transition maps from a state and an input character to a set of possible next states.
    public final Map<Integer, Map<Character, Set<Integer>>> transitions = new HashMap<>();
    public final Set<Integer> states = new HashSet<>();
    public final Set<Character> alphabet = new HashSet<>();
    public int startState;
    public final Set<Integer> finalStates = new HashSet<>();

    public NFA() {
        // A simple example NFA with epsilon transitions.
        // It accepts strings containing 'a' followed by 'b' anywhere.
        // NFA states: 0, 1, 2, 3
        states.addAll(Arrays.asList(0, 1, 2, 3));
        startState = 0;
        finalStates.add(3);
        alphabet.addAll(Arrays.asList('a', 'b'));

        // Define transitions for the example NFA
        // State 0 transitions
        addTransition(0, 'a', 0);
        addTransition(0, 'b', 0);
        addTransition(0, (char) 0, 1); // Using (char) 0 to represent epsilon

        // State 1 transitions
        addTransition(1, 'a', 2);

        // State 2 transitions
        addTransition(2, (char) 0, 3); // Epsilon transition from 2 to 3
    }

    // Helper method to add a transition
    public void addTransition(int from, char onChar, int to) {
        transitions.computeIfAbsent(from, k -> new HashMap<>())
                   .computeIfAbsent(onChar, k -> new HashSet<>())
                   .add(to);
    }
}

// This class represents the converted DFA.
class DFA {
    // The DFA states are sets of NFA states.
    // The transition function maps a set of NFA states and a character to another set.
    public final Map<Set<Integer>, Map<Character, Set<Integer>>> transitions = new HashMap<>();
    public final Set<Set<Integer>> states = new HashSet<>();
    public final Set<Character> alphabet = new HashSet<>();
    public Set<Integer> startState;
    public final Set<Set<Integer>> finalStates = new HashSet<>();
}

public class NFAToDFAConverter {

    public static void main(String[] args) {
        System.out.println("Starting NFA to DFA Conversion...");
        NFA nfa = new NFA();
        DFA dfa = convertNfaToDfa(nfa);
        printDFA(dfa);
    }

    // The main conversion method using the Subset Construction algorithm.
    public static DFA convertNfaToDfa(NFA nfa) {
        DFA dfa = new DFA();
        dfa.alphabet.addAll(nfa.alphabet);
        
        // Step 1: Find the initial DFA state.
        // It's the epsilon-closure of the NFA's start state.
        Set<Integer> startClosure = epsilonClosure(nfa, Set.of(nfa.startState));
        dfa.startState = startClosure;
        dfa.states.add(startClosure);

        // A queue to hold DFA states that need to be processed
        Queue<Set<Integer>> unprocessedStates = new LinkedList<>();
        unprocessedStates.add(startClosure);

        // Step 2: Loop until all new DFA states have been processed.
        while (!unprocessedStates.isEmpty()) {
            Set<Integer> currentDFAState = unprocessedStates.poll();

            // Step 3: For each character in the alphabet, calculate the next state.
            for (char symbol : dfa.alphabet) {
                // Find all NFA states reachable from the current set of states
                // by taking the transition on the given symbol.
                Set<Integer> nextNFAStates = move(nfa, currentDFAState, symbol);

                // Find the epsilon-closure of the new states. This is our next DFA state.
                Set<Integer> nextDFAState = epsilonClosure(nfa, nextNFAStates);
                
                // If the next DFA state is not a new one, we add it and mark it for processing.
                if (!nextDFAState.isEmpty() && !dfa.states.contains(nextDFAState)) {
                    dfa.states.add(nextDFAState);
                    unprocessedStates.add(nextDFAState);
                }
                
                // Step 4: Add the transition to the DFA.
                dfa.transitions.computeIfAbsent(currentDFAState, k -> new HashMap<>())
                               .put(symbol, nextDFAState);
            }
        }
        
        // Step 5: Determine the final states of the DFA.
        // A DFA state is final if it contains at least one of the original NFA's final states.
        for (Set<Integer> dfaState : dfa.states) {
            for (int nfaState : dfaState) {
                if (nfa.finalStates.contains(nfaState)) {
                    dfa.finalStates.add(dfaState);
                    break;
                }
            }
        }
        
        return dfa;
    }

    // Helper method to compute the epsilon-closure of a set of NFA states.
    private static Set<Integer> epsilonClosure(NFA nfa, Set<Integer> states) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> closure = new HashSet<>(states);
        
        for (int state : states) {
            stack.push(state);
        }

        while (!stack.isEmpty()) {
            int currentState = stack.pop();
            // Epsilon transitions are marked with (char) 0 in our example
            Set<Integer> epsilonDestinations = nfa.transitions.getOrDefault(currentState, new HashMap<>())
                                                            .getOrDefault((char) 0, new HashSet<>());
            
            for (int nextState : epsilonDestinations) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.push(nextState);
                }
            }
        }
        return closure;
    }

    // Helper method to find all NFA states reachable from a set of states on a given symbol.
    private static Set<Integer> move(NFA nfa, Set<Integer> fromStates, char symbol) {
        Set<Integer> result = new HashSet<>();
        for (int state : fromStates) {
            Set<Integer> destinations = nfa.transitions.getOrDefault(state, new HashMap<>())
                                                      .getOrDefault(symbol, new HashSet<>());
            result.addAll(destinations);
        }
        return result;
    }

    // Helper method to print the DFA in a readable format.
    private static void printDFA(DFA dfa) {
        System.out.println("\n--- Converted DFA ---");
        
        // A map to assign simple names to the complex DFA states for easier viewing
        Map<Set<Integer>, String> stateNames = new HashMap<>();
        int nameCounter = 0;
        for (Set<Integer> state : dfa.states) {
            String name = "{" + state.toString().substring(1, state.toString().length() - 1) + "}";
            stateNames.put(state, "S" + nameCounter++ + " " + name);
        }

        System.out.println("DFA States:");
        for (Set<Integer> state : dfa.states) {
            String name = stateNames.get(state);
            System.out.println("  " + name + (dfa.finalStates.contains(state) ? " (Final State)" : ""));
        }

        System.out.println("\nStart State: " + stateNames.get(dfa.startState));

        System.out.println("\nTransitions:");
        for (Map.Entry<Set<Integer>, Map<Character, Set<Integer>>> entry : dfa.transitions.entrySet()) {
            Set<Integer> fromState = entry.getKey();
            for (Map.Entry<Character, Set<Integer>> transition : entry.getValue().entrySet()) {
                char onChar = transition.getKey();
                Set<Integer> toState = transition.getValue();
                if (toState.isEmpty()) continue; // Ignore transitions to the empty set
                System.out.println("  " + stateNames.get(fromState) + " --(" + onChar + ")--> " + stateNames.get(toState));
            }
        }
    }
}
