import java.awt.event.*;
import java.util.*;
/**
 * Classe InputEventSwing.
 * Implementa a interface IInputEvent usando eventos de teclado Swing (KeyListener).
 * Permite detetar teclas pressionadas e libertadas durante o jogo.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class InputEventSwing implements IInputEvent, KeyListener {

    private final Set<String> pressedKeys = new HashSet<>();
    private final Set<String> releasedKeys = new HashSet<>();

    /**
     * Verifica se uma tecla esta atualmente pressionada.
     * @param key o nome da tecla (ex: "LEFT", "SPACE")
     * @return true se a tecla estiver pressionada
     *
     */
    @Override
    public boolean isKeyPressed(String key) {
        return pressedKeys.contains(key.toUpperCase());
    }

    /**
     * Verifica se uma tecla foi libertada desde o ultimo frame.
     * @param key o nome da tecla
     * @return true se a tecla foi libertada
     *
     */
    @Override
    public boolean isKeyReleased(String key) {
        return releasedKeys.contains(key.toUpperCase());
    }

    /**
     * Metodo chamado quando uma tecla e pressionada.
     * @param e o evento de tecla
     *
     */
    @Override
    public void keyPressed(KeyEvent e) {
        String keyName = KeyEvent.getKeyText(e.getKeyCode()).toUpperCase();
        pressedKeys.add(keyName);
        releasedKeys.remove(keyName);
    }

    /**
     * Metodo chamado quando uma tecla e libertada.
     * @param e o evento de tecla
     *
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String keyName = KeyEvent.getKeyText(e.getKeyCode()).toUpperCase();
        pressedKeys.remove(keyName);
        releasedKeys.add(keyName);
    }

    /**
     * Metodo chamado quando uma tecla e escrita (nao utilizado).
     * @param e o evento de tecla
     *
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Nao utilizado
    }

    /**
     * Limpa o registo de teclas libertadas apos cada frame.
     *
     */
    public void resetReleasedKeys() {
        releasedKeys.clear();
    }
}
