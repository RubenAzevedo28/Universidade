import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;
/**
 * Classe GameGUI.
 * Interface grafica principal do jogo. Utiliza Swing para apresentar o ecra de inicio, pausa,
 * jogo e fim. Gera e atualiza o rendering dos objetos do jogo.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class GameGUI extends JFrame {
    private long lastTime = System.nanoTime();
    private int frames = 0;
    private double fps = 0;

    private final GameEngine engine;
    private final List<GameObject> objects;
    private final InputEventSwing input = new InputEventSwing();
    private final BufferedImage backgroundSprite;
    private double backgroundOffset = 0;
    private static final double BACKGROUND_SPEED = 40.0;
    private Timer timer; //Importante para controlar o jogo frame a frame

    private BufferedImage heartImage;


    private enum GameState { START, INSTRUCTIONS, RUNNING, PAUSED, GAME_OVER }

    private GameState gameState = GameState.START;

    private int menuSelection = 0; // 0 = Jogar, 1 = Instruções, 2 = Sair
    private final String[] menuOptions = {"Jogar", "Instruções", "Sair"};

    private final String[] pauseMenuOptions = {"Voltar", "Menu"};
    private int pauseMenuSelection = 0;

    private List<Nivel> niveis;
    private int nivelAtual;

    private boolean emTransicao = false;
    private float fadeAlpha = 0f;
    private boolean aEscurecer = true;
    private Runnable aoTerminarFade = null;
    private String textoTransicao = "";

    /**
     * Construtor da interface grafica.
     * Inicializa o ecra, menus, som de fundo e ciclo principal de atualizacao.
     * @param engine instancia do motor de jogo
     *
     */
    public GameGUI(GameEngine engine) {
        this.engine = engine;
        this.objects = engine.getEnabled();

        BufferedImage tempBackground = null;
        try {
            tempBackground = ImageIO.read(getClass().getResourceAsStream("/images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.backgroundSprite = tempBackground;

        try {
            heartImage = ImageIO.read(getClass().getResourceAsStream("/images/heart.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar heart.png");
            heartImage = null;
        }


        setTitle("Game Engine 2D - GUI");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (backgroundSprite != null) {
                    drawBackground(g);
                }

                if (gameState == GameState.START) {
                    drawStartScreen(g);
                } else if (gameState == GameState.INSTRUCTIONS) {
                    drawInstructionsScreen(g);
                }else if (gameState == GameState.RUNNING) {
                    drawObjects(g);
                } else if (gameState == GameState.PAUSED) {
                    drawObjects(g);
                    drawPauseMenu(g);
                }

            }
        };
        panel.setFocusable(true);
        panel.addKeyListener(input); // Conectar input do teclado!
        add(panel);

        SoundPlayer.playLoopSound("/sounds/Ambience_Space_00.wav",  0.0f);

        Timer timer = new Timer(1000 / 60, e -> {
            if (gameState == GameState.START) {
                if (input.isKeyReleased("UP")) {
                    menuSelection = (menuSelection - 1 + menuOptions.length) % menuOptions.length;
                }
                if (input.isKeyReleased("DOWN")) {
                    menuSelection = (menuSelection + 1) % menuOptions.length;
                }
                if (input.isKeyReleased("ENTER")) {
                    if (menuSelection == 0) {
                        iniciarNiveis();
                        gameState = GameState.RUNNING;
                    } else if (menuSelection == 1) {
                        gameState = GameState.INSTRUCTIONS;
                    } else if (menuSelection == 2) {
                        System.exit(0);
                    }
                }
            } else if (gameState == GameState.RUNNING) {
                if (input.isKeyReleased("ESCAPE")) {
                    gameState = GameState.PAUSED;
                } else {
                    engine.run(1, input);
                    if (nivelAtual < niveis.size() && niveis.get(nivelAtual).concluido() && !emTransicao) {
                        engine.destroyAll();
                        nivelAtual++;
                        if (nivelAtual >= niveis.size()) {
                            GameEngineSingleton.setGameWon(true); // sem fade no fim
                            SoundPlayer.playSound("/sounds/Victory.wav", -5.0f);
                        } else {
                            iniciarFade("Nível " + (nivelAtual + 1), () -> {
                                SoundPlayer.playSound("/sounds/levelup.wav", -20.0f);
                                niveis.get(nivelAtual).carregar();
                            });
                        }
                    }
                }
            } else if (gameState == GameState.PAUSED) {
                if (input.isKeyReleased("UP")) {
                    pauseMenuSelection = (pauseMenuSelection - 1 + pauseMenuOptions.length) % pauseMenuOptions.length;
                }
                if (input.isKeyReleased("DOWN")) {
                    pauseMenuSelection = (pauseMenuSelection + 1) % pauseMenuOptions.length;
                }
                if (input.isKeyReleased("ENTER")) {
                    if (pauseMenuSelection == 0) {
                        gameState = GameState.RUNNING;
                    } else if (pauseMenuSelection == 1) {
                        engine.destroyAll();
                        GameEngineSingleton.setGameOver(false);
                        GameEngineSingleton.setGameWon(false);
                        GameEngineSingleton.setScore(0);
                        gameState = GameState.START;
                    }
                }
            }

            if (gameState == GameState.INSTRUCTIONS && input.isKeyReleased("ESCAPE")) {
                gameState = GameState.START;
            }

            // Verifica se carregaste na tecla "R"
            if (input.isKeyReleased("R")) {
                restartGame();
            }

            input.resetReleasedKeys();

            backgroundOffset += BACKGROUND_SPEED / 60.0;
            if (backgroundSprite != null && backgroundOffset > backgroundSprite.getHeight()) {
                backgroundOffset = 0;
            }

//            frames++;
//            long now = System.nanoTime();
//            if (now - lastTime >= 1_000_000_000) { // 1 segundo passou
//                fps = frames;
//                frames = 0;
//                lastTime = now;
//                //System.out.println("FPS: " + fps); // ou atualiza uma label se quiseres mostrar no ecrã
//            }

            if (emTransicao) {
                if (aEscurecer) {
                    fadeAlpha += 0.02f;
                    if (fadeAlpha >= 1f) {
                        fadeAlpha = 1f;
                        aEscurecer = false;
                        if (aoTerminarFade != null) aoTerminarFade.run();
                    }
                } else {
                    fadeAlpha -= 0.02f;
                    if (fadeAlpha <= 0f) {
                        fadeAlpha = 0f;
                        emTransicao = false;
                    }
                }
            }

            repaint();
        });
        timer.start();
    }

    /**
     * Inicia os niveis do jogo, criando a lista de niveis e carregando o primeiro.
     * Tambem define o numero inicial de vidas.
     *
     */
    private void iniciarNiveis() {
        this.niveis = List.of(new Nivel1(engine),
            new Nivel2(engine),
            new Nivel3(engine),
            new Nivel4(engine),
            new Nivel5(engine)
            );
        this.nivelAtual = 0;
        niveis.get(nivelAtual).carregar();
        GameEngineSingleton.setVidas(3);
    }

    /**
     * Inicia o efeito de transicao com fade e texto opcional.
     * Executa uma acao quando o fade escurece totalmente.
     * @param texto texto a exibir durante o fade
     * @param acaoDepoisDoFade acao a executar apos o fade escurecer
     *
     */
    private void iniciarFade(String texto, Runnable acaoDepoisDoFade) {
        emTransicao = true;
        fadeAlpha = 0f;
        aEscurecer = true;
        aoTerminarFade = acaoDepoisDoFade;
        textoTransicao = texto;
    }

    /**
     * Desenha todos os objetos do jogo (sprites ou coliders).
     * Aplica rotacao e escala conforme a transformacao de cada objeto.
     * @param g contexto grafico
     *
     */
    private void drawObjects(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create(); // Criar uma cópia segura

        for (GameObject obj : objects) {
            if (obj.getShape() != null) {
                obj.tickBlink();

                boolean show = true;
                if (obj.isBlinking()) {
                    show = obj.shouldRenderWhileBlinking();
                }

                if (show) {
                    if (obj.getShape() != null) {
                        obj.getShape().draw(g2d, obj);
                    }
                    //obj.collider().draw(g);
                }

            } else {
                obj.collider().draw(g);
            }

        }
        g2d.dispose(); // Limpar o Graphics2D depois

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.setColor(Color.WHITE); // cor do texto
        g.drawString("Score: " + GameEngineSingleton.getScore(), 10, 30);

        if (GameEngineSingleton.isGameWon()) {
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
            g.setColor(Color.GREEN);
            g.drawString("VICTORY!", 475, 500);
        }

        if (GameEngineSingleton.isGameOver()) {
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", 450, 500);
        }

        //FPS
//        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
//        g.setColor(Color.YELLOW);
//        g.drawString("FPS: " + (int) fps, getWidth() - 100, 30);

        //g.drawString("Vidas: " + GameEngineSingleton.getVidas(), 10, 60);
        for (int i = 0; i < GameEngineSingleton.getVidas(); i++) {
            g.drawImage(heartImage, 10 + i * 40, 60, 30, 30, null);
        }

        if (emTransicao) {
            Graphics2D g2dFade = (Graphics2D) g.create();
            g2dFade.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            g2dFade.setColor(Color.BLACK);
            g2dFade.fillRect(0, 0, getWidth(), getHeight());

            // Texto durante o fade
            g2dFade.setFont(new Font("Monospaced", Font.BOLD, 48));
            g2dFade.setColor(new Color(255, 255, 255, (int)(fadeAlpha * 255)));
            int textWidth = g2dFade.getFontMetrics().stringWidth(textoTransicao);
            g2dFade.drawString(textoTransicao, (getWidth() - textWidth) / 2, getHeight() / 2);

            g2dFade.dispose();
        }

    }

    /**
     * Desenha o ecra inicial do menu principal com opcoes.
     * @param g contexto grafico
     *
     */
    private void drawStartScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Monospaced", Font.BOLD, 60)); // Título maior e mais arcade
        g.setColor(Color.WHITE);
        g.drawString("SPACE INVADERS", 350, 150); // ligeiro ajuste de posição

        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        for (int i = 0; i < menuOptions.length; i++) {
            if (i == menuSelection) {
                Color highlight = (System.currentTimeMillis() / 300) % 2 == 0 ? Color.GREEN : Color.YELLOW;
                g.setColor(highlight);
                g.drawString("> " + menuOptions[i], 310, 250 + i * 50);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("  " + menuOptions[i], 310, 250 + i * 50);
            }
        }
    }

    /**
     * Desenha o ecra de instrucoes com os comandos e regras do jogo.
     * @param g contexto grafico usado para desenhar
     *
     */
    private void drawInstructionsScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString("INSTRUÇÕES", 500, 100);

        g.setFont(new Font("Monospaced", Font.PLAIN, 20));
        int y = 160;
        g.drawString("- Setas ou W/A/S/D para mover", 100, y += 40);
        g.drawString("- ESPAÇO para disparar", 100, y += 30);
        g.drawString("- ESC para pausar o jogo", 100, y += 30);
        g.drawString("- R para reiniciar o nível", 100, y += 30);
        g.drawString("- Derrota todos os inimigos para vencer", 100, y += 30);
        g.drawString("- Tens 3 vidas", 100, y += 30);

        g.setFont(new Font("Monospaced", Font.PLAIN, 22));
        g.setColor(Color.YELLOW);
        g.drawString("Pressiona ESCAPE para voltar ao menu", 280, y + 60);
    }

    /**
     * Desenha o menu de pausa com opcoes: voltar ao jogo ou voltar ao menu principal.
     * @param g contexto grafico
     *
     */
    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Monospaced", Font.BOLD, 45));
        g.setColor(Color.WHITE);
        g.drawString("PAUSA", 550, 200);

        g.setFont(new Font("Monospaced", Font.PLAIN, 35));
        for (int i = 0; i < pauseMenuOptions.length; i++) {
            if (i == pauseMenuSelection) {
                g.setColor(Color.GREEN);
                g.drawString(" " + pauseMenuOptions[i], 550, 270 + i * 40);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(pauseMenuOptions[i], 550, 270 + i * 40);
            }
        }
    }

    /**
     * Reinicia o jogo completamente.
     * Limpa objetos, reseta variaveis globais e reinicializa jogador e inimigos.
     *
     */
    private void restartGame() {
        engine.destroyAll(); // Limpa todos os objetos vivos

        GameEngineSingleton.setGameOver(false);
        GameEngineSingleton.setGameWon(false);
        GameEngineSingleton.setScore(0);
        GameEngineSingleton.setVidas(3);

        iniciarNiveis();
        if (timer == null || !timer.isRunning()) {
            return; // Se o timer ainda não existe, simplesmente não faz nada
        }
        timer.start();

    }

    /**
     * Desenha o ecra de fundo com movimento vertical.
     * @param g contexto grafico
     *
     */
    private void drawBackground(Graphics g) {
        if (backgroundSprite != null) {
            int imgHeight = backgroundSprite.getHeight();
            int offset = (int) backgroundOffset;

            g.drawImage(backgroundSprite, 0, offset - imgHeight, getWidth(), imgHeight, null);
            g.drawImage(backgroundSprite, 0, offset, getWidth(), imgHeight, null);
        }
    }

    /**
     * Metodo de entrada para iniciar a GUI.
     * @param engine instancia do motor de jogo
     *
     */
    public static void start(GameEngine engine) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI(engine);
            gui.setVisible(true);
        });
    }
}
