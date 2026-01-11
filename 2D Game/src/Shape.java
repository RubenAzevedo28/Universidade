import javax.imageio.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
/**
 * Representa uma forma baseada numa imagem para desenhar um GameObject.
 * Utiliza transformacoes (escala, rotacao, translacao) para ajustar a imagem ao objeto.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Shape implements IShape {
    private final BufferedImage image;

    /**
     * Construtor da Shape.
     * Carrega a imagem a partir do caminho fornecido.
     * @param path caminho relativo para a imagem
     *
     */
    public Shape(String path) {
        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro ao carregar imagem: " + path);
        }
        this.image = loaded;
    }

    /**
     * Desenha a imagem associada com as transformacoes aplicadas ao GameObject.
     * @param g contexto grafico
     * @param obj objeto a desenhar
     *
     */
    @Override
    public void draw(Graphics2D g, GameObject obj) {
        if (image == null) return;

        Transform t = (Transform) obj.transform();
        double x = t.position().getX();
        double y = t.position().getY();
        double angle = Math.toRadians(t.angle());

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        double larguraAlvo = obj.collider().getLarguraRenderizada();
        double alturaAlvo = obj.collider().getAlturaRenderizada();

        double scaleX = larguraAlvo / imgW;
        double scaleY = alturaAlvo / imgH;

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(angle);
        at.scale(scaleX, scaleY);
        at.translate(-imgW / 2.0, -imgH / 2.0);

        g.drawImage(image, at, null);
    }

    /**
     * Devolve a largura natural da imagem sem escala.
     * @return largura da imagem original
     *
     */
    @Override
    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }

    /**
     * Devolve a altura natural da imagem sem escala.
     * @return altura da imagem original
     *
     */
    @Override
    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }
}
