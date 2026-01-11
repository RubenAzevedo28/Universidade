import java.util.*;
/**
 * Nivel5 representa o quinto e ultimo nivel do jogo.
 * Neste nivel, dois bosses surgem com colisor poligonal e comportamento especial.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Nivel5 extends Nivel {

    /**
     * Construtor do Nivel5.
     * @param engine instancia do motor de jogo
     *
     */
    public Nivel5(GameEngine engine) {
        super(engine);
    }

    /**
     * Carrega os inimigos (bosses) para o nivel.
     * Dois bosses com colisor poligonal e sprite especial sao posicionados no topo do ecrã.
     *
     */
    @Override
    public void carregarInimigos() {

        // Boss
        for (int coluna = 0; coluna < 2; coluna++) {
            Transform transformBoss = new Transform(new Ponto(400 + coluna * 300, 150), 0, 0, 1.5);

            List<Ponto> pontosBoss = List.of(
                    new Ponto(-90, -20),
                    new Ponto(-40, -80),
                    new Ponto(40, -80),
                    new Ponto(90, -20),
                    new Ponto(100, 60),
                    new Ponto(-100, 60)
            );

            PolygonCollider colliderBoss = new PolygonCollider(transformBoss, pontosBoss);
            GameObject boss = new GameObject("Enemy_"+ coluna, transformBoss, colliderBoss);
            boss.setBehaviour(new EnemyBossBehaviour());  // Pode ser substituído por BossBehaviour
            boss.setShape(new Shape("/images/enemy_boss.png"));

            engine.addEnabled(boss, new double[]{0, 0, 0, 0, 0});
        }

    }

    /**
     * Verifica se todos os bosses foram eliminados.
     * @return true se o nivel estiver concluido
     *
     */
    @Override
    public boolean concluido() {
        return engine.getEnabled().stream()
                .noneMatch(go -> go.name().startsWith("Enemy_"));
    }
}
