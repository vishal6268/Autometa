package lab1;
import java.util.ArrayList;
import java.util.List;

public class SimpleAutomata {

    public static void main(String[] args) {
        char[] alphabet = {'a', 'b'};
        int n = 3;

        List<String> generatedStrings = new ArrayList<>();
        if (n >= 0) {
            generatedStrings.add("");
        }

        for (int currentLength = 1; currentLength <= n; currentLength++) {
            char[] currentStringChars = new char[currentLength];
            generateStringsRecursive(alphabet, currentLength, 0, currentStringChars, generatedStrings);
        }

        System.out.println("Generated Strings (length <= " + n + " over {a, b}):");
        for (String s : generatedStrings) {
            System.out.println(s.isEmpty() ? "(empty string)" : s);
        }

        System.out.println("\nLanguage L = {s | s contains at least one 'a'}");
        System.out.println("\nChecking Membership for Generated Strings:");
        for (String s : generatedStrings) {
            // boolean isMember = s.contains("a");
            System.out.printf("String \"%s\" %s to Language L.%n",
                    s.isEmpty() ? "(empty string)" : s,
                    // isMember ? "valid" : "not valid");
                     s.contains("a")?"valid":"not valid");
        }
    }

    private static void generateStringsRecursive(
            char[] alphabet,
            int targetLength,
            int currentIndex,
            char[] currentStringChars,
            List<String> result) {

        if (currentIndex == targetLength) {
            result.add(new String(currentStringChars));
            return;
        }

        for (char character : alphabet) {
            currentStringChars[currentIndex] = character;
            generateStringsRecursive(alphabet, targetLength, currentIndex + 1, currentStringChars, result);
        }
    }
}