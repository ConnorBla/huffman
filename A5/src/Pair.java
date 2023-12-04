public class Pair implements Comparable<Pair> {
    // declare all required fields
    private final char value;
    private final double prob;

    //constructor
    public Pair(char val, double prob) {
        value = val;
        this.prob = prob;
    }
    //getters

    public char getValue() {
        return value;
    }

    public double getProb() {
        return prob;
    }

    //toString


    @Override
    public String toString() {
        return "Pair{" +
                "value = " + value +
                ", prob = " + prob +
                '}';
    }

    /**
     * The compareTo method overrides the compareTo method of the
     * Comparable interface.
     */
    @Override
    public int compareTo(Pair p) {
        return Double.compare(this.getProb(), p.getProb());
    }
}
