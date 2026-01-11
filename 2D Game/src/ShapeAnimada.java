import javax.imageio.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
/**
 * Representa uma forma animada composta por varios frames.
 * Permite animacao de sprites ao longo do tempo.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class ShapeAnimada implements IShape {
    private final BufferedImage[] frames;
    private final double duracaoTotal;
    private final double duracaoFrame;
    private double tempoDecorrido = 0;

    /**
     * Construtor da ShapeAnimada.
     * Carrega uma sequencia de imagens com sufixo numerico.
     * @param basePath caminho base (ex: "/imagens/frame")
     * @param numFrames numero total de frames
     * @param duracaoTotal duracao total da animacao em segundos
     *
     */
    public ShapeAnimada(String basePath, int numFrames, double duracaoTotal) {
        this.frames = new BufferedImage[numFrames];
        this.duracaoTotal = duracaoTotal;
        this.duracaoFrame = duracaoTotal / numFrames;

        for (int i = 0; i < numFrames; i++) {
            String path = String.format("%s%d.png", basePath, i);
            try {
                frames[i] = ImageIO.read(getClass().getResourceAsStream(path));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Erro ao carregar frame: " + path);
            }
        }
    }

    /**
     * Desenha o frame atual da animacao com transformacoes aplicadas.
     * @param g contexto grafico
     * @param obj objeto ao qual a forma pertence
     *
     */
    @Override
    public void draw(Graphics2D g, GameObject obj) {
        tempoDecorrido += 1.0 / 60.0;
        int index = Math.min(frames.length - 1, (int)((tempoDecorrido % duracaoTotal) / duracaoFrame));
        BufferedImage frame = frames[index];
        if (frame == null) return;

        Transform t = (Transform) obj.transform();
        double x = t.position().getX();
        double y = t.position().getY();
        double angle = Math.toRadians(t.angle());

        int imgW = frame.getWidth();
        int imgH = frame.getHeight();

        double larguraAlvo = obj.collider().getLarguraRenderizada();
        double alturaAlvo = obj.collider().getAlturaRenderizada();

        double scaleX = larguraAlvo / imgW;
        double scaleY = alturaAlvo / imgH;

        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(angle);
        at.scale(scaleX, scaleY);
        at.translate(-imgW / 2.0, -imgH / 2.0);

        g.drawImage(frame, at, null);
    }

    /**
     * Devolve a largura da imagem base (sem escala).
     * @return largura em pixeis
     *
     */
    @Override
    public int getWidth() {
        return frames[0] != null ? frames[0].getWidth() : 0;
    }

    /**
     * Devolve a altura da imagem base (sem escala).
     * @return altura em pixeis
     *
     */
    @Override
    public int getHeight() {
        return frames[0] != null ? frames[0].getHeight() : 0;
    }
}
