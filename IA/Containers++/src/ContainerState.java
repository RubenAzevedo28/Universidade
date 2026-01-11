import java.util.*;
/**
 * Class representing the state of a container configuration, implementing the Ilayout interface.
 *
 */
public class ContainerState implements Ilayout {
    private List<List<Container>> stacks;
    private int moveCost;
    private Container lastMovedContainer;

    /**
     * Constructs a ContainerState based on the input configuration string.
     *
     * @param Config The string representing the container configuration.
     * @param withCost {@code true} if costs are included in the configuration, {@code false} otherwise.
     *
     */
    public ContainerState(String Config, boolean withCost) {
        stacks = new ArrayList<>(52);
        for (int i = 0; i < 52; i++) {
            stacks.add(new ArrayList<Container>());
        }
        // Realiza a configuração da pilha depois que o objeto e construido
        if(withCost){
            parseConfigInicial(Config);
        }else {
            parseConfigFinal(Config);
        }
    }

    /**
     * Parses the initial configuration with costs.
     *
     * @param initialConfig The configuration string for the initial state.
     *
     */
    private void parseConfigInicial(String initialConfig) {
        String[] stackStrings = initialConfig.split(" ");

        for (String stackStr : stackStrings) {
            List<Container> stack = new ArrayList<>();

            for (int i = 0; i < stackStr.length(); i += 2) {
                char id = stackStr.charAt(i);
                int cost = Character.getNumericValue(stackStr.charAt(i + 1));
                stack.add(new Container(id, cost));
            }

            // Determina o indice da pilha usando o ID do container de base
            if (!stack.isEmpty()) {
                char bottomContainerId = stack.get(0).getLetter();
                int index = getStackIndex(bottomContainerId);
                stacks.set(index, stack);
            }
        }
    }

    /**
     * Parses the goal configuration without costs.
     *
     * @param finalConfig The configuration string for the goal state.
     *
     */
    private void parseConfigFinal(String finalConfig) {
        String[] stackStrings = finalConfig.split(" ");

        for (String stackStr : stackStrings) {
            List<Container> stack = new ArrayList<>();
            for (int i = 0; i < stackStr.length(); i++) {
                char id = stackStr.charAt(i);
                stack.add(new Container(id, 0));
            }

            // Determina o indice da pilha usando o ID do container de base
            if (!stack.isEmpty()) {
                char bottomContainerId = stack.get(0).getLetter();
                int index = getStackIndex(bottomContainerId);
                stacks.set(index, stack);
            }
        }
    }

    /**
     * Constructs a ContainerState based on a list of container stacks.
     *
     * @param stacks A list of stacks containing containers for this state.
     *
     */
    public ContainerState(List<List<Container>> stacks){
        this.stacks = stacks;
    }

    /**
     * Generates and returns a list of child states from the current state by applying possible moves.
     *
     * @return List of child states.
     *
     */
    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        Set<ContainerState> generated = new HashSet<>();

