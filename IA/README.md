# 🤖 Inteligência Artificial (IA)

Esta pasta contém projetos desenvolvidos no âmbito da unidade curricular de **Inteligência Artificial (IA)** no ano letivo de **2024/2025**, abordando diferentes conceitos, técnicas e implementações práticas.

Cada subpasta corresponde a um projeto independente.

---

## 📂 Projetos Incluídos

### 📦 Containers

Projeto focado na resolução de um problema clássico de **planeamento e pesquisa em espaço de estados**, cujo objetivo é transformar uma configuração inicial de containers numa configuração final, respeitando um conjunto de regras e **minimizando o custo energético total**.

#### 🧠 Descrição do Problema
- Cada container possui um **custo energético** associado ao seu movimento  
- Apenas **um container pode ser movido de cada vez**  
- Só podem ser movidos containers **sem outros em cima**  
- Um container pode ser colocado:
  - No chão
  - Em cima de outro container livre  
- A ordem das pilhas no chão **não influencia o estado**
- Todas as instâncias possuem **solução única**

#### 🎯 Objetivo
Encontrar a **sequência de configurações**, desde o estado inicial até ao estado final, seguindo o **caminho de menor custo**, utilizando **Best-First Search**.

#### 🔍 Abordagem
- Representação explícita de estados
- Geração de sucessores válidos
- **Best-First Search** guiada pelo custo acumulado (f(n) = g(n))
- Comparação de estados independente da ordem das pilhas

#### 🛠️ Tecnologias
- Java
- Algoritmos de procura em espaço de estados

📁 Código-fonte disponível em:  
`Containers/src`

---

### 📦 Containers++
Extensão do projeto **Containers**, destinada à resolução de **instâncias maiores e mais complexas**, recorrendo a uma pesquisa mais eficiente.

#### 🎯 Objetivos
1. Validar a solução do projeto *Containers* em instâncias maiores  
2. Utilizar **Best-First Search com heurística admissível**  
3. Comparar desempenho com pesquisa baseada apenas em custo

#### 🔍 Abordagem
- **Best-First Search heurística**
- Definição de heurística admissível
- Extensão da interface `Ilayout`
- Avaliação do impacto da heurística na eficiência da pesquisa

#### 📊 Avaliação e Métricas
Para cada instância analisada:
- **E** – número de nós expandidos
- **G** – número de nós gerados
- **L** – comprimento da solução
- **P** – penetrância

#### 📤 Output
Ao contrário do projeto *Containers*, apenas é apresentado:
- O **estado final**
- O **custo total mínimo**

#### 🛠️ Tecnologias
- Java
- Best-First Search
- Heurísticas admissíveis
- Planeamento
- Análise de desempenho

📁 Código-fonte disponível em:  
`Containers++/src`

---

### ✍️ Digit Recognizer

Este projeto aborda o problema de **Optical Character Recognition (OCR)**, mais especificamente a tarefa de **reconhecimento de dígitos manuscritos**, uma das aplicações mais clássicas de *Machine Learning*.

O reconhecimento de dígitos é frequentemente utilizado como benchmark para modelos de aprendizagem automática, sendo o **MNIST** um dos conjuntos de dados mais utilizados nesta área.

#### 🎯 Objetivo

O objetivo deste laboratório é **construir e treinar uma pequena rede neuronal em Java**, capaz de distinguir entre os dígitos **0 e 1**, utilizando um subconjunto simplificado do dataset MNIST.

#### 🧠 Descrição do Problema

- Imagens em **tons de cinzento (grayscale)**
- Resolução: **20x20 pixels**
- Cada imagem é representada por **400 atributos**
- Classificação binária: dígitos **0** e **1**
- Total de **800 exemplos** no conjunto de dados

#### 📊 Conjunto de Dados

O projeto utiliza dois ficheiros principais:

##### `dataset.csv`
- Cada linha representa uma imagem
- Cada coluna corresponde à intensidade de um pixel
- Total de **400 colunas** por linha

##### `labels.csv`
- Contém as etiquetas associadas às imagens
- Cada etiqueta indica o dígito correspondente (**0 ou 1**)

#### 🛠️ Tecnologias e Conceitos

- **Java**
- Redes Neuronais
- Machine Learning
- Classificação binária
- Processamento de dados
- Dataset MNIST (subconjunto)

📁 Código-fonte disponível em:  
`DigitRecognizer/src`

#### 🎯 Objetivo Geral
Aplicar conceitos teóricos de Inteligência Artificial em projetos práticos, reforçando a compreensão de algoritmos, modelos e técnicas de resolução de problemas.

---

## 👤 Autor
**Rúben Azevedo**  
Projeto desenvolvido no âmbito académico.
