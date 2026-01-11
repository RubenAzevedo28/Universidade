import java.util.List;
/**
 * Interface IBehaviour.
 * Define o contrato para comportamentos associados a objetos do jogo.
 * Um comportamento permite controlar a logica de atualizacao, resposta a eventos e colisoes.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public interface IBehaviour {

    /**
     * Devolve o objeto de jogo associado a este comportamento.
     * @return o objeto de jogo
     *
     */
    public IGameObject gameObject();

    /**
     * Associa um objeto de jogo a este comportamento.
     * @param go o objeto de jogo a associar
     *
     */
    public void gameObject(IGameObject go);

    /**
     * Metodo chamado quando o objeto e inicializado.
     *
     */
    void onInit();

    /**
     * Metodo chamado quando o objeto e ativado.
     *
     */
    void onEnabled();

    /**
     * Metodo chamado quando o objeto e desativado.
     *
     */
    void onDisabled();

    /**
     * Metodo chamado quando o objeto e destruido.
     *
     */
    void onDestroy();

    /**
     * Metodo chamado em cada frame do jogo.
     * @param dT intervalo de tempo (delta time) desde a ultima atualizacao
     * @param ie o estado atual do input
     *
     */
    void onUpdate(double dT, IInputEvent ie);

    /**
     * Metodo chamado quando ocorre uma colissao com outros objetos.
     * @param go lista de objetos com os quais colidiu
     *
     */
    void onCollision(List<IGameObject> go);


}
