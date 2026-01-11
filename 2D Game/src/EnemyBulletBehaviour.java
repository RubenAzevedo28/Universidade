import java.util.*;
/**
 * Classe EnemyBulletBehaviour.
 * Define o comportamento de uma bala disparada por um inimigo.
 * Move-se numa direcao constante e deteta colisoes com o jogador.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class EnemyBulletBehaviour extends Behaviour {

    private final double dirX;
    private final double dirY;
    private static final double SPEED = 2.0;

    private final GameObject owner;

    /**
     * Construtor do comportamento da bala inimiga.
     * @param owner o inimigo que disparou esta bala
     * @param dirX componente X da direcao do movimento
     * @param dirY componente Y da direcao do movimento
     *
     */
    public EnemyBulletBehaviour(GameObject owner, double dirX, double dirY) {
        this.owner = owner;
        this.dirX = dirX;
        this.dirY = dirY;
    }

    /**
     * Atualiza a posicao da bala em cada frame.
     * Move-se segundo a direcao definida no construtor.
     * @param dT intervalo de tempo desde o ultimo frame
     * @param input evento de input (nao utilizado)
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {
        this.gameObject().transform().move(new Ponto(dirX * SPEED * dT, dirY * SPEED * dT), 0);
    }

    @Override public void onInit() {}

    @Override public void onEnabled() {}

    @Override public void onDisabled() {}

    @Override public void onDestroy() {}

    /**
     * Trata da colisao da bala inimiga com outros objetos.
     * Ignora colisoes com o proprio dono e com outras balas ou inimigos.
     * Se atingir o jogador, termina o jogo.
     * @param collidedObjects lista de objetos com os quais colidiu
     *
     */
    @Override
    public void onCollision(List<IGameObject> collidedObjects) {
        for (IGameObject other : collidedObjects) {
            if (other == owner || other.name().startsWith("Bullet") || other.name().startsWith("EnemyBullet") || other.name().startsWith("Enemy_")) continue;
            GameEngineSingleton.getInstance().destroy((GameObject) this.gameObject());

            ExplosionBehaviour.criarExplosao(other.transform().position(),1.75);

            GameEngineSingleton.perderVida();
            if (GameEngineSingleton.getVidas() == 0) {
                GameEngineSingleton.getInstance().destroy((GameObject) other);
                GameEngineSingleton.setGameOver(true);
            } else {
                GameEngineSingleton.getInstance().disable((GameObject) other);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        GameObject player = (GameObject) other;
                        if (!GameEngineSingleton.getInstance().getEnabled().contains(player)) {
                            player.startBlinking();
                            player.iniciarEntrada(700); // destino final

                            // Move o player para fora da tela, lá em baixo (por exemplo y = 1000)
                            player.transform().move(
                                    new Ponto(600 - player.transform().position().getX(),
                                            1000 - player.transform().position().getY()),
                                    0
                            );

                            GameEngineSingleton.getInstance().enable(player);
                        }
                    }
                }, 1000);
            }
            break;
        }
    }
}
