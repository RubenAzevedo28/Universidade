import java.awt.*;
/**
 * Interface ICollider.
 * Define o comportamento para deteccao de colisoes entre objetos do jogo.
 * Implementacoes incluem colisor circular e colisor poligonal.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public interface ICollider {

    /**
     * Devolve o centroide (ponto central) do colisor.
     * @return o ponto do centroide
     *
     */
    Ponto centroid();

    /**
     * Verifica se existe colisao com outro colisor qualquer.
     * @param other o outro colisor
     * @return true se colidir, false caso contrario
     *
     */
    boolean collidesWith(ICollider other);

    /**
     * Verifica se existe colisao com um colisor circular.
     * @param circle o colisor circular
     * @return true se colidir, false caso contrario
     *
     */
    boolean collidesWith(CircleCollider circle);

    /**
     * Verifica se existe colisao com um colisor poligonal.
     * @param polygon o colisor poligonal
     * @return true se colidir, false caso contrario
     *
     */
    boolean collidesWith(PolygonCollider polygon);

    /**
     * Devolve a largura aproximada usada para desenhar a imagem do objeto.
     * @return largura em pixeis
     *
     */
    double getLarguraRenderizada();

    /**
     * Devolve a altura aproximada usada para desenhar a imagem do objeto.
     * @return altura em pixeis
     *
     */
    double getAlturaRenderizada();


    /**
     * Desenha visualmente o colisor, para debug ou visualizacao.
     * @param g contexto grafico onde desenhar
     *
     */
    void draw(Graphics g);
}

