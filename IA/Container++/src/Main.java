import java.util.*;
/**
 * Main class that runs the program to solve the Containers++ problem using the Best-First search algorithm.
 * It reads the initial and goal configurations of containers and attempts to find the optimal solution.
 *
 */
public class Main {
    /**
     * Main method that reads the initial and goal configurations of containers and solves the problem using the Best-First search algorithm.
     * The method expects two lines of input from the user: the initial configuration (with container costs) and the goal configuration (without costs).
     * If a solution is found, it displays the goal and the total cost.
     *
     * @param args Command-line arguments (not used).
     *
     */
    public static void main(String[] args) {
        // Leitura da configuracao inicial com os custos
        Scanner sc = new Scanner(System.in);

        String initialConfig = sc.nextLine();

        String finalConfig = sc.nextLine();

        ContainerState initialBoard = new ContainerState (initialConfig, true);

        ContainerState goalBoard = new ContainerState (finalConfig, false);
        // Resolver o problema usando BestFirst
        BestFirst solver = new BestFirst();
        Iterator<BestFirst.State> solution = solver.solve(initialBoard, goalBoard);

        if (solution == null) {
            System.out.println("no solution found");
        } else {
            while (solution.hasNext()) {
                BestFirst.State lastState = solution.next();

                if (!solution.hasNext()) {
                    System.out.println(lastState);
                    System.out.println();
                    System.out.println((int)lastState.getG());
                }
            }
        }
        sc.close();
    }
}