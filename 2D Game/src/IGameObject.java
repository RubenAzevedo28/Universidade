/**
 * Interface IGameObject.
 * Representa um objeto no motor de jogo 2D.
 * Um GameObject possui nome, transformacao, colisor e comportamento.
 * Pode ainda possuir uma forma visual (sprite) para renderização.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public interface IGameObject {

    /**
     * Devolve o nome do GameObject.
     * @return o nome como string
     *
     */
    String name();

    /**
     * Devolve o Transform associado ao GameObject.
     * @return a transformacao
     *
     */
    ITransform transform();

    /**
     * Devolve o Collider do GameObject, centrado na posicao do transform.
     * @return o colisor
     *
     */
    ICollider collider();

    /**
     * Devolve o comportamento associado ao GameObject.
     * @return o comportamento
     *
     */
    IBehaviour behaviour();

    /**
     * Retorna a forma visual (sprite ou animacao) associada ao GameObject.
     * @return a forma visual usada para renderizacao
     *
     */
    Object getShape();
}
