/**
 * Nivel4 representa o quarto nivel do jogo.
 * Os inimigos sao organizados em linhas horizontais com um formato triangular invertido,
 * com 7 inimigos na primeira linha e menos um em cada linha seguinte.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Nivel4 extends Nivel {
    /**
     * Construtor do Nivel4.
     * @param engine instancia do motor de jogo
     *
     */
    public Nivel4(GameEngine engine) {
        super(engine);
    }

    /**
     * Carrega os inimigos para o nivel.
     * Os inimigos aparecem em 4 linhas horizontais,
     * com o numero de inimigos a diminuir por linha (7, 6, 5, 4).
     *
     */
    @Override
    public void carregarInimigos() {
        int startY = 100;
        int espacamentoX = 80;
        int espacamentoY = 60;

        for (int linha = 0; linha < 4; linha++) {
            int numInimigos = 7 - linha; // 7, 6, 5, 4
            int totalWidth = (numInimigos - 1) * espacamentoX;
            int startX = 600 - totalWidth / 2;

            for (int i = 0; i < numInimigos; i++) {
                double x = startX + i * espacamentoX;
                double y = startY + linha * espacamentoY;

                Transform t = new Transform(new Ponto(x, y), 0, 0, 1.5);
                CircleCollider c = new CircleCollider(t, new Ponto(0, 0), 20);
                GameObject enemy = new GameObject("Enemy_" + linha + "_" + i, t, c);
                enemy.setBehaviour(new EnemyBehaviour(1.3, 3));
                enemy.setShape(new Shape("/images/enemy2.png"));

                engine.addEnabled(enemy, new double[]{0, 0, 0, 0, 0});
            }
        }
    }

    /**
     * Verifica se todos os inimigos foram eliminados.
     * @return true se o nivel estiver concluido
     *
     */
    @Override
    public boolean concluido() {
        return engine.getEnabled().stream()
                .noneMatch(go -> go.name().startsWith("Enemy_"));
    }
}
