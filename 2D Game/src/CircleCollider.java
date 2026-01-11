import java.awt.*;
/**
 * Representa um colisor circular no Game Engine 2D.
 * Responsavel por detetar colisoes com outros circulos ou poligonos.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class CircleCollider extends Collider {

    private final Ponto centro;
    private final double raio;

    /**
     * Construtor do colisor circular.
     * @param transform o transform associado ao objeto
     * @param centro o centro relativo do circulo
     * @param raio o raio base do circulo
     *
     */
    public CircleCollider(Transform transform, Ponto centro, double raio) {
        super(transform);
        this.centro = centro;
        this.raio = raio;
    }

    /**
     * Devolve o centroide (centro global) do circulo.
     * @return ponto do centroide
     *
     */
    @Override
    public Ponto centroid() {
        return transform.position();
    }

    /**
     * Devolve o raio do circulo ajustado pela escala.
     * @return o raio efetivo
     *
     */
    public double getRaio() {
        return raio * transform.scale();
    }

    /**
     * Devolve o centro relativo do circulo (antes da transformacao).
     * @return ponto relativo ao centro
     *
     */
    public Ponto getCentro() {
        return centro;
    }

    /**
     * Verifica colisao com outro colisor.
     * @param other outro colisor
     * @return true se colidir
     *
     */
    @Override
    public boolean collidesWith(ICollider other) {
        return other.collidesWith(this);
    }

    /**
     * Verifica colisao com outro colisor circular.
     * @param other outro colisor circular
     * @return true se colidir
     *
     */
    @Override
    public boolean collidesWith(CircleCollider other) {
        double distancia = this.centroid().distancia(other.centroid());
        return distancia < this.getRaio() + other.getRaio() - 1e-9;
    }

    /**
     * Verifica colisao com um colisor poligonal.
     * @param poly colisor poligonal
     * @return true se colidir
     *
     */
    @Override
    public boolean collidesWith(PolygonCollider poly) {
        var vertices = poly.getVerticesTransformados();
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Ponto a = vertices.get(i);
            Ponto b = vertices.get((i + 1) % n);
            SegmentoReta lado = new SegmentoReta(a, b);
            if (lado.intersetaCirculo(this)) return true;
        }
        return poly.pontoDentroDoPoligono(this.centroid(), vertices);
    }

    /**
     * Devolve a largura usada para desenhar a imagem.
     * @return largura total do colisor
     *
     */
    @Override
    public double getLarguraRenderizada() {
        return getRaio() * 2;
    }

    /**
     * Devolve a altura usada para desenhar a imagem.
     * @return altura total do colisor
     *
     */
    @Override
    public double getAlturaRenderizada() {
        return getRaio() * 2;
    }


    /**
     * Desenha o colisor circular no contexto grafico.
     * @param g o contexto grafico
     *
     */
    @Override
    public void draw(Graphics g) {
        int x = (int) (centroid().getX() - getRaio());
        int y = (int) (centroid().getY() - getRaio());
        int diameter = (int) (2 * getRaio());
        g.drawOval(x, y, diameter, diameter);
    }
}
