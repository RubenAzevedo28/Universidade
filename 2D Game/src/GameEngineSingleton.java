/**
 * Classe GameEngineSingleton.
 * Fornece acesso global ao estado do jogo, incluindo referencia ao motor,
 * sprites partilhadas, pontuacao e flags de fim de jogo.
 * Implementa o padrao Singleton.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class GameEngineSingleton {
    private static GameEngine instance;

    private static int score = 0;
    private static boolean gameOver = false;

    private static boolean gameWon = false;

    private static int vidas = 3;

    /**
     * Construtor privado para impedir instâncias.
     *
     */
    private GameEngineSingleton() {
    }

    /**
     * Devolve a instancia atual do motor de jogo.
     * @return o GameEngine ativo
     *
     */
    public static GameEngine getInstance() {
        return instance;
    }

    /**
     * Define a instancia do motor de jogo.
     * @param engine o GameEngine a associar
     *
     */
    public static void setInstance(GameEngine engine) {
        instance = engine;
    }

    /**
     * Adiciona pontos a pontuacao atual.
     * @param points numero de pontos a adicionar
     *
     */
    public static void addScore(int points) {
        score += points;
    }

    /**
     * Devolve a pontuacao atual.
     * @return pontuacao total
     *
     */
    public static int getScore() {
        return score;
    }

    /**
     * Verifica se o jogo terminou por derrota.
     * @return true se o jogo terminou, false caso contrario
     *
     */
    public static boolean isGameOver() {
        return gameOver;
    }

    /**
     * Define o estado de fim de jogo por derrota.
     * @param value true se o jogo terminou
     *
     */
    public static void setGameOver(boolean value) {
        gameOver = value;
    }

    /**
     * Verifica se o jogo foi ganho.
     * @return true se o jogador venceu
     *
     */
    public static boolean isGameWon() {
        return gameWon;
    }

    /**
     * Define o estado de fim de jogo por vitoria.
     * @param gw true se o jogador venceu
     *
     */
    public static void setGameWon(boolean gw) {
        gameWon = gw;
    }

    /**
     * Define o valor da pontuacao diretamente.
     * @param i nova pontuacao
     *
     */
    public static void setScore(int i) {
        score = i;
    }

    /**
     * Devolve o numero atual de vidas do jogador.
     * @return numero de vidas restantes
     *
     */
    public static int getVidas() {
        return vidas;
    }

    /**
     * Define o numero de vidas do jogador.
     * @param v novo numero de vidas
     *
     */
    public static void setVidas(int v) {
        vidas = v;
    }

    /**
     * Reduz o numero de vidas do jogador em 1.
     *
     */
    public static void perderVida() {
        vidas--;
    }

}
