import java.util.*;
/**
 * Classe EnemyBehaviour.
 * Define o comportamento autonomo de um inimigo.
 * O inimigo move-se horizontalmente, aguarda um tempo, roda 180 graus e entra em modo de ataque.
 * Quando ataca, persegue o jogador e dispara projeteis em intervalos definidos.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class EnemyBehaviour extends Behaviour {

    private static int horizontalDirection = 1; // 1 = direita, -1 = esquerda
    private static final double HORIZONTAL_SPEED = 1.0; // velocidade lenta para os lados
    private static final int SCREEN_WIDTH = 1200; // largura da janela
    private static final int MARGIN = 20; // margem para inverter

    private final double speedFactor;
    private final int maxAttackers;
    private static int currentAttackers = 0;

    private static final double SPEED = 1;
    private static final double ROTATION_SPEED = 5.0; // graus por segundo
    private static final double WAIT_TIME_BEFORE_ATTACK = 10.0; // segundos parado antes de atacar


    private enum State { IDLE, ROTATING, ATTACKING }

    private State state = State.IDLE;
    private double waitTimer = WAIT_TIME_BEFORE_ATTACK;
    private double rotatedAngle = 0.0;

    private final double fireDelay;
    private double fireCooldown;

    /**
     * Construtor da classe EnemyBehaviour.
     * Define tempos aleatorios para o disparo inicial e intervalo entre tiros.
     *
     */
    public EnemyBehaviour(double speedFactor, int maxAttackers) {
        this.speedFactor = speedFactor;
        this.maxAttackers = maxAttackers;

        this.fireDelay = 120.0 + Math.random() * 120.0; // Cada inimigo terá um delay entre 15s e 25s
        this.fireCooldown = Math.random() * fireDelay; // E começa com cooldown aleatório
    }

    /**
     * Atualiza o estado do inimigo a cada frame.
     * Move-se, roda, ataca e dispara consoante o estado.
     * @param dT tempo desde o ultimo frame
     * @param input input do jogador (nao utilizado pelo inimigo)
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {

        switch (state) {
            case IDLE:
                moveHorizontally(dT);
                waitTimer -= dT;
                if (waitTimer <= 0 && currentAttackers < maxAttackers) {
                    currentAttackers++;
                    state = State.ROTATING;
                }
                break;

            case ROTATING:
                double rotateThisFrame = ROTATION_SPEED * dT;
                this.gameObject().transform().rotate(rotateThisFrame);
                rotatedAngle += rotateThisFrame;
                // ⚡ Movimento para frente enquanto roda
                double angleRad = Math.toRadians(this.gameObject().transform().angle());
                double moverX = Math.cos(angleRad) * SPEED * dT;
                double moverY = Math.sin(angleRad) * SPEED * dT;
                this.gameObject().transform().move(new Ponto(moverX, moverY), 0);
                if (rotatedAngle >= 180.0) {
                    state = State.ATTACKING;
                }
                break;

            case ATTACKING:
                GameObject player = findPlayer();
                if (player == null) return;

                // Posição atual
                Ponto enemyPos = this.gameObject().transform().position();
                Ponto playerPos = player.transform().position();

                double dx = playerPos.getX() - enemyPos.getX();
                double dy = playerPos.getY() - enemyPos.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > 1e-5 ) {
                    double moveXAttack = (dx / dist) * SPEED * dT;
                    double moveYAttack = (dy / dist) * SPEED * dT;
                    this.gameObject().transform().move(new Ponto(moveXAttack, moveYAttack), 0);
                }
                fireCooldown -= dT;

                if (fireCooldown <= 0) {
                    shootAtPlayer();
                    fireCooldown = fireDelay;
                }
                break;

        }
        checkScreenBounds();
    }

    /**
     * Procura e devolve o objeto Player no motor de jogo.
     * @return o GameObject do jogador, ou null se nao existir
     *
     */
    private GameObject findPlayer() {
        for (GameObject go : GameEngineSingleton.getInstance().getEnabled()) {
            if (go.name().equals("Player")) {
                return go;
            }
        }
        return null;
    }

    /**
     * Move o inimigo horizontalmente consoante a direcao global.
     * @param dT delta de tempo
     *
     */
    private void moveHorizontally(double dT) {
        double moveX = HORIZONTAL_SPEED * dT * horizontalDirection;
        this.gameObject().transform().move(new Ponto(moveX, 0), 0);
    }

    /**
     * Verifica se o inimigo atingiu as margens e inverte a direcao se necessario.
     *
     */
    private void checkScreenBounds() {
        double x = this.gameObject().transform().position().getX();
        if (x < MARGIN || x > SCREEN_WIDTH - MARGIN) {
            horizontalDirection *= -1; // inverter direção global
        }
    }

    /**
     * Metodo chamado quando o inimigo e destruido.
     * Liberta o ataque exclusivo e verifica condicao de vitoria.
     *
     */
    @Override
    public void onDestroy() {
        if (state == State.ROTATING || state == State.ATTACKING) {
            currentAttackers = Math.max(0, currentAttackers - 1);
        }
    }

    /**
     * Dispara uma bala na direcao do jogador.
     *
     */
    private void shootAtPlayer() {
        GameObject player = findPlayer();
        if (player == null) return;

        Ponto enemyPos = this.gameObject().transform().position();
        Ponto playerPos = player.transform().position();

        double dx = playerPos.getX() - enemyPos.getX();
        double dy = playerPos.getY() - enemyPos.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 1e-5 && dist < 400) {
            double dirX = dx / dist;
            double dirY = dy / dist;

            Transform bulletTransform = new Transform(
                    new Ponto(enemyPos.getX(), enemyPos.getY()),
                    this.gameObject().transform().layer(),
                    0, 1.25
            );

            CircleCollider bulletCollider = new CircleCollider(bulletTransform, new Ponto(0, 0), 5);
            GameObject bullet = new GameObject("EnemyBullet", bulletTransform, bulletCollider);
            bullet.setBehaviour(new EnemyBulletBehaviour((GameObject) this.gameObject(), dirX, dirY));
            bullet.setShape(new Shape("/images/enemybullet.png"));

            GameEngineSingleton.getInstance().addEnabled(bullet, new double[]{0, 0, 0, 0, 0});
            SoundPlayer.playSound("/sounds/Laser_01.wav", -30.0f);
        }
    }

    @Override public void onInit() {}
    @Override public void onEnabled() {}
    @Override public void onDisabled() {}

    /**
     * Trata da logica de colisao com outros objetos.
     * Ignora colisoes com inimigos e balas inimigas.
     * Destrui o outro objeto e gera uma explosao.
     * @param collidedObjects lista de objetos com os quais colidiu
     *
     */
    @Override public void onCollision(List<IGameObject> collidedObjects) {
        for (IGameObject other : collidedObjects) {
            if (other.name().startsWith("Bullet") || other.name().startsWith("EnemyBullet") || other.name().startsWith("Enemy_")) continue;

            ExplosionBehaviour.criarExplosao(other.transform().position(),1.75);

            break;
        }
    }
}