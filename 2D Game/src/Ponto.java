/**
 * Representa um ponto em coordenadas cartesianas.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Ponto {
    private final double x, y;

    /**
     * Construtor para criar um ponto nas coordenadas (x, y).
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     *
     */
    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calcula a distancia euclidiana entre este ponto e outro ponto.
     *
     * @param outro O ponto para calcular a distancia.
     * @return A distancia entre os dois pontos.
     *
     */
    public double distancia(Ponto outro) {
        return Math.sqrt(Math.pow(this.x - outro.x, 2) + Math.pow(this.y - outro.y, 2));
    }

    /**
     * Retorna um novo ponto transladado por (dx, dy).
     *
     * @param dx Deslocamento em X.
     * @param dy Deslocamento em Y.
     * @return Novo ponto deslocado.
     *
     */
    public Ponto translate(double dx, double dy) {
        return new Ponto(this.x + dx, this.y + dy);
    }

    /**
     * @return A coordenada X do ponto.
     *
     */
    public double getX() { return x; }

    /**
     * @return A coordenada Y do ponto.
     *
     */
    public double getY() { return y; }

}