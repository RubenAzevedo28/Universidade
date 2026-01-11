import java.util.*;
/**
 * Comportamento do boss inimigo.
 * Move-se horizontalmente, descendo ligeiramente ao atingir os limites.
 * Dispara tres balas em leque a intervalos regulares.
 * Possui um numero fixo de vidas que diminuem ao ser atingido.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class EnemyBossBehaviour extends Behaviour {

    private static final double HORIZONTAL_SPEED = 2.0;
    private static final double DESCIDA = 1.0;
    private static final int SCREEN_WIDTH = 1200;
    private static final int MARGIN = 40;

    private final double fireDelay = 40.0;  // Dispara de 2 em 2 segundos
    private double fireCooldown = fireDelay; // Começa com atraso aleatório

    private int direction = 1; // 1 = direita, -1 = esquerda

    private int vida = 14;


    /**
     * Atualiza a posicao do boss, trata do disparo e da direcao.
     * @param dT tempo decorrido
     * @param input evento de input
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {
        GameObject boss = (GameObject) this.gameObject();
        Transform t = (Transform) boss.transform();

        // Movimento horizontal
        double dx = HORIZONTAL_SPEED * dT * direction;
        t.move(new Ponto(dx, 0), 0);

        // Verificar limites da tela
        double x = t.position().getX();
        if (x < MARGIN || x > SCREEN_WIDTH - MARGIN) {
            direction *= -1; // inverte direção
            t.move(new Ponto(0, DESCIDA), 0); // desce ligeiramente
        }

        fireCooldown -= dT;

        if (fireCooldown <= 0) {
            shootTripleShot();
            fireCooldown = fireDelay;
        }

    }

    /**
     * Dispara tres balas do boss em diferentes direcoes.
     *
     */
    private void shootTripleShot() {
        Ponto origem = this.gameObject().transform().position();

        double[] angles = {0, -20, 20}; // central, esquerda, direita (em graus)
        for (double angleDeg : angles) {
            double angleRad = Math.toRadians(angleDeg);
            double dirX = Math.sin(angleRad);
            double dirY = Math.cos(angleRad);

            Transform bulletTransform = new Transform(
                    new Ponto(origem.getX(), origem.getY()),
                    this.gameObject().transform().layer(),
                    0, 1.0
            );

            CircleCollider bulletCollider = new CircleCollider(bulletTransform, new Ponto(0, 0), 5);
            GameObject bullet = new GameObject("EnemyBullet", bulletTransform, bulletCollider);
            bullet.setBehaviour(new EnemyBulletBehaviour((GameObject) this.gameObject(), dirX, dirY));
            bullet.setShape(new Shape("/images/enemybullet.png"));

            GameEngineSingleton.getInstance().addEnabled(bullet, new double[]{0, 0, 0, 0, 0});
        }

        SoundPlayer.playSound("/sounds/Laser_01.wav", -30.0f);
    }

    /**
     * Aplica dano ao boss e verifica se deve ser destruido.
     *
     */
    public void levarDano() {
        vida--;
        ExplosionBehaviour.criarExplosao(this.gameObject().transform().position(), 1.75);

        if (vida <= 0) {
            ExplosionBehaviour.criarExplosao(this.gameObject().transform().position(), 10.0);
            GameEngineSingleton.getInstance().destroy((GameObject) this.gameObject());
            GameEngineSingleton.addScore(1000);
        }
    }

    @Override public void onInit() {}
    @Override public void onEnabled() {}
    @Override public void onDisabled() {}
    @Override public void onDestroy() {}

    /**
     * Trata da colisao com outros objetos, ignorando balas e outros inimigos.
     * @param collidedObjects lista de objetos colididos
     *
     */
    @Override public void onCollision(List<IGameObject> collidedObjects) {
        for (IGameObject other : collidedObjects) {
                if (other.name().startsWith("Bullet") || other.name().startsWith("EnemyBullet") || other.name().startsWith("Enemy_")) continue;

                break;
        }
    }
}
