package dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import indices.HashExtensivel; // 1. IMPORTAR A CLASSE DO ÍNDICE
import model.Fornecedor;

public class FornecedorDAO {

    private final String DB_FILE = "data/fornecedores.db";
    private RandomAccessFile raf;
    private HashExtensivel indice; // 2. ADICIONAR O ÍNDICE COMO ATRIBUTO

    private final int HEADER_SIZE = 4;

    public FornecedorDAO() throws IOException {
        raf = new RandomAccessFile(DB_FILE, "rw");

        // 3. INICIALIZAR O ÍNDICE COM UM NOME ÚNICO
        // Serão criados "data/fornecedores_pk_dir.db" e "data/fornecedores_pk_cestos.db"
        indice = new HashExtensivel("fornecedores_pk");

        if (raf.length() == 0) {
            raf.writeInt(0);
        }
    }
    
    public Fornecedor create(Fornecedor fornecedor) throws IOException {
        raf.seek(0);
        int ultimoID = raf.readInt();
        int novoID = ultimoID + 1;
        fornecedor.setId(novoID);
        raf.seek(0);
        raf.writeInt(novoID);
        
        raf.seek(raf.length());
        
        // 4. CAPTURAR O ENDEREÇO ANTES DE ESCREVER
        long enderecoRegistro = raf.getFilePointer();
        
        raf.writeByte(' '); // Lápide
        byte[] recordBytes = fornecedor.toByteArray();
        raf.writeInt(recordBytes.length);
        raf.write(recordBytes);
        
        // 5. ADICIONAR O NOVO PAR [ID, ENDEREÇO] AO ÍNDICE
        indice.create(novoID, enderecoRegistro);
        
        return fornecedor;
    }

    /**
     * Busca um Fornecedor pelo seu ID usando o índice HASH.
     * Versão RÁPIDA e OTIMIZADA.
     */
    public Fornecedor read(int id) throws IOException {
        long endereco = indice.read(id);

        if (endereco == -1) {
            return null;
        }

        raf.seek(endereco);
        
        byte lapide = raf.readByte();
        int recordSize = raf.readInt();
        byte[] recordBytes = new byte[recordSize];
        raf.read(recordBytes);

        if (lapide == ' ') {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.fromByteArray(recordBytes);
            if (fornecedor.getId() == id) {
                return fornecedor;
            }
        }
        
        return null;
    }

    public boolean update(Fornecedor fornecedor) throws IOException {
        Fornecedor fornecedorAntigo = read(fornecedor.getId());
        if (fornecedorAntigo == null) {
            return false;
        }
        
        // A busca sequencial ainda é necessária para encontrar a POSIÇÃO e TAMANHO originais
        raf.seek(HEADER_SIZE);
        while (raf.getFilePointer() < raf.length()) {
            long currentPos = raf.getFilePointer();
            byte lapide = raf.readByte();
            int recordSize = raf.readInt();
            byte[] recordBytes = new byte[recordSize];
            raf.read(recordBytes);

            if (lapide == ' ') {
                Fornecedor temp = new Fornecedor();
                temp.fromByteArray(recordBytes);
                
                if (temp.getId() == fornecedor.getId()) {
                    byte[] newRecordBytes = fornecedor.toByteArray();
                    
                    if (newRecordBytes.length <= recordSize) {
                        raf.seek(currentPos + 1 + 4);
                        raf.write(newRecordBytes);
                    } else {
                        delete(fornecedor.getId());
                        create(fornecedor);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean delete(int id) throws IOException {
        long endereco = indice.read(id);
        if (endereco == -1) {
            return false;
        }

        raf.seek(endereco);
        raf.writeByte('*');
        
        indice.delete(id); // REMOVE A CHAVE DO ÍNDICE

        return true;
    }

    public void close() throws IOException {
        raf.close();
        indice.close(); // 6. FECHAR OS ARQUIVOS DO ÍNDICE
    }
}