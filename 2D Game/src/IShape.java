import java.awt.Graphics2D;
/**
 * Interface IShape.
 * Define como um objeto grafico deve ser desenhado no contexto 2D.
 * Pode representar uma imagem estatica ou animada.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */

public interface IShape {
    /**
     * Desenha a forma grafica associada ao objeto do jogo.
     *
     * @param g contexto grafico 2D onde desenhar
     * @param obj objeto do jogo ao qual a forma pertence
     *
     */
    void draw(Graphics2D g, GameObject obj);

    /**
     * Retorna a largura natural da imagem (sem aplicar escala).
     *
     * @return largura original em pixeis
     */
    int getWidth();

    /**
     * Retorna a altura natural da imagem (sem aplicar escala).
     *
     * @return altura original em pixeis
     *
     */
    int getHeight();

    /**
     * Atualiza o estado interno da forma (por exemplo, se for animada).
     * Metodo por defeito nao faz nada, util para formas estaticas.
     *
     * @param dT intervalo de tempo desde o ultimo frame
     *
     */
    default void atualizar(double dT) {
        // Por defeito, não faz nada (útil para Shape normal)
    }
}

