# 🚀 Space Invaders 2D — Java Game Engine

## 📝 Descrição do Projeto

Este projeto consiste numa reinterpretação do clássico **Space Invaders**, desenvolvido no âmbito da unidade curricular de **Programação Orientada por Objetos (POO)** da **Universidade do Algarve** no ano letivo de **2024/2025**.

O jogo foi implementado em **Java**, utilizando **Swing/AWT** para a interface gráfica, e assenta numa **arquitetura modular e orientada a objetos**, com separação clara entre lógica de jogo, rendering, deteção de colisões, input e gestão de níveis.

O objetivo do jogador é controlar uma nave espacial e eliminar todos os inimigos ao longo de **5 níveis de dificuldade progressiva**, culminando num combate contra **Bosses**, antes de perder todas as vidas.

## 🎮 Funcionalidades Principais

- 🧩 **5 níveis distintos**, com padrões variados de inimigos  
- ❤️ **Sistema de vidas** e 🏆 **pontuação**
- 🕹️ **Movimento do jogador** em todas as direções
- 🚀 **Disparo de projéteis**
- 👾 **Inimigos com comportamentos distintos**
  - Inimigos normais com movimento coordenado
  - Bosses com vida própria e múltiplos disparos
- 💥 **Sistema de colisões polimórfico**
  - Colisões circulares (`CircleCollider`)
  - Colisões poligonais (`PolygonCollider`)
- ✨ **Feedback visual**
  - Explosões animadas
  - Fade-in / fade-out de objetos e transições entre níveis
  - Piscar temporizado do jogador após colisões
- 🔊 **Som ambiente e efeitos sonoros**
- 📜 **Menu inicial e instruções integradas**

## 🎮 Controlos do Jogo

| ⌨️ Tecla  | 🎯 Ação |
|--------|---------|
| `W` / `↑` | Mover para cima |
| `S` / `↓` | Mover para baixo |
| `A` / `←` | Mover para a esquerda |
| `D` / `→` | Mover para a direita |
| `Espaço` | Disparar |
| `Esc` | Pausa |


## 🏗️ Arquitetura do Projeto

O projeto é composto por **34 classes e interfaces**, organizadas segundo os princípios da Programação Orientada por Objetos, promovendo **modularidade, reutilização e extensibilidade**.

### ⚙️ Núcleo do Motor
- `GameEngine.java` — Loop principal do jogo (update, render e input)
- `GameEngineSingleton.java` — Acesso global ao motor e ao estado do jogo
- `Main.java` — Ponto de entrada da aplicação
- `GameGUI.java` — Interface gráfica baseada em `JFrame`

### 🧱 GameObjects e Componentes
- `GameObject.java`, `IGameObject.java`
- `Transform.java`, `ITransform.java`
- `Behaviour.java`, `IBehaviour.java`
- `Collider.java`, `CircleCollider.java`, `PolygonCollider.java`
- `Shape.java`, `ShapeAnimada.java`, `IShape.java`
- `Ponto.java`, `SegmentoReta.java`

### 🤖 Behaviours
- `PlayerBehaviour.java` — Controlo do jogador e gestão de vidas
- `EnemyBehaviour.java` — Inimigos normais
- `EnemyBossBehaviour.java` — Bosses
- `BulletBehaviour.java`, `EnemyBulletBehaviour.java`
- `ExplosionBehaviour.java` — Explosões animadas

### 🗺️ Níveis
- `Nivel.java` (classe abstrata)
- `Nivel1.java` → `Nivel5.java` — Diferentes padrões de inimigos e Boss final

### ⌨️ Input & 🔊 Som
- `InputEventSwing.java`, `IInputEvent.java` — Captura de eventos de teclado
- `SoundPlayer.java` — Reprodução de sons WAV com suporte a loop

## 🧠 Padrões de Projeto Utilizados

- 🔁 **Singleton** — Garantir uma única instância do motor (`GameEngineSingleton`)
- 🧩 **Strategy** — Encapsulamento dos comportamentos dos objetos do jogo
- 👀 **Observer (implícito)** — Notificação de colisões através do método `onCollision`

## 🛠️ Tecnologias Utilizadas

- ☕ **Java**
- 🪟 **Swing / AWT**
- 🖼️ Pasta `/images` — Sprites e animações
- 🔉 Pasta `/sounds` — Sons e música ambiente

## 👥 Autor

**Rúben Azevedo**

Projeto desenvolvido no âmbito de um **projeto académico em grupo**, inspirado no jogo **Space Invaders**.
