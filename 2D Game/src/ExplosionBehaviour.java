import java.util.*;
/**
 * Classe ExplosionBehaviour.
 * Define o comportamento de uma explosao temporaria que e removida apos alguns segundos.
 * E usada para representar visualmente colisoes ou destruicoes de objetos no jogo.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class ExplosionBehaviour extends Behaviour {

    private double lifetime = 25.0; // duracao da explosao em segundos

    /**
     * Atualiza o tempo de vida da explosao e remove-a quando expira.
     * @param dT intervalo de tempo desde o ultimo frame
     * @param input evento de input (nao utilizado)
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {
        lifetime -= dT;
        ((ShapeAnimada) this.gameObject().getShape()).atualizar(dT);
        if (lifetime <= 0) {
            GameEngineSingleton.getInstance().destroy((GameObject) this.gameObject());
        }
    }

    @Override
    public void onInit() {}

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onCollision(List<IGameObject> collidedObjects) {}

    /**
     * Cria e adiciona uma nova explosao na posicao especificada.
     * Define o transform, colisor, sprite e comportamento da explosao.
     * @param posicao posicao onde deve surgir a explosao
     * @param escala valor de escala da explosao
     *
     */
    public static void criarExplosao(Ponto posicao, double escala) {
        Transform transformExplosao = new Transform(posicao, 1, 0, escala); // escala variável
        CircleCollider colliderExplosao = new CircleCollider(transformExplosao, new Ponto(0, 0), 20);
        GameObject explosao = new GameObject("Explosion", transformExplosao, colliderExplosao);

        explosao.setShape(new ShapeAnimada("/images/explosao", 3, 0.5));
        explosao.setBehaviour(new ExplosionBehaviour());

        GameEngineSingleton.getInstance().addEnabled(explosao, new double[]{0, 0, 0, 0, 0});
        SoundPlayer.playSound("/sounds/Explosion_05.wav", -30.0f);
    }

}
