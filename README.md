# AEDS3TP - Sistema de Gerenciamento de Dados em Memória Secundária

### Projeto Final (Fases 1 a 5)

Este projeto consiste no desenvolvimento de um sistema de banco de dados em baixo nível, focado no gerenciamento de um estoque. A solução realiza a persistência de dados diretamente em arquivos binários, sem o auxílio de um SGBD tradicional, implementando estruturas de dados avançadas, segurança e algoritmos de busca.

## 1\. Componentes do Grupo

  * Arthur Braga de Campos Tinoco
  * Rafael Lima Mendonça Garcia

## 2\. Funcionalidades Implementadas

### Gerenciamento de Dados (Fases 1 e 2)

  * ✅ **CRUD Completo:** Para as entidades `Categoria`, `Fornecedor` e `ItemEstoque`.
  * ✅ **Persistência Binária:** Arquivos de acesso aleatório (`.db`) com cabeçalho de metadados e **exclusão lógica por lápide**.
  * ✅ **Índices:**
      * **Hash Extensível:** Para todas as Chaves Primárias (PK), garantindo busca $O(1)$.
      * **Árvore B+:** Para chaves estrangeiras (Relacionamento 1:N), permitindo listar itens por categoria de forma eficiente.

### Relacionamentos Avançados (Fase 3)

  * ✅ **Relacionamento N:N:** Entre `Fornecedor` e `Categoria`, implementado com uma tabela associativa e índice de **Árvore B+**.

### Segurança e Utilitários (Fase 4)

  * ✅ **Criptografia RSA:** O campo sensível `CNPJ` é criptografado antes de ser gravado no disco e descriptografado apenas na leitura. As chaves (Pública/Privada) são gerenciadas automaticamente.

![licensed-image](https://github.com/user-attachments/assets/0c7669ab-387e-4167-b3b2-f6e02e73c1b7)


  * ✅ **Backup Compactado:** Sistema capaz de realizar backup completo de todos os bancos de dados e chaves.
  * ✅ **Compressão:** Implementação dos algoritmos **Huffman** e **LZW** para reduzir o tamanho dos backups.
  * ✅ **Restore (Recuperação):** Capacidade de restaurar o sistema integralmente a partir de um arquivo `.cmp` em caso de perda de dados.

### Busca Textual (Fase 5)

  * ✅ **Casamento de Padrões:** Implementação de motor de busca para encontrar itens pelo nome (substring).
  * ✅ **Algoritmos:** Opção de escolha entre **KMP (Knuth-Morris-Pratt)** e **Boyer-Moore**.

## 3\. Arquitetura e Estrutura de Pastas

O sistema segue a arquitetura **MVC + DAO**. A estrutura de pacotes reflete a modularização do projeto:

```text
.
├── bin/                 # Arquivos compilados (.class)
├── data/                # Arquivos de dados (.db), índices e chaves de segurança
├── docs/                # Documentação e relatórios
└── src/                 # Código-fonte
    ├── app/             # Main (Ponto de entrada)
    ├── model/           # Entidades (Categoria, Fornecedor, etc.)
    ├── view/            # Interface via Console
    ├── dao/             # Acesso a dados e gerenciamento de arquivos
    ├── indices/         # Estruturas (Hash Extensível, Árvore B+)
    ├── compressao/      # Algoritmos Huffman, LZW e Gerenciador de Backup
    ├── seguranca/       # Criptografia RSA e gestão de chaves
    └── padroes/         # Algoritmos de busca KMP e Boyer-Moore
```

## 4\. Como Compilar e Executar

### Pré-requisitos

  * **JDK 11 ou superior**.

### Compilação

Execute a partir da **raiz** do projeto:

**Windows (PowerShell):**

```powershell
mkdir bin
javac -d bin -encoding UTF-8 @(Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName })
```

**Linux / macOS:**

```bash
mkdir -p bin
javac -d bin -encoding UTF-8 $(find src -name "*.java")
```

### Execução

```bash
java -cp bin app.Main
```

## 5\. Fluxo de Teste Recomendado (Tour Completo)

Para validar todas as funcionalidades do projeto final:

1.  **Limpeza (Opcional):** Apague a pasta `data/` para iniciar o sistema do zero. O sistema recriará arquivos e chaves RSA automaticamente.
2.  **Criação de Dados (CRUD + RSA):**
      * Crie Categorias ("Eletrônicos") e Fornecedores ("Tech Safe").
      * *Observe:* Ao listar o fornecedor, o CNPJ aparece legível, provando que o RSA descriptografou o dado do disco.
3.  **Relacionamentos (B+ Tree):**
      * Crie Itens de Estoque vinculados às categorias.
      * Use a opção "Listar Itens por Categoria" para testar a Árvore B+.
      * Vincule Fornecedor a Categoria (Menu 4) e liste os vínculos.
4.  **Busca Textual (Fase 5):**
      * Vá ao menu **6. Pesquisar Padrão**.
      * Escolha **KMP** ou **Boyer-Moore**.
      * Busque por uma parte do nome (ex: "Dell" para achar "Notebook Dell").
5.  **Backup e Desastre (Fase 4):**
      * Vá ao menu **5. Utilitários**.
      * Faça um **Backup Huffman**.
      * **Simule o Desastre:** Feche o programa e apague todos os arquivos `.db` da pasta `data/`.
      * Abra o programa novamente (os dados terão sumido).
      * Use a opção **3. Restaurar Backup (Huffman)**.
      * Verifique se os dados (e as chaves de criptografia) foram restaurados corretamente listando os fornecedores.
