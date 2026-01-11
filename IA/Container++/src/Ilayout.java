import java.util.*;
/**
 * Interface representing a layout or state in the search problem.
 *
 */
public interface Ilayout {
    /**
     * Generates and returns the children of the current state.
     *
     * @return a list of child states derived from the current state.
     *
     */
    List<Ilayout> children();

    /**
     * Checks if the current state is the goal state.
     *
     * @param l The target state to compare against.
     * @return {@code true} if the current state matches the target state, {@code false} otherwise.
     *
     */
    boolean isGoal(Ilayout l);

    /**
     * Gets the cost of moving from a previous configuration to the current configuration.
     *
     * @return The cost for reaching the current state.
     *
     */
    double getG();

    /**
     * Calculates an estimated cost (heuristic) from the current state to the goal state.
     *
     * @param goal The goal state for heuristic estimation.
     * @return The estimated cost to reach the goal.
     *
     */
    double heuristic(Ilayout goal);
}