import java.util.*;
/**
 * Classe PlayerBehaviour.
 * Define o comportamento da nave do jogador.
 * Permite movimentacao com o teclado e disparo de projeteis com cooldown.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */

public class PlayerBehaviour extends Behaviour {

    private static final double VELOCITY = 4.0; // velocidade constante de movimento
    private double cooldown = 0.0;
    private static final double FIRE_DELAY = 0.3;

    /**
     * Atualiza o comportamento do jogador em cada frame.
     * Permite mover com as teclas direcionais ou WASD, e disparar com ESPACO.
     * @param dT intervalo de tempo desde o ultimo frame
     * @param input input atual do teclado
     *
     */
    @Override
    public void onUpdate(double dT, IInputEvent input) {
        GameObject player = (GameObject) this.gameObject();
        if (player.estaEmEntrada()) {
            player.atualizarEntrada(dT);
            return;
        }

        // Verifica se as teclas foram pressionadas e move o objeto
        double dx = 0;
        double dy = 0;

        if (input.isKeyPressed("LEFT") || input.isKeyPressed("A")) {
            dx -= VELOCITY * dT;
        }
        if (input.isKeyPressed("RIGHT") || input.isKeyPressed("D")) {
            dx += VELOCITY * dT;
        }
        if (input.isKeyPressed("UP") || input.isKeyPressed("W")) {
            dy -= VELOCITY * dT;
        }
        if (input.isKeyPressed("DOWN") || input.isKeyPressed("S")) {
            dy += VELOCITY * dT;
        }

        // Atualiza a posição do GameObject
        if (dx != 0 || dy != 0) {
            this.gameObject().transform().move(new Ponto(dx, dy), 0);
        }

        // Disparo de bala
        cooldown -= dT;
        if (input.isKeyReleased("SPACE") && cooldown <= 0) {
            shootBullet();
            cooldown = FIRE_DELAY;
        }
    }

    /**
     * Cria e dispara uma nova bala a partir da posicao da nave do jogador.
     * Usa um retangulo como colisor e aplica sprite e som.
     *
     */
    private void shootBullet() {
        // Obter a posição atual do Player
        Ponto playerPos = this.gameObject().transform().position();

        // Obter o raio do Player (assumindo que o collider é CircleCollider)
        CircleCollider playerCollider = (CircleCollider) this.gameObject().collider();
        double raio = playerCollider.getRaio();

        // Nova posição inicial da bala (subida do centro do player)
        Ponto bulletStart = new Ponto(playerPos.getX(), playerPos.getY() - raio);

        Transform bulletTransform = new Transform(
                bulletStart,
                this.gameObject().transform().layer(),
                0, 2
        );

        // Criar pequeno retângulo como bala
        List<Ponto> vertices = List.of(
                new Ponto(0, -8),   // Topo
                new Ponto(6, -8),   // Topo direito
                new Ponto(6, 2),    // Base direita
                new Ponto(0, 2)     // Base esquerda
        );

        PolygonCollider bulletCollider = new PolygonCollider(bulletTransform, vertices);
        GameObject bullet = new GameObject("Bullet", bulletTransform, bulletCollider);
        bullet.setBehaviour(new BulletBehaviour((GameObject) this.gameObject()));
        bullet.setShape(new Shape("/images/laserBullet.png"));

        // Adicionar ao motor (precisamos do acesso ao GameEngine aqui!)
        GameEngineSingleton.getInstance().addEnabled(bullet, new double[]{0, 0, 0, 0, 0});
        SoundPlayer.playSound("/sounds/Laser_09.wav", -30.0f);
    }

    @Override
    public void onInit() {
        // Inicializações se forem necessárias
    }

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {}

    @Override
    public void onDestroy() {}

    /**
     * Trata da colisao com inimigos ou objetos perigosos.
     * Destrui o outro objeto, cria explosao e termina o jogo.
     * @param collidedObjects lista de objetos com os quais colidiu
     *
     */
    @Override
    public void onCollision(List<IGameObject> collidedObjects) {

        for (IGameObject other : collidedObjects) {
            if (other.name().startsWith("Bullet") || other.name().startsWith("EnemyBullet") || other.name().startsWith("Player")) continue;
            if (other.behaviour() instanceof EnemyBossBehaviour boss) {
                boss.levarDano(); // reduz vida
            } else {
                ExplosionBehaviour.criarExplosao(other.transform().position(), 1.75); // inimigo normal
                GameEngineSingleton.addScore(100);
                GameEngineSingleton.getInstance().destroy((GameObject) other);
            }

            GameEngineSingleton.perderVida();
            if (GameEngineSingleton.getVidas() == 0) {
                GameEngineSingleton.getInstance().destroy((GameObject) this.gameObject());
                GameEngineSingleton.setGameOver(true);
                SoundPlayer.playSound("/sounds/gameover.wav", 5.0f);
            } else {
                GameEngineSingleton.getInstance().disable((GameObject) this.gameObject());
                // aguarda e reativa
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        GameObject player = (GameObject) gameObject();
                        if (!GameEngineSingleton.getInstance().getEnabled().contains(player)) {
                            player.startBlinking();
                            player.iniciarEntrada(600); // destino final

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
