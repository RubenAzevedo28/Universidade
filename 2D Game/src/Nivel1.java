import java.util.*;
/**
 * Representa o primeiro nivel do jogo.
 * Contem inimigos com forma de triangulo em varias linhas.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Nivel1 extends Nivel {
    public Nivel1(GameEngine engine) {
        super(engine);
    }

    /**
     * Carrega os inimigos especificos do nivel 1.
     * Adiciona inimigos triangulares em 3 linhas por 5 colunas.
     *
     */
    @Override
    public void carregarInimigos() {
        // Criar Inimigos
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 5; coluna++) {
                Transform transformEnemy = new Transform(
                        new Ponto(250 + coluna * 70, 50 + linha * 60),
                        0, 0, 1.75
                );
                List<Ponto> verticesTriangulo = List.of(
                        new Ponto(0, -20),
                        new Ponto(-15, 15),
                        new Ponto(15, 15)
                );
                PolygonCollider colliderEnemy = new PolygonCollider(transformEnemy, verticesTriangulo);
                GameObject enemy = new GameObject("Enemy_" + linha + "_" + coluna, transformEnemy, colliderEnemy);
                enemy.setBehaviour(new EnemyBehaviour(1.0,1));
                enemy.setShape(new Shape("/images/enemy.png"));

                engine.addEnabled(enemy, new double[]{0, 0, 0, 0, 0});
            }
        }
    }

    /**
     * Verifica se o nivel foi concluido.
     * O nivel termina quando nao existem mais objetos com nome que comece por "Enemy_".
     * @return true se todos os inimigos foram derrotados
     *
     */
    @Override
    public boolean concluido() {
        return engine.getEnabled().stream()
                .noneMatch(go -> go.name().startsWith("Enemy_"));
    }
}