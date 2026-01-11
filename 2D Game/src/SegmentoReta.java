/**
 * Representa um segmento de reta definido por dois pontos.
 * Inclui metodos para verificar intersecao com outro segmento e com um retangulo.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class SegmentoReta {
    private final Ponto p1, p2;

    /**
     * Construtor para criar um segmento de reta entre dois pontos.
     *
     * @param p1 Primeiro ponto
     * @param p2 Segundo ponto
     *
     */
    public SegmentoReta(Ponto p1, Ponto p2) {
        if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
            System.exit(0);
        }
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Verifica se este segmento interseta outro segmento.
     * Utiliza o algoritmo de intersecao baseado em determinantes.
     *
     * @param outro Segmento para verificar intersecao
     * @return true se os segmentos se intersectam, false caso contrario
     *
     */
    public boolean interseta(SegmentoReta outro) {
        double det = (outro.p2.getX() - outro.p1.getX()) * (p2.getY() - p1.getY()) - (outro.p2.getY() - outro.p1.getY()) * (p2.getX() - p1.getX());

        if (det == 0) return false; // Segmentos paralelos ou coincidentes

        double s = ((outro.p2.getX() - outro.p1.getX()) * (outro.p1.getY() - p1.getY()) - (outro.p2.getY() - outro.p1.getY()) * (outro.p1.getX() - p1.getX())) / det;
        double t = ((p2.getX() - p1.getX()) * (outro.p1.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (outro.p1.getX() - p1.getX())) / det;

        return (0 < s && s < 1) && (0 < t && t < 1);
    }

    /**
     * Calcula a menor distancia entre este segmento de reta e um ponto dado.
     * Utiliza projecao vetorial para encontrar o ponto mais próximo no segmento.
     *
     * @param p O ponto para calcular a distancia.
     * @return A menor distancia entre o ponto e o segmento.
     *
     */
    public double distanciaParaPonto(Ponto p) {
        double comprimento2 = p1.distancia(p2) * p1.distancia(p2); // Comprimento ao quadrado do segmento
        if (comprimento2 == 0) return p1.distancia(p); // Caso de segmento degenerado (pontos coincidentes)

        // Projecao do vetor ponto -> p1 no vetor p1 -> p2
        double t = ((p.getX() - p1.getX()) * (p2.getX() - p1.getX()) +
                (p.getY() - p1.getY()) * (p2.getY() - p1.getY())) / comprimento2;

        // Limita t ao intervalo [0,1] para garantir que permanece dentro do segmento
        t = Math.max(0, Math.min(1, t));

        // Calcula a projecao do ponto no segmento
        Ponto projecao = new Ponto((int) (p1.getX() + t * (p2.getX() - p1.getX())),
                (int) (p1.getY() + t * (p2.getY() - p1.getY())));

        // Retorna a distancia entre o ponto original e a projecao encontrada
        return p.distancia(projecao);
    }

    /**
     * Verifica se este segmento de reta interseta um circulo.
     * A intersecao ocorre se a menor distancia entre o segmento e o centro do circulo
     * for menor ou igual ao raio do circulo.
     *
     * @param circulo O circulo para verificar intersecao.
     * @return true se houver intersecao, false caso contrario.
     *
     */
    public boolean intersetaCirculo(CircleCollider circulo) {
        double distancia = this.distanciaParaPonto(circulo.centroid());
        return distancia <= circulo.getRaio();
    }
}