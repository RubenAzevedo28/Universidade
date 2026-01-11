import java.util.*;
/**
 * Classe BulletBehaviour.
 * Define o comportamento de uma bala disparada pelo jogador.
 * Move-se para cima a uma velocidade constante e deteta colisoes com inimigos.
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class BulletBehaviour extends Behaviour {

    private static final double SPEED = 3.0; // Velocidade da bala
    private final GameObject owner;

    /**
     * Construtor da BulletBehaviour.
     * @param owner o objeto que disparou esta bala (para evitar colidir com ele)
     *
     */
    public BulletBehaviour(GameObject owner) {
        this.owner = owner;
    }

    /**
     * Atualiza a posicao da bala em cada frame.
     * Move-se verticalmente para cima.
     * @param dT intervalo de tempo desde a ultima atualizacao
     * @param input evento de input (nao usado aqui)
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {
        this.gameObject().transform().move(new Ponto(0, -SPEED * dT), 0);
    }

    @Override
    public void onInit() {}

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {}

    @Override
    public void onDestroy() {}

    /**
     * Trata das colisoes com outros objetos.
     * Ignora colisoes com o proprio owner e outras balas.
     * Se colidir com um inimigo, ambos sao destruidos e adiciona-se pontuacao.
     * @param collidedObjects lista de objetos com os quais colidiu
     *
     */
    @Override
    public void onCollision(List<IGameObject> collidedObjects) {
        for (IGameObject other : collidedObjects) {
            if (other == owner || other.name().startsWith("Bullet") || other.name().startsWith("EnemyBullet")) continue;

            GameEngineSingleton.getInstance().destroy((GameObject) this.gameObject());
            if (other.behaviour() instanceof EnemyBossBehaviour boss) {
                boss.levarDano(); // reduz vida
            } else {
                ExplosionBehaviour.criarExplosao(other.transform().position(), 1.75); // inimigo normal
                GameEngineSingleton.addScore(100);
                GameEngineSingleton.getInstance().destroy((GameObject) other);
            }

            break;
        }
    }
}
