import java.util.*;
/**
 * Classe abstrata Behaviour.
 * Fornece uma implementacao base para comportamentos de GameObject.
 * Subclasses devem implementar o metodo onUpdate para definir o comportamento especifico.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public abstract class Behaviour implements IBehaviour {

    private IGameObject go;

    /**
     * Devolve o GameObject associado a este comportamento.
     * @return o GameObject
     *
     */
    @Override
    public IGameObject gameObject() {
        return go;
    }

    /**
     * Define o GameObject associado a este comportamento.
     * @param go o GameObject a associar
     *
     */
    @Override
    public void gameObject(IGameObject go) {
        this.go = go;
    }

    /**
     * Metodo chamado aquando da inicializacao do objeto.
     *
     */
    @Override
    public void onInit() {}

    /**
     * Metodo chamado quando o objeto e ativado.
     *
     */
    @Override
    public void onEnabled() {}

    /**
     * Metodo chamado quando o objeto e desativado.
     *
     */
    @Override
    public void onDisabled() {}

    /**
     * Metodo chamado quando o objeto e destruido.
     *
     */
    @Override
    public void onDestroy() {}

    /**
     * Metodo chamado quando ocorre colisao com outros objetos.
     * @param gol lista de objetos com os quais colidiu
     *
     */
    @Override
    public void onCollision(List<IGameObject> gol) {}

    /**
     * Metodo abstrato chamado em cada frame do jogo.
     * Deve ser implementado pelas subclasses.
     * @param dT intervalo de tempo desde a ultima atualizacao
     * @param ie evento de input atual
     *
     */
    @Override
    public abstract void onUpdate(double dT, IInputEvent ie);
}
