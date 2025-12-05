# Formulário de Projeto – Fase 4 (Compressão e Criptografia)

## 1. Compressão Huffman

* **a. Tamanho do arquivo original:** ~3 KB (Junção dos bancos de dados e chaves).
* **b. Tamanho do arquivo comprimido:** Variável (dependente da execução), gerado como `backup_huffman.cmp`.
* **c. Taxa de Compressão:** **-27,24%** (Expansão).
* **d. Interpretação do resultado:** O algoritmo implementou corretamente a lógica de frequência e montagem da árvore binária. A taxa de compressão negativa (expansão do arquivo) ocorreu porque o volume de dados testado foi muito pequeno (apenas alguns registros de teste). Nesses casos, o *overhead* do cabeçalho da árvore de Huffman (necessário para a descompressão) supera a economia de bits obtida na compactação dos dados. Em um cenário real com megabytes de dados, a taxa se tornaria positiva e eficiente.

## 2. Compressão LZW

* **a. Tamanho do arquivo original:** ~3 KB.
* **b. Tamanho do arquivo comprimido:** Variável, gerado como `backup_lzw.cmp`.
* **c. Taxa de Compressão:** **-174,87%** (Expansão).
* **d. Interpretação do resultado:** O LZW baseia-se na criação dinâmica de um dicionário de padrões. Como o teste foi realizado com poucos dados e pouca repetição de texto, o algoritmo não teve oportunidade de otimizar padrões recorrentes. Além disso, a implementação acadêmica armazenou os códigos de saída como inteiros de 4 bytes (32 bits) para garantir a integridade e facilitar a didática, o que aumenta o tamanho final em arquivos pequenos. A integridade dos dados, porém, foi mantida e restaurada com sucesso.

## 3. Dificuldades e Soluções

* **Huffman:** A principal dificuldade foi a manipulação de bits (*bitwise operations*) em Java, uma linguagem que trabalha nativamente com Bytes. Para resolver, foi necessário implementar uma lógica manual de bufferização para escrever bit a bit no arquivo final.
* **LZW:** O gerenciamento do dicionário dinâmico e a decisão sobre o tamanho dos códigos de saída foram desafiadores. A solução adotada foi simplificar a saída para tamanho fixo (Int 32 bits) ao invés de variável (9-12 bits) para evitar complexidade excessiva no prazo do projeto.
* **Backup Seguro:** Percebemos que restaurar o banco de dados sem as chaves de criptografia tornaria os dados ilegíveis. A solução foi aprimorar o `GerenciadorBackup` para incluir a pasta `data/chaves/` dentro do arquivo comprimido `.cmp`.

## 4. Estruturas de Dados Utilizadas

* **Huffman:**
    * **`PriorityQueue` (Min-Heap):** Escolhida para construir a árvore de Huffman. Ela permite recuperar os dois nós de menor frequência em tempo $O(\log n)$, garantindo que a árvore seja ótima.
    * **`HashMap`:** Usada para armazenar a tabela de códigos (Byte -> String de Bits), permitindo acesso $O(1)$ durante a varredura de compressão.
* **LZW:**
    * **`HashMap<List<Byte>, Integer>`:** Utilizada para o dicionário. A escolha de uma lista de bytes como chave permite verificar rapidamente ($O(1)$ médio) se uma sequência de caracteres já existe no dicionário.

## 5. Criptografia RSA

* **Campo Escolhido:** `CNPJ` da entidade `Fornecedor`.
* **Justificativa:** O CNPJ é um dado sensível que identifica a pessoa jurídica. Criptografá-lo atende a requisitos modernos de privacidade (como LGPD) e segurança da informação, protegendo o dado contra vazamentos caso o arquivo `.db` seja indevidamente acessado.

## 6. Implementação do RSA

* **a. Estrutura das chaves:** Par de chaves assimétricas (Pública e Privada) geradas pelo algoritmo RSA.
* **b. Armazenamento:** Serializadas em arquivos binários (`public.key` e `private.key`) no diretório `data/chaves/`.
* **c. Carregamento:** A classe `CriptografiaRSA` utiliza `KeyFactory` para ler os bytes dos arquivos e reconstruir as chaves em memória. O sistema possui "auto-cura": se os arquivos de chave não forem encontrados (ex: em uma instalação limpa), novas chaves são geradas automaticamente.
* **d. Tamanho das chaves:** **2048 bits**. Escolhido por ser o padrão de mercado atual recomendado pelo NIST para segurança corporativa.
* **e. Momento da Criptografia:** Ocorre dentro dos métodos `create()` e `update()` do `FornecedorDAO`, imediatamente antes da serialização dos dados para o disco.
* **f. Momento da Descriptografia:** Ocorre dentro dos métodos `read()` e `listAll()` do `FornecedorDAO`, logo após a leitura dos bytes do disco, permitindo que a View exiba o dado legível.
* **g. Conversões:** `String (Texto Claro)` $\to$ `Bytes` $\to$ `RSA Cipher` $\to$ `Bytes Cifrados` $\to$ `Base64 String` (para armazenamento seguro no objeto).