# Formulário de Projeto – Fase 5 (Casamento de Padrões)

## 1. Campo e Justificativa

* **Campo Escolhido:** Atributo `Nome` da entidade `ItemEstoque`.
* **Justificativa:** Em sistemas de gerenciamento de inventário ou e-commerce, a busca mais natural e frequente realizada por usuários é pelo nome ou descrição do produto (ex: buscar "Gamer" para encontrar "Cadeira Gamer" ou "PC Gamer"). Aplicar os algoritmos neste campo simula um cenário real de usabilidade, agregando valor prático à aplicação.

## 2. Funcionamento do KMP (Knuth-Morris-Pratt)

O algoritmo foi implementado para realizar buscas de padrão em tempo linear $O(N + M)$, evitando o retrocesso desnecessário no texto principal.

* **Pré-processamento:** O algoritmo constrói inicialmente um vetor auxiliar chamado **LPS** (*Longest Prefix Suffix*). Esse vetor analisa o padrão e armazena, para cada posição, o tamanho do maior prefixo que também é um sufixo.
* **Busca:** Durante a varredura do texto da esquerda para a direita, quando ocorre um "descasamento" (*mismatch*) entre um caractere do texto e do padrão, o algoritmo consulta o vetor LPS. Isso permite que o índice do padrão "pule" para a próxima posição segura de comparação sem precisar reiniciar a busca do zero no texto.

## 3. Funcionamento do Boyer-Moore

O algoritmo foi implementado utilizando a abordagem de comparação da direita para a esquerda, focando na eficiência através de saltos (desempenho sublinear).

* **Heurística:** Utilizamos a heurística do **Caractere Ruim** (*Bad Character*). O algoritmo pré-processa o padrão e cria uma tabela (baseada na tabela ASCII estendida de 256 posições) que registra a última ocorrência de cada caractere dentro do padrão.
* **Busca:** O algoritmo alinha o padrão com o texto e compara os caracteres do fim para o começo. Ao encontrar um caractere no texto que não corresponde ao padrão, ele verifica a tabela de caracteres ruins. Se o caractere existe no padrão, ele desloca o padrão para alinhar com essa ocorrência. Se não existe, ele realiza um salto maior, pulando todo o trecho alinhado.

## 4. Integração com o Sistema

A integração foi realizada conectando a Camada de Visão (`MainView`) diretamente à Camada de Dados (`ItemEstoqueDAO`):

1.  Foi adicionada a opção **"6. Pesquisar Padrão em Itens (KMP / BM)"** no menu principal.
2.  O usuário digita o termo de busca desejado.
3.  O sistema invoca o método `itemEstoqueDAO.listAll()`, que carrega todos os registros de itens do arquivo binário para uma lista em memória.
4.  O sistema itera sobre essa lista e, para cada objeto `ItemEstoque`, executa o algoritmo escolhido (`KMP.pesquisar` ou `BoyerMoore.pesquisar`) sobre o atributo `nome`.
5.  Caso o algoritmo retorne `true`, o item é exibido no console como um resultado positivo.

## 5. Dificuldades Encontradas

* **KMP:** A maior dificuldade foi compreender e implementar corretamente a lógica de construção do vetor **LPS**. Entender como identificar prefixos que são sufixos para determinar o índice correto de retorno exigiu testes cuidadosos para evitar loops infinitos ou saltos incorretos.
* **Boyer-Moore:** O desafio principal foi implementar a heurística do *Bad Character* de forma robusta, garantindo que o cálculo do deslocamento (*shift*) utilizasse a função `Math.max` para evitar deslocamentos negativos ou nulos, o que poderia travar o algoritmo. Além disso, o mapeamento correto da tabela para o conjunto ASCII estendido foi um ponto de atenção.