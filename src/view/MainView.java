package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.CategoriaDAO;
import dao.FornecedorDAO;
import dao.ItemEstoqueDAO;
import model.Categoria;
import model.Fornecedor;
import model.ItemEstoque;

public class MainView {

    private CategoriaDAO categoriaDAO;
    private FornecedorDAO fornecedorDAO;
    private ItemEstoqueDAO itemEstoqueDAO;
    private Scanner scanner;

    public MainView() {
        try {
            this.categoriaDAO = new CategoriaDAO();
            this.fornecedorDAO = new FornecedorDAO();
            this.itemEstoqueDAO = new ItemEstoqueDAO();
            this.scanner = new Scanner(System.in);
        } catch (IOException e) {
            System.err.println("Erro ao inicializar os DAOs: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenuPrincipal();
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        gerenciarCategorias();
                        break;
                    case 2:
                        gerenciarFornecedores();
                        break;
                    case 3:
                        gerenciarItensEstoque();
                        break;
                    case 0:
                        System.out.println("Saindo do sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Ocorreu um erro: " + e.getMessage());
            }
        }
        
        scanner.close();
        try {
            categoriaDAO.close();
            fornecedorDAO.close();
            itemEstoqueDAO.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar os arquivos dos DAOs: " + e.getMessage());
        }
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Gerenciar Categorias");
        System.out.println("2. Gerenciar Fornecedores");
        System.out.println("3. Gerenciar Itens de Estoque");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // --- MÉTODOS DE GERENCIAMENTO DE CATEGORIAS ---

    private void gerenciarCategorias() throws IOException {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenuCategorias();
            opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1: criarCategoria(); break;
                case 2: buscarCategoria(); break;
                case 3: atualizarCategoria(); break;
                case 4: deletarCategoria(); break;
                case 0: System.out.println("Retornando ao menu principal..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void exibirMenuCategorias() {
        System.out.println("\n--- Gerenciamento de Categorias ---");
        System.out.println("1. Criar Categoria");
        System.out.println("2. Buscar Categoria por ID");
        System.out.println("3. Atualizar Categoria");
        System.out.println("4. Deletar Categoria");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private void criarCategoria() throws IOException {
        System.out.print("Digite o nome da categoria: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a descrição da categoria: ");
        String descricao = scanner.nextLine();
        
        Categoria novaCategoria = new Categoria(nome, descricao);
        Categoria categoriaCriada = categoriaDAO.create(novaCategoria);
        System.out.println("Categoria criada com sucesso! ID: " + categoriaCriada.getId());
    }

    private void buscarCategoria() throws IOException {
        System.out.print("Digite o ID da categoria a ser buscada: ");
        int id = Integer.parseInt(scanner.nextLine());
        Categoria categoria = categoriaDAO.read(id);
        if (categoria != null) {
            System.out.println("Categoria encontrada: " + categoria);
        } else {
            System.out.println("Categoria com ID " + id + " não encontrada.");
        }
    }

    private void atualizarCategoria() throws IOException {
        System.out.print("Digite o ID da categoria a ser atualizada: ");
        int id = Integer.parseInt(scanner.nextLine());
        Categoria categoriaExistente = categoriaDAO.read(id);
        if (categoriaExistente == null) {
            System.out.println("Categoria com ID " + id + " não encontrada.");
            return;
        }
        System.out.print("Digite o novo nome (ou deixe em branco para manter): ");
        String nome = scanner.nextLine();
        System.out.print("Digite a nova descrição (ou deixe em branco para manter): ");
        String descricao = scanner.nextLine();
        if (!nome.isEmpty()) categoriaExistente.setNome(nome);
        if (!descricao.isEmpty()) categoriaExistente.setDescricao(descricao);
        
        if (categoriaDAO.update(categoriaExistente)) {
            System.out.println("Categoria atualizada com sucesso!");
        } else {
            System.out.println("Falha ao atualizar a categoria.");
        }
    }

    private void deletarCategoria() throws IOException {
        System.out.print("Digite o ID da categoria a ser deletada: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (categoriaDAO.delete(id)) {
            System.out.println("Categoria deletada (logicamente) com sucesso!");
        } else {
            System.out.println("Falha ao deletar a categoria. ID não encontrado.");
        }
    }

    // --- MÉTODOS DE GERENCIAMENTO DE FORNECEDORES ---

    private void gerenciarFornecedores() throws IOException {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenuFornecedores();
            opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1: criarFornecedor(); break;
                case 2: buscarFornecedor(); break;
                case 3: atualizarFornecedor(); break;
                case 4: deletarFornecedor(); break;
                case 0: System.out.println("Retornando ao menu principal..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }
    
    private void exibirMenuFornecedores() {
        System.out.println("\n--- Gerenciamento de Fornecedores ---");
        System.out.println("1. Criar Fornecedor");
        System.out.println("2. Buscar Fornecedor por ID");
        System.out.println("3. Atualizar Fornecedor");
        System.out.println("4. Deletar Fornecedor");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private void criarFornecedor() throws IOException {
        System.out.print("Digite o nome do fornecedor: ");
        String nome = scanner.nextLine();
        System.out.print("Digite o CNPJ: ");
        String cnpj = scanner.nextLine();
        System.out.print("Digite o endereço: ");
        String endereco = scanner.nextLine();
        
        List<String> telefones = new ArrayList<>();
        while (true) {
            System.out.print("Digite um telefone (ou deixe em branco para terminar): ");
            String telefone = scanner.nextLine();
            if (telefone.isEmpty()) {
                break;
            }
            telefones.add(telefone);
        }
        
        Fornecedor novoFornecedor = new Fornecedor(nome, cnpj, endereco, telefones);
        Fornecedor fornecedorCriado = fornecedorDAO.create(novoFornecedor);
        System.out.println("Fornecedor criado com sucesso! ID: " + fornecedorCriado.getId());
    }

    private void buscarFornecedor() throws IOException {
        System.out.print("Digite o ID do fornecedor a ser buscado: ");
        int id = Integer.parseInt(scanner.nextLine());
        Fornecedor fornecedor = fornecedorDAO.read(id);
        if (fornecedor != null) {
            System.out.println("Fornecedor encontrado: " + fornecedor);
        } else {
            System.out.println("Fornecedor com ID " + id + " não encontrado.");
        }
    }
    
    private void atualizarFornecedor() throws IOException {
        System.out.print("Digite o ID do fornecedor a ser atualizado: ");
        int id = Integer.parseInt(scanner.nextLine());
        Fornecedor fornecedorExistente = fornecedorDAO.read(id);
        if (fornecedorExistente == null) {
            System.out.println("Fornecedor com ID " + id + " não encontrado.");
            return;
        }

        System.out.print("Digite o novo nome (ou deixe em branco para manter): ");
        String nome = scanner.nextLine();
        if (!nome.isEmpty()) fornecedorExistente.setNome(nome);

        System.out.print("Digite o novo CNPJ (ou deixe em branco para manter): ");
        String cnpj = scanner.nextLine();
        if (!cnpj.isEmpty()) fornecedorExistente.setCnpj(cnpj);

        System.out.print("Digite o novo endereço (ou deixe em branco para manter): ");
        String endereco = scanner.nextLine();
        if (!endereco.isEmpty()) fornecedorExistente.setEndereco(endereco);

        System.out.print("Deseja substituir a lista de telefones? (s/N): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            List<String> novosTelefones = new ArrayList<>();
            while (true) {
                System.out.print("Digite um novo telefone (ou deixe em branco para terminar): ");
                String telefone = scanner.nextLine();
                if (telefone.isEmpty()) break;
                novosTelefones.add(telefone);
            }
            fornecedorExistente.setTelefones(novosTelefones);
        }
        
        if (fornecedorDAO.update(fornecedorExistente)) {
            System.out.println("Fornecedor atualizado com sucesso!");
        } else {
            System.out.println("Falha ao atualizar o fornecedor.");
        }
    }

    private void deletarFornecedor() throws IOException {
        System.out.print("Digite o ID do fornecedor a ser deletado: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (fornecedorDAO.delete(id)) {
            System.out.println("Fornecedor deletado (logicamente) com sucesso!");
        } else {
            System.out.println("Falha ao deletar o fornecedor. ID não encontrado.");
        }
    }

    // --- MÉTODOS DE GERENCIAMENTO DE ITENS DE ESTOQUE ---

    private void gerenciarItensEstoque() throws IOException {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenuItensEstoque();
            opcao = Integer.parseInt(scanner.nextLine());
            switch (opcao) {
                case 1: criarItemEstoque(); break;
                case 2: buscarItemEstoque(); break;
                case 3: atualizarItemEstoque(); break;
                case 4: deletarItemEstoque(); break;
                case 5: listarItensPorCategoria(); break; // <-- NOVA CHAMADA
                case 0: System.out.println("Retornando ao menu principal..."); break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void exibirMenuItensEstoque() {
        System.out.println("\n--- Gerenciamento de Itens de Estoque ---");
        System.out.println("1. Criar Item");
        System.out.println("2. Buscar Item por ID");
        System.out.println("3. Atualizar Item");
        System.out.println("4. Deletar Item");
        System.out.println("5. Listar Itens por Categoria"); // <-- NOVA OPÇÃO
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
    }

    private void criarItemEstoque() throws IOException {
        System.out.print("Digite o nome do item: ");
        String nome = scanner.nextLine();
        
        System.out.print("Digite a quantidade inicial: ");
        int quantidade = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Digite o preço unitário (ex: 12.99): ");
        double preco = Double.parseDouble(scanner.nextLine());

        int idCategoria;
        while (true) {
            System.out.print("Digite o ID da Categoria: ");
            idCategoria = Integer.parseInt(scanner.nextLine());
            if (categoriaDAO.read(idCategoria) != null) {
                break;
            }
            System.out.println("Erro: Categoria com ID " + idCategoria + " não existe. Tente novamente.");
        }

        int idFornecedor;
        while (true) {
            System.out.print("Digite o ID do Fornecedor: ");
            idFornecedor = Integer.parseInt(scanner.nextLine());
            if (fornecedorDAO.read(idFornecedor) != null) {
                break;
            }
            System.out.println("Erro: Fornecedor com ID " + idFornecedor + " não existe. Tente novamente.");
        }

        ItemEstoque novoItem = new ItemEstoque(nome, quantidade, preco, idCategoria, idFornecedor);
        ItemEstoque itemCriado = itemEstoqueDAO.create(novoItem);
        
        System.out.println("Item de estoque criado com sucesso! " + itemCriado);
    }

    private void buscarItemEstoque() throws IOException {
        System.out.print("Digite o ID do item a ser buscado: ");
        int id = Integer.parseInt(scanner.nextLine());
        ItemEstoque item = itemEstoqueDAO.read(id);
        if (item != null) {
            System.out.println("Item encontrado: " + item);
        } else {
            System.out.println("Item com ID " + id + " não encontrado.");
        }
    }
    
    private void atualizarItemEstoque() throws IOException {
        System.out.print("Digite o ID do item a ser atualizado: ");
        int id = Integer.parseInt(scanner.nextLine());
        ItemEstoque itemExistente = itemEstoqueDAO.read(id);
        if (itemExistente == null) {
            System.out.println("Item com ID " + id + " não encontrado.");
            return;
        }

        System.out.print("Digite o novo nome (ou deixe em branco para manter): ");
        String nome = scanner.nextLine();
        if (!nome.isEmpty()) itemExistente.setNome(nome);

        System.out.print("Digite a nova quantidade (ou deixe em branco para manter): ");
        String qtdStr = scanner.nextLine();
        if (!qtdStr.isEmpty()) itemExistente.setQuantidade(Integer.parseInt(qtdStr));

        System.out.print("Digite o novo preço (ou deixe em branco para manter): ");
        String precoStr = scanner.nextLine();
        if (!precoStr.isEmpty()) itemExistente.setPrecoUnitario(Double.parseDouble(precoStr));
        
        if (itemEstoqueDAO.update(itemExistente)) {
            System.out.println("Item atualizado com sucesso!");
        } else {
            System.out.println("Falha ao atualizar o item.");
        }
    }

    private void deletarItemEstoque() throws IOException {
        System.out.print("Digite o ID do item a ser deletado: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (itemEstoqueDAO.delete(id)) {
            System.out.println("Item deletado (logicamente) com sucesso!");
        } else {
            System.out.println("Falha ao deletar o item. ID não encontrado.");
        }
    }

    /**
     * NOVO MÉTODO: Pede um ID de categoria e lista todos os itens associados.
     */
    private void listarItensPorCategoria() throws IOException {
        System.out.print("Digite o ID da Categoria para listar os itens: ");
        int idCategoria = Integer.parseInt(scanner.nextLine());

        // Valida se a categoria existe
        if (categoriaDAO.read(idCategoria) == null) {
            System.out.println("Erro: Categoria com ID " + idCategoria + " não existe.");
            return;
        }

        List<ItemEstoque> itens = itemEstoqueDAO.readAllByIdCategoria(idCategoria);

        if (itens.isEmpty()) {
            System.out.println("Nenhum item encontrado para esta categoria.");
        } else {
            System.out.println("\n--- Itens da Categoria ID " + idCategoria + " ---");
            for (ItemEstoque item : itens) {
                System.out.println(item);
            }
        }
    }
}