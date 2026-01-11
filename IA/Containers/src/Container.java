/**
 * Class representing a container with an identifying letter and an associated cost.
 */
public class Container {
    private char letter; // Identificador do contentor (um caractere que representa uma letra)
    private int cost; // Custo associado ao contentor

    /**
     * Constructor to create a container with a specified letter and cost.
     *
     * @param letter Identifying letter of the container.
     * @param cost Cost associated with the container.
     */
    // Construtor que inicializa a letra e o custo do contentor
    public Container(char letter, int cost) {
        this.letter = letter; // Atribui a letra fornecida ao atributo letter
        this.cost = cost; // Atribui o custo fornecido ao atributo cost
    }

    /**
     * Retrieves the identifying letter of the container.
     *
     * @return The container's letter.
     */
    // Método getter para obter a letra do contentor
    public char getLetter() {
        return letter; // Retorna a letra do contentor
    }

    /**
     * Retrieves the cost associated with the container.
     *
     * @return The container's cost.
     */
    // Método getter para obter o custo do contentor
    public int getCost() {
        return cost; // Retorna o custo do contentor
    }

    /**
     * Represents the container as a string, containing only the identifying letter.
     *
     * @return String containing the container's letter.
     */
    // Método para representar o contentor como uma string (apenas a letra)
    @Override
    public String toString() {
        return Character.toString(letter); // Converte a letra para uma string e a retorna
    }

    /**
     * Compares this container to another object to check for equality.
     *
     * @param o The object to compare.
     * @return {@code true} if the object is equal to this container; {@code false} otherwise.
     */
    // Método para comparar se este contentor é igual a outro objeto
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Verifica se o objeto comparado é a mesma instância
        if (o == null || getClass() != o.getClass()) return false; // Verifica se o objeto é nulo ou se não é da mesma classe

        Container container = (Container) o; // Faz um cast do objeto para Container

        // Compara as letras dos contentores
        return letter == container.letter; // Retorna true se as letras forem iguais
    }

    /**
     * Generates a hash code for the container based on the identifying letter.
     *
     * @return Hash code of the container.
     */
    // Método para gerar um código hash para o contentor (usado em coleções)
    @Override
    public int hashCode() {
        return Character.hashCode(letter); // Retorna o código hash da letra
    }
}
