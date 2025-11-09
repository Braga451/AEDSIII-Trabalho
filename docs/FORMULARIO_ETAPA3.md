## Formulário de Projeto – Decisões Técnicas (Fase 3)

### 1\. Qual foi o relacionamento N:N escolhido e quais tabelas ele conecta?

O relacionamento N:N escolhido foi entre as entidades **`Fornecedor`** e **`Categoria`**. Este relacionamento é implementado através de uma nova tabela associativa (intermediária) chamada **`FornecedorCategoria`**.

Essa escolha foi feita por ser a mais lógica e limpa, permitindo que um fornecedor possa fornecer itens de múltiplas categorias (ex: "Distribuidora ABC" fornece Laticínios e Limpeza) e que uma categoria possa ser fornecida por múltiplos fornecedores (ex: "Laticínios" é fornecido pela "Distribuidora ABC" e pela "Laticínios da Serra"), sem alterar a estrutura 1:N já existente entre `ItemEstoque` e `Fornecedor`.

### 2\. Qual estrutura de índice foi utilizada (B+ ou Hash Extensível)? Justifique a escolha.

Foi utilizada a **Árvore B+** (B+ Tree).

**Justificativa:** A principal função de um índice em um relacionamento N:N é responder a perguntas de *agrupamento* (1-para-N), como "Quais são *todas* as categorias do Fornecedor X?" ou "Quais são *todos* os fornecedores da Categoria Y?".

  * O **Hash Extensível** é otimizado para buscas de ponto (1-para-1), respondendo "O registro X existe?". Ele é ineficiente para buscas de grupo, pois encontrar todos os registros com a mesma chave exigiria uma varredura no índice.
  * A **Árvore B+** é a ferramenta correta para esta tarefa, pois ela armazena chaves de forma ordenada e agrupada. Ao indexar por `idFornecedor`, todos os registros do "Fornecedor 1" ficam juntos nas folhas da árvore. Isso nos permite encontrar o primeiro registro e, em seguida, percorrer a lista ligada de folhas para recuperar *todos* os registros associados de forma extremamente eficiente.

### 3\. Como foi implementada a chave composta da tabela intermediária?

A chave primária composta (`idFornecedor`, `idCategoria`) é implementada **logicamente** dentro da nova entidade `model/FornecedorCategoria.java`. Esta classe armazena ambos os IDs como seus únicos atributos.

Embora o arquivo binário não tenha um índice de chave composta, a unicidade do par é validada no método `create` do `FornecedorCategoriaDAO` através de uma chamada ao método `read(idFornecedor, idCategoria)`, que realiza uma varredura para garantir que o vínculo não exista antes de ser criado.

### 4\. Como é feita a busca eficiente de registros por meio do índice?

A busca eficiente (ex: `readAllByIdFornecedor`) é realizada através da **Árvore B+** indexada pelo `idFornecedor`. O fluxo é o seguinte:

1.  O método `fcDAO.readAllByIdFornecedor(id)` é chamado.
2.  Internamente, ele chama o método `indicePorFornecedor.readAll(id)`.
3.  A Árvore B+ desce até a folha correta e coleta todos os **endereços de disco (ponteiros)** associados àquela chave `idFornecedor`, percorrendo a lista ligada de folhas, se necessário.
4.  O DAO recebe uma `List<Long>` (lista de endereços) de volta.
5.  O DAO itera por essa lista, usando `raf.seek(endereco)` para pular diretamente para a posição de cada registro no arquivo `fornecedor_categoria.db`, lendo e retornando apenas os registros válidos (lápide `' '`).

### 5\. Como o sistema trata a integridade referencial (remoção/atualização)?

A integridade referencial é mantida no nível da **aplicação** (na `MainView`):

  * **Criação:** Antes de o método `vincularFornecedorCategoria()` ser executado, o sistema chama `fornecedorDAO.read(idF)` e `categoriaDAO.read(idC)`. O vínculo N:N só é criado se ambas as entidades principais forem encontradas, evitando a criação de "vínculos órfãos".
  * **Remoção:** A remoção do vínculo é feita na tabela intermediária (`fcDAO.delete()`). Se um `Fornecedor` ou `Categoria` for deletado (Fase 2), os vínculos N:N se tornam órfãos, mas não quebram o sistema. Nas buscas de N:N (ex: `listarCategoriasPorFornecedor`), o sistema tenta buscar a Categoria pelo ID; se ela não for encontrada (pois foi deletada), ela simplesmente não é exibida na lista, tratando o "vínculo órfão" de forma elegante.

### 6\. Como foi organizada a persistência dos dados dessa nova tabela?

A persistência da `FornecedorCategoria` segue **exatamente o mesmo padrão** das tabelas anteriores, garantindo consistência:

  * **Arquivo:** `data/fornecedor_categoria.db`.
  * **Cabeçalho:** Um inteiro de 4 bytes na posição 0, que usamos para armazenar a contagem total de relacionamentos ativos.
  * **Registros:** Cada registro segue o formato `[ Lápide (1 byte) ] [ Tamanho do Registro (int - 4 bytes) ] [ Dados Serializados (N bytes) ]`.
  * **Dados:** Os dados serializados consistem em `[ idFornecedor (int) ] [ idCategoria (int) ]`.

### 7\. Descreva como o código da tabela intermediária se integra com o CRUD das tabelas principais.

A integração ocorre na `MainView` e no `FornecedorCategoriaDAO`:

1.  Um novo DAO, `FornecedorCategoriaDAO` (ou `fcDAO`), foi criado e instanciado na `MainView` junto com os outros DAOs.
2.  Um novo menu, "Relacionar Fornecedor/Categoria (N:N)", foi adicionado à `MainView`.
3.  Este menu chama os métodos do `fcDAO` (ex: `create`, `delete`).
4.  Crucialmente, para exibir os resultados de forma amigável (ex: "Listar Categorias de um Fornecedor"), o método `listarCategoriasPorFornecedor` na `MainView` primeiro usa o `fcDAO` para obter os IDs das categorias e, em seguida, usa o **`categoriaDAO.read(id)`** para buscar o nome e a descrição de cada categoria, demonstrando a integração entre os DAOs.

### 8\. Descreva como está organizada a estrutura de diretórios e módulos no repositório após esta fase.

A estrutura de diretórios permanece a mesma da Fase 2, provando sua escalabilidade. A nova funcionalidade foi integrada apenas adicionando novos arquivos aos pacotes existentes, sem a necessidade de refatoração da arquitetura:

  * `src/model/FornecedorCategoria.java`: Novo arquivo adicionado.
  * `src/dao/FornecedorCategoriaDAO.java`: Novo arquivo adicionado.
  * `src/view/MainView.java`: Arquivo existente que foi *atualizado* para incluir o novo menu e as chamadas ao novo DAO.
  * `data/`: Agora contém os novos arquivos de dados e índice: `fornecedor_categoria.db` e `fc_idx_fornecedor_bplus.db`.

