import java.util.*;
/**
 * Class representing the state of a container configuration, implementing the Ilayout interface.
 */
public class ContainerState implements Ilayout {
    private final List<List<Container>> stacks;
    private int moveCost;
    private Container lastMovedContainer;

    /**
     * Constructor that initializes the state with a list of container stacks and a move cost of zero.
     *
     * @param stacks List of container stacks.
     */
    public ContainerState(List<List<Container>> stacks) {
        this(stacks, 0);
    }

    /**
     * Constructor that initializes the state with a list of container stacks and a specified move cost.
     *
     * @param stacks List of container stacks.
     * @param moveCost Cost associated with the move.
     */
    public ContainerState(List<List<Container>> stacks, int moveCost) {
        this.stacks = new ArrayList<>();
        for (List<Container> stack : stacks) {
            this.stacks.add(new ArrayList<>(stack));
        }
        this.moveCost = moveCost;
    }

    /**
     * Generates a list of child states from the current state by applying possible container moves.
     *
     * @return List of child states.
     */
    @Override
    public List<Ilayout> children() {
        List<Ilayout> children = new ArrayList<>();
        Set<ContainerState> generated = new HashSet<>();

        for (int i = 0; i < stacks.size(); i++) {
            List<Container> fromStack = stacks.get(i);

            if (!fromStack.isEmpty()) {
                ContainerState newStacks = this.cloneStacks();
                newStacks.moveToGround(i);

                if (generated.add(newStacks)) {
                    newStacks.moveCost = newStacks.lastMovedContainer.getCost();
                    children.add(newStacks);
                }

                for (int j = 0; j < stacks.size(); j++) {
                    if (i != j) {
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
     * Moves the top container from a specified stack to the ground as a new stack.
     *
     * @param stackIndex The index of the stack from which to move the container.
     */
    public void moveToGround(int stackIndex) {
        Container container = stacks.get(stackIndex).remove(stacks.get(stackIndex).size() - 1);
        lastMovedContainer = container;

        if (stacks.get(stackIndex).isEmpty()) {
            stacks.remove(stackIndex);
        }

        List<Container> newStack = new ArrayList<>();
        newStack.add(container);
        stacks.add(newStack);

        sortStacks();
    }

    /**
     * Moves the top container from one stack to another specified stack.
     *
     * @param fromStack The index of the stack to move from.
     * @param toStack The index of the stack to move to.
     * @return {@code true} if the move was successful; {@code false} otherwise.
     */
    public boolean moveToStack(int fromStack, int toStack) {
        if (stacks.get(fromStack).isEmpty() || fromStack == toStack) {
            return false;
        }

        Container container = stacks.get(fromStack).remove(stacks.get(fromStack).size() - 1);
        lastMovedContainer = container;

        stacks.get(toStack).add(container);

        if (stacks.get(fromStack).isEmpty()) {
            stacks.remove(fromStack);
            if (fromStack < toStack) {
                toStack--;
            }
        }
        sortStacks();
        return true;
    }

    /**
     * Checks if this container state is equal to another object.
     *
     * @param o The object to compare with.
     * @return {@code true} if the object is equal to this container state; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerState)) return false;
        ContainerState that = (ContainerState) o;
        if (this.stacks.size() != that.stacks.size()) return false;

        // Ordena as pilhas nas duas instâncias
        this.sortStacks();
        that.sortStacks();

        // Compara as pilhas diretamente
        for (int i = 0; i < this.stacks.size(); i++) {
            List<Container> thisStack = this.stacks.get(i);
            List<Container> thatStack = that.stacks.get(i);

            if (thisStack.size() != thatStack.size()) return false;
            for (int j = 0; j < thisStack.size(); j++) {
                if (thisStack.get(j).getLetter() != thatStack.get(j).getLetter()) {
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
     */
    @Override
    public int hashCode() {
        return Objects.hash(stacks);
    }

    /**
     * Returns a string representation of the container stacks.
     *
     * @return String representation of the stacks.
     */
    @Override
    public String toString() {
        // Ordena as pilhas
        sortStacks();

        StringBuilder sb = new StringBuilder();
        for (List<Container> stack : stacks) {
            sb.append('[');
            for (int i = 0; i < stack.size(); i++) {
                sb.append(stack.get(i).getLetter());
                if (i < stack.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(']');
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    /**
     * Creates a deep copy of the container stacks.
     *
     * @return A new list of stacks that is a deep copy of the original stacks.
     */
    private List<List<Container>> copyStacks() {
        List<List<Container>> newStacks = new ArrayList<>();
        for (List<Container> stack : this.stacks) {
            newStacks.add(new ArrayList<>(stack));
        }
        return newStacks;
    }

    /**
     * Creates a cloned ContainerState instance with copied stacks.
     *
     * @return A new ContainerState instance that is a clone of the current state.
     */
    public ContainerState cloneStacks() {
        List<List<Container>> newStacks = copyStacks();
        ContainerState clonedStacks = new ContainerState(newStacks);
        return clonedStacks;
    }

    /**
     * Sorts the container stacks based on the first container's letter in each stack.
     */
    public void sortStacks() {
        stacks.sort(Comparator.comparing(stack -> stack.isEmpty() ? ' ' : stack.get(0).getLetter()));
    }

    /**
     * Checks if the current container state is the goal state.
     *
     * @param l The layout to compare to.
     * @return {@code true} if this container state is the goal state; {@code false} otherwise.
     */
    @Override
    public boolean isGoal(Ilayout l) {
        return this.equals(l);
    }

    /**
     * Gets the cumulative move cost associated with reaching this state.
     *
     * @return The move cost of this state.
     */
    @Override
    public double getG() {
        return this.moveCost;
    }

    /**
     * Parses input to convert it into a list of container stacks.
     *
     * @param input The string input representing stacks.
     * @param withCost {@code true} if the cost should be considered; {@code false} otherwise.
     * @return A new ContainerState created from the input string.
     */
    // Método auxiliar para converter a entrada em uma lista de pilhas de contentores
    public static ContainerState parseInput(String input, boolean withCost) {
        List<List<Container>> stacks = new ArrayList<>();
        String[] stackStrings = input.split(" ");

        for (String stackString : stackStrings) {
            List<Container> stack = new ArrayList<>();
            for (int i = 0; i < stackString.length(); i++) {
                char id = stackString.charAt(i);
                int cost = 0;

                if (withCost && i + 1 < stackString.length()) {
                    cost = Character.getNumericValue(stackString.charAt(i + 1));
                    i++; // Incrementa para pular o número de custo
                }
                stack.add(new Container(id, cost));
            }
            stacks.add(stack);
        }

        return new ContainerState(stacks);
    }
}
