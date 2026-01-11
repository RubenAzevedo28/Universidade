import java.util.*;
/**
 * Main class that runs the program to solve the container problem using the Best-First algorithm.
 */
public class Main {
    /**
     * Main method that reads the initial and goal configurations of containers and solves the problem using the Best-First algorithm.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Leitura da configuração inicial com os custos
        String initialInput = scanner.nextLine().trim();

        // Leitura da configuração final sem os custos
        String goalInput = scanner.nextLine().trim();

        // Criação das configurações iniciais e finais
        ContainerState initialConfig = ContainerState.parseInput(initialInput, true);
        ContainerState goalConfig = ContainerState.parseInput(goalInput, false);

        // Resolver o problema usando BestFirst
        BestFirst solver = new BestFirst();
        Iterator<BestFirst.State> solution = solver.solve(initialConfig, goalConfig);

        if (solution == null) {
            System.out.println("no solution found");
        } else {
            while (solution.hasNext()) {
                BestFirst.State lastState = solution.next();
                System.out.println(lastState);
                System.out.println();

                if (!solution.hasNext()) {
                    System.out.println((int)lastState.getG());
                }
            }
        }
        scanner.close();
    }
}
