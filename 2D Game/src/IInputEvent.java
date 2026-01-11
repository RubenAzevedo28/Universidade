/**
 * Interface IInputEvent.
 * Representa o estado atual do teclado durante o jogo.
 * Permite verificar se uma tecla esta a ser pressionada ou foi libertada.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 */
public interface IInputEvent {

    /**
     * Verifica se a tecla especificada esta a ser pressionada neste momento.
     * @param key a tecla (ex: \"LEFT\", \"SPACE\")
     * @return true se estiver pressionada, false caso contrario
     *
     */
    boolean isKeyPressed(String key);

    /**
     * Verifica se a tecla especificada foi libertada desde o ultimo frame.
     * @param key a tecla (ex: \"ESCAPE\", \"R\")
     * @return true se foi libertada, false caso contrario
     *
     */
    boolean isKeyReleased(String key);
}
