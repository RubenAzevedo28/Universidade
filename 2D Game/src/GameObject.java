/**
 * Representa um GameObject no Game Engine 2D, contendo Transform e Collider.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class GameObject implements IGameObject {
    private final String name;
    private final ITransform transform;
    private final ICollider collider;
    private IBehaviour behaviour;
    private IShape shape;

    private int fadeFrames = 0;

    private int blinkFrames = 0;

    private boolean emEntrada = false;
    private double destinoY = 600;

    /**
     * Constroi um GameObject com nome, Transform e Collider.
     * @param name Nome do objeto.
     * @param transform Transform associada ao objeto.
     * @param collider Collider para detecao de colisoes.
     *
     */
    public GameObject(String name, ITransform transform, ICollider collider) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
    }

    /**
     * Obtem o nome do GameObject.
     * @return O nome do objeto.
     *
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Obtem o Transform do GameObject.
     * @return O transform associado.
     *
     */
    @Override
    public ITransform transform() {
        return this.transform;
    }

    /**
     * Obtem o Collider do GameObject.
     * @return O collider associado.
     *
     */
    @Override
    public ICollider collider() {
        return this.collider;
    }

    /**
     * Devolve o comportamento atual do objeto.
     * @return comportamento associado
     *
     */
    public IBehaviour behaviour() {
        return this.behaviour;
    }

    /**
     * Associa um comportamento ao objeto e inicializa-o.
     * @param behaviour comportamento a associar
     *
     */
    public void setBehaviour(IBehaviour behaviour) {
        this.behaviour = behaviour;
        if (behaviour != null) {
            behaviour.gameObject(this);
            behaviour.onInit();
        }
    }

    /**
     * Define a forma visual do GameObject.
     * @param shape objeto IShape a associar
     *
     */
    public void setShape(IShape shape) {
        this.shape = shape;
    }

    /**
     * Devolve a forma associada ao objeto.
     * @return shape atual
     *
     */
    public IShape getShape() {
        return shape;
    }

    /**
     * Inicia o efeito de fade-in para a sprite.
     *
     */
    public void startFadeIn() {
        this.fadeFrames = 30; // duração do fade-in em frames (~0.5s a 60 FPS)
    }

    /**
     * Devolve o numero de frames restantes de fade.
     * @return frames de fade restantes
     *
     */
    public int getFadeFrames() {
        return fadeFrames;
    }

    /**
     * Decrementa o contador de frames de fade.
     *
     */
    public void decrementFade() {
        if (fadeFrames > 0) fadeFrames--;
    }

    /**
     * Ativa o efeito de piscar no objeto.
     *
     */
    public void startBlinking() {
        this.blinkFrames = 120; // 2 segundos a 60 FPS
    }

    /**
     * Verifica se o objeto esta a piscar.
     * @return true se estiver a piscar
     *
     */
    public boolean isBlinking() {
        return blinkFrames > 0;
    }

    /**
     * Atualiza o contador de piscar por frame.
     *
     */
    public void tickBlink() {
        if (blinkFrames > 0) blinkFrames--;
    }

    /**
     * Indica se o objeto deve ser desenhado enquanto pisca.
     * @return true se deve ser desenhado
     *
     */
    public boolean shouldRenderWhileBlinking() {
        // Alterna entre mostrar/esconder a cada 10 frames
        return (blinkFrames / 14) % 2 == 0;
    }

    /**
     * Marca o objeto para animacao de entrada vindo de fora da tela.
     * @param destinoY coordenada Y final
     *
     */
    public void iniciarEntrada(double destinoY) {
        this.emEntrada = true;
        this.destinoY = destinoY;
    }

    /**
     * Verifica se o objeto esta em fase de entrada.
     * @return true se ainda estiver em animacao de entrada
     *
     */
    public boolean estaEmEntrada() {
        return emEntrada;
    }

    /**
     * Atualiza a animacao de entrada frame a frame.
     * @param dT delta de tempo
     *
     */
    public void atualizarEntrada(double dT) {
        if (!emEntrada) return;

        double atualY = this.transform().position().getY();
        double deltaY = destinoY - atualY;

        double velocidade = 8 * dT;
        if (Math.abs(deltaY) <= velocidade) {
            this.transform().move(new Ponto(0, deltaY), 0);
            emEntrada = false;
        } else {
            this.transform().move(new Ponto(0, Math.signum(deltaY) * velocidade), 0);
        }
    }

}
