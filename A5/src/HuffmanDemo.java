import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HuffmanDemo {
    public static void main(String[] args) {
        BinaryTree<Pair> tree = createHuffman();
        String[] encoder = findEncoding(tree);
        Scanner in = new Scanner(System.in);

        System.out.println("Enter a line of text, IN UPPERCASE");
        String input = in.nextLine();

        String encoded = encode(input, encoder);
        System.out.println("Here's your String, encoded:\n" + encoded);

        String decoded = decode(encoded, tree);
        System.out.println("Here's your String, decoded:\n" + decoded);
    }

    private static String encode(String input, String[] encoder) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            //If the char is an uppercase letter (ascii 65 - 90),
            if (input.charAt(i) < 91 && input.charAt(i) > 64) {
                //then encode it using the string at the ascii location in your array
                //A == 65 == encoder[65-65], B == 66 == encoder[66-65], C == 67 == encoder[67-65], etc
                output.append(encoder[input.charAt(i) - 65]);
            }
            //Else, leave as-is and append to String output
            //this is because we are only encoding uppercase letters, nothing else
            else {
                output.append(input.charAt(i));
            }
        }

        return output.toString();
    }

    //This method was provided by Srini
    private static String[] findEncoding(BinaryTree<Pair> bt) {
        String[] result = new String[26];
        findEncoding(bt, result, "");
        return result;
    }

    //This method was provided by Srini
    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix) {
        // test is node/tree is a leaf
        if (bt.getLeft() == null && bt.getRight() == null) {
            a[bt.getData().getValue() - 65] = prefix;
        }
        // recursive calls
        else {
            findEncoding(bt.getLeft(), a, prefix + "0");
            findEncoding(bt.getRight(), a, prefix + "1");
        }
    }

    //Just to make file. This will ask for a filepath until it successfully finds a file at the specified location
    private static Scanner makeScanner() {
        Scanner in = new Scanner(System.in);
        File probabilityFile = null;
        Scanner fileScanner = null;
        // To skip the step where you input the filename, uncomment line below
//        try {fileScanner = new Scanner(new File("LettersProbability.txt"));}catch (FileNotFoundException e) {}

        while (fileScanner == null) {
            try {
                System.out.print("Enter the name of the file with letters and probability (WITH the file extension IE .txt): \n");
                try {
                    String pathname = in.nextLine();
                    probabilityFile = new File(pathname);
                }
                catch (NullPointerException e) {
                    System.out.println("IDK how you got here. Somehow you input a null filename in the console. Fix that");
                }
                fileScanner = new Scanner(probabilityFile);
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found. Try again.");
            }
        }
        return fileScanner;
    }

    public static BinaryTree<Pair> createHuffman() {
        Scanner in = makeScanner();

        System.out.print("Building Huffman Tree: ... ");
        LinkedList<BinaryTree<Pair>> s = new LinkedList<>();
        LinkedList<BinaryTree<Pair>> t = new LinkedList<>();

        //List s assumed to be in ascending order,
        //because input file is assumed to be in ascending order in terms of probability.
        while (in.hasNextLine()) {
            Pair p = new Pair(in.next().charAt(0), in.nextDouble());
            BinaryTree<Pair> tree = new BinaryTree<>();
            tree.setData(p);
            s.add(tree);
        }
        //until list s is empty, take trees from s and combine them, putting into list t
        //the resulting list t will also be in order
        while (!s.isEmpty()) {

            BinaryTree<Pair> a = null, b = null, p, temp = null;

            for (int i = 0; i < 2; i++) {

                //if (t is empty) OR (s isn't empty AND val of s.first() is smaller than that of t.first())
                //take from front of s
                if (t.isEmpty() || (!s.isEmpty() && s.getFirst().getData().compareTo(t.getFirst().getData()) < 0)) {
                    temp = s.getFirst();
                    s.remove(temp);
                }
                //if (s is empty) OR (t isn't empty AND val of t.first() is smaller than that of s.first())
                //take from front of t
                else if (s.isEmpty() || (!t.isEmpty() && s.getFirst().getData().compareTo(t.getFirst().getData()) > 0)) {
                    temp = t.getFirst();
                    t.remove(temp);
                }

                //if 0, set a
                //else, set b
                if (i == 0) {
                    a = temp;
                }
                else {
                    b = temp;
                }
            }
            //combine a and b, with parent value = sum of a value and b value.
            //put newly formed binary tree into list t
            p = new BinaryTree<>();
            //this char field is just a placeholder, so it doesn't matter what char it is
            p.setData(new Pair('!', (a.getData().getProb() + b.getData().getProb())));
            p.setLeft(a);
            p.setRight(b);
            t.add(p);
        }

        /*This is something that, while it works for this purpose, i believe it makes the tree slightly incorrectly
          It should be adding the item to list t at the proper place in order (IE the list t should always be in order,
          where the binary tree with the smallest value of percentage is at the beginning of the list t).
          In this implementation, it doesn't. Perhaps it should be an orderedList? using comparable interface?
          idk, it works and that's good enough for me atm.*/

        //reduce the size of t to 1 by building one big binary tree, with a single reference to the root.
        while (t.size() > 1) {
            BinaryTree<Pair> a, b, p;
            //a and b are just the first 2 elements in List t
            a = t.getFirst();
            t.remove(a);
            b = t.getFirst();
            t.remove(b);

            //create a new tree with probability of sum of children.
            //the char value of this node is irrelevant to the program.
            p = new BinaryTree<>();
            p.setData(new Pair('~', (a.getData().getProb() + b.getData().getProb())));
            p.setLeft(a);
            p.setRight(b);
            t.add(p);
        }
        System.out.print("Complete! \n");
        return t.getFirst();
    }

    public static String decode(String s, BinaryTree<Pair> root) {
        StringBuilder output = new StringBuilder();
        BinaryTree<Pair> pointer = root;

        for (int i = 0; i < s.length(); i++) {
            //if the char isn't '0' or '1', then don't decode it, just add it to the output string.
            if (!(s.charAt(i) == '0' || s.charAt(i) == '1')) {
                output.append(s.charAt(i));
                if(pointer != root){
                   return "ERROR: Something here broke. Theres a non-encoded char in the middle of an encoded char"
                            + " \nThis means this isnt working properly";
                }
            }
            //0 = left, 1 = right
            else {
                if (s.charAt(i) == '0') {
                    pointer = pointer.getLeft();
                }
                else if (s.charAt(i) == '1') {
                    pointer = pointer.getRight();
                }
            }

            //if a tree has no children, then it is the leaf that has the value-pair we want
            if(pointer.getLeft() == null && pointer.getRight() == null) {
                //add the char of pointed node to the output string, and pointer gets reset back to root
                output.append(pointer.getData().getValue());
                pointer = root;
            }

        }
        return output.toString();
    }
}
