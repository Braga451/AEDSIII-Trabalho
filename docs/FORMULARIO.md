## Formulário de Projeto – Decisões Técnicas

### a) Qual a estrutura usada para representar os registros?

A persistência dos dados foi implementada utilizando arquivos binários de acesso aleatório, através da classe `java.io.RandomAccessFile`. Cada arquivo de entidade (ex: `categorias.db`) possui um **cabeçalho** de 4 bytes que armazena o último ID utilizado, garantindo o controle sequencial dos identificadores.

Cada registro no arquivo segue a estrutura:
`[ Lápide (1 byte) ] [ Tamanho do Registro (int - 4 bytes) ] [ Dados Serializados (N bytes) ]`

* **Lápide:** Um marcador de 1 byte onde `' '` indica um registro ativo e `'*'` indica um registro logicamente excluído.
* **Tamanho do Registro:** Um inteiro que armazena o tamanho exato dos dados serializados, permitindo pular registros de tamanho variável de forma eficiente durante a leitura.

### b) Como atributos multivalorados do tipo string foram tratados?

O atributo multivalorado de telefones na entidade `Fornecedor` foi tratado convertendo a lista de Strings (`List<String>`) em uma única String concatenada, utilizando um caractere delimitador ``;``.

* No método `toByteArray`, a função `String.join(";")` é usada para criar a string única antes da gravação.
* No método `fromByteArray`, o método `split(";")` é utilizado para reconstruir a `ArrayList` de telefones a partir da string lida do arquivo.

### c) Como foi implementada a exclusão lógica?

A exclusão lógica foi implementada através de uma **"lápide"**, que corresponde ao primeiro byte de cada registro. Quando o método `delete` de um DAO é invocado, ele localiza o registro (utilizando o índice primário) e sobrescreve apenas este byte da lápide com um asterisco (`'*'`).

As operações de leitura são programadas para ignorar qualquer registro que comece com o caractere de lápide. Crucialmente, a chave do registro excluído também é removida do **índice primário (Hash Extensível)** para que o registro não seja mais encontrado em buscas diretas por ID.

### d) Além das PKs, quais outras chaves foram utilizadas nesta etapa?

Além das **chaves primárias (PKs)** de todas as tabelas, foi utilizada a **chave estrangeira (FK)** `idCategoria` na tabela `ItemEstoque`. Esta chave estabelece o relacionamento 1:N, onde uma Categoria pode conter múltiplos Itens de Estoque.

### e) Quais tipos de estruturas (hash, B+ Tree, extensível, etc.) foram utilizadas para cada chave de pesquisa?

* **Para todas as Chaves Primárias (PKs):** Foi implementado um índice de **Hash Extensível**. Esta estrutura foi escolhida por sua alta eficiência em buscas diretas por chave (complexidade O(1) em média), ideal para operações de `read`, `update` e `delete` baseadas em um ID específico.

* **Para a Chave Estrangeira `idCategoria`:** Foi implementado um índice secundário utilizando uma **Árvore B+**. Esta estrutura foi escolhida por ser extremamente eficiente em buscas por faixa e por agrupar chaves iguais, permitindo recuperar rapidamente todos os registros ('N') associados a uma chave específica ('1'), o que é a exata definição da busca no relacionamento 1:N.

### f) Como foi implementado o relacionamento 1:N (explique a lógica da navegação entre registros e integridade referencial)?

O relacionamento 1:N entre `Categoria` e `ItemEstoque` foi implementado através de um índice secundário de **Árvore B+** sobre a chave estrangeira `idCategoria` no arquivo `itens_estoque.db`.

A navegação funciona da seguinte forma: para listar todos os itens de uma categoria, o sistema consulta a Árvore B+ com o `idCategoria` desejado. A árvore retorna uma lista de todos os endereços de disco (ponteiros) para os registros de `ItemEstoque` que possuem aquele `idCategoria`. O DAO então percorre essa lista, acessando diretamente cada registro no arquivo de dados sem a necessidade de uma varredura sequencial.

A **integridade referencial** é mantida no nível da aplicação: antes de criar um `ItemEstoque`, a `MainView` utiliza os métodos `read` do `CategoriaDAO` e `FornecedorDAO` para verificar se os IDs da categoria e do fornecedor informados são válidos.

### g) Como os índices são persistidos em disco? (formato, atualização, sincronização com os dados).

Cada estrutura de índice gerencia seus próprios arquivos binários dedicados, garantindo a persistência entre execuções:

* O **Hash Extensível** utiliza dois arquivos: um para o diretório (`_dir.db`), que armazena a profundidade global e a lista de ponteiros para os cestos; e outro para os cestos (`_cestos.db`), que armazena a profundidade local e os pares chave/endereço de cada cesto.

* A **Árvore B+** utiliza um único arquivo (`_bplus.db`) que contém um cabeçalho com o ponteiro para a página raiz, seguido pelas páginas (nós) da árvore serializadas em disco.

A **sincronização** com os dados é imediata. A cada operação de `create` ou `delete` no DAO, a estrutura do índice em memória é modificada e, em seguida, as alterações são gravadas diretamente nos arquivos de índice correspondentes. Isso garante que os índices e os dados nunca fiquem dessincronizados.

### h) Como está estruturado o projeto no GitHub (pastas, módulos, arquitetura)?

O projeto está estruturado seguindo o padrão arquitetural **MVC + DAO**, organizado em pacotes Java distintos dentro da pasta `src/`:

* `/src/app`: Contém a classe `Main`, ponto de entrada da aplicação.
* `/src/view`: Contém a classe de interface com o usuário via console (`MainView`).
* `/src/model`: Contém as classes de domínio (`Categoria`, `Fornecedor`, `ItemEstoque`).
* `/src/dao`: Contém as classes de acesso a dados, responsáveis pela manipulação dos arquivos binários e pela interação com os índices.
* `/src/indices`: Contém as implementações das estruturas de dados (`HashExtensivel`, `ArvoreBPlus`).
* `/data`: Diretório na raiz do projeto onde todos os arquivos de dados (`.db`) e de índices são criados e mantidos.
