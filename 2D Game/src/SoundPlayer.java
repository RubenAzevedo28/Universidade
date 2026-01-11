import javax.sound.sampled.*;
import java.io.*;

/**
 * Classe SoundPlayer.
 * Responsavel por carregar e tocar ficheiros de audio WAV no jogo.
 * Suporta reproducao unica e em loop com ajuste de volume.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class SoundPlayer {

    /**
     * Toca um som uma unica vez.
     * @param filePath caminho para o ficheiro de som (.wav), ex: "/sounds/explosao.wav"
     * @param volumeDb volume em decibeis (ex: -10.0f = mais baixo, 0.0f = volume maximo)
     *
     */
    public static void playSound(String filePath, float volumeDb) {
        try (InputStream inputStream = SoundPlayer.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                System.err.println("Nao foi possivel encontrar o ficheiro: " + filePath);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeDb);
            }

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toca um som continuamente em loop.
     * Utilizado por exemplo para som ambiente.
     * @param filePath caminho para o ficheiro de som (.wav), ex: "/sounds/ambiente.wav"
     * @param volumeDb volume em decibeis
     *
     */
    public static void playLoopSound(String filePath, float volumeDb) {
        try (InputStream inputStream = SoundPlayer.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                System.err.println("Nao foi possivel encontrar o ficheiro: " + filePath);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeDb);
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
