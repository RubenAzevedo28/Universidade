/**
 * Representa a posicao, rotacao, escala e camada de um GameObject no espaço 2D.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Transform implements ITransform {
    private Ponto position;
    private int layer;
    private double angle;
    private double scale;

    /**
     * Constroi um Transform com posicao inicial, camada, angulo e escala.
     * @param position Posicao Y inicial.
     * @param layer Camada do GameObject.
     * @param angle Angulo de rotacao (graus).
     * @param scale Fator de escala.
     *
     */
    public Transform(Ponto position, int layer, double angle, double scale) {
        this.position = position;
        this.layer = layer;
        this.angle = angle;
        this.scale = scale;
    }

    /**
     * Move o objeto na posicao e camada especificadas.
     * @param dPos Ponto com deslocamento.
     * @param dlayer Variacao da camada.
     *
     */
    @Override
    public void move(Ponto dPos, int dlayer) {
        position = position.translate(dPos.getX(), dPos.getY());
        layer += dlayer;
    }

    /**
     * Rotaciona o objeto em graus.
     * @param dTheta Variacao do angulo em graus.
     *
     */
    @Override
    public void rotate(double dTheta) {
        angle = (angle + dTheta) % 360;
        if (angle < 0) angle += 360; // Garante que o ângulo esteja entre 0 e 360
    }

    /**
     * Aplica uma escala ao objeto.
     * @param dScale Novo fator de escala.
     *
     */
    @Override
    public void scale(double dScale) {
        scale += dScale;
        if (scale < 0) scale = 0; // Evita escala negativa
    }

    /**
     * Obtem a posicao atual.
     * @return A posicao do objeto.
     *
     */
    @Override
    public Ponto position() {
        return position;
    }

    /**
     * Devolve a camada atual do objeto.
     * @return valor da camada
     *
     */
    @Override
    public int layer() {
        return this.layer;
    }

    /**
     * Devolve o angulo atual de rotacao do objeto.
     * @return angulo em graus (0-360)
     *
     */
    @Override
    public double angle() {
        return this.angle;
    }

    /**
     * Devolve o fator de escala atual.
     * @return valor da escala
     *
     */
    @Override
    public double scale() {
        return this.scale;
    }

}
