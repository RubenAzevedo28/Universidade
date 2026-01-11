/**
 * Nivel3 representa o terceiro nivel do jogo.
 * Os inimigos aparecem organizados em linhas curvas,
 * formando uma formacao semicircular mais aberta.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Nivel3 extends Nivel {

    /**
     * Construtor do Nivel3.
     * @param engine instancia do motor de jogo
     *
     */
    public Nivel3(GameEngine engine) {
        super(engine);
    }

    /**
     * Carrega os inimigos para o nivel.
     * Os inimigos sao dispostos em 3 linhas curvas, com mais inimigos por linha
     * e angulos mais abertos para uma formacao mais larga.
     *
     */
    @Override
    public void carregarInimigos() {
        int centerX = 600;
        int centerY = 450; // Posição mais para baixo
        int linhas = 3;

        for (int linha = 0; linha < linhas; linha++) {
            int numInimigos = 7 + linha * 2;
            int radius = 300 + linha * 60;

            // Novo intervalo de ângulos: curva mais aberta e mais "achatada"
            double anguloInicial = -150;
            double anguloFinal = -30;

            for (int i = 0; i < numInimigos; i++) {
                double angle = Math.toRadians(anguloInicial + (anguloFinal - anguloInicial) * i / (numInimigos - 1));
                double x = centerX + Math.cos(angle) * radius;
                double y = centerY + Math.sin(angle) * radius;

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
