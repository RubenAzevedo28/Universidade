import java.awt.*;
/**
 * Representa um colisor genérico para um GameObject.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public abstract class Collider implements ICollider {
    public final Transform transform;

    /**
     * Constroi um colisor associado a um Transform.
     * @param transform Transform do colisor.
     *
     */
    public Collider(Transform transform) {
        this.transform = transform;
    }

    /**
     * Obtem o centroide do colisor.
     * @return O ponto central do colisor.
     *
     */
    @Override
    public Ponto centroid() {
        return this.transform.position();
    }

    /**
     * Metodo abstrato que deve desenhar visualmente o colisor.
     *
     * @param g contexto grafico onde desenhar
     *
     */
    public abstract void draw(Graphics g);
}

