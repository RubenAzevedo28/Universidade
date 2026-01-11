/**
 * Classe abstrata Nivel.
 * Representa um nivel do jogo, incluindo o carregamento de inimigos e logica de conclusao.
 * Cada subclasse define os seus proprios inimigos.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public abstract class Nivel {
    protected final GameEngine engine;

    /**
     * Construtor do nivel.
     *
     * @param engine instancia do motor de jogo associada ao nivel
     *
     */
    public Nivel(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Metodo principal para carregar o nivel.
     * Cria o jogador e invoca o metodo especifico de carregamento de inimigos.
     *
     */
    public void carregar() {
        criarPlayer(); // sempre que o nível for carregado
        carregarInimigos();
    }

    /**
     * Cria o objeto jogador na posicao inicial predefinida.
     * Utiliza colisor circular e sprite associada.
     *
     */
    private void criarPlayer() {
        Transform transformPlayer = new Transform(new Ponto(600, 700), 0, 0, 1.5);
        CircleCollider colliderPlayer = new CircleCollider(transformPlayer, new Ponto(0, 0), 20);
        GameObject player = new GameObject("Player", transformPlayer, colliderPlayer);
        player.setBehaviour(new PlayerBehaviour());
        player.setShape(new Shape("/images/player.png"));
        engine.addEnabled(player, new double[]{0, 0, 0, 0, 0});
    }

    /**
     * Metodo abstrato para carregar os inimigos do nivel.
     * Deve ser implementado por cada subclasse.
     *
     */
    public abstract void carregarInimigos();

    /**
     * Verifica se o nivel foi concluido.
     * Cada nivel define a sua propria condicao de conclusao.
     *
     * @return true se o nivel estiver concluido
     *
     */
    public abstract boolean concluido();
}
