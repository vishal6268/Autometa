package lab1;
import java.util.*;

class Automata1 {
    String c[];

    public void readAlphabet(Scanner sc1) {
        int n;
            System.out.print("Enter number of alphabets: ");
            n = Integer.parseInt(sc1.next());
            c = new String[n];
            for (int i = 0; i < n; i++) {
                System.out.println("Enter Alphabets: ");
                c[i] = sc1.next();
            }
        System.out.println("Alphabets entered:");
        for (int i = 0; i < n; i++) {
            System.out.println(c[i]);
        }
    }

    public List<String> generateStrings(int l) {
        List<String> allStrings = new ArrayList<>();
        for (int i = 1; i <= l; i++) {
            generateStringOfLength(i, "", allStrings);
        }
        return allStrings;
    }

    public void generateStringOfLength(int len, String curr, List<String> str) {
        if (len == 0) {
            str.add(curr);
            return;
        }
        for (String ch : c)
            generateStringOfLength(len - 1, curr + ch, str);
    }

    public boolean isValid(String str) {
        return str.contains("a");
    }
}

public class AutomataLab1{
    public static void main(String[] s) {
        Automata1 a = new Automata1();
        Scanner sc = new Scanner(System.in);
        a.readAlphabet(sc);
 
        int m;
            System.out.println("Enter the length of Strings: ");
            m = sc.nextInt();
        List<String> allStr = a.generateStrings(m);

        System.out.println("\n\nAll the generated Strings are:\n\nSTRING\tVALIDITY\n----------------------\n");
        for (String str : allStr) {
            System.out.print(str + "\t" );
            
            if (a.isValid(str))
                System.out.println("Valid");
            else 
                System.out.println("Invalid");
        }
        sc.close();

    }
}