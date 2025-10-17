# AEDS3TP - Aplicativo de Gerenciamento de Dados em Memória Secundária

### Fase 2 - Índices B+ e Hash Extensível

Este projeto consiste no desenvolvimento de um aplicativo de gerenciamento de estoque que realiza a persistência de dados diretamente em arquivos binários, sem o uso de um SGBD. A Fase 2 implementa o CRUD completo para todas as entidades e utiliza estruturas de dados avançadas para indexação, como Hash Extensível para chaves primárias e Árvore B+ para chaves estrangeiras, otimizando as operações de busca.

## Componentes do Grupo

  * Arthur Braga de Campos Tinoco
  * Rafael Lima Mendonça Garcia

## Visão Geral do Projeto

O principal desafio deste trabalho é construir um sistema de banco de dados do zero, controlando o acesso aos dados em baixo nível. O sistema gerencia três entidades principais (Categorias, Fornecedores e Itens de Estoque) e seus relacionamentos, garantindo que todas as informações sejam persistidas em disco e que as buscas sejam eficientes através de índices customizados.

## Estrutura e Arquitetura do Projeto

O sistema segue a arquitetura **MVC + DAO (Model-View-Controller + Data Access Object)**, com os pacotes organizados por responsabilidade para garantir um código limpo e de fácil manutenção.

  * `src/app`: Ponto de entrada da aplicação. Contém a classe `Main` que inicializa e executa o sistema.
  * `src/view`: Camada de apresentação. A classe `MainView` é responsável por toda a interação com o usuário via console, exibindo menus e coletando entradas.
  * `src/model`: Classes de domínio (entidades). Representam a estrutura dos dados do sistema, como `Categoria`, `Fornecedor` e `ItemEstoque`.
  * `src/dao`: Camada de acesso a dados. As classes DAO são o coração da persistência, contendo a lógica para ler e escrever nos arquivos binários e para interagir com os índices.
  * `src/indices`: Implementações das estruturas de dados avançadas. Contém as classes `HashExtensivel` e `ArvoreBPlus`, que são utilizadas pelos DAOs para otimizar as buscas.
  * `data/`: Diretório na raiz do projeto onde todos os arquivos de dados (`.db`) e de índices são gerados e armazenados.

## Decisões Chave de Projeto

  * **Persistência Binária com `RandomAccessFile`:** A escolha por arquivos de acesso aleatório permite a leitura e escrita em posições específicas do arquivo, fundamental para a implementação de exclusão lógica e para o acesso direto aos registros via índices.
  * **Exclusão Lógica (Lápide):** Em vez de reescrever o arquivo inteiro a cada exclusão, apenas um byte (a "lápide") é modificado no registro. Isso torna a operação de exclusão extremamente rápida e eficiente.
  * **Índice Primário com Hash Extensível:** Para buscas diretas por ID (chave primária), o Hash Extensível foi escolhido por sua performance média de O(1), garantindo que a busca de um registro específico seja praticamente instantânea, independentemente do tamanho do arquivo.
  * **Índice Secundário com Árvore B+:** Para implementar o relacionamento 1:N (listar todos os itens de uma categoria), a Árvore B+ é a estrutura ideal. Ela é otimizada para buscas em grupo e por faixa, permitindo encontrar todos os registros associados a uma chave estrangeira de forma muito mais eficiente que uma varredura sequencial.

## Como Compilar e Executar

### Pré-requisitos

  * JDK (Java Development Kit) versão 11 ou superior, devidamente instalado e configurado nas variáveis de ambiente do sistema.

### Compilação

Todos os comandos devem ser executados a partir do **diretório raiz do projeto**.

#### Windows (Usando PowerShell ou Windows Terminal)

1.  Crie o diretório de saída para os arquivos compilados:
    ```powershell
    mkdir bin
    ```
2.  Compile todos os arquivos `.java` recursivamente para dentro do diretório `bin`:
    ```powershell
    javac -d bin @(Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName })
    ```
    *(Nota: Corrigido o erro de digitação de `-Recourse` para `-Recurse`)*

#### Linux e macOS

1.  Crie o diretório de saída (o `-p` evita erros caso o diretório já exista):
    ```bash
    mkdir -p bin
    ```
2.  Compile todos os arquivos `.java` encontrados recursivamente na pasta `src`:
    ```bash
    javac -d bin $(find src -name "*.java")
    ```

#### Windows (Usando CMD Clássico)

1.  Gere um arquivo `sources.txt` com a lista de todos os arquivos `.java`:
    ```cmd
    dir /s /b src\*.java > sources.txt
    ```
2.  Compile o projeto usando a lista de arquivos gerada:
    ```cmd
    javac -d bin @sources.txt
    ```

### Execução

Após a compilação bem-sucedida, execute o programa com o seguinte comando (o mesmo para todos os sistemas):

```bash
java -cp bin app.Main
```

O menu interativo do sistema será exibido no console, pronto para uso. Para testar a funcionalidade principal, recomenda-se criar algumas categorias e fornecedores, depois criar itens de estoque associados a eles e, por fim, usar a opção "Listar Itens por Categoria".
