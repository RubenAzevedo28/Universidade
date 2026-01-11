/**
 * Interface ITransform.
 * Define as transformacoes espaciais de um GameObject no espaco 2D.
 * Permite controlar posicao, rotacao, escala e layer.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public interface ITransform {

    /**
     * Move este ITransform pela diferenca dada e altera a layer.
     * @param dPos deslocamento nas coordenadas x e y
     * @param dlayer variacao da layer
     *
     */
    public void move(Ponto dPos, int dlayer);

    /**
     * Roda este ITransform por um angulo adicional.
     * @param dTheta variacao do angulo em graus (0 &lt;= angulo &lt; 360)
     *
     */
    public void rotate(double dTheta);

    /**
     * Incrementa a escala deste ITransform.
     * @param dScale incremento da escala (fator multiplicativo)
     *
     */
    public void scale(double dScale);

    /**
     * Devolve a posicao atual do objeto.
     * @return coordenadas (x, y) como objeto Ponto
     *
     */
    public Ponto position();

    /**
     * Devolve o valor da layer atual.
     * @return numero da camada
     *
     */
    public int layer();

    /**
     * Devolve o angulo de rotacao atual.
     * @return angulo em graus
     *
     */
    public double angle();

    /**
     * Devolve o fator de escala atual.
     * @return escala do objeto
     *
     */
    public double scale();
}
