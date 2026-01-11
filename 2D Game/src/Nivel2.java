import java.util.*;
/**
 * Nivel2 representa o segundo nivel do jogo.
 * Cria inimigos com maior velocidade e dois a atacar simultaneamente.
 * Os inimigos sao organizados numa formacao retangular.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Nivel2 extends Nivel {

    /**
     * Construtor do Nivel2.
     * @param engine instancia do GameEngine
     *
     */
    public Nivel2(GameEngine engine) {
        super(engine);
    }

    /**
     * Carrega os inimigos para o nivel.
     * Inimigos sao colocados em 3 linhas e 6 colunas,
     * com velocidade aumentada e dois a atacar de cada vez.
     *
     */
    @Override
    public void carregarInimigos() {
        // Criar Inimigos mais rápidos
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 6; coluna++) {
                Transform t = new Transform(new Ponto(200 + coluna * 70, 50 + linha * 60), 0, 0, 1.75);
                List<Ponto> pontos = List.of(
                        new Ponto(0, -20),
                        new Ponto(-15, 15),
                        new Ponto(15, 15)
                );
                PolygonCollider c = new PolygonCollider(t, pontos);
                GameObject enemy = new GameObject("Enemy_" + linha + "_" + coluna, t, c);
                enemy.setBehaviour(new EnemyBehaviour(1.2, 2)); // Velocidade maior e 2 a atacar
                enemy.setShape(new Shape("/images/enemy.png"));
                engine.addEnabled(enemy, new double[]{0, 0, 0, 0, 0});
            }
        }
    }

    /**
     * Verifica se o nivel esta concluido, ou seja,
     * se todos os inimigos foram eliminados.
     * @return true se nenhum inimigo restar
     *
     */
    @Override
    public boolean concluido() {
        return engine.getEnabled().stream().noneMatch(e -> e.name().startsWith("Enemy_"));
    }
}