        for (int i = 0; i < stacks.size(); i++) {
            List<Container> fromStack = stacks.get(i);


            if (!fromStack.isEmpty()) {
                Container topContainer = fromStack.get(fromStack.size()-1);
                int index = getStackIndex(topContainer.getLetter());
                if (i != index){
                    ContainerState newStacks = this.cloneStacks();
                    newStacks.moveToGround(i);

                    if (generated.add(newStacks)) {
                        newStacks.moveCost = newStacks.lastMovedContainer.getCost();
                        children.add(newStacks);
                    }
                }

                for (int j = 0; j < stacks.size(); j++) {
                    if (i != j && j != index && !stacks.get(j).isEmpty()) {
                        ContainerState movedStacks = this.cloneStacks();
                        if (movedStacks.moveToStack(i, j)) {
                            if (generated.add(movedStacks)) {
                                movedStacks.moveCost = movedStacks.lastMovedContainer.getCost();
                                children.add(movedStacks);
                            }
                        }
                    }
                }
            }
        }
        return children;
    }

    /**
     * Moves a container from the specified stack to ground .
     *
     * @param fromStackIndex The index of the stack to move from.
     *
     */
    public void moveToGround(int fromStackIndex) {
        List<Container> fromStack = stacks.get(fromStackIndex);
        if (fromStack.isEmpty()) {
            return;
        }
        Container container = fromStack.remove(fromStack.size() - 1);
        lastMovedContainer = container;
        // Adiciona o container a pilha correspondente a sua letra
        int toStackIndex = getStackIndex(container.getLetter());
        stacks.get(toStackIndex).add(container);
    }

    /**
     * Moves a container from one stack to another.
     *
     * @param fromStackIndex The index of the stack to move from.
     * @param toStackIndex The index of the stack to move to.
     * @return {@code true} if the move is successful, {@code false} otherwise.
     *
     */
    public boolean moveToStack(int fromStackIndex, int toStackIndex) {
        if (fromStackIndex == toStackIndex) {
            return false;
        }
        List<Container> fromStack = stacks.get(fromStackIndex);
        if (fromStack.isEmpty()) {
            return false;
        }
        Container container = fromStack.remove(fromStack.size() - 1);
        lastMovedContainer = container;
        stacks.get(toStackIndex).add(container);
        return true;
    }

    /**
     * Calculates the heuristic cost to the goal state.
     *
     * @param goal The goal state for heuristic estimation.
     * @return The estimated cost to reach the goal state.
     *
     */
    @Override
    public double heuristic(Ilayout goal) {
        ContainerState goalConfig = (ContainerState) goal;
        double heuristic = 0;

        // Usa o tamanho maximo entre as duas configuracoes de pilhas
        int maxStacks = 52 ;

        for (int i = 0; i < maxStacks; i++) {
            List<Container> currentStack = this.stacks.get(i);
            List<Container> goalStack =  i < goalConfig.stacks.size() ? goalConfig.stacks.get(i) : null;

            // Se ambas as pilhas estao vazias, nao ha custo
            if (currentStack.isEmpty() && goalStack.isEmpty()) {
                continue;
            }

            // Se uma pilha esta vazia e a outra nao, conta o custo dos containers
            if (currentStack.isEmpty() || goalStack.isEmpty()) {
                List<Container> nonEmptyStack = currentStack.isEmpty() ? goalStack : currentStack;
                for (int j = 0; j < nonEmptyStack.size(); j++) {
                    Container container = nonEmptyStack.get(j);
                    heuristic += container.getCost();
                    if (j + 1 < nonEmptyStack.size()) {
                        heuristic += nonEmptyStack.get(j + 1).getCost() * 0.5; // Custo adicional para containers que seguem
                    }
                }
                continue;
            }

            // Compara as alturas das pilhas
            int maxHeight = Math.max(currentStack.size(), goalStack.size());
            for (int j = 0; j < maxHeight; j++) {
                if (j < currentStack.size() && j < goalStack.size()) {
                    Container currentContainer = currentStack.get(j);
                    Container goalContainer = goalStack.get(j);
                    if (!currentContainer.equals(goalContainer)) {
                        heuristic += currentContainer.getCost();
                        if (j + 1 < currentStack.size()) {
                            heuristic += currentStack.get(j + 1).getCost() * 0.85; // Custo adicional para containers que seguem
                        }
                    }
                } else if (j < currentStack.size()) {
                    // Containers adicionais na pilha atual
                    Container currentContainer = currentStack.get(j);
                    heuristic += currentContainer.getCost();
                    if (j + 1 < currentStack.size()) {
                        heuristic += currentStack.get(j + 1).getCost() * 0.95; // Custo adicional para containers que seguem
                    }
                } else if (j < goalStack.size()) {
                    // Containers adicionais na pilha do objetivo
                    Container goalContainer = goalStack.get(j);
                    heuristic += goalContainer.getCost();
                    if (j + 1 < goalStack.size()) {
                        heuristic += goalStack.get(j + 1).getCost() * 0.95; // Custo adicional para containers que seguem
                    }
                }
            }
        }

        return heuristic;
    }

    /**
     * Checks if this container state is equal to another object.
     *
     * @param o The object to compare with.
     * @return {@code true} if the object is equal to this container state; {@code false} otherwise.
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerState)) return false;
        ContainerState that = (ContainerState) o;

        for (int i = 0; i < 52; i++) {
            List<Container> thisStack = this.stacks.get(i);
            List<Container> thatStack = that.stacks.get(i);

            if (thisStack.isEmpty() && thatStack.isEmpty()) {
                continue;
            }
            if (thisStack.isEmpty() || thatStack.isEmpty()) {
                return false;
            }
            if (thisStack.size() != thatStack.size()) {
                return false;
            }
            for (int j = 0; j < thisStack.size(); j++) {
                if (!thisStack.get(j).equals(thatStack.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Generates a hash code for the container state based on its stacks.
     *
     * @return Hash code of the container state.
     *
     */
    @Override
    public int hashCode() {
        return Objects.hash(stacks);
    }

    /**
     * Returns a string representation of the container stacks.
     *
     * @return A string representing the container stacks.
     *
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 52; i++) {
            List<Container> stack = stacks.get(i);
            if (!stack.isEmpty()) {
                sb.append('[');
                for (int j = 0; j < stack.size(); j++) {
                    sb.append(stack.get(j).getLetter());
                    if (j < stack.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(']');
                sb.append('\n');
            }
        }
        return sb.toString().trim();
    }

    /**
     * Creates a copy of the current container stacks.
     *
     * @return A deep copy of the container stacks.
     *
     */
    private List<List<Container>> copyStacks() {
        List<List<Container>> newStacks = new ArrayList<>(52);
        for (int i = 0; i < 52; i++) {
            List<Container> stack = this.stacks.get(i);
            newStacks.add(new ArrayList<>(stack));
        }
        return newStacks;
    }

    /**
     * Creates a cloned ContainerState instance with copied stacks.
     *
     *  @return A new ContainerState with a deep copy of the current stacks.
     *
     */
    public ContainerState cloneStacks() {
        List<List<Container>> newStacks = copyStacks();
        ContainerState clonedState = new ContainerState(newStacks);
        clonedState.lastMovedContainer = this.lastMovedContainer;
        return clonedState;
    }

    /*
    public void sortStacks() {
        stacks.sort(Comparator.comparing(stack -> stack.isEmpty() ? ' ' : stack.get(0).getLetter()));
    }*/

    /**
     * Checks if this state matches the goal state.
     *
     * @param l The layout to compare against.
     * @return {@code true} if this state matches the goal, {@code false} otherwise.
     *
     */
    @Override
    public boolean isGoal(Ilayout l) {
        return this.equals(l);
    }

    /**
     * Gets the cost of the last move applied to this state.
     *
     * @return The cost of the last move.
     *
     */
    @Override
    public double getG() {
        return this.moveCost;
    }

    /**
     * Gets the stack index based on the container's identifier.
     *
     * @param c The identifier of the container.
     * @return The stack index corresponding to the identifier.
     * @throws IllegalArgumentException if the identifier is invalid.
     *
     */
    // Metodo auxiliar para converter a entrada em uma lista de pilhas de contentores
    private static int getStackIndex(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';  // Indices de 0 a 25 para letras maiusculas
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 26; // Indices 26 a 51 para letras minusculas
        } else {
            throw new IllegalArgumentException("Invalid letter: " + c);
        }
    }
}