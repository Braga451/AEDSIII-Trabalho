# AEDS3TP - Aplicativo de Gerenciamento de Dados em Memória Secundária

### Fase 3 - Índices B+, Hash Extensível e Relacionamento N:N

Este projeto consiste no desenvolvimento de um sistema de banco de dados em baixo nível, focado no gerenciamento de um estoque. A solução realiza a persistência de dados diretamente em arquivos binários, sem o auxílio de um SGBD tradicional.

O sistema implementa o CRUD completo para todas as entidades e utiliza estruturas de índice avançadas — **Hash Extensível** para chaves primárias e **Árvore B+** para chaves estrangeiras — para garantir operações de busca eficientes e escaláveis.

## 1\. Componentes do Grupo

  * Arthur Braga de Campos Tinoco
  * Rafael Lima Mendonça Garcia

## 2\. Funcionalidades Implementadas

  - ✅ **CRUD Completo:** Para as entidades `Categoria`, `Fornecedor` e `ItemEstoque`.
  - ✅ **Relacionamento 1:N:** Entre `Categoria` e `ItemEstoque`, otimizado com um índice secundário de **Árvore B+**.
  - ✅ **Relacionamento N:N:** Entre `Fornecedor` e `Categoria`, implementado com uma tabela associativa (`FornecedorCategoria`) e um índice de **Árvore B+** para buscas eficientes.
  - ✅ **Persistência Binária Robusta:** Os dados são armazenados em arquivos de acesso aleatório (`.db`) com um cabeçalho para controle de IDs e um sistema de **exclusão lógica por lápide**.
  - ✅ **Índice Primário com Hash Extensível:** Todas as buscas por ID de registro são otimizadas com um índice Hash Extensível, garantindo performance média de **O(1)**.

## 3\. Arquitetura e Estrutura de Pastas

O sistema segue a arquitetura **MVC + DAO (Model-View-Controller + Data Access Object)**. A estrutura de pastas foi projetada para separar claramente as responsabilidades de cada componente:

```
.
├── bin/               # Diretório de saída dos arquivos compilados (.class)
├── data/              # Armazena os arquivos de dados (.db) e índices
├── docs/              # Contém a documentação PDF do projeto (Fase 2)
└── src/               # Código-fonte do projeto
    ├── app/           # Ponto de entrada da aplicação (classe Main)
    ├── view/          # Camada de apresentação (interface com o usuário via console)
    ├── model/         # Classes de domínio (entidades: Categoria, Fornecedor, etc.)
    ├── dao/           # Camada de acesso a dados (manipulação dos arquivos e índices)
    └── indices/       # Implementações das estruturas de dados (Hash e B+ Tree)
```

## 4\. Como Compilar e Executar

### Pré-requisitos

  * **JDK (Java Development Kit) versão 11 ou superior**, devidamente instalado e configurado nas variáveis de ambiente do sistema.

### Compilação

Todos os comandos devem ser executados a partir do **diretório raiz do projeto**.

-----

#### Windows (PowerShell / Windows Terminal)

*Recomendado para Windows 10 e 11.*

1.  Crie o diretório de saída (necessário apenas na primeira vez):
    ```powershell
    mkdir bin
    ```
2.  Compile todos os arquivos `.java` recursivamente:
    ```powershell
    javac -d bin @(Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName })
    ```
    *(Nota: Corrigido o erro de digitação de `-Recourse` para `-Recurse`)*

-----

#### Linux e macOS

1.  Crie o diretório de saída:
    ```bash
    mkdir -p bin
    ```
2.  Compile todos os arquivos `.java` recursivamente:
    ```bash
    javac -d bin $(find src -name "*.java")
    ```

-----

#### Windows (CMD Clássico)

*Método alternativo para versões mais antigas do Windows.*

1.  Crie um arquivo com a lista de todos os fontes:
    ```cmd
    dir /s /b src\*.java > sources.txt
    ```
2.  Compile o projeto usando a lista de arquivos gerada:
    ```cmd
    javac -d bin @sources.txt
    ```

### Execução

Após a compilação, execute o programa com o seguinte comando (válido para todos os sistemas):

```bash
java -cp bin app.Main
```

*O comando `-cp bin` (classpath) informa à JVM para procurar os arquivos `.class` compilados dentro do diretório `bin`.*

O menu interativo do sistema será exibido no console.

## 5\. Fluxo de Teste Recomendado

Para validar todas as funcionalidades implementadas (Fases 2 e 3), siga este fluxo:

1.  **Comece com uma base limpa:** Apague as pastas `data/` e `bin/` (se existirem) antes de compilar.
2.  **Compile e Execute** o programa.
3.  **Crie as Entidades Principais:**
      * Acesse `1. Gerenciar Categorias` e crie pelo menos duas (ex: "Laticínios" e "Limpeza").
      * Acesse `2. Gerenciar Fornecedores` e crie pelo menos dois (ex: "Distribuidora ABC" e "Laticínios da Serra").
4.  **Teste o Relacionamento 1:N (Fase 2):**
      * Acesse `3. Gerenciar Itens de Estoque` e crie alguns itens, associando-os às categorias e fornecedores criados (ex: crie "Queijo" e "Iogurte", ambos para a categoria "Laticínios").
      * No mesmo menu, use a opção `5. Listar Itens por Categoria` e digite o ID de "Laticínios". O sistema deve listar apenas "Queijo" e "Iogurte".
5.  **Teste o Relacionamento N:N (Fase 3):**
      * Acesse `4. Relacionar Fornecedor/Categoria (N:N)`.
      * Use a opção `1. Vincular Fornecedor a Categoria` e crie os seguintes vínculos:
          * "Distribuidora ABC" (ID 1) -\> "Laticínios" (ID 1)
          * "Distribuidora ABC" (ID 1) -\> "Limpeza" (ID 2)
      * No mesmo menu, use a opção `3. Listar Categorias de um Fornecedor` e digite o ID da "Distribuidora ABC" (ID 1). O sistema deve listar "Laticínios" e "Limpeza", comprovando o funcionamento do índice B+ no relacionamento N:N.
