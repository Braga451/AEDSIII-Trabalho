# AEDS3TP
# Aplicativo de Gerenciamento de Dados em Memória Secundária

### Fase 2 - Índices B+ e Hash Extensível

Este projeto consiste no desenvolvimento de um aplicativo de gerenciamento de estoque que realiza a persistência de dados diretamente em arquivos binários, sem o uso de um SGBD. A Fase 2 implementa o CRUD completo para todas as entidades e utiliza estruturas de dados avançadas para indexação, como Hash Extensível para chaves primárias e Árvore B+ para chaves estrangeiras, otimizando as operações de busca.

## Componentes do Grupo

  * Arthur Braga de Campos Tinoco
  * Rafael Lima Mendonça Garcia

## Funcionalidades Implementadas (Fase 2)

  * **CRUD completo** para as entidades Categoria, Fornecedor e Item de Estoque.
  * **Persistência direta em arquivos binários** com cabeçalho de controle e exclusão lógica por lápide.
  * **Índice primário com Hash Extensível** para todas as tabelas, garantindo busca por ID com alta performance.
  * **Relacionamento 1:N (Categoria -\> Item de Estoque)** implementado com um índice secundário de **Árvore B+**, permitindo a listagem eficiente de todos os itens de uma determinada categoria.

## Estrutura do Projeto

O sistema segue a arquitetura **MVC + DAO**, com os pacotes organizados da seguinte forma:

  * `src/model`: Classes de domínio (entidades).
  * `src/dao`: Classes de acesso a dados, que manipulam os arquivos binários e os índices.
  * `src/indices`: Implementações das estruturas de dados (Hash Extensível e Árvore B+).
  * `src/view`: Camada de apresentação (interface via console).
  * `src/app`: Ponto de entrada da aplicação.
  * `data/`: Diretório onde os arquivos de dados (`.db`) são gerados.

## Como Compilar e Executar

### Pré-requisitos

  * JDK (Java Development Kit) versão 11 ou superior, devidamente instalado e configurado nas variáveis de ambiente do sistema.

### Compilação

Todos os comandos devem ser executados a partir do **diretório raiz do projeto**.

#### Windows (Usando PowerShell ou Windows Terminal)

O processo é feito em duas etapas para maior clareza.

1.  Crie o diretório de saída para os arquivos compilados:
    ```powershell
    mkdir bin
    ```
2.  Compile todos os arquivos `.java` recursivamente para dentro do diretório `bin`:
    ```powershell
    javac -d bin @(Get-ChildItem -Recourse -Path src -Filter *.java | ForEach-Object { $_.FullName })
    ```

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

Este método utiliza um arquivo auxiliar para listar os fontes.

1.  Gere um arquivo `sources.txt` com a lista de todos os arquivos `.java`:
    ```cmd
    dir /s /b src\*.java > sources.txt
    ```
2.  Compile o projeto usando a lista de arquivos gerada:
    ```cmd
    javac -d bin @sources.txt
    ```

### Execução

Após a compilação bem-sucedida, execute o programa com o seguinte comando (é o mesmo para todos os sistemas operacionais):

```bash
java -cp bin app.Main
```

  * O comando `-cp bin` (classpath) informa à JVM para procurar os arquivos `.class` compilados dentro do diretório `bin`.

O menu interativo do sistema será exibido no console, pronto para uso.
