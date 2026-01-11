/**
 * Class representing a container with an identifying letter and an associated cost.
 *
 */
public class Container {
    private char letter; // Identificador do contentor (um caractere que representa uma letra)
    private int cost; // Custo associado ao contentor

    /**
     * Constructor to create a container with a specified letter and cost.
     *
     * @param letter Identifying letter of the container.
     * @param cost Cost associated with the container.
     *
     */
    // Construtor que inicializa a letra e o custo do contentor
    public Container(char letter, int cost) {
        this.letter = letter; // Atribui a letra fornecida ao atributo letter
        this.cost = cost; // Atribui o custo fornecido ao atributo cost
    }

    /**
     * Gets the identifying letter of the container.
     *
     * @return The container's letter.
     *
     */
    // Metodo getter para obter a letra do contentor
    public char getLetter() {
        return letter; // Retorna a letra do contentor
    }

    /**
     * Gets the cost associated with the container.
     *
     * @return The container's cost.
     *
     */
    // Metodo getter para obter o custo do contentor
    public int getCost() {
        return cost; // Retorna o custo do contentor
    }

    /**
     * Returns a string representation of the container, showing only its letter.
     *
     * @return A string representing the container's letter.
     *
     */
    // Metodo para representar o contentor como uma string (apenas a letra)
    @Override
    public String toString() {
        return Character.toString(letter); // Converte a letra para uma string e a retorna
    }

    /**
     * Checks if this container is equal to another object based on the letter identifier.
     *
     * @param o The object to compare with.
     * @return {@code true} if the containers have the same letter, {@code false} otherwise.
     *
     */
    // Metodo para comparar se este contentor é igual a outro objeto
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Verifica se o objeto comparado e a mesma instância
        if (o == null || getClass() != o.getClass()) return false; // Verifica se o objeto e nulo ou se não e da mesma classe

        Container container = (Container) o; // Faz um cast do objeto para Container

        // Compara as letras dos contentores
        return letter == container.letter; // Retorna true se as letras forem iguais
    }

    /**
     * Generates a hash code based on the identifying letter of the container.
     *
     * @return The hash code of the container.
     *
     */
    // Metodo para gerar um código hash para o contentor (usado em colecoes)
    @Override
    public int hashCode() {
        return Character.hashCode(letter); // Retorna o codigo hash da letra
    }
}