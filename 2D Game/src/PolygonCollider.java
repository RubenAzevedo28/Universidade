import java.util.*;
import java.awt.Graphics;
/**
 * Representa um colisor poligonal dinamico, com base no Transform associado.
 * Aplica as transformacoes (rotaçao, escala, translaçao) dinamicamente aos pontos originais.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */

public class PolygonCollider extends Collider {
    private final List<Ponto> pontosOriginais;
    private final Ponto centroideOriginal;

    /**
     * Construtor de PolygonCollider.
     * @param transform transformacao associada ao colisor
     * @param pontos lista de vertices do poligono no referencial local
     *
     */
    public PolygonCollider(Transform transform, List<Ponto> pontos) {
        super(transform);
        this.pontosOriginais = new ArrayList<>(pontos);
        this.centroideOriginal = calcularCentroID(pontosOriginais);
    }

    /**
     * Aplica as transformacoes de rotacao, escala e translacao a todos os vertices.
     * @return lista dos vertices transformados em coordenadas globais
     *
     */
    public List<Ponto> getVerticesTransformados() {
        List<Ponto> transformados = new ArrayList<>();
        double anguloRad = Math.toRadians(transform.angle());
        double cos = Math.cos(anguloRad);
        double sin = Math.sin(anguloRad);

        for (Ponto p : pontosOriginais) {
            double dx = p.getX() - centroideOriginal.getX();
            double dy = p.getY() - centroideOriginal.getY();

            double xRot = dx * cos - dy * sin;
            double yRot = dx * sin + dy * cos;

            xRot *= transform.scale();
            yRot *= transform.scale();

            double novoX = xRot + transform.position().getX();
            double novoY = yRot + transform.position().getY();

            transformados.add(new Ponto(novoX, novoY));
        }

        return transformados;
    }

    /**
     * Calcula o centroide de um poligono atraves da formula da area ponderada.
     * @param pontos lista de vertices
     * @return centroide do poligono
     *
     */
    private Ponto calcularCentroID(List<Ponto> pontos) {
        double cx = 0, cy = 0, area = 0;
        int n = pontos.size();

        for (int i = 0; i < n; i++) {
            Ponto p1 = pontos.get(i);
            Ponto p2 = pontos.get((i + 1) % n);
            double cross = p1.getX() * p2.getY() - p2.getX() * p1.getY();
            cx += (p1.getX() + p2.getX()) * cross;
            cy += (p1.getY() + p2.getY()) * cross;
            area += cross;
        }

        area /= 2.0;
        cx /= (6.0 * area);
        cy /= (6.0 * area);
        return new Ponto(cx, cy);
    }

    /**
     * @return centroide atual (com transformacoes aplicadas)
     *
     */
    @Override
    public Ponto centroid() {
        return calcularCentroID(getVerticesTransformados());
    }

    /**
     * Deteta colisao com outro colisor.
     * @param other colisor externo
     * @return true se colide
     *
     */
    @Override
    public boolean collidesWith(ICollider other) {
        return other.collidesWith(this);
    }

    /**
     * Deteta colisao com um colisor circular.
     * @param circle o colisor circular
     * @return true se colide com o circulo
     *
     */
    @Override
    public boolean collidesWith(CircleCollider circle) {
        for (Ponto v : getVerticesTransformados()) {
            if (v.distancia(circle.centroid()) <= circle.getRaio() + 1e-9) return true;
        }
        if (pontoDentroDoPoligono(circle.centroid(), getVerticesTransformados())) return true;
        return false;
    }

    /**
     * Deteta colisao com outro poligono.
     * Utiliza intersecao de segmentos e inclusao de pontos.
     * @param other outro poligono
     * @return true se houver colisao
     *
     */
    @Override
    public boolean collidesWith(PolygonCollider other) {
        List<Ponto> v1 = this.getVerticesTransformados();
        List<Ponto> v2 = other.getVerticesTransformados();

        for (int i = 0; i < v1.size(); i++) {
            Ponto a1 = v1.get(i);
            Ponto a2 = v1.get((i + 1) % v1.size());
            SegmentoReta s1 = new SegmentoReta(a1, a2);

            for (int j = 0; j < v2.size(); j++) {
                Ponto b1 = v2.get(j);
                Ponto b2 = v2.get((j + 1) % v2.size());
                SegmentoReta s2 = new SegmentoReta(b1, b2);

                if (s1.interseta(s2)) return true;
            }
        }

        // Se não houve interseções entre lados, verifica inclusão
        for (Ponto v : v1) {
            if (pontoDentroDoPoligono(v, v2)) return true;
        }
        for (Ponto v : v2) {
            if (pontoDentroDoPoligono(v, v1)) return true;
        }

        return false;
    }

    /**
     * Determina se um ponto esta dentro de um poligono.
     * Algoritmo do numero impar de cruzamentos (ray casting).
     * @param p ponto a testar
     * @param vertices vertices do poligono
     * @return true se estiver dentro
     *
     */
    public boolean pontoDentroDoPoligono(Ponto p, List<Ponto> vertices) {
        int cruzamentos = 0;
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Ponto a = vertices.get(i);
            Ponto b = vertices.get((i + 1) % n);
            if (((a.getY() > p.getY()) != (b.getY() > p.getY())) &&
                    (p.getX() < (b.getX() - a.getX()) * (p.getY() - a.getY()) / (b.getY() - a.getY()) + a.getX())) {
                cruzamentos++;
            }
        }
        return cruzamentos % 2 == 1;
    }

    /**
     * @return largura total ocupada apos transformacoes
     *
     */
    @Override
    public double getLarguraRenderizada() {
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        for (Ponto p : getVerticesTransformados()) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
        }
        return maxX - minX;
    }

    /**
     * @return altura total ocupada apos transformacoes
     *
     */
    @Override
    public double getAlturaRenderizada() {
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Ponto p : getVerticesTransformados()) {
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }
        return maxY - minY;
    }

    /**
     * Desenha o poligono com os vertices transformados.
     * @param g contexto grafico
     *
     */
    @Override
    public void draw(Graphics g) {
        List<Ponto> vertices = getVerticesTransformados();
        int[] xPoints = vertices.stream().mapToInt(p -> (int) p.getX()).toArray();
        int[] yPoints = vertices.stream().mapToInt(p -> (int) p.getY()).toArray();
        g.drawPolygon(xPoints, yPoints, vertices.size());
    }
}
