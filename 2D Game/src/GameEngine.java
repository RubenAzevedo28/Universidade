import java.util.*;
/**
 * Classe GameEngine.
 * Representa o motor de jogo responsavel por gerir objetos ativos,
 * aplicar transformacoes e detetar colisoes.
 *
 * @author [P4Grupo8]
 * @version 3.0 (25/05/2025)
 *
 */
public class GameEngine {
    private final List<GameObject> enabledObjects = new ArrayList<>();
    private final List<GameObject> disabledObjects = new ArrayList<>();
    private final Map<GameObject, double[]> movimentos = new HashMap<>();

    /**
     * Adiciona um objeto ao jogo como ativo.
     * @param go o GameObject a adicionar
     * @param movimento vetor com [dx, dy, dlayer, drot, dscale]
     *
     */
    public void addEnabled(GameObject go, double[] movimento) {
        enabledObjects.add(go);
        movimentos.put(go, movimento);
        if (go.behaviour() != null) go.behaviour().onEnabled();
    }

    /**
     * Adiciona um objeto ao jogo como desativado.
     * @param go o GameObject a adicionar
     * @param movimento vetor com [dx, dy, dlayer, drot, dscale]
     *
     */
    public void addDisabled(GameObject go, double[] movimento) {
        disabledObjects.add(go);
        movimentos.put(go, movimento);
        if (go.behaviour() != null) go.behaviour().onDisabled();
    }

    /**
     * Ativa um GameObject que estava desativado.
     * @param go o GameObject a ativar
     *
     */
    public void enable(GameObject go) {
        if (disabledObjects.remove(go)) {
            enabledObjects.add(go);
            if (go.behaviour() != null) go.behaviour().onEnabled();
        }
    }

    /**
     * Desativa um GameObject que estava ativo.
     * @param go o GameObject a desativar
     *
     */
    public void disable(GameObject go) {
        if (enabledObjects.remove(go)) {
            disabledObjects.add(go);
            if (go.behaviour() != null) go.behaviour().onDisabled();
        }
    }

    /**
     * Remove um GameObject do motor e invoca o seu onDestroy.
     * @param go o GameObject a destruir
     *
     */
    public void destroy(GameObject go) {
        enabledObjects.remove(go);
        disabledObjects.remove(go);
        movimentos.remove(go);
        if (go.behaviour() != null) go.behaviour().onDestroy();
    }

    /**
     * Remove todos os objetos do jogo e limpa os dados internos.
     *
     */
    public void destroyAll() {
        for (GameObject go : enabledObjects) {
            if (go.behaviour() != null) go.behaviour().onDestroy();
        }
        for (GameObject go : disabledObjects) {
            if (go.behaviour() != null) go.behaviour().onDestroy();
        }
        enabledObjects.clear();
        disabledObjects.clear();
        movimentos.clear();
    }

    /**
     * Executa o ciclo de jogo durante um numero de frames.
     * Aplica movimentos, atualiza comportamentos e deteta colisoes.
     * @param frames numero de frames a simular
     * @param input estado do input atual
     *
     */
    public void run(int frames, IInputEvent input) {
        if (GameEngineSingleton.isGameOver() || GameEngineSingleton.isGameWon()) {
            return; // Se o jogo acabou, para tudo
        }
        for (int f = 0; f < frames; f++) {
            List<GameObject> snapshot = new ArrayList<>(enabledObjects);
            for (GameObject go : snapshot) {
                double[] m = movimentos.get(go);
                if (m != null) {
                    go.transform().move(new Ponto(m[0], m[1]), (int) m[2]);
                    go.transform().rotate(m[3]);
                    go.transform().scale(m[4]);
                }
                if (go.behaviour() != null) {
                    go.behaviour().onUpdate(1.0, input); // dT = 1.0 frame
                }
            }

            //guardar colisões
            Map<GameObject, Set<GameObject>> colisoes = collectCollisions();
            for (var entry : colisoes.entrySet()) {
                GameObject a = entry.getKey();
                for (GameObject b : entry.getValue()) {
                    //System.out.printf("Frame %d: %s colidiu com %s%n", f + 1, a.name(), b.name());
                }
            }
        }
        //System.out.println("Pontuação atual: " + GameEngineSingleton.getScore());
    }

    /**
     * Deteta colisoes entre objetos ativos e notifica os comportamentos.
     * @return mapa de colisoes entre objetos
     *
     */
    private Map<GameObject, Set<GameObject>> collectCollisions() {
        Map<GameObject, Set<GameObject>> colisoes = new HashMap<>();

        for (int i = 0; i < enabledObjects.size(); i++) {
            GameObject a = enabledObjects.get(i);
            for (int j = i + 1; j < enabledObjects.size(); j++) {
                GameObject b = enabledObjects.get(j);
                if (a.transform().layer() != b.transform().layer()) continue;
                if (a.collider().collidesWith(b.collider())) {
                    colisoes.computeIfAbsent(a, k -> new LinkedHashSet<>()).add(b);
                    colisoes.computeIfAbsent(b, k -> new LinkedHashSet<>()).add(a);

                    if (a.behaviour() != null)
                        a.behaviour().onCollision(Collections.singletonList(b));
                    if (b.behaviour() != null)
                        b.behaviour().onCollision(Collections.singletonList(a));
                }
            }
        }
        return colisoes;
    }

    /**
     * Devolve a lista de objetos atualmente ativos.
     * @return lista de GameObject ativos
     *
     */
    public List<GameObject> getEnabled() {
        return enabledObjects;
    }

    /**
     * Devolve a lista de objetos atualmente desativados.
     * @return lista de GameObject desativados
     *
     */
    public List<GameObject> getDisabled() {
        return disabledObjects;
    }
}