import java.util.*;

public class BestFirst {
    protected Queue<State> abertos;
    private Map<Ilayout, Double> fechados;
    private State actual;
    private Ilayout objective;

    static class State {
        final Ilayout layout;
        private final State father;
        private final double g;

        public State(Ilayout l, State n) {
            layout = l;
            father = n;

            if (father != null)
                g = father.g + l.getG(); //
            else g = 0.0f;
        }

        public double getG() {
            return g;
        }

        @Override
        public String toString() {
            return layout.toString();
        }


        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o==null) return false;
            if (this.getClass() != o.getClass()) return false;
            State n = (State) o;
            return this.layout.equals(n.layout);
        }
    }

    final private List<State> sucessores(State n) {
        List<State> sucs = new ArrayList<>();
        List<Ilayout> children = n.layout.children(); // Cria uma lista para armazenar os filhos


        // Para cada configuração filha, gera os sucessores
        for (Ilayout e : children) {
            if (n.father == null || !e.equals(n.father.layout)) { // Verifica se não é igual ao pai
                State nn = new State(e, n);
                sucs.add(nn);
            }
        }
        return sucs;
    }


    final public Iterator<State> solve(Ilayout s, Ilayout goal) {
        objective = goal;
        abertos = new PriorityQueue<>(10, Comparator.comparingDouble(State::getG));
        fechados = new HashMap<>();
        abertos.add(new State(s, null));
        List<State> sucs;
        fechados.put(s, 0.0);

        while(!abertos.isEmpty()) {

            actual = abertos.poll();

            if (fechados.get(actual.layout) < actual.getG()) {
                continue;
            }

            if (actual.layout.isGoal(objective)) {
                List<State> solutions = new ArrayList<>();
                State temp = actual;
                for (; temp.father != null; temp = temp.father) {
                    solutions.add(0, temp);
                }
                solutions.add(0, temp);
                return solutions.iterator();
            } else {
                sucs = this.sucessores(actual);
                for (State successor : sucs) {
                    double totalCost = successor.getG();
                    Double existingCost = fechados.get(successor.layout);

                    if (existingCost == null || totalCost < existingCost) {
                        fechados.put(successor.layout, totalCost);
                        abertos.add(successor);
                    }
                }
            }
        }
        return null;
    }
}
