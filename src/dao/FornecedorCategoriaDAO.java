package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import indices.ArvoreBPlus; // Importa a nossa Árvore B+
import model.FornecedorCategoria;

public class FornecedorCategoriaDAO {

    private final String DB_FILE = "data/fornecedor_categoria.db";
    private RandomAccessFile raf;
    private ArvoreBPlus indicePorFornecedor; // O nosso índice B+

    // O cabeçalho guardará o número total de relações cadastradas
    private final int HEADER_SIZE = 4; 

    public FornecedorCategoriaDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");

        // Inicializa o índice B+ que usará o idFornecedor como chave
        indicePorFornecedor = new ArvoreBPlus("fc_idx_fornecedor");

        // Se o arquivo é novo, inicializa o cabeçalho com 0 (0 relações)
        if (raf.length() == 0) {
            raf.writeInt(0);
        }
    }

    private void atualizarCabecalho(int novaContagem) throws IOException {
        raf.seek(0);
        raf.writeInt(novaContagem);
    }

    private int lerContagemDoCabecalho() throws IOException {
        raf.seek(0);
        return raf.readInt();
    }

    /**
     * Cria um novo relacionamento N:N entre um fornecedor e uma categoria.
     */
    public boolean create(int idFornecedor, int idCategoria) throws IOException {
        // Validação básica (métodos readAll já fazem isso, mas é uma boa prática)
        if (read(idFornecedor, idCategoria) != null) {
            System.err.println("Erro: Este relacionamento já existe.");
            return false;
        }

        FornecedorCategoria fc = new FornecedorCategoria(idFornecedor, idCategoria);
        byte[] recordBytes = fc.toByteArray();
        
        raf.seek(raf.length());
        long enderecoRegistro = raf.getFilePointer();
        
        raf.writeByte(' '); // Lápide
        raf.writeInt(recordBytes.length);
        raf.write(recordBytes);
        
        // Adiciona a entrada ao índice B+
        // Chave: idFornecedor, Valor: endereço do registro
        indicePorFornecedor.create(idFornecedor, enderecoRegistro);
        
        // Atualiza a contagem no cabeçalho
        int contagem = lerContagemDoCabecalho();
        atualizarCabecalho(contagem + 1);
        
        return true;
    }

    /**
     * Busca um relacionamento específico.
     * Este método usa varredura sequencial, pois não temos um índice pela chave composta.
     */
    public FornecedorCategoria read(int idFornecedor, int idCategoria) throws IOException {
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            
            if (lapide == ' ') {
                byte[] recordBytes = new byte[recordSize];
                raf.read(recordBytes);
                FornecedorCategoria fc = new FornecedorCategoria();
                fc.fromByteArray(recordBytes);
                
                if (fc.getIdFornecedor() == idFornecedor && fc.getIdCategoria() == idCategoria) {
                    return fc; // Encontrado
                }
            } else {
                raf.seek(currentPos + 1 + 4 + recordSize); // Pula registro excluído
            }
        }
        return null; // Não encontrado
    }

    /**
     * Deleta um relacionamento (exclusão lógica).
     */
    public boolean delete(int idFornecedor, int idCategoria) throws IOException {
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);
            
            if (lapide == ' ') {
                FornecedorCategoria fc = new FornecedorCategoria();
                fc.fromByteArray(recordBytes);
                
                if (fc.getIdFornecedor() == idFornecedor && fc.getIdCategoria() == idCategoria) {
                    // Encontrou o registro, marca a lápide
                    raf.seek(currentPos);
                    raf.writeByte('*');
                    
                    // NOTA: A remoção da Árvore B+ não será implementada (complexo).
                    // O método 'readAll' tratará disso verificando a lápide.
                    
                    return true;
                }
            }
        }
        return false; // Não encontrado
    }

    /**
     * [BUSCA OTIMIZADA COM B+ TREE]
     * Lista todas as categorias associadas a um fornecedor.
     */
    public List<FornecedorCategoria> readAllByIdFornecedor(int idFornecedor) throws IOException {
        List<FornecedorCategoria> resultados = new ArrayList<>();
        
        // 1. Pede à Árvore B+ a lista de todos os endereços para este idFornecedor
        List<Long> enderecos = indicePorFornecedor.readAll(idFornecedor);
        
        // 2. Itera sobre os endereços e lê cada registro
        for (long endereco : enderecos) {
            raf.seek(endereco);
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);

            if (lapide == ' ') { // Importante: ignora registros deletados logicamente
                FornecedorCategoria fc = new FornecedorCategoria();
                fc.fromByteArray(recordBytes);
                // Confirmação dupla (boa prática)
                if (fc.getIdFornecedor() == idFornecedor) {
                    resultados.add(fc);
                }
            }
        }
        return resultados;
    }

    /**
     * [BUSCA COM VARREDURA SEQUENCIAL]
     * Lista todos os fornecedores associados a uma categoria.
     * Cumpre o requisito (2) de ser acessível de ambos os lados.
     */
    public List<FornecedorCategoria> readAllByIdCategoria(int idCategoria) throws IOException {
        List<FornecedorCategoria> resultados = new ArrayList<>();
        
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            
            if (lapide == ' ') {
                byte[] recordBytes = new byte[recordSize];
                raf.read(recordBytes);
                FornecedorCategoria fc = new FornecedorCategoria();
                fc.fromByteArray(recordBytes);
                
                if (fc.getIdCategoria() == idCategoria) {
                    resultados.add(fc);
                }
            } else {
                raf.seek(currentPos + 1 + 4 + recordSize);
            }
        }
        return resultados;
    }

    public void close() throws IOException {
        raf.close();
        indicePorFornecedor.close();
    }
}