import java.util.*;
/**
 * Class implementing the Best-First search algorithm for solving the container problem.
 *
 */
public class BestFirst {
    /** Priority queue for open states to be evaluated by the algorithm. */
    protected Queue<State> abertos;
    /** Map to store closed states that have already been evaluated. */
    private Map<Ilayout, State> fechados;
    /** The current state being processed by the search algorithm. */
    private State actual;
    /** The goal layout that the algorithm aims to reach. */
    private Ilayout objective;

    /**
     * Nested class representing a search state in the algorithm.
     *
     */
    static class State {
        final Ilayout layout;
        private final State father;
        private final double g;

        /**
         * Constructs a State with a layout and a parent state.
         *
         * @param l The layout or configuration of the state.
         * @param n The parent state from which this state is derived.
         *
         */
        public State(Ilayout l, State n) {
            layout = l;
            father = n;

            if (father != null)
                g = father.g + l.getG();
            else g = 0.0f;

        }

        /**
         * Gets the cumulative cost of moves to reach this state.
         *
         * @return The total move cost of this state.
         *
         */
        public double getG() {
            return g;
        }

        /**
         * Gets the layout associated with this state.
         *
         * @return The layout of this state.
         *
         */
        public Ilayout getLayout() {
            return layout;
        }

        /**
         * Returns a string representation of the state.
         *
         * @return A string representing the layout of this state.
         *
         */
        @Override
        public String toString() {
            return layout.toString();
        }

        /**
         * Returns the hash code for this state.
         * The hash code is based on the string representation of the layout, ensuring that states with the same layout have the same hash code.
         *
         * @return The hash code value for this state.
         *
         */
        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        /**
         * Compares this state to another object for equality.
         * Two states are considered equal if they are of the same class and have identical layouts.
         *
         * @param o The object to compare with this state.
         * @return {@code true} if this state has the same layout as the specified object; {@code false} otherwise.
         *
         */
        @Override
        public boolean equals(Object o) {
            if (o==null) return false;
            if (this.getClass() != o.getClass()) return false;
            State n = (State) o;
            return this.layout.equals(n.layout);
        }
    }

    /**
     * Generates a list of successor states for the given state.
     *
     * @param n The state for which successors are generated.
     * @return A list of successors states.
     *
     */
    private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.layout.children();

        for (Ilayout e : children) {
            if (n.father == null || !e.equals(n.father.layout)) {
                sucs.add(new State(e, n));
            }
        }
        return sucs;
    }

    /**
     * Solves the container problem using the Best-First search algorithm.
     *
     * @param s The initial layout.
     * @param goal The goal layout.
     * @return An iterator of the solution path, or {@code null} if no solution is found.
     *
     */
    final public Iterator<State> solve(Ilayout s, Ilayout goal) {
        //long startTime = System.currentTimeMillis();
        objective = goal;
        abertos = new PriorityQueue<>(10, (s1, s2) -> {
            double f1 = s1.getG() + s1.getLayout().heuristic(objective);
            double f2 = s2.getG() + s2.getLayout().heuristic(objective);
            return Double.compare(f1, f2);  // Compare states by their f-values (g + h)
        });
        fechados = new HashMap<>();
        abertos.add(new State(s, null));
        List<State> sucs;


        while(!abertos.isEmpty()) {
            actual = abertos.poll();

            if (actual.layout.isGoal(objective)) {
                List<State> solutions = new ArrayList<>();
                State temp = actual;
                for (; temp.father != null; temp = temp.father) {
                    solutions.add(0, temp);
                }
                solutions.add(0, temp);
                //System.out.println("filhos: " + fechados.size());
                //long endTime = System.currentTimeMillis();
                //long totalTime = endTime - startTime;
                //System.out.println("tempo total : " + totalTime);
                return solutions.iterator();
            }
            fechados.put(actual.layout, actual);
            sucs = this.sucessores(actual);
            for (State successor : sucs) {
                if (!fechados.containsKey(successor.layout)) {
                    abertos.add(successor);
                }
            }
        }
        return null;
    }
}