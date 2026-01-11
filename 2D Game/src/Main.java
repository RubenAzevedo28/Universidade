/**
 * Classe principal do programa.
 * Inicializa o motor de jogo, carrega sprites, cria os objetos iniciais (jogador e inimigos)
 * e inicia a interface grafica.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class Main {
    /**
     * Ponto de entrada do jogo.
     * Cria o motor de jogo, carrega imagens, instacia os objetos iniciais e arranca a GUI.
     * @param args argumentos de linha de comando (nao utilizados)
     *
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        GameEngineSingleton.setInstance(engine);

        // Iniciar a GUI
        GameGUI.start(engine);
    }
}
